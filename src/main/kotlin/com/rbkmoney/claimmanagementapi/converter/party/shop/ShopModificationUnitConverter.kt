package com.rbkmoney.claimmanagementapi.converter.party.shop

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.damsel.claim_management.ShopModification
import dev.vality.damsel.domain.CategoryRef
import dev.vality.damsel.domain.ShopLocation
import dev.vality.swag.claim_management.model.PartyModificationType.PartyModificationTypeEnum
import dev.vality.swag.claim_management.model.ShopAccountCreationModification
import dev.vality.swag.claim_management.model.ShopCategoryModification
import dev.vality.swag.claim_management.model.ShopContractModification
import dev.vality.swag.claim_management.model.ShopCreationModification
import dev.vality.swag.claim_management.model.ShopDetailsModification
import dev.vality.swag.claim_management.model.ShopLocationModification
import dev.vality.swag.claim_management.model.ShopLocationUrl
import dev.vality.swag.claim_management.model.ShopPayoutScheduleModification
import dev.vality.swag.claim_management.model.ShopPayoutToolModification
import org.springframework.stereotype.Component
import dev.vality.damsel.claim_management.ShopModificationUnit as ThriftShopModificationUnit
import dev.vality.swag.claim_management.model.CategoryRef as SwagCategoryRef
import dev.vality.swag.claim_management.model.ShopModification.ShopModificationTypeEnum as SwagShopModificationTypeEnum
import dev.vality.swag.claim_management.model.ShopModificationUnit as SwagShopModificationUnit

@Component
class ShopModificationUnitConverter(
    private val shopParamsConverter: ShopCreationModificationConverter,
    private val scheduleModificationConverter: ShopPayoutScheduleModificationConverter,
    private val claimShopDetailsConverter: ShopDetailsModificationConverter,
    private val shopAccountParamsConverter: ShopAccountCreationModificationConverter,
    private val shopContractModificationConverter: ShopContractModificationConverter
) : DarkApiConverter<ThriftShopModificationUnit, SwagShopModificationUnit> {

    override fun convertToThrift(value: SwagShopModificationUnit): ThriftShopModificationUnit {
        val thriftShopModification = ShopModification()
        val swagModification = value.modification
        when (swagModification.shopModificationType) {
            SwagShopModificationTypeEnum.SHOPCREATIONMODIFICATION -> {
                val swagCreation: ShopCreationModification = swagModification as ShopCreationModification
                thriftShopModification.creation = shopParamsConverter.convertToThrift(swagCreation)
            }
            SwagShopModificationTypeEnum.SHOPCATEGORYMODIFICATION -> {
                val swagShopCategoryModification = swagModification as ShopCategoryModification
                thriftShopModification.categoryModification =
                    CategoryRef().setId(swagShopCategoryModification.category.categoryID)
            }
            SwagShopModificationTypeEnum.SHOPDETAILSMODIFICATION -> {
                val swagShopDetails = swagModification as ShopDetailsModification
                thriftShopModification.detailsModification = claimShopDetailsConverter.convertToThrift(swagShopDetails)
            }
            SwagShopModificationTypeEnum.SHOPCONTRACTMODIFICATION -> {
                val swagShopContractModification = swagModification as ShopContractModification
                thriftShopModification.contractModification =
                    shopContractModificationConverter.convertToThrift(swagShopContractModification)
            }
            SwagShopModificationTypeEnum.SHOPPAYOUTTOOLMODIFICATION -> {
                val swagShopPayoutToolModification = swagModification as ShopPayoutToolModification
                thriftShopModification.payoutToolModification = swagShopPayoutToolModification.payoutToolModification
            }
            SwagShopModificationTypeEnum.SHOPLOCATIONMODIFICATION -> {
                val swagShopLocation = swagModification as ShopLocationModification
                val locationModification = ShopLocation().apply {
                    url = (swagShopLocation.location as ShopLocationUrl).url
                }
                thriftShopModification.locationModification = locationModification
            }
            SwagShopModificationTypeEnum.SHOPACCOUNTCREATIONMODIFICATION -> {
                val swagShopAccountParams = swagModification as ShopAccountCreationModification
                thriftShopModification.shopAccountCreation =
                    shopAccountParamsConverter.convertToThrift(swagShopAccountParams)
            }
            SwagShopModificationTypeEnum.SHOPPAYOUTSCHEDULEMODIFICATION -> {
                val swagScheduleModification = swagModification as ShopPayoutScheduleModification
                thriftShopModification.payoutScheduleModification =
                    scheduleModificationConverter.convertToThrift(swagScheduleModification)
            }
            else -> throw IllegalArgumentException(
                "Unknown shop modification type: ${swagModification.shopModificationType}"
            )
        }

        return ThriftShopModificationUnit()
            .setId(value.id)
            .setModification(thriftShopModification)
    }

    override fun convertToSwag(value: ThriftShopModificationUnit): SwagShopModificationUnit {
        val swagShopModificationUnit = SwagShopModificationUnit().apply {
            id = value.getId()
            partyModificationType = PartyModificationTypeEnum.SHOPMODIFICATIONUNIT
        }
        val thriftShopModification = value.getModification()
        when {
            thriftShopModification.isSetCreation -> {
                swagShopModificationUnit.modification =
                    shopParamsConverter.convertToSwag(thriftShopModification.creation)
            }
            thriftShopModification.isSetContractModification -> {
                swagShopModificationUnit.modification =
                    shopContractModificationConverter.convertToSwag(thriftShopModification.contractModification)
            }
            thriftShopModification.isSetCategoryModification -> {
                val categoryModification = thriftShopModification.categoryModification
                val categoryRef = SwagCategoryRef().categoryID(categoryModification.id)
                swagShopModificationUnit.modification = ShopCategoryModification()
                    .category(categoryRef)
                    .shopModificationType(SwagShopModificationTypeEnum.SHOPCATEGORYMODIFICATION)
            }
            thriftShopModification.isSetDetailsModification -> {
                swagShopModificationUnit.modification =
                    claimShopDetailsConverter.convertToSwag(thriftShopModification.detailsModification)
            }
            thriftShopModification.isSetLocationModification -> {
                val shopLocation = thriftShopModification.locationModification
                val swagShopLocation = ShopLocationUrl().url(shopLocation.url)
                swagShopModificationUnit.modification = ShopLocationModification()
                    .location(swagShopLocation)
                    .shopModificationType(SwagShopModificationTypeEnum.SHOPLOCATIONMODIFICATION)
            }
            thriftShopModification.isSetShopAccountCreation -> {
                swagShopModificationUnit.modification =
                    shopAccountParamsConverter.convertToSwag(thriftShopModification.shopAccountCreation)
            }
            thriftShopModification.isSetPayoutScheduleModification -> {
                swagShopModificationUnit.modification =
                    scheduleModificationConverter.convertToSwag(thriftShopModification.payoutScheduleModification)
            }
            thriftShopModification.isSetPayoutToolModification -> {
                val shopPayoutToolModification = ShopPayoutToolModification().apply {
                    payoutToolModification = thriftShopModification.payoutToolModification
                    shopModificationType = SwagShopModificationTypeEnum.SHOPPAYOUTTOOLMODIFICATION
                }
                swagShopModificationUnit.modification = shopPayoutToolModification
            }
            thriftShopModification.isSetCashRegisterModificationUnit -> {
                // todo
            }
            else -> throw IllegalArgumentException("Unknown party modification type!")
        }
        return swagShopModificationUnit
    }
}
