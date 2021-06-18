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

## Architecture
![image](https://user-images.githubusercontent.com/41066039/122537227-523b7c00-d060-11eb-9b42-2904893cc07f.png)

본 프로그램은 ```clean architecture``` 구조를 따르고 있고 크게 4개의 패키지로 구분된다.

* ```controller```: HTTP controller 와 controller 의 request, response DTO 를 정의한 패키지
* ```domain```:  어플리케이션의 업무 로직을 정의한 패키지
  * ```.entity```: POJO entity 를 정의한 패키지
  * ```.application```: Application use case 를 정의한 패키지
  * ```.ports```: 외부 infrastructure 와 통신할 때 사용하는 인터페이스를 정의한 패키지
* ```infra```: 외부 infrastructure 의 구현체를 정의한 패키지
* ```system```: 유틸리티, 설정 및 ```spring boot``` 어플리케이션 구성에 필요한 필터, 헨들러 등을 정의한 패키지
