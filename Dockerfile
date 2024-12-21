FROM openjdk:21-jdk-slim
COPY target/*.jar poc.jar
ENV JAVA_OPTS="-XX:+AllowRedefinitionToAddDeleteMethods -XX:+EnableDynamicAgentLoading"
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS  -jar poc.jar