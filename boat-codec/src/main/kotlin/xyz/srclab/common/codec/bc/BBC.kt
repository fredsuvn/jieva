@file:JvmName("BBC")

package xyz.srclab.common.codec.bc

import org.bouncycastle.asn1.gm.GMNamedCurves
import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECParameterSpec

@JvmField
val DEFAULT_X9_EC_PARAMETERS: X9ECParameters = GMNamedCurves.getByName("sm2p256v1")

@JvmField
val DEFAULT_EC_DOMAIN_PARAMETERS: ECDomainParameters = ECDomainParameters(
    DEFAULT_X9_EC_PARAMETERS.curve, DEFAULT_X9_EC_PARAMETERS.g,
    DEFAULT_X9_EC_PARAMETERS.n
)

@JvmField
val DEFAULT_EC_PARAMETER_SPEC: ECParameterSpec =
    ECParameterSpec(
        DEFAULT_X9_EC_PARAMETERS.curve,
        DEFAULT_X9_EC_PARAMETERS.g,
        DEFAULT_X9_EC_PARAMETERS.n,
        DEFAULT_X9_EC_PARAMETERS.h
    )

@JvmField
val DEFAULT_BCPROV_PROVIDER: BouncyCastleProvider = BouncyCastleProvider()