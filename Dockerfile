FROM openjdk:17-jdk
VOLUME /tmp
ADD ./build/libs/*SNAPSHOT.jar nicein.jar
ENTRYPOINT ["java", "-Dspring.profiles.active={dev}", "-jar", "/nicein.jar"]
