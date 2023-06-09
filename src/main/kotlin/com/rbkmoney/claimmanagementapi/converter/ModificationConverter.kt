package com.rbkmoney.claimmanagementapi.converter

import com.rbkmoney.claimmanagementapi.converter.claim.ClaimModificationConverter
import com.rbkmoney.claimmanagementapi.converter.identity.IdentityModificationUnitConverter
import com.rbkmoney.claimmanagementapi.converter.party.PartyModificationConverter
import com.rbkmoney.claimmanagementapi.converter.wallet.WalletModificationUnitConverter
import dev.vality.swag.claim_management.model.*
import org.springframework.stereotype.Component
import dev.vality.damsel.claim_management.Modification as ThriftModification
import dev.vality.swag.claim_management.model.Modification as SwagModification

@Component
class ModificationConverter(
    private val claimModificationConverter: ClaimModificationConverter,
    private val partyModificationConverter: PartyModificationConverter,
    private val identityModificationUnitConverter: IdentityModificationUnitConverter,
    private val walletModificationUnitConverter: WalletModificationUnitConverter
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
            SwagModification.ModificationTypeEnum.IDENTITYMODIFICATIONUNIT -> {
                val swagIdentityModificationUnit = value as IdentityModificationUnit
                identityModificationUnitConverter.convertToThrift(swagIdentityModificationUnit)
            }
            SwagModification.ModificationTypeEnum.WALLETMODIFICATIONUNIT -> {
                val swagWalletModificationUnit = value as WalletModificationUnit
                walletModificationUnitConverter.convertToThrift(swagWalletModificationUnit)
            }
            else -> throw IllegalArgumentException("Unknown claim management modification type: $value")
        }
    }

    override fun convertToSwag(value: ThriftModification): SwagModification {
        return when {
            value.isSetClaimModification -> claimModificationConverter.convertToSwag(value)
            value.isSetPartyModification -> partyModificationConverter.convertToSwag(value)
            value.isSetIdentityModification -> identityModificationUnitConverter.convertToSwag(value)
            value.isSetWalletModification -> walletModificationUnitConverter.convertToSwag(value)
            else -> {
                throw IllegalArgumentException("Unknown modification type!")
            }
        }
    }
}
