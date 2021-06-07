package io.omnipede.metadium.did.resolver.system.util

import org.apache.commons.validator.routines.UrlValidator


/**
 * Checks whether parameter string has valid URL format
 * @return true when parameter has valid format, else return false
 */
internal fun String.isValidDid(): Boolean {
    val regex = Regex("^did:meta:((testnet|mainnet):)?[0-9a-f]{64}$")
    if (this.matches(regex))
        return true
    return false
}

/**
 * Checks whether parameter string has valid metadium address format
 * @return true when parameter has valid format, else return false
 */
internal fun String.isValidMetadiumAddress(): Boolean {
    val regex = Regex("^(0x|0X)?[0-9a-fA-F]{40}$")
    if (this.matches(regex))
        return true
    return false
}

/**
 * Checks whether parameter string has valid URL format
 * @return true when parameter has valid format, else return false
 */
internal fun String.isValidWebUrl(): Boolean {
    val schemes = arrayOf("http", "https")
    val urlValidator = UrlValidator(schemes)
    return urlValidator.isValid(this)
}