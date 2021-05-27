package com.rbkmoney.claimmanagementapi.converter.party.contractor

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.damsel.domain.ContactInfo
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.domain.PrivateEntity as ThriftPrivateEntity
import com.rbkmoney.damsel.domain.RussianPrivateEntity as ThriftRussianPrivateEntity
import com.rbkmoney.swag.claim_management.model.ContactInfo as SwagContactInfo
import com.rbkmoney.swag.claim_management.model.ContractorType as SwagContractorType
import com.rbkmoney.swag.claim_management.model.PrivateEntity as SwagPrivateEntity

@Component
class PrivateEntityConverter :
    DarkApiConverter<ThriftPrivateEntity, SwagPrivateEntity> {

    override fun convertToThrift(value: SwagPrivateEntity): ThriftPrivateEntity {
        val contactInfo = ContactInfo()
            .setEmail(value.contactInfo.email)
            .setPhoneNumber(value.contactInfo.phoneNumber)
        val russianPrivateEntity = ThriftRussianPrivateEntity()
            .setFirstName(value.firstName)
            .setMiddleName(value.middleName)
            .setSecondName(value.secondName)
            .setContactInfo(contactInfo)
        return ThriftPrivateEntity().apply { this.russianPrivateEntity = russianPrivateEntity }
    }

    override fun convertToSwag(value: ThriftPrivateEntity): SwagPrivateEntity {
        val thriftRussianPrivateEntity = value.russianPrivateEntity
        val thriftContactInfo = thriftRussianPrivateEntity.contactInfo
        val swagContactInfo = SwagContactInfo().apply {
            email = thriftContactInfo.getEmail()
            phoneNumber = thriftContactInfo.phoneNumber
        }
        return SwagPrivateEntity().apply {
            contractorType = SwagContractorType.ContractorTypeEnum.PRIVATEENTITY
            firstName = thriftRussianPrivateEntity.firstName
            secondName = thriftRussianPrivateEntity.secondName
            middleName = thriftRussianPrivateEntity.middleName
            contactInfo = swagContactInfo
        }
    }
}
