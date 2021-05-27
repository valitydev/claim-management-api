package com.rbkmoney.claimmanagementapi.converter.party.contractor

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.domain.Contractor as ThriftContractor
import com.rbkmoney.damsel.domain.RegisteredUser as ThriftRegisteredUser
import com.rbkmoney.swag.claim_management.model.Contractor as SwagContractor
import com.rbkmoney.swag.claim_management.model.ContractorModification as SwagContractorModification
import com.rbkmoney.swag.claim_management.model.ContractorType as SwagContractorType
import com.rbkmoney.swag.claim_management.model.LegalEntity as SwagLegalEntity
import com.rbkmoney.swag.claim_management.model.PrivateEntity as SwagPrivateEntity
import com.rbkmoney.swag.claim_management.model.RegisteredUser as SwagRegisteredUser

@Component
class ClaimContractorConverter(
    private val claimLegalEntityConverter: ClaimLegalEntityConverter,
    private val privateEntityConverter: PrivateEntityConverter
) : DarkApiConverter<ThriftContractor, SwagContractor> {

    override fun convertToThrift(value: SwagContractor): ThriftContractor {
        val thriftContractor = ThriftContractor()
        val swagContractorType = value.contractorType
        when (swagContractorType.contractorType) {
            SwagContractorType.ContractorTypeEnum.LEGALENTITY -> {
                val swagLegalEntity = swagContractorType as SwagLegalEntity
                thriftContractor.legalEntity = claimLegalEntityConverter.convertToThrift(swagLegalEntity)
            }
            SwagContractorType.ContractorTypeEnum.PRIVATEENTITY -> {
                val swagPrivateEntity = swagContractorType as SwagPrivateEntity
                thriftContractor.privateEntity = privateEntityConverter.convertToThrift(swagPrivateEntity)
            }
            SwagContractorType.ContractorTypeEnum.REGISTEREDUSER -> {
                val swagRegisteredUser = swagContractorType as SwagRegisteredUser
                thriftContractor.registeredUser = ThriftRegisteredUser().setEmail(swagRegisteredUser.email)
            }
            else -> throw IllegalArgumentException("Unknown contractor type: $swagContractorType")
        }
        return thriftContractor
    }

    override fun convertToSwag(value: ThriftContractor): SwagContractor {
        val swagContractor = SwagContractor()

        when {
            value.isSetLegalEntity -> {
                swagContractor.contractorType = claimLegalEntityConverter.convertToSwag(value.legalEntity)
            }
            value.isSetPrivateEntity -> {
                val thriftPrivateEntity = value.privateEntity
                if (thriftPrivateEntity.isSetRussianPrivateEntity) {
                    swagContractor.contractorType = privateEntityConverter.convertToSwag(thriftPrivateEntity)
                } else {
                    throw IllegalArgumentException("Unknown private entity type!")
                }
            }
            value.isSetRegisteredUser -> {
                val swagRegisteredUser = SwagRegisteredUser().apply {
                    contractorType = SwagContractorType.ContractorTypeEnum.REGISTEREDUSER
                    email = value.registeredUser.getEmail()
                }
                swagContractor.contractorType = swagRegisteredUser
            }
            else -> {
                throw IllegalArgumentException("Unknown contractor type!")
            }
        }
        swagContractor.contractorModificationType = SwagContractorModification.ContractorModificationTypeEnum.CONTRACTOR
        return swagContractor
    }
}
