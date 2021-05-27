package com.rbkmoney.claimmanagementapi.converter.party.shop

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.swag.claim_management.model.ShopModification.ShopModificationTypeEnum
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.claim_management.ShopParams as ThriftShopParams
import com.rbkmoney.damsel.domain.CategoryRef as ThriftCategoryRef
import com.rbkmoney.damsel.domain.ShopDetails as ThriftShopDetails
import com.rbkmoney.damsel.domain.ShopLocation as ThriftShopLocation
import com.rbkmoney.swag.claim_management.model.CategoryRef as SwagCategoryRef
import com.rbkmoney.swag.claim_management.model.ShopCreationModification as SwagShopCreationModification
import com.rbkmoney.swag.claim_management.model.ShopDetails as SwagShopDetails
import com.rbkmoney.swag.claim_management.model.ShopLocation.LocationTypeEnum as SwagLocationTypeEnum
import com.rbkmoney.swag.claim_management.model.ShopLocationUrl as SwagShopLocationUrl

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
