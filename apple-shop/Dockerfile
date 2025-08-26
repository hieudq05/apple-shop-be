FROM --platform=linux/amd64 openjdk:24-jdk

WORKDIR /app

COPY target/apple-shop-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]