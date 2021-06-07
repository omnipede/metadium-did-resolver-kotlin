package io.omnipede.metadium.did.resolver.application

/**
 * 서버 외부 환경설정을 조회할 때 사용하는 인터페이스
 */
interface EnvService {

    /**
     * 환경설정의 network 와 parameter 의 network 가 일치하는지 확인하는 메소드
     * @return Network 일치여부
     */
    fun isSameNetwork(network: String): Boolean
}