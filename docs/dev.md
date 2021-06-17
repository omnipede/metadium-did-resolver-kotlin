# Development guide

## Requirements
* JAVA 8 or higher

## Build, test, run
* Build

```
$ ./gradlew clean build
```

* Test

```
$ ./gradlew test
```
테스트 커버리지 결과는 ```build/reports/jacoco/test/html/index.html``` 에 존재

* Run

```
$ java -jar ./build/libs/*.jar
```
