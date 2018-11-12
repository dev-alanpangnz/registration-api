FROM gradle:4.2.1-jdk8-alpine

ADD build/libs/*.jar /opt/registration.jar

WORKDIR /opt/
CMD java -jar registration.jar

EXPOSE 8080