package com.rbkmoney.claimmanagementapi.converter.identity

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.swag.claim_management.model.IdentityCreationModification
import dev.vality.swag.claim_management.model.IdentityModification.IdentityModificationTypeEnum
import org.springframework.stereotype.Component
import dev.vality.damsel.claim_management.IdentityModification as ThriftIdentityModification
import dev.vality.damsel.claim_management.IdentityModificationUnit as ThriftIdentityUnitModification
import dev.vality.damsel.claim_management.Modification as ThriftModification
import dev.vality.swag.claim_management.model.IdentityModificationUnit as SwagIdentityModificationUnit
import dev.vality.swag.claim_management.model.Modification as SwagModification

@Component
class IdentityModificationUnitConverter(
    private val identityModificationCreationConverter: IdentityModificationCreationConverter,
) : DarkApiConverter<ThriftModification, SwagIdentityModificationUnit> {

    override fun convertToThrift(value: SwagIdentityModificationUnit): ThriftModification {
        val thriftIdentityUnitModification = ThriftIdentityUnitModification()
        val thriftIdentityModification = ThriftIdentityModification()
        val swagIdentityModification = value.modification
        when (swagIdentityModification.identityModificationType) {
            IdentityModificationTypeEnum.IDENTITYCREATIONMODIFICATION -> {
                val swagIdentityParams = swagIdentityModification as IdentityCreationModification
                thriftIdentityModification.creation =
                    identityModificationCreationConverter.convertToThrift(swagIdentityParams)
            }
            else -> throw IllegalArgumentException(
                "Unknown identity modification type: ${swagIdentityModification.identityModificationType}"
            )
        }
        thriftIdentityUnitModification.modification = thriftIdentityModification
        thriftIdentityUnitModification.id = value.id
        return ThriftModification().apply { identityModification = thriftIdentityUnitModification }
    }

    override fun convertToSwag(value: ThriftModification): SwagIdentityModificationUnit {
        val thriftIdentityModification = value.identityModification
        val swagIdentityModification = when {
            value.identityModification.isSetModification
                .and(thriftIdentityModification.getModification().isSetCreation) -> {
                identityModificationCreationConverter.convertToSwag(thriftIdentityModification.modification.creation)
            }
            else -> throw IllegalArgumentException("Unknown identity modification type!")
        }
        return SwagIdentityModificationUnit().apply {
            id = value.identityModification.id
            modification = swagIdentityModification
            SwagModification.ModificationTypeEnum.IDENTITYMODIFICATIONUNIT
        }
    }
}
