package com.rbkmoney.claimmanagementapi.converter.party

import com.rbkmoney.claimmanagementapi.converter.party.contract.*
import com.rbkmoney.claimmanagementapi.converter.party.data.TestContractData.prepareSwagDocument
import com.rbkmoney.claimmanagementapi.converter.party.data.TestContractData.testSwagContractModificationUnit
import com.rbkmoney.claimmanagementapi.converter.party.data.TestContractData.testSwagInternationalBankAccount
import com.rbkmoney.claimmanagementapi.converter.party.data.TestContractData.testSwagPayoutToolModificationUnit
import com.rbkmoney.claimmanagementapi.converter.party.data.TestContractData.testThriftContractModificationUnit
import com.rbkmoney.claimmanagementapi.converter.party.data.TestContractData.testThriftInternationalBankAccount
import dev.vality.geck.serializer.kit.mock.MockMode
import dev.vality.geck.serializer.kit.mock.MockTBaseProcessor
import dev.vality.geck.serializer.kit.tbase.TBaseHandler
import dev.vality.swag.claim_management.model.ArticlesOfAssociation
import io.github.benas.randombeans.api.EnhancedRandom
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import dev.vality.damsel.claim_management.ContractAdjustmentModificationUnit as ThriftContractAdjustmentModificationUnit
import dev.vality.damsel.claim_management.ContractParams as ThriftContractParams
import dev.vality.damsel.claim_management.PayoutToolModification as ThriftPayoutToolModification
import dev.vality.damsel.claim_management.PayoutToolModificationUnit as ThriftPayoutToolModificationUnit
import dev.vality.damsel.claim_management.PayoutToolParams as ThriftPayoutToolParams
import dev.vality.damsel.domain.CurrencyRef as ThriftCurrencyRef
import dev.vality.damsel.domain.InternationalBankAccount as ThriftInternationalBankAccount
import dev.vality.damsel.domain.LegalAgreement as ThriftLegalAgreement
import dev.vality.damsel.domain.PayoutToolInfo as ThriftPayoutToolInfo
import dev.vality.damsel.domain.ReportPreferences as ThriftReportPreferences
import dev.vality.damsel.domain.RepresentativeDocument as ThriftRepresentativeDocument
import dev.vality.damsel.domain.RussianBankAccount as ThriftRussianBankAccount
import dev.vality.swag.claim_management.model.ContractAdjustmentModificationUnit as SwagContractAdjustmentModificationUnit
import dev.vality.swag.claim_management.model.ContractCreationModification as SwagContractCreationModification
import dev.vality.swag.claim_management.model.ContractLegalAgreementBindingModification as SwagContractLegalAgreementBindingModification
import dev.vality.swag.claim_management.model.ContractModification as SwagContractModification
import dev.vality.swag.claim_management.model.ContractReportPreferencesModification as SwagContractReportPreferencesModification
import dev.vality.swag.claim_management.model.PayoutToolInfo as SwagPayoutToolInfo
import dev.vality.swag.claim_management.model.RepresentativeDocument as SwagRepresentativeDocument
import dev.vality.swag.claim_management.model.RussianBankAccount as SwagRussianBankAccount

class ContractConvertersTest {

    @Test
    fun russianBankAccountConverterTest() {
        val converter = ClaimRussianBankAccountConverter()
        val swagRussianBankAccount = EnhancedRandom.random(
            SwagRussianBankAccount::class.java
        ).apply {
            payoutToolType = SwagPayoutToolInfo.PayoutToolTypeEnum.RUSSIANBANKACCOUNT
        }
        val resultSwagRussianBankAccount = converter.convertToSwag(converter.convertToThrift(swagRussianBankAccount))
        assertEquals(
            swagRussianBankAccount, resultSwagRussianBankAccount,
            "Swag objects 'RussianBankAccount' not equals"
        )
        var thriftRussianBankAccount = MockTBaseProcessor(MockMode.ALL)
            .process(ThriftRussianBankAccount(), TBaseHandler(ThriftRussianBankAccount::class.java))
        var resultThriftRussianBankAccount =
            converter.convertToThrift(converter.convertToSwag(thriftRussianBankAccount))
        assertEquals(
            thriftRussianBankAccount, resultThriftRussianBankAccount,
            "Thrift objects 'RussianBankAccount' (MockMode.ALL) not equals"
        )
        thriftRussianBankAccount = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(thriftRussianBankAccount, TBaseHandler(ThriftRussianBankAccount::class.java))
        resultThriftRussianBankAccount = converter.convertToThrift(converter.convertToSwag(thriftRussianBankAccount))
        assertEquals(
            thriftRussianBankAccount, resultThriftRussianBankAccount,
            "Thrift objects 'RussianBankAccount' (MockMode.REQUIRED_ONLY) not equals"
        )
    }

