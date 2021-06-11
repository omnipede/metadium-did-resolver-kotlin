package io.omnipede.metadium.did.resolver.system.exception

class SystemException: RuntimeException  {

    var errorCode: ErrorCode
        private set

    constructor(errorCode: ErrorCode): super(errorCode.defaultMessage) {
        this.errorCode = errorCode
    }

    constructor(errorCode: ErrorCode, message: String): super(message) {
        this.errorCode = errorCode
    }
}
