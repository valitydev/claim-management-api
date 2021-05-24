package com.rbkmoney.claimmanagementapi.converter.party.contract

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.damsel.claim_management.ContractTermination
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.claim_management.ContractModification as ThriftContractModification
import com.rbkmoney.damsel.claim_management.ContractModificationUnit as ThriftContractModificationUnit
import com.rbkmoney.swag.claim_management.model.ContractAdjustmentModificationUnit as SwagContractAdjustmentModificationUnit
import com.rbkmoney.swag.claim_management.model.ContractContractorModification as SwagContractContractorModification
import com.rbkmoney.swag.claim_management.model.ContractCreationModification as SwagContractCreationModification
import com.rbkmoney.swag.claim_management.model.ContractLegalAgreementBindingModification as SwagContractLegalAgreementBindingModification
import com.rbkmoney.swag.claim_management.model.ContractModification as SwagContractModification
import com.rbkmoney.swag.claim_management.model.ContractModificationUnit as SwagContractModificationUnit
import com.rbkmoney.swag.claim_management.model.ContractPayoutToolModificationUnit as SwagContractPayoutToolModificationUnit
import com.rbkmoney.swag.claim_management.model.ContractReportPreferencesModification as SwagContractReportPreferencesModification
import com.rbkmoney.swag.claim_management.model.ContractTerminationModification as SwagContractTerminationModification
import com.rbkmoney.swag.claim_management.model.PartyModificationType as SwagPartyModificationType

@Component
class ContractModificationUnitConverter(
    private val contractModificationCreationConverter: ContractModificationCreationConverter,
    private val reportPreferencesConverter: ContractReportPreferencesModificationConverter,
    private val payoutToolModificationUnitConverter: PayoutToolModificationUnitConverter,
    private val adjustmentModificationConverter: ContractAdjustmentModificationUnitConverter,
    private val legalAgreementConverter: LegalAgreementConverter
) : DarkApiConverter<ThriftContractModificationUnit, SwagContractModificationUnit> {

    override fun convertToThrift(value: SwagContractModificationUnit): ThriftContractModificationUnit {
        val swagContractModification = value.modification
        val thriftContractModification = ThriftContractModification()
        when (swagContractModification.contractModificationType) {
            SwagContractModification.ContractModificationTypeEnum.CONTRACTCREATIONMODIFICATION -> {
                val swagContractParams = swagContractModification as SwagContractCreationModification
                thriftContractModification.creation =
                    contractModificationCreationConverter.convertToThrift(swagContractParams)
            }
            SwagContractModification.ContractModificationTypeEnum.CONTRACTCONTRACTORMODIFICATION -> {
                val swagContractorId = swagContractModification as SwagContractContractorModification
                thriftContractModification.contractorModification = swagContractorId.contractorID
            }
            SwagContractModification.ContractModificationTypeEnum.CONTRACTLEGALAGREEMENTBINDINGMODIFICATION -> {
                val swagLegalAgreement = swagContractModification as SwagContractLegalAgreementBindingModification
                thriftContractModification.legalAgreementBinding =
                    legalAgreementConverter.convertToThrift(swagLegalAgreement)
            }
            SwagContractModification.ContractModificationTypeEnum.CONTRACTREPORTPREFERENCESMODIFICATION -> {
                val swagReportPreferences = swagContractModification as SwagContractReportPreferencesModification
                thriftContractModification.reportPreferencesModification =
                    reportPreferencesConverter.convertToThrift(swagReportPreferences)
            }
            SwagContractModification.ContractModificationTypeEnum.CONTRACTTERMINATIONMODIFICATION -> {
                val swagContractTerm = swagContractModification as SwagContractTerminationModification
                thriftContractModification.termination =
                    ContractTermination().setReason(swagContractTerm.reason)
            }
            SwagContractModification.ContractModificationTypeEnum.CONTRACTPAYOUTTOOLMODIFICATIONUNIT -> {
                val swagPayoutToolModificationUnit = swagContractModification as SwagContractPayoutToolModificationUnit
                thriftContractModification.payoutToolModification =
                    payoutToolModificationUnitConverter.convertToThrift(swagPayoutToolModificationUnit)
            }
            SwagContractModification.ContractModificationTypeEnum.CONTRACTADJUSTMENTMODIFICATIONUNIT -> {
                val swagContractAdjustmentModificationUnit =
                    swagContractModification as SwagContractAdjustmentModificationUnit
                thriftContractModification.adjustmentModification =
                    adjustmentModificationConverter.convertToThrift(swagContractAdjustmentModificationUnit)
            }
            else -> throw IllegalArgumentException(
                "Unknown contract modification type: ${swagContractModification.contractModificationType}"
            )
        }

        return ThriftContractModificationUnit(value.id, thriftContractModification)
    }

    override fun convertToSwag(value: ThriftContractModificationUnit): SwagContractModificationUnit {
        val thriftContractModification = value.getModification()
        val swagContractModification = when {
            thriftContractModification.isSetCreation -> {
                val creation = thriftContractModification.creation
                contractModificationCreationConverter.convertToSwag(creation)
            }
            thriftContractModification.isSetAdjustmentModification -> {
                adjustmentModificationConverter.convertToSwag(thriftContractModification.adjustmentModification)
            }
            thriftContractModification.isSetContractorModification -> {
                SwagContractContractorModification().apply {
                    contractModificationType =
                        SwagContractModification.ContractModificationTypeEnum.CONTRACTCONTRACTORMODIFICATION
                    contractorID = thriftContractModification.contractorModification
                }
            }
            thriftContractModification.isSetTermination -> {
                val thriftContractTermination = thriftContractModification.termination
                SwagContractTerminationModification().apply {
                    contractModificationType =
                        SwagContractModification.ContractModificationTypeEnum.CONTRACTTERMINATIONMODIFICATION
                    reason = thriftContractTermination.getReason()
                }
            }
            thriftContractModification.isSetLegalAgreementBinding -> {
                legalAgreementConverter.convertToSwag(thriftContractModification.legalAgreementBinding)
            }
            thriftContractModification.isSetPayoutToolModification -> {
                payoutToolModificationUnitConverter.convertToSwag(thriftContractModification.payoutToolModification)
            }
            thriftContractModification.isSetReportPreferencesModification -> {
                reportPreferencesConverter.convertToSwag(thriftContractModification.reportPreferencesModification)
            }
            else -> throw IllegalArgumentException("Unknown contract modification type!")
        }
        return SwagContractModificationUnit().apply {
            id = value.id
            partyModificationType = SwagPartyModificationType.PartyModificationTypeEnum.CONTRACTMODIFICATIONUNIT
            modification = swagContractModification
        }
    }
}
