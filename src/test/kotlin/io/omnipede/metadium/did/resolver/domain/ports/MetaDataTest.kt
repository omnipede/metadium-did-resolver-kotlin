package io.omnipede.metadium.did.resolver.domain.ports

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

internal class MetaDataTest {

    @Nested
    @DisplayName("MetaDataTest")
    inner class MetaDataTest {

        @Test
        @DisplayName("객체 생성 테스트")
        fun should_be_created_properly() {

            // Given
            val methodMetaData = MethodMetaData(
                network="mainnet",
                registryAddress="0x42bbff659772231bb63c7c175a1021e080a4cf9d"
            )
            val resolverMetaData = ResolverMetaData(
                driverId = "did-meta"
            )

            // When
            val metaData = MetaData(methodMetaData, resolverMetaData)
            Thread.sleep(100)
            metaData.markCached()
            metaData.endResolving()

            // Then
            assertThat(metaData).isNotNull
            assertThat(metaData.methodMetaData).isNotNull
            assertThat(metaData.resolverMetaData).isNotNull
            assertThat(metaData.resolverMetaData.cached).isTrue
            assertThat(metaData.resolverMetaData.duration)
                .isGreaterThanOrEqualTo(100)
                .isLessThan(200)
        }
    }

    @Nested
    @DisplayName("MethodMetaData")
    inner class MethodMetaDataTest {

        @Test
        @DisplayName("객체 생성 테스트")
        fun should_be_created_properly() {
            // Given
            val methodMetaData = MethodMetaData(
                network="mainnet",
                registryAddress="0x42bbff659772231bb63c7c175a1021e080a4cf9d"
            )

            // When

            // Then
            assertThat(methodMetaData).isNotNull
            assertThat(methodMetaData.registryAddress).isEqualTo("0x42bbff659772231bb63c7c175a1021e080a4cf9d")
            assertThat(methodMetaData.network).isEqualTo("mainnet")
        }
    }

    @Nested
    @DisplayName("ResolverMetaData")
    inner class ResolverMetaDataTest {

        @Test
        @DisplayName("객체 생성 테스트")
        fun should_be_created_property() {

            // Given
            val now = Date()
            val resolverMetaData = ResolverMetaData(
                driverId = "did-meta"
            )

            // When

            // Then
            assertThat(resolverMetaData).isNotNull
            assertThat(resolverMetaData.driverId).isEqualTo("did-meta")
            assertThat(resolverMetaData.driver).isEqualTo("HttpDriver")
            assertThat(resolverMetaData.retrieved).isEqualToIgnoringMillis(now)
        }

        @Test
        @DisplayName("MetaData.endResolving() 메소드는 resolve duration 을 정확히 측정해야한다")
        fun endResolving_method_test() {

            // Given
            val resolverMetaData = ResolverMetaData(
                driverId = "did-meta"
            )

            // When
            Thread.sleep(100)
            resolverMetaData.endResolving()

            // Then
            assertThat(resolverMetaData.duration)
                .isGreaterThanOrEqualTo(100)
                .isLessThan(1000)
        }
    }
}