    @Test
    fun internationalBankAccountConverterTest() {
        val converter = InternationalBankAccountConverter()
        val swagInternationalBankAccount = testSwagInternationalBankAccount
        val resultSwagInternationalBankAccount =
            converter.convertToSwag(converter.convertToThrift(swagInternationalBankAccount))
        assertEquals(
            swagInternationalBankAccount, resultSwagInternationalBankAccount,
            "Swag objects 'InternationalBankAccountConverter' not equals"
        )
        val thriftInternationalBankAccount = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(ThriftInternationalBankAccount(), TBaseHandler(ThriftInternationalBankAccount::class.java))
        var resultInternationalBankAccount = converter.convertToThrift(
            converter.convertToSwag(thriftInternationalBankAccount)
        )
        assertEquals(
            thriftInternationalBankAccount, resultInternationalBankAccount,
            "Thrift objects 'InternationalBankAccountConverter' (MockMode.REQUIRED_ONLY) not equals"
        )
        resultInternationalBankAccount = converter.convertToThrift(
            converter.convertToSwag(testThriftInternationalBankAccount)
        )
        assertEquals(
            testThriftInternationalBankAccount, resultInternationalBankAccount,
            "Thrift objects 'InternationalBankAccountConverter' (MockMode.ALL) not equals"
        )
    }

    @Test
    fun representativeDocumentConverterTest() {
        val converter = RepresentativeDocumentConverter()
        val swagRepresentativeDocument = prepareSwagDocument(
            EnhancedRandom.random(SwagRepresentativeDocument::class.java)
        )
        val resultSwagRepresentativeDocument = converter.convertToSwag(
            converter.convertToThrift(swagRepresentativeDocument)
        )
        assertEquals(
            swagRepresentativeDocument, resultSwagRepresentativeDocument,
            "Swag objects 'RepresentativeDocument' not equals"
        )
        var thriftRepresentativeDocument = MockTBaseProcessor(MockMode.ALL)
            .process(ThriftRepresentativeDocument(), TBaseHandler(ThriftRepresentativeDocument::class.java))
        val resultRepresentativeDocumentAllFields: ThriftRepresentativeDocument = converter.convertToThrift(
            converter.convertToSwag(thriftRepresentativeDocument)
        )
        assertEquals(
            thriftRepresentativeDocument, resultRepresentativeDocumentAllFields,
            "Thrift objects 'RepresentativeDocument' (MockMode.ALL) not equals"
        )
        thriftRepresentativeDocument = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(thriftRepresentativeDocument, TBaseHandler(ThriftRepresentativeDocument::class.java))
        val resultRepresentativeDocumentReqFields: ThriftRepresentativeDocument = converter.convertToThrift(
            converter.convertToSwag(thriftRepresentativeDocument)
        )
        assertEquals(
            thriftRepresentativeDocument, resultRepresentativeDocumentReqFields,
            "Thrift objects 'RepresentativeDocument' (MockMode.ALL) not equals"
        )
    }

    @Test
    fun legalAgreementConverterTest() {
        val converter = LegalAgreementConverter()
        val swagLegalAgreement = EnhancedRandom.random(SwagContractLegalAgreementBindingModification::class.java)
            .apply {
                contractModificationType =
                    SwagContractModification.ContractModificationTypeEnum.CONTRACTLEGALAGREEMENTBINDINGMODIFICATION
            }
        val resultSwagLegalAgreement = converter.convertToSwag(converter.convertToThrift(swagLegalAgreement))
        assertEquals(swagLegalAgreement, resultSwagLegalAgreement, "Swag objects 'LegalAgreement' not equals")
        var thriftLegalAgreement = MockTBaseProcessor(MockMode.ALL)
            .process(ThriftLegalAgreement(), TBaseHandler(ThriftLegalAgreement::class.java))
        val resultThriftLegalAgreement: ThriftLegalAgreement = converter.convertToThrift(
            converter.convertToSwag(thriftLegalAgreement)
        )
        assertEquals(
            thriftLegalAgreement, resultThriftLegalAgreement,
            "Thrift objects 'LegalAgreement' (MockMode.ALL) not equals"
        )
        thriftLegalAgreement = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(thriftLegalAgreement, TBaseHandler(ThriftLegalAgreement::class.java))
        val resultThriftLegalAgreementReq = converter.convertToThrift(converter.convertToSwag(thriftLegalAgreement))
        assertEquals(
            thriftLegalAgreement, resultThriftLegalAgreementReq,
            "Thrift objects 'LegalAgreement' (MockMode.REQUIRED_ONLY) not equals"
        )
    }

