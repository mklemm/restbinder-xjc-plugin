###  Usage

####  General

jaxb2-rich-contract-plugin is a plugin to the XJC "XML to Java compiler" shipped with the reference implementation of JAXB, included in all JDKs since 1.6. It is targeted on version 2.2 of the JAXB API. In order to make it work, you need to:

* Add the jar file to the classpath of XJC
* Add the JAXB 2.2 XJC API to the classpath of XJC, if your environment is running by default under JAXB 2.1 or lower.
* Add the corresponding activating command-line option to XJC's invocation, see below for details of each of the plugins
* Each of the plugins, except "-Ximmutable", has one or more sub-options to fine-control its behavior. These sub-option must be given after the corresponding main "-X..." activation option, to avoid naming conflicts. Names of sub-options can be given dash-separated or in camelCase.
* The "immutable" and "constrained-properties" plugins are mutually exclusive. An object cannot be both immutable and send change notifications.

####  From Maven

You should add "maven-jaxb2-plugin" to your `<build>` configuration. Then add "jaxb2-rich-contract-plugin" as an XJC plugin ("plugin for plugin") to the maven plugin declaration. The following example shows all possible options reflecting their default values:

``` xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.jvnet.jaxb2.maven2</groupId>
                <artifactId>maven-jaxb2-plugin</artifactId>
                <version>0.11.0</version>
                <executions>
                    <execution>
                        <id>xsd-generate</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <schemaIncludes>
                        <schemaInclude>**/*.xsd</schemaInclude>
                    </schemaIncludes>
                    <strict>true</strict>
                    <verbose>true</verbose>
                    <extension>true</extension>
                    <removeOldOutput>true</removeOldOutput>
                    <args>
                        <arg>-Xrestbinder</arg>
                            <arg>-webDocumentVariableName=__webDocument__</arg>
                            <arg>-linkGetterSuffix=Link</arg>
                            <arg>-webDocumentClass=net.codesup.restbinder.client.WebDocument</arg>
                    </args>
                    <plugins>
                        <plugin>
                            <groupId>net.codesup.util</groupId>
                            <artifactId>jaxb2-rich-contract-plugin</artifactId>
                            <version>1.6.7</version>
                        </plugin>
                    </plugins>
                    <dependencies>
                        <!-- Put this in if your default JAXB version is 2.1 or lower,
                        or if &quot;tools.jar&quot; isn't in your classpath -->
                        <dependency>
                            <groupId>org.glassfish.jaxb</groupId>
                            <artifactId>jaxb-runtime</artifactId>
                            <version>2.2.11</version>
                        </dependency>
                        <dependency>
                            <groupId>org.glassfish.jaxb</groupId>
                            <artifactId>jaxb-core</artifactId>
                            <version>2.2.11</version>
                        </dependency>
                        <dependency>
                            <groupId>org.glassfish.jaxb</groupId>
                            <artifactId>jaxb-xjc</artifactId>
                            <version>2.2.11</version>
                        </dependency>
                    </dependencies>
                </configuration>
            </plugin>
        </plugins>
    </build>
```
Note: the `<extension/>` flag must be set to "true" in order to make XJC accept any extensions at all.

Note: jaxb2-rich-contract-plugin implements JAXB and XJC APIs version 2.2. You most likely will have to add the dependencies to these libraries to your classpath effective at XJC runtime. See the `<dependencies>` element above on how to do this.


