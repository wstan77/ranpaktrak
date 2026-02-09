#!/bin/bash
mvn spring-boot:run -PLOCAL -Dspring-boot.run.jvmArguments="-Xmx512m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=*:5005\
 --add-opens java.base/java.lang=ALL-UNNAMED"
