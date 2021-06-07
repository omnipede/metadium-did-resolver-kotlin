package io.omnipede.metadium.did.resolver.application

import io.omnipede.metadium.did.resolver.domain.PublicKey
import io.omnipede.metadium.did.resolver.domain.AssociatedService

/**
 * 서버 외부 환경설정을 조회할 때 사용하는 인터페이스
 */
interface EnvService {

    /**
     * 환경설정의 network 와 parameter 의 network 가 일치하는지 확인하는 메소드
     * @param network 대상 network
     * @return Network 일치여부
     */
    fun isSameNetwork(network: String): Boolean

    /**
     * 환경설정 값을 이용해서 DID document 에 포함될 service 를 생성하는 메소드
     * @param pubKey DID document 에 포함될 service 의
     * @return 생성된 service object
     */
    fun createService(pubKey: PublicKey): AssociatedService
}