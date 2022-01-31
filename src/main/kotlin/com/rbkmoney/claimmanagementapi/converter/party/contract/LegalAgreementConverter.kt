package com.rbkmoney.claimmanagementapi.converter.party.contract

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import org.springframework.stereotype.Component
import dev.vality.damsel.domain.LegalAgreement as ThriftLegalAgreement
import dev.vality.swag.claim_management.model.ContractLegalAgreementBindingModification as SwagContractLegalAgreementBindingModification
import dev.vality.swag.claim_management.model.ContractModification as SwagContractModification
import dev.vality.swag.claim_management.model.LegalAgreement as SwagLegalAgreement

@Component
class LegalAgreementConverter : DarkApiConverter<ThriftLegalAgreement, SwagContractLegalAgreementBindingModification> {

    override fun convertToThrift(value: SwagContractLegalAgreementBindingModification): ThriftLegalAgreement {
        val swagLegalAgreement = value.legalAgreement
        return ThriftLegalAgreement()
            .setLegalAgreementId(swagLegalAgreement.legalAgreementID)
            .setSignedAt(swagLegalAgreement.signedAt)
            .setValidUntil(swagLegalAgreement.validUntil)
    }

    override fun convertToSwag(value: ThriftLegalAgreement): SwagContractLegalAgreementBindingModification {
        return SwagContractLegalAgreementBindingModification().apply {
            contractModificationType =
                SwagContractModification.ContractModificationTypeEnum.CONTRACTLEGALAGREEMENTBINDINGMODIFICATION
            legalAgreement = SwagLegalAgreement()
                .legalAgreementID(value.legalAgreementId)
                .signedAt(value.signedAt)
                .validUntil(value.validUntil)
        }
    }
}
