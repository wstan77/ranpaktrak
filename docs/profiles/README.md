# Profiles and Configuration

## Table of Contents
- [Why Profiles?](#why)
- [Spring Profile](#spring)
- [Maven Profile](#mvn)
- [Spring vs Maven](#vs)
- [Tomcat context.xml](#tomcat)
- [Configuration Sources](#config)
- [Creating Configuration](#create)
- [Using Configuration in Code](#use)

<a name="why"></a>
## Why Profiles?
Deploying an application to multiple environments requires different configurations: database URL's, credentials, authentication, or even enabled features. In `Maven` applications, these types of settings are typically stored in an appropriately-named `Properties` class. These classes are typically populated from a corresponding `.properties` file. It may be very cumbersome to change configurations before making builds for certain environments. Therefore, we use `Profiles` to be able to load respectful configurations. 

<a name="spring"></a>
## Spring Profile
> Spring @Profile allows developers to register beans by condition. For example, register beans based on what operating system (Windows, *nix) your application is running, or load a database properties file based on the application running in development, test, staging or production environment.

- [Example](https://www.mkyong.com/spring/spring-profiles-example/)
- [Spring Docs](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/Profile.html)

<a name="mvn"></a>
## Maven Profile
> A Maven profile is a set of configuration values, which can be used to set or override default values of Maven build. Using a build profile, you can customize build for different environments such as Production vs. Development environments.

They are configured in the applications `pom.xml` files `profiles` element.

```xml
<profiles>
  <profile>
    <id>LOCAL</id>
    <activation>
        <activeByDefault>true</activeByDefault>
    </activation>
    <properties>
      <profile>LOCAL</profile>
      <datasource.schema>DEV</datasource.schema>
      <datasource.username>sa</datasource.username>
    </properties>
  </profile>

  <profile>
    <id>PROD</id>
    <properties>
      <profile>DEV</profile>
      <datasource.schema>APP</datasource.schema>
      <datasource.username>admin</datasource.username>
    </properties>
  </profile>
</profiles>
```

- [Example](https://www.tutorialspoint.com/maven/maven_build_profiles.htm)
- [Maven Docs](https://maven.apache.org/guides/introduction/introduction-to-profiles.html)

<a name="vs"></a>
## Spring vs Maven

### Spring Profile
- Activated at **run-time** and allows for `@Conditional` annotations (such as `@Profile("dev")`) to load corresponding beans:
```Java
@Bean
@Profile("dev")
public DataSource dataSource() throws Exception{
	// configure dev datasource, only when the "dev" profile is active
    return dataSource;
}
@Bean
public DataSource dataSource() throws Exception{
	// configure default datasource, only when the specified profiles are not active
    return dataSource;
}
```

### Maven Profile
- Activated at **build time** and the profile is set via the command line, the following runs Maven with the `dev` profile:
> mvn clean package -Pdev

### Our Framework
In our framework the **Spring Profile** and the **Maven Profile** are kept in sync by the following lines in the `pom.xml`.
```xml
<plugin>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-maven-plugin</artifactId>
  <configuration>
    <profiles>
      <profile>${profile}</profile>
    </profiles>
    ...
  </configuration>
</plugin>
```

<a name="tomcat"></a>
## Tomcat `context.xml`
> The Context element represents a web application, which is run within a particular virtual host. Each web application is based on a Web Application Archive (WAR) file, or a corresponding directory containing the corresponding unpacked contents

A third way to configure an application's settings is to put the settings inside of Tomcat's `context.xml`. This us an appropriate solution if you would like the settings to be independent of the active profile, especially if you don't want to keep sensitive data such as passwords in our Git repository.

```xml
  <Resource name="jdbc/TestDB" auth="Container" type="javax.sql.DataSource"
               maxActive="100" maxIdle="30" maxWait="10000"
               username="javauser" password="javadude" driverClassName="com.mysql.jdbc.Driver"
               url="jdbc:mysql://localhost:3306/javatest"/>
```
[Tomcat Docs](https://tomcat.apache.org/tomcat-8.0-doc/config/context.html)
<a name="config"></a>
## Configuration Sources

Spring pulls configuration from many sources, the most common sources in order of precedence are:
* System Environment Variables
* Tomcat context.xml
* Profile specific properties file (e.g. application-LOCAL.properties)
* General properties file (i.e. application.properties)

In practice this means that application.properties acts as a starting point. Then we use profiles to override some of the configuration at build-time. Then we use the context.xml or System Environment Variables to override configuration in a deployment environment.

[Spring Documentation on External Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)

<a name="create"></a>
## Creating Configuration

The following are examples of the same configuration done in various configuration sources:

### Properties files
```YAML
# Email Configuration
email.server=exch.example.com
email.port=25
email.from-address=no-reply@example.com
```

### context.xml
```XML
<!-- Email Configuration -->
<Environment name="email.server" value="exch.example.com" type="java.lang.String" description="Email SMTP Server Address"/>
<Environment name="email.port" value="25" type="java.lang.Integer" description="Email SMTP Server Port"/>
<Environment name="email.fromAddress" value="no-reply@example.com" type="java.lang.String" description="From Address for System Emails"/>
```

### System Environment Variables
```Bash
setx EMAIL_SERVER "exch.example.com"
setx EMAIL_PORT 25
setx EMAIL_FROMADDRESS "no-reply@example.com"
```

[More Information](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-relaxed-binding)

<a name="use"></a>
## Using Configuration in Code
### @ConfigurationProperties
The preferred way to load configuration is `Spring`'s `@ConfigurationProperties` annotation, which will populate a singleton with configuration values. `@ConfigurationProperties` uses a specified prefix to group together related properties.

The following will consume the configuration examples above:
```Java
@Component
@ConfigurationProperties("email")
public class EmailSettings {
  @NotNull
  private String server;
  @NotNull
  private Integer port;
  @NotNull
  private String fromAddress;
  ...
}
```

The settings can then be `@Autowire`d into any component that needs to access the configuration values.

### @Value
In the case of one-off configuration properties that are only used in one place, it may be acceptable to load the values directly. You can use Spring's `@Value` annotation to load the configuration value:

```Java
@Value("${email.fromAddress}")
private String fromAddress;
```
