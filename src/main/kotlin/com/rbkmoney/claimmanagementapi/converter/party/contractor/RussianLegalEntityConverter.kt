package com.rbkmoney.claimmanagementapi.converter.party.contractor

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.damsel.domain.RussianBankAccount
import org.springframework.stereotype.Component
import dev.vality.damsel.domain.RussianLegalEntity as ThriftRussianLegalEntity
import dev.vality.swag.claim_management.model.LegalEntityType as SwagLegalEntityType
import dev.vality.swag.claim_management.model.RussianBankAccount as SwagRussianBankAccount
import dev.vality.swag.claim_management.model.RussianLegalEntity as SwagRussianLegalEntity

@Component
class RussianLegalEntityConverter : DarkApiConverter<ThriftRussianLegalEntity, SwagRussianLegalEntity> {

    override fun convertToThrift(
        value: SwagRussianLegalEntity
    ): ThriftRussianLegalEntity {
        val swagRussianBankAccount = value.russianBankAccount
        val thriftRussianBankAccount = RussianBankAccount()
            .setBankName(swagRussianBankAccount.bankName)
            .setBankBik(swagRussianBankAccount.bankBik)
            .setAccount(swagRussianBankAccount.account)
            .setBankPostAccount(swagRussianBankAccount.bankPostAccount)
        return ThriftRussianLegalEntity()
            .setActualAddress(value.actualAddress)
            .setInn(value.inn)
            .setPostAddress(value.postAddress)
            .setRegisteredName(value.registeredName)
            .setRegisteredNumber(value.registeredNumber)
            .setRepresentativeDocument(value.representativeDocument)
            .setRepresentativeFullName(value.representativeFullName)
            .setRepresentativePosition(value.representativePosition)
            .setRussianBankAccount(thriftRussianBankAccount)
    }

    override fun convertToSwag(
        value: ThriftRussianLegalEntity
    ): SwagRussianLegalEntity {
        val russianBankAccount = value.russianBankAccount
        val swagRussianBankAccount = SwagRussianBankAccount().apply {
            account = russianBankAccount.getAccount()
            bankName = russianBankAccount.bankName
            bankBik = russianBankAccount.bankBik
            bankPostAccount = russianBankAccount.bankPostAccount
        }
        return SwagRussianLegalEntity().apply {
            legalEntityType = SwagLegalEntityType.LegalEntityTypeEnum.RUSSIANLEGALENTITY
            actualAddress = value.actualAddress
            representativePosition = value.representativePosition
            inn = value.getInn()
            postAddress = value.postAddress
            registeredName = value.registeredName
            registeredNumber = value.registeredNumber
            representativeDocument = value.representativeDocument
            representativeFullName = value.representativeFullName
            this.russianBankAccount = swagRussianBankAccount
        }
    }
}
