package com.rbkmoney.claimmanagementapi.converter.party

import com.rbkmoney.claimmanagementapi.converter.party.contractor.ClaimContractorConverter
import com.rbkmoney.claimmanagementapi.converter.party.contractor.ClaimLegalEntityConverter
import com.rbkmoney.claimmanagementapi.converter.party.contractor.ContractorIdentificationLevelConverter
import com.rbkmoney.claimmanagementapi.converter.party.contractor.ContractorModificationUnitConverter
import com.rbkmoney.claimmanagementapi.converter.party.contractor.InternationalLegalEntityConverter
import com.rbkmoney.claimmanagementapi.converter.party.contractor.PrivateEntityConverter
import com.rbkmoney.claimmanagementapi.converter.party.contractor.RussianLegalEntityConverter
import com.rbkmoney.damsel.domain.ContractorIdentificationLevel
import com.rbkmoney.damsel.domain.CountryCode
import com.rbkmoney.geck.serializer.kit.mock.MockMode
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler
import com.rbkmoney.swag.claim_management.model.RegisteredUser
import io.github.benas.randombeans.api.EnhancedRandom
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import com.rbkmoney.damsel.claim_management.ContractorModificationUnit as ThriftContractorModificationUnit
import com.rbkmoney.damsel.domain.Contractor as ThriftContractor
import com.rbkmoney.damsel.domain.InternationalLegalEntity as ThriftInternationalLegalEntity
import com.rbkmoney.damsel.domain.LegalEntity as ThriftLegalEntity
import com.rbkmoney.damsel.domain.PrivateEntity as ThriftPrivateEntity
import com.rbkmoney.damsel.domain.RussianLegalEntity as ThriftRussianLegalEntity
import com.rbkmoney.swag.claim_management.model.Contractor as SwagContractor
import com.rbkmoney.swag.claim_management.model.ContractorIdentificationLevel as SwagContractorIdentificationLevel
import com.rbkmoney.swag.claim_management.model.ContractorModification as SwagContractorModification
import com.rbkmoney.swag.claim_management.model.ContractorModificationUnit as SwagContractorModificationUnit
import com.rbkmoney.swag.claim_management.model.ContractorType as SwagContractorType
import com.rbkmoney.swag.claim_management.model.InternationalLegalEntity as SwagInternationalLegalEntity
import com.rbkmoney.swag.claim_management.model.LegalEntity as SwagLegalEntity
import com.rbkmoney.swag.claim_management.model.LegalEntityType as SwagLegalEntityType
import com.rbkmoney.swag.claim_management.model.PartyModificationType as SwagPartyModificationType
import com.rbkmoney.swag.claim_management.model.PrivateEntity as SwagPrivateEntity
import com.rbkmoney.swag.claim_management.model.RussianLegalEntity as SwagRussianLegalEntity

class ClaimContractorConvertersTest {

    @Test
    fun russianLegalEntityConverterTest() {
        val converter = RussianLegalEntityConverter()
        val swagRussianLegalEntity = EnhancedRandom.random(
            SwagRussianLegalEntity::class.java
        ).apply {
            legalEntityType = SwagLegalEntityType.LegalEntityTypeEnum.RUSSIANLEGALENTITY
            russianBankAccount.payoutToolType = null
        }
        val resultSwagRussianLegalEntity = converter.convertToSwag(
            converter.convertToThrift(swagRussianLegalEntity)
        )
        assertEquals(
            swagRussianLegalEntity, resultSwagRussianLegalEntity,
            "Swag objects 'RussianLegalEntity' not equals"
        )
        var thriftRussianLegalEntity = MockTBaseProcessor(MockMode.ALL)
            .process(
                ThriftRussianLegalEntity(), TBaseHandler(ThriftRussianLegalEntity::class.java)
            )
        var resultThriftRussianLegalEntity = converter.convertToThrift(
            converter.convertToSwag(thriftRussianLegalEntity)
        )
        assertEquals(
            thriftRussianLegalEntity, resultThriftRussianLegalEntity,
            "Thrift objects 'RussianLegalEntity' (MockMode.ALL) not equals"
        )
        thriftRussianLegalEntity = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(
                thriftRussianLegalEntity, TBaseHandler(ThriftRussianLegalEntity::class.java)
            )
        resultThriftRussianLegalEntity = converter.convertToThrift(
            converter.convertToSwag(thriftRussianLegalEntity)
        )
        assertEquals(
            thriftRussianLegalEntity, resultThriftRussianLegalEntity,
            "Thrift objects 'RussianLegalEntity' (MockMode.REQUIRED_ONLY) not equals"
        )
    }

