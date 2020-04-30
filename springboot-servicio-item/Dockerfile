FROM openjdk:8-jre-alpine
EXPOSE 8002
VOLUME /tmp
ADD ./target/springboot-servicio-item-0.0.1-SNAPSHOT.jar servicio-items.jar
ENTRYPOINT ["java","-jar","/servicio-items.jar"]