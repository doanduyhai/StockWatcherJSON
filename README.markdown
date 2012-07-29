GWT JSON integration with Spring MVC Demo
================

Presentation
------------------

 This is a demo project for GWT JSON integration with Spring MVC based on the original [StockWatcher] (http://code.google.com/p/google-web-toolkit/downloads/detail?name=Tutorial-GettingStarted-2.1.zip) tutorial from Google.
 
 This demo is a support for the following article [GWT JSON integration with Spring MVC] (http://doanduyhai.wordpress.com/2012/07/29/gwt-json-integration-with-spring-mvc)
 
 
 Installation
------------

- Install [Maven 3](http://maven.apache.org/)
- Compile the project with "mvn clean install". Maven will download the gwt-dev.jar which is quite big (32Mb) so take a coffee
- Once compilation done, run Jetty from Maven : mvn jetty:run

 Usage
------------

- Connect to the application at http://127.0.0.1:8080/

 Try to play around with the "Add" and "Randomize" buttons. Each of them is triggering a different JSON service in the backend, handled by Spring