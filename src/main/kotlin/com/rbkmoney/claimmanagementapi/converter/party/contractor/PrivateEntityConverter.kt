package com.rbkmoney.claimmanagementapi.converter.party.contractor

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.damsel.domain.ContactInfo
import org.springframework.stereotype.Component
import dev.vality.damsel.domain.PrivateEntity as ThriftPrivateEntity
import dev.vality.damsel.domain.RussianPrivateEntity as ThriftRussianPrivateEntity
import dev.vality.swag.claim_management.model.ContactInfo as SwagContactInfo
import dev.vality.swag.claim_management.model.ContractorType as SwagContractorType
import dev.vality.swag.claim_management.model.PrivateEntity as SwagPrivateEntity

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
