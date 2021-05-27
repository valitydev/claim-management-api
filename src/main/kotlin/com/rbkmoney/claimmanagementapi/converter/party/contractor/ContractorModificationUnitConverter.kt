package com.rbkmoney.claimmanagementapi.converter.party.contractor

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.damsel.claim_management.ContractorModification
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.claim_management.ContractorModificationUnit as ThriftContractorModificationUnit
import com.rbkmoney.swag.claim_management.model.Contractor as SwagContractor
import com.rbkmoney.swag.claim_management.model.ContractorIdentificationLevel as SwagContractorIdentificationLevel
import com.rbkmoney.swag.claim_management.model.ContractorModification as SwagContractorModification
import com.rbkmoney.swag.claim_management.model.ContractorModificationUnit as SwagContractorModificationUnit
import com.rbkmoney.swag.claim_management.model.PartyModificationType as SwagPartyModificationType

@Component
class ContractorModificationUnitConverter(
    private val claimContractorConverter: ClaimContractorConverter,
    private val identificationLevelConverter: ContractorIdentificationLevelConverter
) : DarkApiConverter<ThriftContractorModificationUnit, SwagContractorModificationUnit> {

    override fun convertToThrift(value: SwagContractorModificationUnit): ThriftContractorModificationUnit {
        val thriftContractorModification = ContractorModification()
        val swagModification = value.modification
        when (swagModification.contractorModificationType) {
            SwagContractorModification.ContractorModificationTypeEnum.CONTRACTOR -> {
                val swagContractor = swagModification as SwagContractor
                thriftContractorModification.creation = claimContractorConverter.convertToThrift(swagContractor)
            }
            SwagContractorModification.ContractorModificationTypeEnum.CONTRACTORIDENTIFICATIONLEVEL -> {
                val swagContractorIdentificationLevel = swagModification as SwagContractorIdentificationLevel
                thriftContractorModification.identificationLevelModification =
                    identificationLevelConverter.convertToThrift(swagContractorIdentificationLevel)
            }
            else -> throw IllegalArgumentException(
                "Unknown contractor modification type: ${swagModification.contractorModificationType}"
            )
        }

        return ThriftContractorModificationUnit(value.id, thriftContractorModification)
    }

    override fun convertToSwag(value: ThriftContractorModificationUnit): SwagContractorModificationUnit {
        val thriftModification = value.modification
        val swagModification = when {
            thriftModification.isSetCreation -> {
                claimContractorConverter.convertToSwag(thriftModification.creation)
            }
            thriftModification.isSetIdentificationLevelModification -> {
                identificationLevelConverter.convertToSwag(thriftModification.identificationLevelModification)
            }
            else -> {
                throw IllegalArgumentException("Unknown contractor modification type!")
            }
        }
        return SwagContractorModificationUnit().apply {
            id = value.getId()
            partyModificationType = SwagPartyModificationType.PartyModificationTypeEnum.CONTRACTORMODIFICATIONUNIT
            modification = swagModification
        }
    }
}
