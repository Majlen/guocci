guocci
==============

OCCI Web GUI using Vaadin framework.

Requirements
------------

* Java 8
* Maven
  * Vaadin 8.1
  * jOCCI libraries (core and API)
  * SLF4J and Log4J 2 for logging

Installation
------------

To produce a deployable production mode WAR:
- change productionMode to true in the servlet class configuration (nested in the UI class)
- run "mvn clean package"

For demo purposes it is possible to run the application by invoking "mvn jetty:run". Application will run using Jetty on http://localhost:8080/ .

