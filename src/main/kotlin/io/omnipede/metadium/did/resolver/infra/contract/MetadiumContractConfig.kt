package io.omnipede.metadium.did.resolver.infra.contract

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider
import java.util.stream.Collectors
import javax.validation.constraints.NotEmpty

/**
 * Metadium 관련 bean configuration
 */
@Configuration
@ConstructorBinding
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "metadium")
@Validated
class MetadiumContractConfig {

    val logger: Logger = LoggerFactory.getLogger(MetadiumContractConfig::class.java)

    // Web3 http provider URL
    @NotEmpty
    lateinit var httpProvider: String

    // IdentityRegistry contract 주소
    @NotEmpty
    lateinit var identityRegistryAddress: String

    // PublicKeyResolver contract 주소
    @NotEmpty
    lateinit var publicKeyResolverAddressList: List<String>

    // ServiceKeyResolver contract 주소
    @NotEmpty
    lateinit var serviceKeyResolverAddressList: List<String>

    /**
     * DID 발급 정보를 조회할 smart contract wrapper bean
     * @return Identity registry contract wrapper bean
     */
    @Bean
    fun identityRegistry(web3j: Web3j, credentials: Credentials): IdentityRegistry {
        logger.info("Loading IdentityRegistry contract with address {}", identityRegistryAddress)
        return IdentityRegistry.load(identityRegistryAddress, web3j, credentials, DefaultGasProvider())
    }

    /**
     * PublicKeyResolver contract wrapper object list bean
     * @param web3j Web3 client
     * @param credentials Contract 호출 시 사용할 credential
     * @return PublicKeyResolver contract wrapper object list
     */
    @Bean
    fun publicKeyResolvers(web3j: Web3j, credentials: Credentials): List<PublicKeyResolver> {

        return publicKeyResolverAddressList.stream().map {
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

        return serviceKeyResolverAddressList.stream().map {
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

        return Web3j.build(HttpService(httpProvider))
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