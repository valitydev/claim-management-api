package com.rbkmoney.claimmanagementapi.converter.party.shop

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.swag.claim_management.model.ShopModification.ShopModificationTypeEnum
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.claim_management.ShopContractModification as ThriftShopContractModification
import com.rbkmoney.swag.claim_management.model.ShopContractModification as SwagShopContractModification

@Component
class ShopContractModificationConverter :
    DarkApiConverter<ThriftShopContractModification, SwagShopContractModification> {

    override fun convertToThrift(value: SwagShopContractModification): ThriftShopContractModification =
        ThriftShopContractModification()
            .setContractId(value.contractID)
            .setPayoutToolId(value.payoutToolID)

    override fun convertToSwag(value: ThriftShopContractModification) =
        SwagShopContractModification().apply {
            contractID = value.contractId
            payoutToolID = value.payoutToolId
            shopModificationType = ShopModificationTypeEnum.SHOPCONTRACTMODIFICATION
        }
}
