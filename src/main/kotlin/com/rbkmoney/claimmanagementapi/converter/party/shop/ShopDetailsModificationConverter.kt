package com.rbkmoney.claimmanagementapi.converter.party.shop

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.swag.claim_management.model.ShopModification.ShopModificationTypeEnum
import org.springframework.stereotype.Component
import dev.vality.damsel.domain.ShopDetails as ThriftShopDetails
import dev.vality.swag.claim_management.model.ShopDetails as SwagShopDetails
import dev.vality.swag.claim_management.model.ShopDetailsModification as SwagShopDetailsModification

@Component
class ShopDetailsModificationConverter : DarkApiConverter<ThriftShopDetails, SwagShopDetailsModification> {

    override fun convertToThrift(value: SwagShopDetailsModification): ThriftShopDetails =
        ThriftShopDetails()
            .setName(value.details.name)
            .setDescription(value.details.description)

    override fun convertToSwag(value: ThriftShopDetails) =
        SwagShopDetailsModification().apply {
            shopModificationType = ShopModificationTypeEnum.SHOPDETAILSMODIFICATION
            details = SwagShopDetails()
                .name(value.name)
                .description(value.description)
        }
}
