package io.omnipede.metadium.did.resolver.system.exception

import javax.validation.constraints.NotEmpty

internal class TempRequestDTO{
    @NotEmpty
    var a: String? = null
    var b: Int? = null
}
