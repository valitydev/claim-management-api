package com.rbkmoney.claimmanagementapi.converter.party.data

import com.rbkmoney.damsel.domain.Residence
import com.rbkmoney.swag.claim_management.model.ContractModification.ContractModificationTypeEnum
import com.rbkmoney.swag.claim_management.model.ContractPayoutToolModification.PayoutToolModificationTypeEnum
import com.rbkmoney.swag.claim_management.model.PartyModificationType.PartyModificationTypeEnum
import com.rbkmoney.swag.claim_management.model.PayoutToolInfo.PayoutToolTypeEnum
import com.rbkmoney.swag.claim_management.model.RepresentativeDocument.DocumentTypeEnum
import io.github.benas.randombeans.api.EnhancedRandom
import com.rbkmoney.damsel.claim_management.ContractModification as ThriftContractModification
import com.rbkmoney.damsel.claim_management.ContractModificationUnit as ThriftContractModificationUnit
import com.rbkmoney.damsel.domain.InternationalBankAccount as ThriftInternationalBankAccount
import com.rbkmoney.damsel.domain.InternationalBankDetails as ThriftInternationalBankDetails
import com.rbkmoney.damsel.domain.LegalAgreement as ThriftLegalAgreement
import com.rbkmoney.swag.claim_management.model.ArticlesOfAssociation as SwagArticlesOfAssociation
import com.rbkmoney.swag.claim_management.model.ContractModificationUnit as SwagContractModificationUnit
import com.rbkmoney.swag.claim_management.model.ContractPayoutToolCreationModification as SwagContractPayoutToolCreationModification
import com.rbkmoney.swag.claim_management.model.ContractPayoutToolInfoModification as SwagContractPayoutToolInfoModification
import com.rbkmoney.swag.claim_management.model.ContractPayoutToolModificationUnit as SwagContractPayoutToolModificationUnit
import com.rbkmoney.swag.claim_management.model.ContractTerminationModification as SwagContractTerminationModification
import com.rbkmoney.swag.claim_management.model.InternationalBankAccount as SwagInternationalBankAccount
import com.rbkmoney.swag.claim_management.model.PowerOfAttorney as SwagPowerOfAttorney
import com.rbkmoney.swag.claim_management.model.RepresentativeDocument as SwagRepresentativeDocument
import com.rbkmoney.swag.claim_management.model.RussianBankAccount as SwagRussianBankAccount

object TestContractData {

    val testThriftInternationalBankAccount: ThriftInternationalBankAccount
        get() = ThriftInternationalBankAccount()
            .setNumber("123456")
            .setIban("54321")
            .setAccountHolder("holder")
            .setBank(
                ThriftInternationalBankDetails()
                    .setAbaRtn("rtn")
                    .setAddress("addr")
                    .setBic("bic123456")
                    .setCountry(Residence.AGO)
                    .setName("some name")
            )
            .setCorrespondentAccount(
                ThriftInternationalBankAccount()
                    .setNumber("3332211")
                    .setIban("123322")
                    .setAccountHolder("123123")
                    .setBank(
                        ThriftInternationalBankDetails()
                            .setAbaRtn("21312")
                            .setAddress("qweqwe")
                            .setBic("321321")
                            .setCountry(Residence.ALB)
                            .setName("aasdasd")
                    )
            )

    val testSwagInternationalBankAccount: SwagInternationalBankAccount
        get() =
            EnhancedRandom.random(
                SwagInternationalBankAccount::class.java
            ).apply {
                payoutToolType = PayoutToolTypeEnum.INTERNATIONALBANKACCOUNT
                bank.country = "RUS"
                correspondentAccount.bank.country = "RUS"
            }

    val testSwagPayoutToolModificationUnit: SwagContractPayoutToolModificationUnit
        get() {
            val swagPayoutToolModificationUnit = EnhancedRandom.random(
                SwagContractPayoutToolModificationUnit::class.java
            ).apply {
                contractModificationType =
                    ContractModificationTypeEnum.CONTRACTPAYOUTTOOLMODIFICATIONUNIT
            }
            when (swagPayoutToolModificationUnit.modification.payoutToolModificationType) {
                PayoutToolModificationTypeEnum.CONTRACTPAYOUTTOOLCREATIONMODIFICATION -> {
                    val swagRussianBankAccount = EnhancedRandom.random(
                        SwagRussianBankAccount::class.java
                    ).apply { payoutToolType = PayoutToolTypeEnum.RUSSIANBANKACCOUNT }

                    val swagPayoutToolParams = EnhancedRandom.random(
                        SwagContractPayoutToolCreationModification::class.java
                    ).apply {
                        payoutToolModificationType =
                            PayoutToolModificationTypeEnum.CONTRACTPAYOUTTOOLCREATIONMODIFICATION
                        toolInfo = swagRussianBankAccount
                    }
                    swagPayoutToolModificationUnit.modification = swagPayoutToolParams
                }
                PayoutToolModificationTypeEnum.CONTRACTPAYOUTTOOLINFOMODIFICATION -> {
                    val swagInternationalBankAccount = EnhancedRandom.random(
                        SwagInternationalBankAccount::class.java
                    ).apply {
                        payoutToolType = PayoutToolTypeEnum.INTERNATIONALBANKACCOUNT
                        bank.country = "RUS"
                        correspondentAccount.bank.country = "RUS"
                    }
                    val contractPayoutToolInfoModification = EnhancedRandom.random(
                        SwagContractPayoutToolInfoModification::class.java
                    ).apply {
                        payoutToolInfo = swagInternationalBankAccount
                        payoutToolModificationType = PayoutToolModificationTypeEnum.CONTRACTPAYOUTTOOLINFOMODIFICATION
                    }
                    swagPayoutToolModificationUnit.modification = contractPayoutToolInfoModification
                }
                else -> throw IllegalArgumentException("Unknown PayoutTool modification type!")
            }
            return swagPayoutToolModificationUnit
        }

    val testSwagContractModificationUnit: SwagContractModificationUnit
        get() {
            val swagContractTerm = SwagContractTerminationModification()
                .apply {
                    contractModificationType = ContractModificationTypeEnum.CONTRACTTERMINATIONMODIFICATION
                    reason = "some reason!"
                }
            return SwagContractModificationUnit()
                .apply {
                    id = "123"
                    partyModificationType = PartyModificationTypeEnum.CONTRACTMODIFICATIONUNIT
                    modification = swagContractTerm
                }
        }

    val testThriftContractModificationUnit: ThriftContractModificationUnit
        get() {
            val legalAgreement = ThriftLegalAgreement()
                .setLegalAgreementId("111")
                .setSignedAt("222")
                .setValidUntil("333")
            val contractModification = ThriftContractModification().apply { legalAgreementBinding = legalAgreement }

            return ThriftContractModificationUnit("123", contractModification)
        }

    fun prepareSwagDocument(swagRepresentativeDocument: SwagRepresentativeDocument): SwagRepresentativeDocument =
        when (swagRepresentativeDocument.documentType) {
            DocumentTypeEnum.ARTICLESOFASSOCIATION -> {
                SwagArticlesOfAssociation().apply { documentType = DocumentTypeEnum.ARTICLESOFASSOCIATION }
            }
            DocumentTypeEnum.POWEROFATTORNEY -> {
                SwagPowerOfAttorney().apply {
                    legalAgreementID = "123"
                    signedAt = "324234"
                    validUntil = "12312333"
                    documentType = DocumentTypeEnum.POWEROFATTORNEY
                }
            }
            else -> throw IllegalArgumentException("Unknown representative document type!")
        }
}
