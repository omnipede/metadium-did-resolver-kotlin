package io.omnipede.metadium.did.resolver.infra.contract

import arrow.core.Either
import arrow.core.getOrHandle
import io.omnipede.metadium.did.resolver.domain.ports.ContractService
import io.omnipede.metadium.did.resolver.domain.entity.MetadiumDID
import io.omnipede.metadium.did.resolver.domain.ports.NotFoundIdentityException
import io.omnipede.metadium.did.resolver.domain.ports.PublicKeyListResult
import org.springframework.stereotype.Service

@Service
internal class MetadiumContractService(
    private val keyManagerService: KeyManagerService
) : ContractService {

    /**
     * Metadium DID 와 맵핑되는 public key list 를 반환하는 메소드
     * @param metaDID 대상 DID
     * @return 대상 DID 와 맵핑되는 public key list
     */
    override fun findPublicKeyList(metaDID: MetadiumDID): Either<NotFoundIdentityException, PublicKeyListResult> {

        val keyManager = keyManagerService.createKeyManager(metaDID)
            .getOrHandle {
                return Either.Left(it)
            }

        val publicKeyList = keyManager.findPublicKeys()
        val serviceKeyList = keyManager.findServiceKeys()

        return Either.Right(
            PublicKeyListResult(publicKeyList, serviceKeyList)
        )
    }
}