    @Test
    fun internationalLegalEntityConverterTest() {
        val converter = InternationalLegalEntityConverter()
        val swagInternationalLegalEntity = EnhancedRandom.random(
            SwagInternationalLegalEntity::class.java
        ).apply {
            legalEntityType = SwagLegalEntityType.LegalEntityTypeEnum.INTERNATIONALLEGALENTITY
            country = CountryCode.GLP.name
        }
        val resultSwagInternationalLegalEntity = converter.convertToSwag(
            converter.convertToThrift(swagInternationalLegalEntity)
        )
        assertEquals(
            swagInternationalLegalEntity, resultSwagInternationalLegalEntity,
            "Swag objects 'InternationalLegalEntity' not equals"
        )
        var thriftInternationalLegalEntity = MockTBaseProcessor(MockMode.ALL)
            .process(
                ThriftInternationalLegalEntity(),
                TBaseHandler(ThriftInternationalLegalEntity::class.java)
            )
        var resultThriftInternationalLegalEntity = converter.convertToThrift(
            converter.convertToSwag(thriftInternationalLegalEntity)
        )
        assertEquals(
            thriftInternationalLegalEntity, resultThriftInternationalLegalEntity,
            "Thrift objects 'InternationalLegalEntity' (MockMode.ALL) not equals"
        )
        thriftInternationalLegalEntity = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(
                thriftInternationalLegalEntity,
                TBaseHandler(ThriftInternationalLegalEntity::class.java)
            )
        resultThriftInternationalLegalEntity = converter.convertToThrift(
            converter.convertToSwag(thriftInternationalLegalEntity)
        )
        assertEquals(
            thriftInternationalLegalEntity, resultThriftInternationalLegalEntity,
            "Thrift objects 'InternationalLegalEntity' (MockMode.REQUIRED_ONLY) not equals"
        )
    }

