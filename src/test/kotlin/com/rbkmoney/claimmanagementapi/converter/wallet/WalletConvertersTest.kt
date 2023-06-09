package com.rbkmoney.claimmanagementapi.converter.wallet

import dev.vality.damsel.claim_management.Modification
import dev.vality.damsel.msgpack.Value
import dev.vality.geck.serializer.kit.mock.MockMode
import dev.vality.geck.serializer.kit.mock.MockTBaseProcessor
import dev.vality.geck.serializer.kit.tbase.TBaseHandler
import dev.vality.swag.claim_management.model.Modification.ModificationTypeEnum
import dev.vality.swag.claim_management.model.WalletModification.WalletModificationTypeEnum
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import dev.vality.damsel.claim_management.NewWalletModification as ThriftWalletModification
import dev.vality.damsel.claim_management.NewWalletModificationUnit as ThriftWalletModificationUnit
import dev.vality.damsel.claim_management.NewWalletParams as ThriftWalletParams
import dev.vality.damsel.domain.CurrencyRef as ThriftCurrency
import dev.vality.swag.claim_management.model.CurrencyRef as SwagCurrency
import dev.vality.swag.claim_management.model.WalletCreationModification as SwagWalletCreationModification
import dev.vality.swag.claim_management.model.WalletModificationUnit as SwagWalletModificationUnit

class WalletConvertersTest {

    @Test
    fun walletModificationCreationConverterTest() {
        val walletCreationConverter = WalletModificationCreationConverter()
        val swagWalletCreationModification = testSwagWalletCreationModification()
        val tmpThriftWalletModification = walletCreationConverter.convertToThrift(swagWalletCreationModification)
        val resultSwagWalletModification = walletCreationConverter.convertToSwag(tmpThriftWalletModification)
        assertEquals(
            swagWalletCreationModification, resultSwagWalletModification,
            "Swag objects 'WalletCreationModification' not equals"
        )
        val thriftWalletCreationModification = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(testThriftWalletParams(), TBaseHandler(ThriftWalletParams()::class.java))
        val tmpSwagWalletModification = walletCreationConverter.convertToSwag(thriftWalletCreationModification)
        val resultThriftWalletCreation = walletCreationConverter.convertToThrift(tmpSwagWalletModification)
        assertEquals(
            thriftWalletCreationModification,
            resultThriftWalletCreation,
            "Thrift objects 'WalletCreationModification' not equals"
        )
    }

    @Test
    fun walletModificationUnitConverterTest() {
        val converter = WalletModificationUnitConverter(WalletModificationCreationConverter())
        val swagWalletModificationUnit = testSwagWalletModificationUnit()
        val tmpThriftWalletModificationUnit = converter.convertToThrift(swagWalletModificationUnit)
        val swagWalletModificationUnitResult = converter.convertToSwag(tmpThriftWalletModificationUnit)
        assertEquals(
            swagWalletModificationUnit, swagWalletModificationUnitResult,
            "Swag objects 'WalletModificationUnit' not equals"
        )
        val thriftWalletModificationUnit = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(
                testThriftWalletModificationUnit(),
                TBaseHandler(ThriftWalletModificationUnit()::class.java)
            )
        val thriftModification = Modification().apply { walletModification = thriftWalletModificationUnit }
        val resultThriftModification = converter.convertToThrift(
            converter.convertToSwag(thriftModification)
        )
        assertEquals(
            thriftModification, resultThriftModification,
            "Thrift objects 'WalletModificationUnit' not equals"
        )
    }

    private fun testSwagWalletModificationUnit(): SwagWalletModificationUnit {
        return SwagWalletModificationUnit().apply {
            id = "test"
            modification = testSwagWalletCreationModification()
            ModificationTypeEnum.WALLETMODIFICATIONUNIT
        }
    }

    private fun testSwagWalletCreationModification(): SwagWalletCreationModification {
        return SwagWalletCreationModification()
            .apply {
                identityID = "testIdentityId"
                name = "testName"
                currency = SwagCurrency().apply { symbolicCode = "RUB" }
                metadata = testMetadata()
                walletModificationType = WalletModificationTypeEnum.WALLETCREATIONMODIFICATION
            }
    }

    private fun testThriftWalletModificationUnit(): ThriftWalletModificationUnit {
        return ThriftWalletModificationUnit().apply {
            id = "test"
            modification = testThriftWalletModification()
        }
    }

    private fun testThriftWalletModification(): ThriftWalletModification {
        return ThriftWalletModification().apply {
            creation = testThriftWalletParams()
        }
    }

    private fun testThriftWalletParams(): ThriftWalletParams {
        return ThriftWalletParams().apply {
            identityId = "testIdentityId"
            name = "testName"
            currency = ThriftCurrency().apply { symbolicCode = "RUB" }
            metadata = testMetadata()
        }
    }

    private fun testMetadata(): MutableMap<String, Value> {
        val metadata: MutableMap<String, Value> = HashMap()
        metadata["test_key"] = Value()
        return metadata
    }
}
