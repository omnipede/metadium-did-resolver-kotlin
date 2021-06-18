# API spec

## Protocol
```http/https```

## 공통 에러 형식

상세 에러 정보는 response ```body``` 에 ```JSON``` 형식으로 표현된다

```
{
    "success": false,
    "message": "Error reason"
}
```

## GET DID document

DID document 를 조회하는 API. 한번 조회된 document 는 서버 내부에 캐시된다.

### Request

* Method
  
```GET```

* URI

```/1.0/identifiers/{did}```

* Path parameter

| key | value |
| --- | --- |
| did | Metadium DID |

* Headers

| key | value | default |
| --- | --- | --- |
| ```no-cache``` | 캐시 비활성화 여부. ```true``` 일 경우 캐시된 document 가 아닌 원본 document 를 반환한다 | false |
 

### Response (OK)

* body
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

### Response (Error)
| Status | Description |
| --- | --- |
| 400 | 요청 형식 오류 |
| 404 | DID 가 존재하지 않거나 삭제됨 |
| 500 | 내부 서버 에러 |


## DELETE cached document

서버 내부에 캐시된 DID document 를 삭제하는 API

### Request

* Method

```DELETE```

* URI

```/1.0/identifiers/{did}```

* Path parameter

| key | value |
| --- | --- |
| did | Metadium DID |

### Response (OK)

```
{
    "success": true,
    "message": "Cache purging of 'did:meta:000000000000000000000000000000000000000000000000000000000000112a' has been completed"
}
```

### Response (Error)
| Status | Description |
| --- | --- |
| 400 | 요청 형식 오류 |
| 404 | DID 가 존재하지 않음 |
| 500 | 내부 서버 에러 |
