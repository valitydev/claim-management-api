package com.rbkmoney.claimmanagementapi.converter.party.contract

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.damsel.domain.CountryCode
import dev.vality.damsel.domain.InternationalBankDetails
import dev.vality.swag.claim_management.model.CorrespondentAccount
import dev.vality.swag.claim_management.model.PayoutToolInfo
import org.springframework.stereotype.Component
import dev.vality.damsel.domain.InternationalBankAccount as ThriftInternationalBankAccount
import dev.vality.swag.claim_management.model.CorrespondentBankDetails as SwagCorrespondentBankDetails
import dev.vality.swag.claim_management.model.InternationalBankAccount as SwagInternationalBankAccount
import dev.vality.swag.claim_management.model.InternationalBankDetails as SwagInternationalBankDetails

@Component
class InternationalBankAccountConverter :
    DarkApiConverter<ThriftInternationalBankAccount, SwagInternationalBankAccount> {

    override fun convertToThrift(value: SwagInternationalBankAccount): ThriftInternationalBankAccount {
        val internationalBankDetails = value.bank?.let {
            InternationalBankDetails()
                .setAbaRtn(it.abaRtn)
                .setAddress(it.address)
                .setBic(it.bic)
                .setName(it.name)
                .setCountry(convertCountryToResidence(it.country))
        }
        val internationalBankAccount = ThriftInternationalBankAccount()
            .setAccountHolder(value.accountHolder)
            .setIban(value.iban)
            .setNumber(value.number)
            .setBank(internationalBankDetails)

        val swagCorrespondentAccount = value.correspondentAccount
        swagCorrespondentAccount?.let {
            val correspondentAccount = ThriftInternationalBankAccount()
                .setAccountHolder(swagCorrespondentAccount.accountHolder)
                .setIban(swagCorrespondentAccount.iban)
                .setNumber(swagCorrespondentAccount.number)
            swagCorrespondentAccount.bank?.let {
                correspondentAccount.setBank(
                    InternationalBankDetails()
                        .setAbaRtn(it.abaRtn)
                        .setAddress(it.address)
                        .setBic(it.bic)
                        .setName(it.name)
                        .setCountry(CountryCode.valueOf(it.country))
                )
                internationalBankAccount.correspondentAccount = correspondentAccount
            }
        }
        return internationalBankAccount
    }

    override fun convertToSwag(value: ThriftInternationalBankAccount): SwagInternationalBankAccount {
        val swagInternationalBankAccount = SwagInternationalBankAccount().apply {
            payoutToolType = PayoutToolInfo.PayoutToolTypeEnum.INTERNATIONALBANKACCOUNT
            accountHolder = value.accountHolder
            number = value.getNumber()
            iban = value.getIban()
        }
        if (value.isSetBank) {
            val thriftBank = value.bank
            val swagBank = SwagInternationalBankDetails().apply {
                abaRtn = thriftBank.abaRtn
                address = thriftBank.address
                bic = thriftBank.bic
                name = thriftBank.name
                country = thriftBank.country?.name
            }
            swagInternationalBankAccount.bank = swagBank
        }
        if (value.isSetCorrespondentAccount) {
            val thriftCorrespondentAccount = value.correspondentAccount
            val swagCorrespondentAccount = CorrespondentAccount().apply {
                accountHolder = thriftCorrespondentAccount.accountHolder
                iban = thriftCorrespondentAccount.getIban()
                number = thriftCorrespondentAccount.getNumber()
            }
            if (thriftCorrespondentAccount.isSetBank) {
                val thriftCorrespondentAccountBank = thriftCorrespondentAccount.getBank()
                val swagCorrespondentBankDetails = SwagCorrespondentBankDetails().apply {
                    abaRtn = thriftCorrespondentAccountBank.abaRtn
                    address = thriftCorrespondentAccountBank.address
                    bic = thriftCorrespondentAccountBank.bic
                    name = thriftCorrespondentAccountBank.name
                    country = thriftCorrespondentAccountBank.country?.name
                }
                swagCorrespondentAccount.bank = swagCorrespondentBankDetails
            }
            swagInternationalBankAccount.correspondentAccount = swagCorrespondentAccount
        }
        return swagInternationalBankAccount
    }

    private fun convertCountryToResidence(country: String?) =
        country?.let { CountryCode.valueOf(it) }
}
