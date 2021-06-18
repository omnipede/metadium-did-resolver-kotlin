# Configuration

## 설정 예시

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
  level:
    # Change below to change application logging level
    io.omnipede.metadium.did.resolver: INFO

cache:
  # 단위: 초
  duration: 600
  # 캐시될 entry 개수
  maximumSize: 10000

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

## 설정 값에 대한 설명

* ```spring.*``` Normal spring boot configuration
* ```logging.file``` 파일에 로깅하고 싶다면 사용
* ```logging.level``` 로그 레벨 수정시 이 부분을 수정한다
* ```cache```
    * ```.duration``` 캐시 지속 시간 (초 단위)
    * ```.maximumSize``` 캐시할 entry 최대 개수
* ```identityHub.*``` Identity hub service 설정
* ```metadium```
    * ```.network``` 'mainnet' or 'testnet' 만 가능하다
    * ```http-provider``` metadium 블록체인의 http provider 주소
    * ```identityRegistry-address``` IdentityRegistry 스마트 컨트랙트의 주소
    * ```publicKeyResolver-address-list``` PublicKeyResolver 스마트 컨트랙트들의 주소
    * ```serviceKeyResolver-address-list``` ServiceKeyResolver 스마트 컨트랙트들의 주소
