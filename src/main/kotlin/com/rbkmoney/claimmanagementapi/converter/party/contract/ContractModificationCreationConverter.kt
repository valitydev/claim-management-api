package com.rbkmoney.claimmanagementapi.converter.party.contract

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.damsel.domain.ContractTemplateRef
import com.rbkmoney.damsel.domain.PaymentInstitutionRef
import com.rbkmoney.swag.claim_management.model.ContractModification.ContractModificationTypeEnum
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.claim_management.ContractParams as ThriftContractParams
import com.rbkmoney.swag.claim_management.model.ContractCreationModification as SwagContractCreationModification
import com.rbkmoney.swag.claim_management.model.ContractTemplateRef as SwagContractTemplateRef
import com.rbkmoney.swag.claim_management.model.PaymentInstitutionRef as SwagPaymentInstitutionRef

@Component
class ContractModificationCreationConverter : DarkApiConverter<ThriftContractParams, SwagContractCreationModification> {

    override fun convertToThrift(value: SwagContractCreationModification): ThriftContractParams {
        val params = ThriftContractParams().apply {
            contractorId = value.contractorID
        }
        value.template?.let {
            val contractTemplateRef = ContractTemplateRef().setId(value.template.id)
            params.setTemplate(contractTemplateRef)
        }
        value.paymentInstitution?.let {
            val paymentInstitution = PaymentInstitutionRef().setId(value.paymentInstitution.id)
            params.paymentInstitution = paymentInstitution
        }
        return params
    }

    override fun convertToSwag(value: ThriftContractParams): SwagContractCreationModification {
        val swagContractCreationModification = SwagContractCreationModification().apply {
            contractModificationType = ContractModificationTypeEnum.CONTRACTCREATIONMODIFICATION
            contractorID = value.contractorId
        }
        value.paymentInstitution?.let {
            val paymentInstitutionRef = SwagPaymentInstitutionRef().apply { id = it.id }
            swagContractCreationModification.paymentInstitution = paymentInstitutionRef
        }
        value.template?.let {
            val contractTemplateRef = SwagContractTemplateRef().apply { id = it.id }
            swagContractCreationModification.template = contractTemplateRef
        }
        return swagContractCreationModification
    }
}
