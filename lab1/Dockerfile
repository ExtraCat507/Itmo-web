FROM gradle:8.10-jdk17 AS build
COPY --chown=gradle:gradle ../.. /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle jar --no-daemon

FROM eclipse-temurin:17-jre
EXPOSE 9000
RUN mkdir /app
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/fcgi-bin/app.jar
ENTRYPOINT ["java", "-DFCGI_PORT=9000", "-jar", "./fcgi-bin/app.jar"]

