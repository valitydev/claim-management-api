package com.rbkmoney.claimmanagementapi.converter.party.contractor

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.swag.claim_management.model.ContractorModification
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.domain.ContractorIdentificationLevel as ThriftContractorIdentificationLevel
import com.rbkmoney.swag.claim_management.model.ContractorIdentificationLevel as SwagContractorIdentificationLevel

@Component
class ContractorIdentificationLevelConverter :
    DarkApiConverter<ThriftContractorIdentificationLevel, SwagContractorIdentificationLevel> {

    override fun convertToThrift(value: SwagContractorIdentificationLevel): ThriftContractorIdentificationLevel =
        ThriftContractorIdentificationLevel.findByValue(value.contractorIdentificationLevel)

    override fun convertToSwag(
        value: ThriftContractorIdentificationLevel
    ) = SwagContractorIdentificationLevel().apply {
        contractorModificationType = ContractorModification.ContractorModificationTypeEnum.CONTRACTORIDENTIFICATIONLEVEL
        contractorIdentificationLevel = value.value
    }
}
