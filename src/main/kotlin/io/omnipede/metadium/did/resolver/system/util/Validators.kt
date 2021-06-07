package io.omnipede.metadium.did.resolver.system.util

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

/**
 * Valid http web url constraint
 */
@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Constraint(validatedBy = [WebUrlValidator::class])
annotation class WebUrl(
    val message: String = "Should have valid web url format",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

/**
 * Validator for WebUrl constraint
 */
class WebUrlValidator: ConstraintValidator<WebUrl, String> {

    /**
     * Validation function
     * @return 만약 property 가 valid url format 이면 true 를, 아니면 false 를 반환한다
     */
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return false
        return value.isValidWebUrl()
    }
}

/**
 * Valid metadium network constraint
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.TYPE)
@MustBeDocumented
@Constraint(validatedBy = [MetadiumNetValidator::class])
annotation class MetadiumNet(
    val message: String = "Should be one of 'mainnet' or 'testnet'",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

/**
 * Validator for MetadiumNet constraint
 */
class MetadiumNetValidator: ConstraintValidator<MetadiumNet, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return false
        if (value == "mainnet" || value == "testnet")
            return true
        return false
    }
}


/**
 * Valid metadium address constraint
 */
@Target(allowedTargets = [AnnotationTarget.FIELD, ])
@MustBeDocumented
@Constraint(validatedBy = [MetadiumAddressValidator::class])
annotation class MetadiumAddress(
    val message: String = "Should have valid metadium address format",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)


/**
 * Validator for MetadiumAddress constraint
 */
class MetadiumAddressValidator: ConstraintValidator<MetadiumAddress, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return false
        return value.isValidMetadiumAddress()
    }
}

/**
 * Valid metadium DID constraint
 */
@Target(allowedTargets = [AnnotationTarget.FIELD, ])
@MustBeDocumented
@Constraint(validatedBy = [MetadiumDIDValidator::class])
annotation class MetadiumDID(
    val message: String = "Should have valid metadium DID format",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

/**
 * Validator for MetadiumDID constraint
 */
class MetadiumDIDValidator: ConstraintValidator<MetadiumDID, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return false
        return value.isValidDid()
    }
}