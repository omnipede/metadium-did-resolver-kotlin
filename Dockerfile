FROM java:8

ADD ./build/libs/*.jar application.jar

EXPOSE 3030

ENTRYPOINT ["java", "-jar", "application.jar"]
