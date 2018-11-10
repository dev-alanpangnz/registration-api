FROM openjdk:alpine

ADD build/libs/*.jar /opt/registration.jar

WORKDIR /opt/
CMD java -jar registration.jar

EXPOSE 8080