package io.omnipede.metadium.did.resolver.system.config

import io.omnipede.metadium.did.resolver.system.util.MetadiumAddress
import io.omnipede.metadium.did.resolver.system.util.MetadiumNet
import io.omnipede.metadium.did.resolver.system.util.WebUrl
import io.omnipede.metadium.did.resolver.system.util.isValidWebUrl
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.valueextraction.Unwrapping

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "metadium")
@Validated
class MetadiumConfigProperty {

    // "mainnet" or "testnet"
    @NotEmpty
    @MetadiumNet
    lateinit var network: String

    // Web3 http provider URL
    @NotEmpty
    @WebUrl
    lateinit var httpProvider: String

    // IdentityRegistry contract 주소
    @NotEmpty
    @MetadiumAddress
    lateinit var identityRegistryAddress: String

    // PublicKeyResolver contract 주소
    @NotEmpty
    @MetadiumAddress(payload = [Unwrapping.Unwrap::class])
    lateinit var publicKeyResolverAddressList: List<String>

    // ServiceKeyResolver contract 주소
    @NotEmpty
    @MetadiumAddress(payload = [Unwrapping.Unwrap::class])
    lateinit var serviceKeyResolverAddressList: List<String>
}