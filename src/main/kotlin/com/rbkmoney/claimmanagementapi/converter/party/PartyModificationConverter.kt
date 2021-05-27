package com.rbkmoney.claimmanagementapi.converter.party

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.claimmanagementapi.converter.party.contract.ContractModificationUnitConverter
import com.rbkmoney.claimmanagementapi.converter.party.contractor.ContractorModificationUnitConverter
import com.rbkmoney.claimmanagementapi.converter.party.shop.ShopModificationUnitConverter
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.claim_management.Modification as ThriftModification
import com.rbkmoney.damsel.claim_management.PartyModification as ThriftPartyModification
import com.rbkmoney.swag.claim_management.model.ContractModificationUnit as SwagContractModificationUnit
import com.rbkmoney.swag.claim_management.model.ContractorModificationUnit as SwagContractorModificationUnit
import com.rbkmoney.swag.claim_management.model.Modification as SwagModification
import com.rbkmoney.swag.claim_management.model.PartyModification as SwagPartyModification
import com.rbkmoney.swag.claim_management.model.PartyModificationType as SwagPartyModificationType
import com.rbkmoney.swag.claim_management.model.ShopModificationUnit as SwagShopModificationUnit

@Component
class PartyModificationConverter(
    private val contractModificationUnitConverter: ContractModificationUnitConverter,
    private val contractorModificationUnitConverter: ContractorModificationUnitConverter,
    private val shopModificationUnitConverter: ShopModificationUnitConverter
) : DarkApiConverter<ThriftModification, SwagPartyModification> {

    override fun convertToThrift(value: SwagPartyModification): ThriftModification {
        val swagPartyModificationType = value.partyModificationType
        val thriftPartyModification = ThriftPartyModification()
        when (swagPartyModificationType.partyModificationType) {
            SwagPartyModificationType.PartyModificationTypeEnum.CONTRACTMODIFICATIONUNIT -> {
                val swagContractModificationUnit = swagPartyModificationType as SwagContractModificationUnit
                thriftPartyModification.contractModification =
                    contractModificationUnitConverter.convertToThrift(swagContractModificationUnit)
            }
            SwagPartyModificationType.PartyModificationTypeEnum.CONTRACTORMODIFICATIONUNIT -> {
                val swagContractorModificationUnit = swagPartyModificationType as SwagContractorModificationUnit
                thriftPartyModification.contractorModification =
                    contractorModificationUnitConverter.convertToThrift(swagContractorModificationUnit)
            }
            SwagPartyModificationType.PartyModificationTypeEnum.SHOPMODIFICATIONUNIT -> {
                val swagShopModificationUnit = swagPartyModificationType as SwagShopModificationUnit
                thriftPartyModification.shopModification =
                    shopModificationUnitConverter.convertToThrift(swagShopModificationUnit)
            }
            else -> throw IllegalArgumentException("Unknown claim management party modification type: $value")
        }
        return ThriftModification().apply { partyModification = thriftPartyModification }
    }

    override fun convertToSwag(value: ThriftModification): SwagPartyModification {
        val thriftPartyModification = value.partyModification
        val swagPartyModificationType = when {
            thriftPartyModification.isSetContractModification ->
                contractModificationUnitConverter.convertToSwag(thriftPartyModification.contractModification)
            thriftPartyModification.isSetContractorModification ->
                contractorModificationUnitConverter.convertToSwag(thriftPartyModification.contractorModification)
            thriftPartyModification.isSetShopModification ->
                shopModificationUnitConverter.convertToSwag(thriftPartyModification.shopModification)
            else -> throw IllegalArgumentException("Unknown party modification type!")
        }
        return SwagPartyModification().apply {
            modificationType = SwagModification.ModificationTypeEnum.PARTYMODIFICATION
            partyModificationType = swagPartyModificationType
        }
    }
}
