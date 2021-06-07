package io.omnipede.metadium.did.resolver.infra.env

import io.omnipede.metadium.did.resolver.application.EnvService
import io.omnipede.metadium.did.resolver.system.config.MetadiumConfigProperty
import org.springframework.stereotype.Service

/**
 * Property yaml file 에서 환경설정을 읽어오는 인터페이스
 */
@Service
class PropertyService(
    private val metadiumConfigProperty: MetadiumConfigProperty
): EnvService{

    /**
     * 환경설정의 network 와 parameter 의 network 가 일치하는지 확인하는 메소드
     * @return Network 일치여부
     */
    override fun isSameNetwork(network: String): Boolean {
        if (metadiumConfigProperty.network == network)
            return true
        return false
    }
}