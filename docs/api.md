# API spec

## Protocol
```http/https```

## GET did document

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
