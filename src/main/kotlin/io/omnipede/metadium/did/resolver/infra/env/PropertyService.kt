package io.omnipede.metadium.did.resolver.infra.env

import io.omnipede.metadium.did.resolver.application.EnvService
import io.omnipede.metadium.did.resolver.application.MetaData
import io.omnipede.metadium.did.resolver.application.MethodMetaData
import io.omnipede.metadium.did.resolver.application.ResolverMetaData
import io.omnipede.metadium.did.resolver.domain.AssociatedService
import io.omnipede.metadium.did.resolver.domain.PublicKey
import io.omnipede.metadium.did.resolver.system.config.IdentityHubProperty
import io.omnipede.metadium.did.resolver.system.config.MetadiumConfigProperty
import io.omnipede.metadium.did.resolver.system.config.ResolverProperty
import org.springframework.stereotype.Service

/**
 * Property yaml file 에서 환경설정을 읽어오는 인터페이스
 */
@Service
class PropertyService(
    private val metadiumConfigProperty: MetadiumConfigProperty,
    private val identityHubProperty: IdentityHubProperty,
    private val resolverProperty: ResolverProperty
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

    /**
     * Property 파일의 환경변수를 읽어 did document 에 포함될 service 객체를 생성하는 메소드
     * @param pubKey 생성할 service 객체 내부에 포함될 public key
     * @return DID document 에 포함될 service 객체
     */
    override fun createService(pubKey: PublicKey): AssociatedService {

        return AssociatedService(
            did=identityHubProperty.id,
            publicKey=pubKey,
            url=identityHubProperty.url
        )
    }

    /**
     * Property 파일의 환경변수를 읽어 application 의 metadata 객체를 생성하는 메소드
     * @return Application metadata
     */
    override fun loadMetaData(): MetaData {
        val methodMetaData = MethodMetaData(
            network = metadiumConfigProperty.network,
            registryAddress = metadiumConfigProperty.identityRegistryAddress
        );

        val resolverMetaData = ResolverMetaData(
            driverId=resolverProperty.driverId
        );

        return MetaData(
            methodMetaData, resolverMetaData
        )
    }
}