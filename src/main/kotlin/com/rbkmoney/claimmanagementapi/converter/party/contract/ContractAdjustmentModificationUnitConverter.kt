package com.rbkmoney.claimmanagementapi.converter.party.contract

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.damsel.domain.ContractTemplateRef
import dev.vality.swag.claim_management.model.ContractModification.ContractModificationTypeEnum
import org.springframework.stereotype.Component
import dev.vality.damsel.claim_management.ContractAdjustmentModification as ThriftContractAdjustmentModification
import dev.vality.damsel.claim_management.ContractAdjustmentModificationUnit as ThriftContractAdjustmentModificationUnit
import dev.vality.damsel.claim_management.ContractAdjustmentParams as ThriftContractAdjustmentParams
import dev.vality.swag.claim_management.model.ContractAdjustmentModification as SwagContractAdjustmentModification
import dev.vality.swag.claim_management.model.ContractAdjustmentModificationUnit as SwagContractAdjustmentModificationUnit
import dev.vality.swag.claim_management.model.ContractAdjustmentParams as SwagContractAdjustmentParams
import dev.vality.swag.claim_management.model.ContractTemplateRef as SwagContractTemplateRef

@Component
class ContractAdjustmentModificationUnitConverter :
    DarkApiConverter<ThriftContractAdjustmentModificationUnit, SwagContractAdjustmentModificationUnit> {

    override fun convertToThrift(value: SwagContractAdjustmentModificationUnit): ThriftContractAdjustmentModificationUnit {
        val contractAdjustmentParams = ThriftContractAdjustmentParams().apply {
            template = ContractTemplateRef().setId(value.modification.creation.template.id)
        }
        val adjustmentModification = ThriftContractAdjustmentModification().apply {
            creation = contractAdjustmentParams
        }
        return ThriftContractAdjustmentModificationUnit().apply {
            adjustmentId = value.adjustmentID
            modification = adjustmentModification
        }
    }

    override fun convertToSwag(value: ThriftContractAdjustmentModificationUnit): SwagContractAdjustmentModificationUnit {
        require(value.getModification().isSetCreation) {
            "Unknown adjustment modification type"
        }
        val contractTemplateRef = SwagContractTemplateRef().apply {
            id = value.modification.creation.template.id
        }
        val contractAdjustmentParams = SwagContractAdjustmentParams().apply {
            template = contractTemplateRef
        }
        val contractAdjustmentModification = SwagContractAdjustmentModification().apply {
            creation = contractAdjustmentParams
        }
        return SwagContractAdjustmentModificationUnit().apply {
            adjustmentID = value.adjustmentId
            contractModificationType = ContractModificationTypeEnum.CONTRACTADJUSTMENTMODIFICATIONUNIT
            modification = contractAdjustmentModification
        }
    }
}
