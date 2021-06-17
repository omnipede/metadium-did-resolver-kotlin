package io.omnipede.metadium.did.resolver.system.filter.accesslog

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.net.InetAddress
import java.net.UnknownHostException

internal class HostNameTest {

    @Test
    @DisplayName("UnknownHostException 이 발생할 경우 Unknown 을 반환해야 한다")
    fun should_return_Unknown_when_UnknownHostException_occurs() {

        // Given
        val mockInetAddressSession = mockStatic(InetAddress::class.java)
        val unknownHostException = UnknownHostException("Foo bar")
        `when`(InetAddress.getLocalHost())
            .thenThrow(unknownHostException)

        // When
        val hostName = HostName()

        // Then
        assertThat(hostName).isNotNull
        assertThat(hostName.hostName).isEqualTo("Unknown")
        mockInetAddressSession.close()
    }
}
