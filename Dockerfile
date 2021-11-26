FROM openjdk:8-jdk-alpine
COPY /target/Sell-it-api.jar Sell-it-api.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/Sell-it-api.jar"]