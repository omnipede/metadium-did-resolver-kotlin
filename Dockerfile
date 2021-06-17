FROM java:8

WORKDIR app

ADD ./build/libs/*.jar application.jar
ADD ./src/main/resources/application.yaml resources/application.yaml

ENTRYPOINT ["java", "-jar", "application.jar", "--spring.config.location=./resources/application.yaml"]
