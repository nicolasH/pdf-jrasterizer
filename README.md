## PDF JRasterizer

The aim of the project is to allow users to create images out of PDF files. It is based on the java.net pdf-renderer library accessible at [https://pdf-renderer.dev.java.net/](https://pdf-renderer.dev.java.net/).

This project is licensed under the LGPL, as is the pdf-renderer project.

I have finlly managed to cobble a pom.xml that creates a signed jar and a jnlp file (I had to alter the file to make the jnlp work). You can [launch an instance of the app](http://www.niconomicon.net/tests/maven/net/niconomicon/pdf-jrasterizer/pdf-jrasterizer.jnlp "laucnh it by clicking this link") from my website. It is self signed, so there will be a warning about running untrusted code.

<!--Currently you can create the app by running the `mvn package appassembler:assemble` command, if you have java, maven and a bit of luck. The resulting bash/bat script should end up un the target/scripts/bin/ directory.-->
