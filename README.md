# metadium-did-resolver-kotlin
Metadium did resolver ported to kotlin

## Test coverage
테스트 완료 후 테스트 커버리지는 ```build/reports/jacoco/test/html/index.html``` 에 존재하는 리포트 확인

## Smart contract wrapper class

본 프로젝트에서는 metadium block chain 에 배포된 smart contract 와 통신하기 위해 java wrapper class 를 생성함.
[web3j]() 를 이용해서 생성했으며 생성 방법은 다음과 같음
```
$ web3j generate solidity -a ./src/main/resources/smartcontract/IdentityRegistry.json -o ./src/main/java  -p io.omnipede.metadium.did.resolver.infra.contract
```