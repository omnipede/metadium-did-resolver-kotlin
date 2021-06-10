package io.omnipede.metadium.did.resolver.system.util

import org.apache.commons.validator.routines.UrlValidator
import java.util.stream.Collectors


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
 * Check whether parameter string has valid metadium public key hex format
 * @return True when parameter has valid format, else return false
 */
internal fun String.isValidMetadiumPublicKeyHex(): Boolean {
    val regex = Regex("^(0x|0X)?[0-9a-fA-F]{128}$")
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

/**
 * 16 진수 문자열의 접두사를 제거하고 소문자로 수정하는 메소드
 * @return 정규화된 16진수 문자열
 */
internal fun String.toNormalizedHex(): String {
    return this.removePrefix("0x")
        .removePrefix("0X")
        .toList().stream().parallel().map {
            it.toLowerCase()
        }.collect(Collectors.toList()).joinToString("")
}
