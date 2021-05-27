package com.rbkmoney.claimmanagementapi.converter

import com.rbkmoney.claimmanagementapi.converter.claim.ClaimModificationConverter
import com.rbkmoney.claimmanagementapi.converter.party.PartyModificationConverter
import com.rbkmoney.swag.claim_management.model.ClaimModification
import com.rbkmoney.swag.claim_management.model.PartyModification
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.claim_management.Modification as ThriftModification
import com.rbkmoney.swag.claim_management.model.Modification as SwagModification

@Component
class ModificationConverter(
    private val claimModificationConverter: ClaimModificationConverter,
    private val partyModificationConverter: PartyModificationConverter
) : DarkApiConverter<ThriftModification, SwagModification> {

    override fun convertToThrift(value: SwagModification): ThriftModification {
        return when (value.modificationType) {
            SwagModification.ModificationTypeEnum.CLAIMMODIFICATION -> {
                val claimModification = value as ClaimModification
                claimModificationConverter.convertToThrift(claimModification)
            }
            SwagModification.ModificationTypeEnum.PARTYMODIFICATION -> {
                val swagPartyModification = value as PartyModification
                partyModificationConverter.convertToThrift(swagPartyModification)
            }
            else -> throw IllegalArgumentException("Unknown claim management modification type: $value")
        }
    }

    override fun convertToSwag(value: ThriftModification): SwagModification {
        return when {
            value.isSetClaimModification -> claimModificationConverter.convertToSwag(value)
            value.isSetPartyModification -> partyModificationConverter.convertToSwag(value)
            else -> {
                throw IllegalArgumentException("Unknown modification type!")
            }
        }
    }
}
