package io.omnipede.metadium.did.resolver.controller

import arrow.core.Either
import io.omnipede.metadium.did.resolver.domain.application.ResolverApplication
import io.omnipede.metadium.did.resolver.domain.entity.DidDocument
import io.omnipede.metadium.did.resolver.domain.ports.MetaData
import io.omnipede.metadium.did.resolver.domain.ports.MethodMetaData
import io.omnipede.metadium.did.resolver.domain.ports.ResolverMetaData
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * HTTP API 형식 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
internal class ResolverControllerITTest {

    @MockBean
    private var resolverApplication: ResolverApplication? = null

    @Autowired
    private var mockMvc: MockMvc? = null

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
    private fun <T> eq(obj: T): T = Mockito.eq<T>(obj)

    @Nested
    inner class GetDidDocument {

        @Test
        @DisplayName("정상적인 경우 status 200 을 반환해야 한다")
        fun should_return_200_when_document_found() {

            // Given
            val did = "did:meta:mainnet:000000000000000000000000000000000000000000000000000000000000112a"
            val didDocument = DidDocument(did)
            val network = "mainnet"
            val registryAddress = "0x42bbff659772231bb63c7c175a1021e080a4cf9d"
            val driverId = "did-meta"
            val metaData = MetaData(
                methodMetaData = MethodMetaData(network, registryAddress),
                resolverMetaData = ResolverMetaData(driverId)
            )

            doReturn(Either.Right(didDocument to metaData))
                .`when`(resolverApplication)!!
                .resolve(eq(did), any(Boolean::class.java))

            // When
            mockMvc!!.perform(get("/1.0/identifiers/${did}"))

                // Then
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.didDocument").isMap)
                .andExpect(jsonPath("$.resolverMetadata").isMap)
                .andExpect(jsonPath("$.methodMetadata").isMap)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        }

        @Test
        @DisplayName("요청 형식이 올바르지 않을 경우 status 400 을 반환한다")
        fun should_return_400_when_request_format_is_invalid() {

            // Given
            val did = "Did:meta:000000000000000000000000000000000000000000000000000000000000112a"

            // When
            mockMvc!!.perform(get("/1.0/identifiers/${did}"))

                // Then
                .andExpect(status().`is`(400))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isString)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        }

        @Test
        @DisplayName("헤더 형식이 올바르지 않을 경우 status 400 을 반환한다")
        fun should_return_400_when_header_is_invalid() {

            // Given
            val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112a"

            // Wrong boolean format
            val noCache = "TRUEE"

            // When
            mockMvc!!.perform(
                get("/1.0/identifiers/${did}")
                    .header("no-cache", noCache)
            )

                // Then
                .andExpect(status().`is`(400))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isString)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        }
    }

    @Nested
    inner class DeleteDocumentTest {

        @Test
        @DisplayName("캐시된 document 가 삭제될 경우 status 200 을 반환해야 한다")
        fun should_return_200_when_document_deleted() {

            // Given
            val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112a"

            doReturn(true)
                .`when`(resolverApplication)!!
                .deleteDocumentFromCache(did)

            // When
            mockMvc!!.perform(
                delete("/1.0/identifiers/${did}")
            )

                // Then
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").isString)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        }

        @Test
        @DisplayName("요청 형식이 올바르지 않을 경우 status 400 을 반환한다")
        fun should_return_400_when_request_format_is_invalid() {

            // Given
            val did = "Did:meta:000000000000000000000000000000000000000000000000000000000000112a"

            // When
            mockMvc!!.perform(
                delete("/1.0/identifiers/${did}")
            )

                // Then
                .andExpect(status().`is`(400))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isString)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        }
    }
}
