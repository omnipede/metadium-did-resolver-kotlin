# metadium-did-resolver-kotlin

```Kotlin``` 으로 구현한 metadium 블록체인 DID resolver. DID 와 연관된 public key 와 서비스 엔드포인트 정보를 포함하는 문서를 DID document 라고 한다.
본 서버는 DID 와 맵핑되는 DID document 를 블록체인상에서 찾아 클라이언트에게 반환하는 역할을 수행한다.

## API spec

* ```Protocol```: ```http/https``` 

### GET did document

* Request
```
$ curl --location --request GET 'http://localhost:3030/1.0/identifiers/did:meta:000000000000000000000000000000000000000000000000000000000000112b'
```

* Response (OK)

```
{
    "didDocument": {
        "@context": "https://w3id.org/did/v0.11",
        "id": "did:meta:000000000000000000000000000000000000000000000000000000000000112b",
        "publicKey": [
            {
                "type": "EcdsaSecp256k1VerificationKey2019",
                "controller": "did:meta:000000000000000000000000000000000000000000000000000000000000112b",
                "id": "did:meta:000000000000000000000000000000000000000000000000000000000000112b#MetaManagementKey#0c65a336fc97d4cf830baeb739153f312cbefcc9",
                "publicKeyHex": "0449f78d9ef20ede7f29702b6c30236482e35528adb1be25e0cea5c55a6337b0adc3e9d12c75bb46e6b7a589c7cd538a9d47a1cadca37286d249be01b83a95db83"
            },
            {
                "type": "EcdsaSecp256k1VerificationKey2019",
                "controller": "did:meta:000000000000000000000000000000000000000000000000000000000000112b",
                "publicKeyHash": "69245e218e182e67564bd4387070f6588cf77d33",
                "id": "did:meta:000000000000000000000000000000000000000000000000000000000000112b#f7c5b186-41b9-11ea-ab1a-0a0f3ad235f2#69245e218e182e67564bd4387070f6588cf77d33"
            }
        ],
        "authentication": [
            "did:meta:000000000000000000000000000000000000000000000000000000000000112b#MetaManagementKey#0c65a336fc97d4cf830baeb739153f312cbefcc9",
            "did:meta:000000000000000000000000000000000000000000000000000000000000112b#f7c5b186-41b9-11ea-ab1a-0a0f3ad235f2#69245e218e182e67564bd4387070f6588cf77d33"
        ],
        "service": [
            {
                "publicKey": "did:meta:000000000000000000000000000000000000000000000000000000000000112b#MetaManagementKey#0c65a336fc97d4cf830baeb739153f312cbefcc9",
                "id": "did:meta:0000000000000000000000000000000000000000000000000000000000000527",
                "type": "identityHub",
                "serviceEndpoint": "https://datahub.metadium.com"
            }
        ]
    },
    "resolverMetadata": {
        "driverId": "did-meta",
        "driver": "HttpDriver",
        "retrieved": "2021-06-17T03:06:15.665+00:00",
        "duration": "387 ms",
        "cached": false
    },
    "methodMetadata": {
        "network": "mainnet",
        "registryAddress": "0x42bbff659772231bb63c7c175a1021e080a4cf9d"
    }
}
```

* Response (404)  

DID 에 대한 did document 가 존재하지 않을 때 발생

```
{
    "status": 404,
    "message": "Not found"
}
```

## Development requirements
* \>= JAVA 8

## Build, test, run
* Build

```
$ ./gradlew build
```

* Test

```
$ ./gradlew test
```
테스트 커버리지 결과는 ```build/reports/jacoco/test/html/index.html``` 에 존재

* Run (without docker)

```
$ java -jar ./build/libs/*.jar
```

## Configuration
```
spring:
  application:
    name: "metadium-did-resolver"
  resources:
    add-mappings: false
  mvc:
    throw-exception-if-no-handler-found: true
  jackson:
    default-property-inclusion: non_null

server:
  port: 3030

# Logging config
logging:
#  file:
#    path: "./log"
#    max-size: 10MB
#    max-history: 30
  level:
    # Change below to change application logging level
    io.omnipede.metadium.did.resolver: INFO

resolver:
  driverId: "did-meta"

# IdentityHub configuration
identityHub:
  id: "did:meta:0000000000000000000000000000000000000000000000000000000000000527"
  url: "https://datahub.metadium.com"

# Metadium blockchain configuration
metadium:
  network: 'mainnet'
  http-provider: 'https://api.metadium.com/prod'
  identityRegistry-address: "0x42bbff659772231bb63c7c175a1021e080a4cf9d"
  publicKeyResolver-address-list:
    - "0xd9f39ab902f835400cfb424529bb0423d7342331"
  serviceKeyResolver-address-list:
    - "0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3"

```
* ```spring.*``` Normal spring boot configuration
* ```logging.file``` 파일에 로깅하고 싶다면 사용
* ```logging.level``` 로그 레벨 수정시 이 부분을 수정한다
* ```identityHub.*``` Identity hub service 설정
* ```metadium```
    * ```.network``` 'mainnet' or 'testnet'
    * ```http-provider``` metadium 블록체인의 http provider 주소
    * ```identityRegistry-address``` IdentityRegistry 스마트 컨트랙트의 주소
    * ```publicKeyResolver-address-list``` PublicKeyResolver 스마트 컨트랙트들의 주소
    * ```serviceKeyResolver-address-list``` ServiceKeyResolver 스마트 컨트랙트들의 주소