    @Test
    fun reportPreferencesConverterTest() {
        val converter = ContractReportPreferencesModificationConverter(RepresentativeDocumentConverter())
        val articlesOfAssociation = ArticlesOfAssociation()
            .apply { documentType = SwagRepresentativeDocument.DocumentTypeEnum.ARTICLESOFASSOCIATION }
        val swagReportPreferences = EnhancedRandom.random(SwagContractReportPreferencesModification::class.java)
            .apply {
                contractModificationType =
                    SwagContractModification.ContractModificationTypeEnum.CONTRACTREPORTPREFERENCESMODIFICATION
                reportPreferences.serviceAcceptanceActPreferences.signer.document = articlesOfAssociation
            }
        val resultSwagReportPreferences = converter.convertToSwag(converter.convertToThrift(swagReportPreferences))
        assertEquals(
            swagReportPreferences, resultSwagReportPreferences,
            "Swag objects 'ReportPreferences' not equals"
        )
        var thriftReportPreferences = MockTBaseProcessor(MockMode.ALL)
            .process(ThriftReportPreferences(), TBaseHandler(ThriftReportPreferences::class.java))
        val resultThriftReportPreferences =
            converter.convertToThrift(converter.convertToSwag(thriftReportPreferences))
        assertEquals(
            thriftReportPreferences, resultThriftReportPreferences,
            "Thrift objects 'ReportPreferences' (MockMode.ALL) not equals"
        )
        thriftReportPreferences = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(thriftReportPreferences, TBaseHandler(ThriftReportPreferences::class.java))
        val resultReportPreferencesReq = converter.convertToThrift(converter.convertToSwag(thriftReportPreferences))
        assertEquals(
            thriftReportPreferences, resultReportPreferencesReq,
            "Thrift objects 'ReportPreferences' (MockMode.ALL) not equals"
        )
    }

    @Test
    fun payoutToolModificationUnitConverterTest() {
        val converter = PayoutToolModificationUnitConverter(
            PayoutToolInfoConverter(InternationalBankAccountConverter(), ClaimRussianBankAccountConverter())
        )
        val swagPayoutToolModificationUnit = testSwagPayoutToolModificationUnit
        val tmpThriftPayoutToolModificationUnit = converter.convertToThrift(swagPayoutToolModificationUnit)
        val resultSwagPayoutToolModificationUnit = converter.convertToSwag(tmpThriftPayoutToolModificationUnit)
        assertEquals(
            swagPayoutToolModificationUnit, resultSwagPayoutToolModificationUnit,
            "Swag objects 'PayoutToolModificationUnit' not equals"
        )
        val tmpSwagPayoutToolModificationUnitAll = converter.convertToSwag(testThriftPayoutToolModificationUnit)
        val resultPayoutToolModificationUnitAll = converter.convertToThrift(tmpSwagPayoutToolModificationUnitAll)
        assertEquals(
            testThriftPayoutToolModificationUnit, resultPayoutToolModificationUnitAll,
            "Thrift objects 'PayoutToolModificationUnit' (MockMode.ALL) not equals"
        )
    }

    @Test
    @RepeatedTest(10)
    fun payoutToolModificationUnitThriftRandomConverterTest() {
        val converter = PayoutToolModificationUnitConverter(
            PayoutToolInfoConverter(InternationalBankAccountConverter(), ClaimRussianBankAccountConverter())
        )
        val thriftPayoutToolModificationUnit = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(ThriftPayoutToolModificationUnit(), TBaseHandler(ThriftPayoutToolModificationUnit::class.java))
        if ((thriftPayoutToolModificationUnit.getModification().isSetCreation
                    && !thriftPayoutToolModificationUnit.getModification().creation.toolInfo.isSetDummyAccount)
            || (thriftPayoutToolModificationUnit.getModification().isSetInfoModification
            && !thriftPayoutToolModificationUnit.getModification().infoModification.isSetDummyAccount)) {
            val resultPayoutToolModificationUnit = converter.convertToThrift(
                converter.convertToSwag(thriftPayoutToolModificationUnit)
            )
            assertEquals(
                thriftPayoutToolModificationUnit, resultPayoutToolModificationUnit,
                "Thrift objects 'PayoutToolModificationUnit' (MockMode.REQUIRED_ONLY) not equals"
            )
        }
    }

    @Test
    fun contractModificationUnitConverterTest() {
        val converter = ContractModificationUnitConverter(
            ContractModificationCreationConverter(),
            ContractReportPreferencesModificationConverter(RepresentativeDocumentConverter()),
            PayoutToolModificationUnitConverter(
                PayoutToolInfoConverter(InternationalBankAccountConverter(), ClaimRussianBankAccountConverter())
            ),
            ContractAdjustmentModificationUnitConverter(),
            LegalAgreementConverter()
        )
        val swagContractModificationUnit = testSwagContractModificationUnit
        val tmpThriftContractModificationUnit = converter.convertToThrift(swagContractModificationUnit)
        val resultSwagContractModificationUnit = converter.convertToSwag(tmpThriftContractModificationUnit)
        assertEquals(
            swagContractModificationUnit, resultSwagContractModificationUnit,
            "Swag objects 'ContractParams' not equals"
        )
        val resultThriftContractParams = converter.convertToThrift(
            converter.convertToSwag(testThriftContractModificationUnit)
        )
        assertEquals(
            testThriftContractModificationUnit, resultThriftContractParams,
            "Thrift objects 'ContractModificationUnit' not equals"
        )
    }

