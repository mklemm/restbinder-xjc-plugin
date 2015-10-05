# restbinder-xjc-plugin

## JAXB XJC plugin for transparent link traversal with the restbinder client

Current Version: 1.0.0

Thisplugin for the JAXB "XSD-to-Java Compiler" (XJC) generates additional code into JAXB generated classes that lets
you access hyperlinked resources as a virtual graph of Java objects.

Hyperlinks are resolved automatically when navigating a relationship (by a call to a "getXXX" accessor), and the
result is then returned as an object that supports the same functionality in turn.

Code generated with this plugin will need the RestBinder client library on the classpath to compile and run.
