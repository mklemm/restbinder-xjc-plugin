package net.codesup.restbinder.plugins.xjc;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.kscs.util.plugins.xjc.base.AbstractPlugin;
import com.kscs.util.plugins.xjc.base.Opt;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CCustomizable;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;


/**
 * @author Mirko Klemm 2015-09-29
 */
public class RestBinderPlugin extends AbstractPlugin {
	public static final String CUSTOMIZATION_NS = "http://www.codesup.net/restbinder";
	public static final String TARGET_CUSTOMIZATION_ELEMENT_NAME = "target";

	@Opt
	protected String webDocumentVariableName = "__webDocument__";

	@Opt
	protected String linkGetterSuffix = "Link";

	@Opt
	protected String webDocumentClass = "net.codesup.restbinder.client.WebDocument";

	@Override
	public List<String> getCustomizationURIs() {
		return Collections.singletonList(RestBinderPlugin.CUSTOMIZATION_NS);
	}

	@Override
	public boolean isCustomizationTagName(final String nsUri, final String localName) {
		return RestBinderPlugin.CUSTOMIZATION_NS.equals(nsUri) && localName.equals(RestBinderPlugin.TARGET_CUSTOMIZATION_ELEMENT_NAME);
	}

	@Override
	public String getOptionName() {
		return "Xrestbinder";
	}

	@Override
	public boolean run(final Outline outline, final Options opt, final ErrorHandler errorHandler) throws SAXException {
		for(final ClassOutline classOutline:outline.getClasses()) {
			JVar webDocumentVar = classOutline.getSuperClass() == null ? null : classOutline.getSuperClass().implClass.fields().get(this.webDocumentVariableName);
			for(final FieldOutline fieldOutline:classOutline.getDeclaredFields()) {
				final CPluginCustomization targetElement = getCustomizationElement(fieldOutline.getPropertyInfo(), RestBinderPlugin.TARGET_CUSTOMIZATION_ELEMENT_NAME);
				if (targetElement != null) {
					targetElement.markAsAcknowledged();
					final String targetElementSpec = getCustomizationAttribute(targetElement, "ref", null);
					if (targetElementSpec != null) {
						final String[] targetNameFields = targetElementSpec.split(":");
						final String targetNamespacePrefix = targetNameFields[0];
						final String targetLocalName = targetNameFields[1];
						final String targetNamespaceUri = targetElement.element.lookupNamespaceURI(targetNamespacePrefix);
						final QName targetElementName = new QName(targetNamespaceUri, targetLocalName);
						final CClassInfo targetClassInfo = getClassOutline(outline, targetElementName);
						if(targetClassInfo == null) {
							errorHandler.error(new SAXParseException(getMessage("error.elementNotFound", targetElementName), targetElement.locator));
						} else {
							final JClass classRef = outline.getCodeModel().ref(targetClassInfo.fullName());
							final JMethod oldGetter = classOutline.implClass.getMethod("get" + fieldOutline.getPropertyInfo().getName(true), new JType[0]);
							if (oldGetter != null) {
								oldGetter.name("get" + fieldOutline.getPropertyInfo().getName(true) + this.linkGetterSuffix);
							}
							if (webDocumentVar == null) {
								webDocumentVar = classOutline.implClass.field(JMod.PROTECTED | JMod.TRANSIENT, outline.getCodeModel().ref(this.webDocumentClass).narrow(classOutline.implClass), this.webDocumentVariableName);
								webDocumentVar.annotate(outline.getCodeModel().ref("net.codesup.restbinder.client.Context"));
							}
							final JMethod newGetter = classOutline.implClass.method(JMod.PUBLIC, classRef, "get" + fieldOutline.getPropertyInfo().getName(true));
							newGetter.body()._return(JExpr._this().ref(webDocumentVar).invoke("resolve").arg(classRef.dotclass()).arg(fieldOutline.getPropertyInfo().getName(false)).invoke("getContent"));
						}
					}
				}
			}
		}
		return false;
	}

	private String getCustomizationValue(final ErrorHandler errorHandler, final ClassOutline classOutline, final String elementName, final String attributeName) throws SAXException {
		final CPluginCustomization annotation = classOutline.target.getCustomizations().find(RestBinderPlugin.CUSTOMIZATION_NS, elementName);
		if (annotation != null) {
			final String attributeValue = annotation.element.getAttribute(attributeName);
			if (attributeValue != null && attributeValue.length() > 0) {
				annotation.markAsAcknowledged();
				return attributeValue;
			} else {
				errorHandler.error(new SAXParseException(MessageFormat.format(getMessage("exception.missingCustomizationAttribute"), attributeName, elementName), annotation.locator));
				return null;
			}
		}
		return null;
	}


	private String getCustomizationAttribute(final CPluginCustomization annotation, final String attributeName, final String defaultValue) {
		if (annotation != null) {
			final String attributeValue = annotation.element.getAttribute(attributeName);
			if (attributeValue != null && attributeValue.length() > 0) {
				return attributeValue;
			} else {
				return defaultValue;
			}
		}
		return null;
	}

	private CPluginCustomization getCustomizationElement(final CCustomizable customizable, final String elementName) {
		return customizable.getCustomizations().find(RestBinderPlugin.CUSTOMIZATION_NS, elementName);
	}


	private static CClassInfo getClassOutline(final Outline outline, final QName elementName) {
		for(final CClassInfo classInfo:outline.getModel().beans().values()) {
			if(elementName.equals(classInfo.getElementName())) {
				return classInfo;
			}
		}
		return null;
	}
}
