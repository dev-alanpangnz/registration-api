# FROM gradle:4.2.1-jdk8-alpine

# #ADD build/libs/*.jar /opt/registration.jar

# ADD  . /opt/registration-api
# WORKDIR /opt/registration-api
# RUN ./gradlew jar

# WORKDIR /opt/registration-api/build/libs
# CMD java -jar registration.jar

# EXPOSE 8080


FROM gradle:jdk10 as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :bootjar

FROM openjdk:10-jre-slim
EXPOSE 8080
COPY --from=builder /home/gradle/src/build/libs/*.jar /opt/registration.jar
WORKDIR /opt
CMD java -jar registration.jar