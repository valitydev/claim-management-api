package com.rbkmoney.claimmanagementapi.converter.party.data

import dev.vality.swag.claim_management.model.PartyModificationType.PartyModificationTypeEnum
import dev.vality.swag.claim_management.model.ShopCategoryModification
import dev.vality.swag.claim_management.model.ShopLocation.LocationTypeEnum
import dev.vality.swag.claim_management.model.ShopLocationModification
import dev.vality.swag.claim_management.model.ShopModification.ShopModificationTypeEnum
import io.github.benas.randombeans.api.EnhancedRandom
import dev.vality.swag.claim_management.model.CategoryRef as SwagCategoryRef
import dev.vality.swag.claim_management.model.ShopAccountCreationModification as SwagShopAccountCreationModification1
import dev.vality.swag.claim_management.model.ShopContractModification as SwagShopContractModification
import dev.vality.swag.claim_management.model.ShopCreationModification as SwagShopCreationModification
import dev.vality.swag.claim_management.model.ShopDetailsModification as SwagShopDetailsModification
import dev.vality.swag.claim_management.model.ShopLocationUrl as SwagShopLocationUrl
import dev.vality.swag.claim_management.model.ShopModificationUnit as SwagShopModificationUnit
import dev.vality.swag.claim_management.model.ShopPayoutScheduleModification as SwagShopPayoutScheduleModification
import dev.vality.swag.claim_management.model.ShopPayoutToolModification as SwagShopPayoutToolModification

object TestShopData {

    val testSwagShopParams: SwagShopCreationModification
        get() =
            EnhancedRandom.random(
                SwagShopCreationModification::class.java
            ).apply {
                location = SwagShopLocationUrl()
                    .url("http://example.com")
                    .locationType(LocationTypeEnum.SHOPLOCATIONURL)
                shopModificationType = ShopModificationTypeEnum.SHOPCREATIONMODIFICATION
            }

    val testSwagShopModificationUnit: SwagShopModificationUnit
        get() {
            val swagShopModificationUnit = EnhancedRandom.random(
                SwagShopModificationUnit::class.java
            ).apply {
                partyModificationType = PartyModificationTypeEnum.SHOPMODIFICATIONUNIT
                modification.shopModificationType = ShopModificationTypeEnum.SHOPCREATIONMODIFICATION
            }
            when (swagShopModificationUnit.modification.shopModificationType) {
                ShopModificationTypeEnum.SHOPCREATIONMODIFICATION -> {
                    val shopParams = EnhancedRandom.random(
                        SwagShopCreationModification::class.java
                    ).apply {
                        shopModificationType = ShopModificationTypeEnum.SHOPCREATIONMODIFICATION
                        location = SwagShopLocationUrl().url("http://example.com/")
                            .locationType(LocationTypeEnum.SHOPLOCATIONURL)
                    }
                    swagShopModificationUnit.modification = shopParams
                }
                ShopModificationTypeEnum.SHOPPAYOUTSCHEDULEMODIFICATION -> {
                    val scheduleModification = EnhancedRandom.random(
                        SwagShopPayoutScheduleModification::class.java
                    ).apply { shopModificationType = ShopModificationTypeEnum.SHOPPAYOUTSCHEDULEMODIFICATION }
                    swagShopModificationUnit.modification = scheduleModification
                }
                ShopModificationTypeEnum.SHOPACCOUNTCREATIONMODIFICATION -> {
                    val shopAccountParams = EnhancedRandom.random(
                        SwagShopAccountCreationModification1::class.java
                    ).apply { shopModificationType = ShopModificationTypeEnum.SHOPACCOUNTCREATIONMODIFICATION }
                    swagShopModificationUnit.modification = shopAccountParams
                }
                ShopModificationTypeEnum.SHOPLOCATIONMODIFICATION -> {
                    val shopLocationType = EnhancedRandom.random(SwagShopLocationUrl::class.java)
                    swagShopModificationUnit.modification = ShopLocationModification()
                        .location(shopLocationType)
                        .shopModificationType(ShopModificationTypeEnum.SHOPLOCATIONMODIFICATION)
                }
                ShopModificationTypeEnum.SHOPPAYOUTTOOLMODIFICATION -> {
                    val shopPayoutToolModification = EnhancedRandom.random(
                        SwagShopPayoutToolModification::class.java
                    ).apply { shopModificationType = ShopModificationTypeEnum.SHOPPAYOUTTOOLMODIFICATION }
                    swagShopModificationUnit.modification = shopPayoutToolModification
                }
                ShopModificationTypeEnum.SHOPCONTRACTMODIFICATION -> {
                    val shopContractModification = EnhancedRandom.random(
                        SwagShopContractModification::class.java
                    ).apply { shopModificationType = ShopModificationTypeEnum.SHOPCONTRACTMODIFICATION }
                    swagShopModificationUnit.modification = shopContractModification
                }
                ShopModificationTypeEnum.SHOPDETAILSMODIFICATION -> {
                    val shopDetails = EnhancedRandom.random(
                        SwagShopDetailsModification::class.java
                    ).apply { shopModificationType = ShopModificationTypeEnum.SHOPDETAILSMODIFICATION }
                    swagShopModificationUnit.modification = shopDetails
                }
                ShopModificationTypeEnum.SHOPCATEGORYMODIFICATION -> {
                    val categoryRef = EnhancedRandom.random(
                        SwagCategoryRef::class.java
                    )
                    swagShopModificationUnit.modification = ShopCategoryModification()
                        .category(categoryRef)
                        .shopModificationType(ShopModificationTypeEnum.SHOPCATEGORYMODIFICATION)
                }
                else -> throw IllegalArgumentException("Unknown shop modification type!")
            }
            return swagShopModificationUnit
        }
}
