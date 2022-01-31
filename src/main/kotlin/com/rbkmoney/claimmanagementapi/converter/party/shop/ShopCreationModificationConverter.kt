package com.rbkmoney.claimmanagementapi.converter.party.shop

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.swag.claim_management.model.ShopModification.ShopModificationTypeEnum
import org.springframework.stereotype.Component
import dev.vality.damsel.claim_management.ShopParams as ThriftShopParams
import dev.vality.damsel.domain.CategoryRef as ThriftCategoryRef
import dev.vality.damsel.domain.ShopDetails as ThriftShopDetails
import dev.vality.damsel.domain.ShopLocation as ThriftShopLocation
import dev.vality.swag.claim_management.model.CategoryRef as SwagCategoryRef
import dev.vality.swag.claim_management.model.ShopCreationModification as SwagShopCreationModification
import dev.vality.swag.claim_management.model.ShopDetails as SwagShopDetails
import dev.vality.swag.claim_management.model.ShopLocation.LocationTypeEnum as SwagLocationTypeEnum
import dev.vality.swag.claim_management.model.ShopLocationUrl as SwagShopLocationUrl

@Component
class ShopCreationModificationConverter : DarkApiConverter<ThriftShopParams, SwagShopCreationModification> {

    override fun convertToThrift(value: SwagShopCreationModification): ThriftShopParams {
        val shopLocation = ThriftShopLocation()
        if (value.location.locationType == SwagLocationTypeEnum.SHOPLOCATIONURL) {
            shopLocation.url = (value.location as SwagShopLocationUrl).url
        }
        return ThriftShopParams()
            .setContractId(value.contractID)
            .setPayoutToolId(value.payoutToolID)
            .setCategory(ThriftCategoryRef().setId(value.category.categoryID))
            .setDetails(
                ThriftShopDetails()
                    .setDescription(value.details.description)
                    .setName(value.details.name)
            )
            .setLocation(shopLocation)
    }

    override fun convertToSwag(value: ThriftShopParams): SwagShopCreationModification {
        val swagShopParams = SwagShopCreationModification().apply {
            contractID = value.contractId
            payoutToolID = value.payoutToolId
            shopModificationType = ShopModificationTypeEnum.SHOPCREATIONMODIFICATION
            category = SwagCategoryRef().categoryID(value.category.id)
            details = SwagShopDetails()
                .name(value.details.name)
                .description(value.details.description)
        }
        if (value.getLocation().isSetUrl) {
            val swagShopLocation = SwagShopLocationUrl().apply {
                locationType = SwagLocationTypeEnum.SHOPLOCATIONURL
                url = value.getLocation().url
            }
            swagShopParams.location = swagShopLocation
        }
        return swagShopParams
    }
}
