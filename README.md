CP2K Input Transformer
======================

Introduction
------------

The CP2K Input Transformer is a micro-service for taking CP2K input files in
text format and transforming them into an XML representation.

The XML representation is defined by a schema file generated from the CP2K
XML manual by an XSLT transformation, primarily for use with the Libhpc
TemPSS service.

Build
-----

The project is a Maven project. Run

    mvn test

to run the JUnit tests and

    mvn build

to generate a war file.

Deploy
------

Deploy the war file built above using your favourite application server.
The service has been tested using Apache Tomcat 7 and 8.

Usage
-----

Assuming the application server is running on localhost, port 8080, then:

* To verify the service is running send an HTTP GET request to <http://localhost:8080/CP2KInputTransformer/api/verify>. The following text should be seen: `CP2KInputEditorRESTService Successfully started...`

* To transform a text file, send an HTTP multipart/form-data POST to `http://localhost:8080/CP2KInputTransformer/templateId/transform`. `templateId` should be the name of a schema file that the transform will respect, eg cp2k-2.6, making the URL: <http://localhost:8080/CP2KInputTransformer/cp2k-2.6/transform>. The CP2K input file should be attached with the key `inputFile`. The response will be the transformed XML, or a suitable error message.

Client code can is usually available to ease this process, eg if using JavaScript the `FormData` interface may be used as described at <https://developer.mozilla.org/en/docs/Web/API/FormData>

eg:

    var cp2kInputFile;
    // read or populate input file here
    var fd = new FormData();
    fd.append('inputFile', cp2kInputFile);
