# metadium-did-resolver-kotlin

```Kotlin``` 으로 구현한 metadium 블록체인 DID resolver. DID 와 연관된 public key 와 서비스 엔드포인트 정보를 포함하는 문서를 DID document 라고 한다.
본 서버는 DID 와 맵핑되는 DID document 를 블록체인상에서 찾아 클라이언트에게 반환하는 HTTP REST API 서버 로서의 역할을 수행한다.

![image](https://user-images.githubusercontent.com/41066039/122346853-6316bf80-cf84-11eb-806f-a37907a2e98e.png)


## 어플리케이션 실행 방법

본 어플리케이션은 [Docker](https://www.docker.com/) 로 실행할 수 있다. 어플리케이션의 도커 이미지에 대한 정보는 [Docker hub](https://hub.docker.com/r/omnipede/metadium-did-resolver) 참조.

[기본 설정](./src/main/resources/application.yaml) 값으로 어플리케이션을 실행하고자 한다면 아래 명령어로 실행. ```name``` 은 원하는 값으로 수정한다

```
$ docker run -d -p 3030:3030 \
  --name=did-resolver \
omnipede/metadium-did-resolver:latest
```

세부 설정을 수정하고 싶다면 먼저 설정 파일을 생성해야 한다. 설정파일의 이름은 ```application.yaml``` 고.  [설정](./docs/config.md) 문서를 바탕으로 적절한 위치에 설정 파일을 생성하고 해당 설정 파일의
절대 경로를 컨테이너의 설정 파일 경로 (```/app/resources```) 로 마운트 시킨다.

```
$ docker run -d -p 3030:3030 \
  --name=did-resolver \
  -v /path/to/config:/app/resources \
omnipede/metadium-did-resolver:latest
```

## API spec
[API 문서](./docs/api.md) 참조

## 개발 가이드
[개발 가이드](./docs/dev.md) 참조
