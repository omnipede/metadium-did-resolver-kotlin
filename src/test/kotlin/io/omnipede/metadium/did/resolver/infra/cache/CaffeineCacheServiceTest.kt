package io.omnipede.metadium.did.resolver.infra.cache

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.omnipede.metadium.did.resolver.domain.entity.DidDocument
import io.omnipede.metadium.did.resolver.domain.entity.MetadiumDID
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class CaffeineCacheServiceTest {

    private var caffeineCacheService: CaffeineCacheService? = null

    @BeforeEach
    fun setup() {
        val cache: Cache<String, DidDocument> = Caffeine.newBuilder()
            .maximumSize(10_000)
            .build()
        caffeineCacheService = CaffeineCacheService(cache)
    }

    @Test
    @DisplayName("캐시 저장 및 조회 테스트")
    fun should_save_and_find() {

        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val document = DidDocument(did)

        // When
        caffeineCacheService!!.save(document)
        val cached = caffeineCacheService!!.find(MetadiumDID(did))

        // Then
        assertThat(cached.isPresent).isTrue
        assertThat(cached.get()).usingRecursiveComparison().isEqualTo(document)
    }

    @Test
    @DisplayName("캐시 miss 테스트")
    fun should_not_return_when_not_cached() {

        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val anotherDid = "did:meta:000000000000000000000000000000000000000000000000000000000000112c"
        val document = DidDocument(did)

        // When
        caffeineCacheService!!.save(document)
        val cached = caffeineCacheService!!.find(MetadiumDID(anotherDid))

        // Then
        assertThat(cached.isPresent).isFalse
    }

    @Test
    @DisplayName("캐시 삭제 테스트")
    fun cache_delete_test() {

        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val document = DidDocument(did)
        caffeineCacheService!!.save(document)

        // When
        val beforeDelete = caffeineCacheService!!.find(MetadiumDID(did))
        val deleteResult = caffeineCacheService!!.delete(MetadiumDID(did))
        val afterDelete = caffeineCacheService!!.find(MetadiumDID(did))

        // Then
        assertThat(beforeDelete.isPresent).isTrue
        assertThat(deleteResult).isTrue
        assertThat(afterDelete.isPresent).isFalse
    }

    @Test
    @DisplayName("캐시 삭제 시 삭제할 데이터가 없으면 false 반환")
    fun should_return_false_when_no_data_to_delete() {

        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val anotherDid = "did:meta:000000000000000000000000000000000000000000000000000000000000112c"
        val document = DidDocument(did)
        caffeineCacheService!!.save(document)

        // When
        val deletionResult = caffeineCacheService!!.delete(MetadiumDID(anotherDid))

        // Then
        assertThat(deletionResult).isFalse
    }
}
