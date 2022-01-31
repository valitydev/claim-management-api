package com.rbkmoney.claimmanagementapi.converter.party.shop

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.swag.claim_management.model.ShopModification
import org.springframework.stereotype.Component
import dev.vality.damsel.claim_management.ShopAccountParams as ThriftShopAccountParams
import dev.vality.damsel.domain.CurrencyRef as ThriftCurrencyRef
import dev.vality.swag.claim_management.model.CurrencyRef as SwagCurrencyRef
import dev.vality.swag.claim_management.model.ShopAccountCreationModification as SwagShopAccountCreationModification

@Component
class ShopAccountCreationModificationConverter :
    DarkApiConverter<ThriftShopAccountParams, SwagShopAccountCreationModification> {

    override fun convertToThrift(value: SwagShopAccountCreationModification) =
        ThriftShopAccountParams().apply {
            currency = ThriftCurrencyRef(value.currency.symbolicCode)
        }

    override fun convertToSwag(value: ThriftShopAccountParams) =
        SwagShopAccountCreationModification().apply {
            currency = SwagCurrencyRef().apply { symbolicCode = value.currency.symbolicCode }
            shopModificationType = ShopModification.ShopModificationTypeEnum.SHOPACCOUNTCREATIONMODIFICATION
        }
}
