FROM openjdk:21-jdk-slim
COPY target/*.jar poc.jar
ENV JAVA_OPTS="-XX:+AllowRedefinitionToAddDeleteMethods -XX:+EnableDynamicAgentLoading"
EXPOSE 9999
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar poc.jar"]