FROM amazoncorretto:11
COPY target/*.jar /app.jar
WORKDIR /server
ENTRYPOINT ["java","-jar","/app.jar"]