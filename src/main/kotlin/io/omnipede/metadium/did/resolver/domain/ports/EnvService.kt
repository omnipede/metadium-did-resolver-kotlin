package io.omnipede.metadium.did.resolver.domain.ports

import io.omnipede.metadium.did.resolver.domain.entity.PublicKey
import io.omnipede.metadium.did.resolver.domain.entity.AssociatedService

/**
 * 서버 외부 환경설정을 조회할 때 사용하는 인터페이스
 */
interface EnvService {

    /**
     * 환경설정 값을 이용해서 DID document 에 포함될 service 를 생성하는 메소드
     * @param pubKey DID document 에 포함될 service 의
     * @return 생성된 service object
     */
    fun createService(pubKey: PublicKey): AssociatedService

    /**
     * 환경설정 값을 이용해서 메타데이터 객체를 불러오는 메소드
     * @return Metadata 객체
     */
    fun loadMetaData(): MetaData
}
