#Getting Started#

The *webapp* module is a framework for building web applications on top of java servlets. Its packaged as a web-fragment jar that includes most of the basic configuration. In fact it should be possible to construct a basic application that handles registration/login etc. just by including the webapp jar file and adding a property file to specify the final configuration.

For the application to do anything useful you will have to add additional code and content. To understand how to do this you should look at the javadoc. Probably the best place to start is the uk.ac.ed.epcc.webapp.AppContext class. This is the core of the framework and a large fraction of the classes contain a reference to an AppContext. The AppContext acts as a container for *services* these are policy objects representing different services needed by the application so:
* A ConfigService handles application configuration properties
* A LoggerService handles logging.
* A DatabaseService handles database connections
* A SessionService handles application users and sessions.
* A ResourceService handles the loading of resource files.

Despite the *webapp* name most of the code base is independent of html or servlets. The dependencies that do exist are hidden inside specific *service* implementations.

##Creating a project##

First step is to create a new Java web-application project. For this guide we will assume the project will be built using
maven e.g.

```
mvn archetype:generate -DgroupId=uk.ac.ed.epcc.example -DartifactId=example -DarchetypeArtifactId=maven-archetype-webapp -DinteractiveMode=false 
```

The standard maven archetype creates a "hello world" index.jsp. The webapp jar will provide its own default page so we should remove this for now.

```
rm src/main/webapp/index.jsp
```

Add a dependency on the webapp-jar to the projects **pom.xml**

```
<dependencies>
  <dependency>
    <groupId>epcc.ed.ac.uk</groupId>
    <artifactId>webapp</artifactId>
    <version>1.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```

The application is configured using java properties. The default configuration service 
starts from the java system properties then loads a series of property files. By default you place these in *src/main/resources*. However the names and locations of these can be changed by setting additional properties in the system properties. Locations can be in:
1. The war-file WEB-INF directory
2. The classpath
3. The file-system

Searched in that order. Though normally the default locations are fine.

The standard property files loaded are:
+ **default-config.properties** Default values for the application. Set **default.path** to relocate.
+ **service-config.properties** This is the only required file. It is intended to set the config for the specific instance of the application. Set **config.path** to relocate.
+ **deploy-config.properties** This is intended to contain overrides for a specific flavour of an instance. For example to create a debug version or a differently branded view. Set **deploy.path** to relocate.
+ **build-config.properties** This is loaded last and is intended to insert build versions and timestamps into the properties so they can be added to error messages.

The only file we require is **service-config.properties** So we need to add this. First of all we need to import some standard properties. The default
config-service will include additional property files if you reference them with a property that starts with **add_properties.**

```
add_properties.webapp=/uk/ac/ed/epcc/webapp/webapp.properties
add_properties.registry=/uk/ac/ed/epcc/webapp/model/data/form-registry.properties
```

Next we should give a name for service we are writing. Properties that start with the prefix **service.** are automatically expanded in content and email templates. The most commonly used parameter is **service.name**.

```
service.name=Demo
```

Next we need to specify database connection details. For now we will just hardcode these as properties:

```
db_name=jdbc:mariadb://localhost/demo?characterEncoding=utf8
db_username=demo
db_password=demo
db_driver=org.mariadb.jdbc.Driver
```

In production it might be better to pass these in from the servlet container as a JNDI datasource. You have to create the database and set up the access credentials in advance but the code will be able to auto-create its database tables if you tell it to (and the credential has the necessary access permissions to create tables).

```
service.feature.auto_create.tables=on
```

This is an example of a feature switch that enables or disables optional behaviour. 


