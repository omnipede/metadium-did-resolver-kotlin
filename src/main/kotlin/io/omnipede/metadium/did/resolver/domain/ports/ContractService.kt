package io.omnipede.metadium.did.resolver.domain.ports

import arrow.core.Either
import io.omnipede.metadium.did.resolver.domain.entity.MetadiumDID

/**
 * Blockchain 상의 Contract 와 통신하여 필요한 데이터를 조회할 때 사용하는 인터페이스
 */
interface ContractService {

    fun findPublicKeyList(metaDID: MetadiumDID): Either<NotFoundIdentityException, PublicKeyListResult>
}
