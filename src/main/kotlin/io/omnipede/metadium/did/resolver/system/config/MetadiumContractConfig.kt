package io.omnipede.metadium.did.resolver.system.config

import io.omnipede.metadium.did.resolver.infra.contract.IdentityRegistry
import io.omnipede.metadium.did.resolver.infra.contract.PublicKeyResolver
import io.omnipede.metadium.did.resolver.infra.contract.ServiceKeyResolver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider
import java.util.stream.Collectors

/**
 * Metadium 관련 bean configuration
 */
@Configuration
class MetadiumContractConfig(
    // Spring application properties 파일에서 환경설정 값을 읽어온다
    private val metadiumConfigProperty: MetadiumConfigProperty
) {
    val logger: Logger = LoggerFactory.getLogger(MetadiumContractConfig::class.java)

    /**
     * DID 발급 정보를 조회할 smart contract wrapper bean
     * @return Identity registry contract wrapper bean
     */
    @Bean
    fun identityRegistry(web3j: Web3j, credentials: Credentials): IdentityRegistry {
        logger.info("Loading IdentityRegistry contract with address {}", metadiumConfigProperty.identityRegistryAddress)
        return IdentityRegistry.load(metadiumConfigProperty.identityRegistryAddress, web3j, credentials, DefaultGasProvider())
    }

    /**
     * PublicKeyResolver contract wrapper object list bean
     * @param web3j Web3 client
     * @param credentials Contract 호출 시 사용할 credential
     * @return PublicKeyResolver contract wrapper object list
     */
    @Bean
    fun publicKeyResolvers(web3j: Web3j, credentials: Credentials): List<PublicKeyResolver> {
        // Contract address 를 읽어서 contract wrapper class 를 초기화시킨다
        return metadiumConfigProperty.publicKeyResolverAddressList.stream().map {
            logger.info("Loading PublicKeyResolver contract with address {}", it)
            PublicKeyResolver.load(it, web3j, credentials, DefaultGasProvider())
        }.collect(Collectors.toList())
    }

    /**
     * ServiceKeyResolver contract wrapper object list bean
     * @param web3j Web3 client
     * @param credentials Contract 호출 시 사용할 credential
     * @return ServiceKeyResolver contract wrapper object list
     */
    @Bean
    fun serviceKeyResolvers(web3j: Web3j, credentials: Credentials): List<ServiceKeyResolver> {
        // Contract address 를 읽어서 contract wrapper class 를 초기화시킨다
        return metadiumConfigProperty.serviceKeyResolverAddressList.stream().map {
            logger.info("Loading ServiceKeyResolver contract with address {}", it)
            ServiceKeyResolver.load(it, web3j, credentials, DefaultGasProvider())
        }.collect(Collectors.toList())
    }

    /**
     * BlockChain 통신 시 사용하는 web3 client bean
     * @return Web3 client bean
     */
    @Bean
    fun web3j(): Web3j {

        return Web3j.build(HttpService(metadiumConfigProperty.httpProvider))
    }

    /**
     * Contract 호출 시 사용할 dummy credential
     * @return Dummy credential bean
     */
    @Bean
    fun credentials(): Credentials {

        // Fee 를 소모하는 contract method 를 호출하지 않기 때문에, dummy credential 을 생성한다.
        return Credentials.create(Keys.createEcKeyPair())
    }
}