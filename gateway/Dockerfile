FROM amazoncorretto:11
COPY /target/*.jar app.jar
WORKDIR /gateway
ENTRYPOINT ["java","-jar","/app.jar"]