    @Test
    fun legalEntityConverterTest() {
        val converter = ClaimLegalEntityConverter(
            InternationalLegalEntityConverter(),
            RussianLegalEntityConverter()
        )
        val swagLegalEntity = EnhancedRandom.random(SwagLegalEntity::class.java)
            .apply { contractorType = SwagContractorType.ContractorTypeEnum.LEGALENTITY }
        when (swagLegalEntity.legalEntityType.legalEntityType) {
            SwagLegalEntityType.LegalEntityTypeEnum.RUSSIANLEGALENTITY -> {
                val swagRussianLegalEntity = EnhancedRandom.random(SwagRussianLegalEntity::class.java).apply {
                    legalEntityType = SwagLegalEntityType.LegalEntityTypeEnum.RUSSIANLEGALENTITY
                    russianBankAccount.payoutToolType = null
                }
                swagLegalEntity.legalEntityType = swagRussianLegalEntity
            }
            SwagLegalEntityType.LegalEntityTypeEnum.INTERNATIONALLEGALENTITY -> {
                val swagInternationalLegalEntity = EnhancedRandom.random(SwagInternationalLegalEntity::class.java)
                    .apply {
                        legalEntityType = SwagLegalEntityType.LegalEntityTypeEnum.INTERNATIONALLEGALENTITY
                        country = CountryCode.EGY.name
                    }
                swagLegalEntity.legalEntityType = swagInternationalLegalEntity
            }
            else -> throw IllegalArgumentException("Unknown legal entity type!")
        }
        val resultSwagLegalEntity = converter.convertToSwag(converter.convertToThrift(swagLegalEntity))
        assertEquals(swagLegalEntity, resultSwagLegalEntity, "Swag objects 'LegalEntity' not equals")
        var thriftLegalEntity = MockTBaseProcessor(MockMode.ALL)
            .process(ThriftLegalEntity(), TBaseHandler(ThriftLegalEntity::class.java))
        var resultThriftLegalEntity = converter.convertToThrift(converter.convertToSwag(thriftLegalEntity))
        assertEquals(
            thriftLegalEntity, resultThriftLegalEntity,
            "Thrift objects 'LegalEntity' (MockMode.ALL) not equals"
        )
        thriftLegalEntity = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(thriftLegalEntity, TBaseHandler(ThriftLegalEntity::class.java))
        resultThriftLegalEntity = converter.convertToThrift(converter.convertToSwag(thriftLegalEntity))
        assertEquals(
            thriftLegalEntity, resultThriftLegalEntity,
            "Thrift objects 'LegalEntity' (MockMode.REQUIRED_ONLY) not equals"
        )
    }

    @Test
    fun privateEntityConverterTest() {
        val converter = PrivateEntityConverter()
        val swagPrivateEntity = EnhancedRandom.random(SwagPrivateEntity::class.java)
            .apply { contractorType = SwagContractorType.ContractorTypeEnum.PRIVATEENTITY }
        val resultSwagPrivateEntity = converter.convertToSwag(converter.convertToThrift(swagPrivateEntity))
        assertEquals(swagPrivateEntity, resultSwagPrivateEntity, "Swag objects 'PrivateEntity' not equals")
        var thriftPrivateEntity = MockTBaseProcessor(MockMode.ALL)
            .process(ThriftPrivateEntity(), TBaseHandler(ThriftPrivateEntity::class.java))
        var resultThriftPrivateEntity = converter.convertToThrift(converter.convertToSwag(thriftPrivateEntity))
        assertEquals(
            thriftPrivateEntity, resultThriftPrivateEntity,
            "Thrift objects 'PrivateEntity' (MockMode.ALL) not equals"
        )
        thriftPrivateEntity = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(thriftPrivateEntity, TBaseHandler(ThriftPrivateEntity::class.java))
        resultThriftPrivateEntity = converter.convertToThrift(converter.convertToSwag(thriftPrivateEntity))
        assertEquals(
            thriftPrivateEntity, resultThriftPrivateEntity,
            "Thrift objects 'PrivateEntity' (MockMode.REQUIRED_ONLY) not equals"
        )
    }

    @Test
    fun contractorIdentificationLevelConverterTest() {
        val converter = ContractorIdentificationLevelConverter()
        val swagIdentificationLevel = testContractorIdentificationLevel
        val resultSwagIdentificationLevel = converter.convertToSwag(converter.convertToThrift(swagIdentificationLevel))
        assertEquals(
            swagIdentificationLevel, resultSwagIdentificationLevel,
            "Swag objects 'ContractorIdentificationLevel' not equals"
        )
        val resultThriftContractorIdentificationLevel = converter.convertToThrift(
            converter.convertToSwag(ContractorIdentificationLevel.full)
        )
        assertEquals(
            ContractorIdentificationLevel.full, resultThriftContractorIdentificationLevel,
            "Thrift objects 'ContractorIdentificationLevel' (MockMode.ALL) not equals"
        )
    }

