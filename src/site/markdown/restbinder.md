## restbinder
### Motivation
Sometimes, you need information about the properties of a class, or you wish to have a constant for the names of properties.
The "meta" plugin creates an inner class (the name of which can be controlled by a command-line option), and adds a constant
field for each property. If the `-extended=y` command-line option is specified, these constants will hold instances of the
`PropertyInfo` class, on which the name, type, multiplicity (collection or not) and default value (from XSD) are exposed.
Without `-extended`, the constants are simply string constants holding the property names.


### Function
The RestBinderPlugin extends code generated by the JAXB XSD-to-Java compiler (XJC), such that
there will be new `get` accessor methods for each XLink-like reference element in all generated
classes.

The link element defintitions must be annotated with the `target` customization element in the following way:

Add this to the `schema` element of your XSD:
```
xmlns:rb="http://www.codesup.net/restbinder"
xmlns:jxb="http://java.sun.com/xml/ns/jaxb"
jxb:version="1.0"
jxb:extension-element-prefixes="rb"
```


Then, at a reference element that implements XLink:
```
	<element name="my-ref" type="xlink:simple">
		<annotation>
			<appinfo>
				<rb:target ref="tns:other-element/>
			</appinfo>
		</annotation>
	</element>
```


### Usage
#### -Xrestbinder

#### Options

##### -webDocumentVariableName=`<string>` (__webDocument__)
Name of the generated variable that holds the WebDocument instance used to resolve the links internally.


##### -linkGetterSuffix=`<string>` (Link)
Suffix to append to the original getter method that returns the link object itself, not the referenced object.


##### -webDocumentClass=`<string>` (net.codesup.restbinder.client.WebDocument)
Class to declare the web document variable as. Can be overridden here to add a custom class with additional functionality.

