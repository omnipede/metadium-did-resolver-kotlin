package io.omnipede.metadium.did.resolver.infra.env

import io.omnipede.metadium.did.resolver.domain.ports.EnvService
import io.omnipede.metadium.did.resolver.domain.ports.MetaData
import io.omnipede.metadium.did.resolver.domain.ports.MethodMetaData
import io.omnipede.metadium.did.resolver.domain.ports.ResolverMetaData
import io.omnipede.metadium.did.resolver.domain.entity.AssociatedService
import io.omnipede.metadium.did.resolver.domain.entity.PublicKey
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
): EnvService {

    /**
     * Property 파일의 network 변수를 반환하는 메소드
     * @return Network 변수
     */
    override fun getNetwork(): String {
        return metadiumConfigProperty.network
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
