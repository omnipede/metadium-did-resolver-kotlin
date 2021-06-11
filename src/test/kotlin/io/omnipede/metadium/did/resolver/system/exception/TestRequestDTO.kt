package io.omnipede.metadium.did.resolver.system.exception

import javax.validation.constraints.NotEmpty

internal class TestRequestDTO{
    @NotEmpty
    var a: String? = null
    var b: Int? = null
}