    @Test
    fun contractorModificationUnitConverterTest() {
        val converter = ContractorModificationUnitConverter(
            ClaimContractorConverter(
                ClaimLegalEntityConverter(InternationalLegalEntityConverter(), RussianLegalEntityConverter()),
                PrivateEntityConverter()
            ),
            ContractorIdentificationLevelConverter()
        )
        val swagContractorModificationUnit = EnhancedRandom.random(SwagContractorModificationUnit::class.java)
            .apply {
                partyModificationType = SwagPartyModificationType.PartyModificationTypeEnum.CONTRACTORMODIFICATIONUNIT
                modification = testContractorIdentificationLevel
            }
        val resultSwagContractorModification =
            converter.convertToSwag(converter.convertToThrift(swagContractorModificationUnit))
        assertEquals(
            swagContractorModificationUnit, resultSwagContractorModification,
            "Swag objects 'ContractorModificationUnit' not equals"
        )
        var thriftContractorModificationUnit = MockTBaseProcessor(MockMode.ALL)
            .process(ThriftContractorModificationUnit(), TBaseHandler(ThriftContractorModificationUnit::class.java))
        var resultThriftContractorModificationUnit = converter.convertToThrift(
            converter.convertToSwag(thriftContractorModificationUnit)
        )
        assertEquals(
            thriftContractorModificationUnit, resultThriftContractorModificationUnit,
            "Thrift objects 'ContractorModificationUnit' (MockMode.ALL) not equals"
        )
        thriftContractorModificationUnit = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(thriftContractorModificationUnit, TBaseHandler(ThriftContractorModificationUnit::class.java))
        resultThriftContractorModificationUnit = converter.convertToThrift(
            converter.convertToSwag(thriftContractorModificationUnit)
        )
        assertEquals(
            thriftContractorModificationUnit, resultThriftContractorModificationUnit,
            "Thrift objects 'ContractorModificationUnit' (MockMode.REQUIRED_ONLY) not equals"
        )
    }

    @Test
    fun contractorConverterTest() {
        val converter = ClaimContractorConverter(
            ClaimLegalEntityConverter(InternationalLegalEntityConverter(), RussianLegalEntityConverter()),
            PrivateEntityConverter()
        )
        val swagRegisteredUser = RegisteredUser().apply {
            contractorType = SwagContractorType.ContractorTypeEnum.REGISTEREDUSER
            email = "some email"
        }
        val swagContractor = SwagContractor().apply {
            contractorModificationType = SwagContractorModification.ContractorModificationTypeEnum.CONTRACTOR
            contractorType = swagRegisteredUser
        }
        val resultSwagContractor = converter.convertToSwag(converter.convertToThrift(swagContractor))
        assertEquals(
            swagContractor, resultSwagContractor,
            "Swag objects 'ContractorIdentificationLevel' not equals"
        )
        var thriftContractor = MockTBaseProcessor(MockMode.ALL).process(
            ThriftContractor(), TBaseHandler(ThriftContractor::class.java)
        )
        var resultThriftContractor = converter.convertToThrift(converter.convertToSwag(thriftContractor))
        assertEquals(
            thriftContractor, resultThriftContractor,
            "Thrift objects 'Contractor' (MockMode.ALL) not equals"
        )
        thriftContractor = MockTBaseProcessor(MockMode.REQUIRED_ONLY).process(
            thriftContractor, TBaseHandler(ThriftContractor::class.java)
        )
        resultThriftContractor = converter.convertToThrift(converter.convertToSwag(thriftContractor))
        assertEquals(
            thriftContractor, resultThriftContractor,
            "Thrift objects 'Contractor' (MockMode.REQUIRED_ONLY) not equals"
        )
    }

    companion object {
        private val testContractorIdentificationLevel: SwagContractorIdentificationLevel
            get() =
                EnhancedRandom.random(SwagContractorIdentificationLevel::class.java)
                    .apply {
                        contractorModificationType =
                            SwagContractorModification.ContractorModificationTypeEnum.CONTRACTORIDENTIFICATIONLEVEL
                        contractorIdentificationLevel = 100
                    }
    }
}
