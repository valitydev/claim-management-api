package com.rbkmoney.claimmanagementapi.converter.wallet

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.damsel.msgpack.Value
import dev.vality.swag.claim_management.model.WalletModification.WalletModificationTypeEnum
import org.springframework.stereotype.Component
import dev.vality.damsel.claim_management.NewWalletParams as ThriftWalletParams
import dev.vality.damsel.domain.CurrencyRef as ThriftCurrencyRef
import dev.vality.swag.claim_management.model.CurrencyRef as SwagCurrencyRef
import dev.vality.swag.claim_management.model.WalletCreationModification as SwagWalletCreationModification

@Component
class WalletModificationCreationConverter : DarkApiConverter<ThriftWalletParams, SwagWalletCreationModification> {

    override fun convertToThrift(value: SwagWalletCreationModification): ThriftWalletParams {
        return ThriftWalletParams().apply {
            name = value.name
            identityId = value.identityID
            currency = ThriftCurrencyRef(value.currency.symbolicCode)
            metadata = if (value.metadata != null) value.metadata as MutableMap<String, Value> else emptyMap()
        }
    }

    override fun convertToSwag(value: ThriftWalletParams): SwagWalletCreationModification {
        return SwagWalletCreationModification().apply {
            name = value.name
            identityID = value.identityId
            currency = SwagCurrencyRef().apply { symbolicCode = value.currency.symbolicCode }
            metadata = value.metadata
            walletModificationType = WalletModificationTypeEnum.WALLETCREATIONMODIFICATION
        }
    }
}
