package com.rbkmoney.claimmanagementapi.converter.wallet

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.swag.claim_management.model.WalletCreationModification
import dev.vality.swag.claim_management.model.WalletModification.WalletModificationTypeEnum
import org.springframework.stereotype.Component
import dev.vality.damsel.claim_management.Modification as ThriftModification
import dev.vality.damsel.claim_management.NewWalletModification as ThriftWalletModification
import dev.vality.damsel.claim_management.NewWalletModificationUnit as ThriftWalletUnitModification
import dev.vality.swag.claim_management.model.Modification as SwagModification
import dev.vality.swag.claim_management.model.WalletModificationUnit as SwagWalletModificationUnit

@Component
class WalletModificationUnitConverter(
    private val walletModificationCreationConverter: WalletModificationCreationConverter,
) : DarkApiConverter<ThriftModification, SwagWalletModificationUnit> {

    override fun convertToThrift(value: SwagWalletModificationUnit): ThriftModification {
        val thriftWalletUnitModification = ThriftWalletUnitModification()
        val thriftWalletModification = ThriftWalletModification()
        val swagWalletModification = value.modification
        when (swagWalletModification.walletModificationType) {
            WalletModificationTypeEnum.WALLETCREATIONMODIFICATION -> {
                val swagWalletParams = swagWalletModification as WalletCreationModification
                thriftWalletModification.creation =
                    walletModificationCreationConverter.convertToThrift(swagWalletParams)
            }
            else -> throw IllegalArgumentException(
                "Unknown wallet modification type: ${swagWalletModification.walletModificationType}"
            )
        }
        thriftWalletUnitModification.modification = thriftWalletModification
        thriftWalletUnitModification.id = value.id
        return ThriftModification().apply { walletModification = thriftWalletUnitModification }
    }

    override fun convertToSwag(value: ThriftModification): SwagWalletModificationUnit {
        val thriftWalletModification = value.walletModification
        val swagWalletModification = when {
            thriftWalletModification.isSetModification
                .and(thriftWalletModification.getModification().isSetCreation) -> {
                walletModificationCreationConverter.convertToSwag(thriftWalletModification.modification.creation)
            }
            else -> throw IllegalArgumentException("Unknown wallet modification type!")
        }
        return SwagWalletModificationUnit().apply {
            id = value.walletModification.id
            modification = swagWalletModification
            SwagModification.ModificationTypeEnum.WALLETMODIFICATIONUNIT
        }
    }
}
