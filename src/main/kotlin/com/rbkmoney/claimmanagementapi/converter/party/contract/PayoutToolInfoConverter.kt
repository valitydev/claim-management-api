package com.rbkmoney.claimmanagementapi.converter.party.contract

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.damsel.domain.InternationalBankAccount
import dev.vality.damsel.domain.PaymentInstitutionAccount
import org.springframework.stereotype.Component
import dev.vality.damsel.domain.PayoutToolInfo as ThriftPayoutToolInfo
import dev.vality.damsel.domain.WalletInfo as ThriftWalletInfo
import dev.vality.swag.claim_management.model.InternationalBankAccount as SwagInternationalBankAccount
import dev.vality.swag.claim_management.model.PaymentInstitutionAccount as SwagPaymentInstitutionAccount
import dev.vality.swag.claim_management.model.PayoutToolInfo as SwagPayoutToolInfo
import dev.vality.swag.claim_management.model.RussianBankAccount as SwagRussianBankAccount
import dev.vality.swag.claim_management.model.WalletInfo as SwagWalletInfo

@Component
class PayoutToolInfoConverter(
    private val internationalBankAccountConverter: InternationalBankAccountConverter,
    private val claimRussianBankAccountConverter: ClaimRussianBankAccountConverter
) : DarkApiConverter<ThriftPayoutToolInfo, SwagPayoutToolInfo> {

    override fun convertToThrift(value: SwagPayoutToolInfo): ThriftPayoutToolInfo {
        val thriftPayoutToolInfo = ThriftPayoutToolInfo()
        when (value.payoutToolType) {
            SwagPayoutToolInfo.PayoutToolTypeEnum.RUSSIANBANKACCOUNT -> {
                val swagRussianBankAccount = value as SwagRussianBankAccount
                thriftPayoutToolInfo.russianBankAccount =
                    claimRussianBankAccountConverter.convertToThrift(swagRussianBankAccount)
            }

            SwagPayoutToolInfo.PayoutToolTypeEnum.INTERNATIONALBANKACCOUNT -> {
                val swagInternationalBankAccount = value as SwagInternationalBankAccount
                val internationalBankAccount: InternationalBankAccount =
                    internationalBankAccountConverter.convertToThrift(swagInternationalBankAccount)
                thriftPayoutToolInfo.internationalBankAccount = internationalBankAccount
            }

            SwagPayoutToolInfo.PayoutToolTypeEnum.WALLETINFO -> {
                val swagWalletInfo = value as SwagWalletInfo
                thriftPayoutToolInfo.walletInfo = ThriftWalletInfo().setWalletId(swagWalletInfo.walletID)
            }

            SwagPayoutToolInfo.PayoutToolTypeEnum.PAYMENTINSTITUTIONACCOUNT -> {
                thriftPayoutToolInfo.paymentInstitutionAccount = PaymentInstitutionAccount()
            }

            else -> throw IllegalArgumentException("Unknown payout tool type: ${value.payoutToolType}")
        }
        return thriftPayoutToolInfo
    }

    override fun convertToSwag(value: ThriftPayoutToolInfo): SwagPayoutToolInfo {
        return when {
            value.isSetRussianBankAccount -> {
                val russianBankAccount = value.russianBankAccount
                claimRussianBankAccountConverter.convertToSwag(russianBankAccount)
            }

            value.isSetInternationalBankAccount -> {
                val internationalBankAccount = value.internationalBankAccount
                internationalBankAccountConverter.convertToSwag(internationalBankAccount)
            }

            value.isSetWalletInfo -> {
                SwagWalletInfo().apply {
                    payoutToolType = SwagPayoutToolInfo.PayoutToolTypeEnum.WALLETINFO
                    walletID = value.walletInfo.walletId
                }
            }

            value.isSetPaymentInstitutionAccount -> {
                SwagPaymentInstitutionAccount().apply {
                    payoutToolType = SwagPayoutToolInfo.PayoutToolTypeEnum.PAYMENTINSTITUTIONACCOUNT
                }
            }

            else -> throw IllegalArgumentException("Unknown payout tool type: $value!")
        }
    }
}
