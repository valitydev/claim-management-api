package com.rbkmoney.claimmanagementapi.converter.party.contract

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.swag.claim_management.model.PayoutToolInfo
import org.springframework.stereotype.Component
import dev.vality.damsel.domain.RussianBankAccount as ThriftRussianBankAccount
import dev.vality.swag.claim_management.model.RussianBankAccount as SwagRussianBankAccount

@Component
class ClaimRussianBankAccountConverter : DarkApiConverter<ThriftRussianBankAccount, SwagRussianBankAccount> {

    override fun convertToThrift(value: SwagRussianBankAccount) =
        ThriftRussianBankAccount().apply {
            bankName = value.bankName
            bankBik = value.bankBik
            bankPostAccount = value.bankPostAccount
            account = value.account
        }

    override fun convertToSwag(value: ThriftRussianBankAccount) =
        SwagRussianBankAccount().apply {
            payoutToolType = PayoutToolInfo.PayoutToolTypeEnum.RUSSIANBANKACCOUNT
            account = value.account
            bankName = value.bankName
            bankBik = value.bankBik
            bankPostAccount = value.bankPostAccount
        }
}
