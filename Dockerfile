FROM amazoncorretto:21-alpine-jdk

COPY target/test-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT [ "java" ,"-Djava.security.egd=file:/dev/./urandom", "-Doracle.net.tns_admin=/etc/secrets", "-jar", "/app.jar" ]
EXPOSE 8080