    @Test
    fun contractModificationCreationConverterTest() {
        val converter = ContractModificationCreationConverter()
        val swagContractParams = EnhancedRandom.random(SwagContractCreationModification::class.java)
            .apply {
                contractModificationType =
                    SwagContractModification.ContractModificationTypeEnum.CONTRACTCREATIONMODIFICATION
            }
        val resultSwagContractParams = converter.convertToSwag(converter.convertToThrift(swagContractParams))
        assertEquals(swagContractParams, resultSwagContractParams, "Swag objects 'ContractParams' not equals")
        var thriftContractParams = MockTBaseProcessor(MockMode.ALL)
            .process(ThriftContractParams(), TBaseHandler(ThriftContractParams::class.java))
        val resultThriftContractParams = converter.convertToThrift(converter.convertToSwag(thriftContractParams))
        assertEquals(
            thriftContractParams, resultThriftContractParams,
            "Thrift objects 'ContractParams' (MockMode.ALL) not equals"
        )
        thriftContractParams = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(thriftContractParams, TBaseHandler(ThriftContractParams::class.java))
        val resultThriftContractParamsReq = converter.convertToThrift(converter.convertToSwag(thriftContractParams))
        assertEquals(
            thriftContractParams, resultThriftContractParamsReq,
            "Thrift objects 'ContractParams' (MockMode.REQUIRED_ONLY) not equals"
        )
    }

    @Test
    fun contractAdjustmentModificationUnitConverterTest() {
        val converter = ContractAdjustmentModificationUnitConverter()
        val swagContractAdjustmentModificationUnit =
            EnhancedRandom.random(SwagContractAdjustmentModificationUnit::class.java).apply {
                contractModificationType =
                    SwagContractModification.ContractModificationTypeEnum.CONTRACTADJUSTMENTMODIFICATIONUNIT
            }
        val tmpThriftContractAdjustmentModificationUnit =
            converter.convertToThrift(swagContractAdjustmentModificationUnit)
        val resultSwagContractAdjustmentModificationUnit =
            converter.convertToSwag(tmpThriftContractAdjustmentModificationUnit)
        assertEquals(
            swagContractAdjustmentModificationUnit, resultSwagContractAdjustmentModificationUnit,
            "Swag objects 'ContractAdjustmentModificationUnit' not equals"
        )
        var thriftContractAdjustmentModificationUnit = MockTBaseProcessor(MockMode.ALL)
            .process(
                ThriftContractAdjustmentModificationUnit(),
                TBaseHandler(ThriftContractAdjustmentModificationUnit::class.java)
            )
        val resultThriftContractAdjustmentModificationUnit =
            converter.convertToThrift(converter.convertToSwag(thriftContractAdjustmentModificationUnit))
        assertEquals(
            thriftContractAdjustmentModificationUnit, resultThriftContractAdjustmentModificationUnit,
            "Thrift objects 'ContractAdjustmentModificationUnit' (MockMode.ALL) not equals"
        )
        thriftContractAdjustmentModificationUnit = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(
                thriftContractAdjustmentModificationUnit,
                TBaseHandler(ThriftContractAdjustmentModificationUnit::class.java)
            )
        val resultThriftContractAdjustmentModificationUnitReq =
            converter.convertToThrift(converter.convertToSwag(thriftContractAdjustmentModificationUnit))
        assertEquals(
            thriftContractAdjustmentModificationUnit, resultThriftContractAdjustmentModificationUnitReq,
            "Thrift objects 'ContractAdjustmentModificationUnit' (MockMode.REQUIRED_ONLY) not equals"
        )
    }

    companion object {
        private val testThriftPayoutToolModificationUnit: ThriftPayoutToolModificationUnit
            get() {
                val payoutToolInfo = ThriftPayoutToolInfo()
                    .apply { internationalBankAccount = testThriftInternationalBankAccount }
                val payoutToolModification = ThriftPayoutToolModification()
                    .apply {
                        creation = ThriftPayoutToolParams()
                            .setCurrency(ThriftCurrencyRef().setSymbolicCode("RUB"))
                            .setToolInfo(payoutToolInfo)
                    }
                return ThriftPayoutToolModificationUnit()
                    .setPayoutToolId("toolID")
                    .setModification(payoutToolModification)
            }
    }
}
