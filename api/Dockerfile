FROM maven:3-amazoncorretto-17

# Copy Build
RUN mkdir -p /build
COPY target/*.jar /build/app.jar

ENV JAVA_TOOL_OPTIONS="--add-opens=java.base/java.lang=ALL-UNNAMED"

# LOGS
RUN mkdir /logs

RUN yum install shadow-utils.x86_64 -y
RUN groupadd --system appgroup && useradd --system appuser -g appgroup
RUN chown -R appuser /logs
RUN chown -R appuser /run
USER appuser

EXPOSE 8080
ENTRYPOINT java -jar build/app.jar
