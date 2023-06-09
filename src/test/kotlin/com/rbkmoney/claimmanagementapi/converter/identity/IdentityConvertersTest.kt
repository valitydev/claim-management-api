package com.rbkmoney.claimmanagementapi.converter.identity

import dev.vality.damsel.claim_management.Modification
import dev.vality.damsel.msgpack.Value
import dev.vality.geck.serializer.kit.mock.MockMode
import dev.vality.geck.serializer.kit.mock.MockTBaseProcessor
import dev.vality.geck.serializer.kit.tbase.TBaseHandler
import dev.vality.swag.claim_management.model.IdentityModification.IdentityModificationTypeEnum
import dev.vality.swag.claim_management.model.Modification.ModificationTypeEnum
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import dev.vality.damsel.claim_management.IdentityModification as ThriftIdentityModification
import dev.vality.damsel.claim_management.IdentityModificationUnit as ThriftIdentityModificationUnit
import dev.vality.damsel.claim_management.IdentityParams as ThriftIdentityParams
import dev.vality.swag.claim_management.model.IdentityCreationModification as SwagIdentityCreationModification
import dev.vality.swag.claim_management.model.IdentityModificationUnit as SwagIdentityModificationUnit


class IdentityConvertersTest {

    @Test
    fun identityModificationCreationConverterTest() {
        val identityCreationConverter = IdentityModificationCreationConverter()
        val swagIdentityCreationModification = testSwagIdentityCreationModification()
        val tmpThriftIdentityModification = identityCreationConverter.convertToThrift(swagIdentityCreationModification)
        val resultSwagIdentityModification = identityCreationConverter.convertToSwag(tmpThriftIdentityModification)
        assertEquals(
            swagIdentityCreationModification, resultSwagIdentityModification,
            "Swag objects 'IdentityCreationModification' not equals"
        )
        val thriftIdentityCreationModification = MockTBaseProcessor(MockMode.ALL)
            .process(testThriftIdentityParams(), TBaseHandler(ThriftIdentityParams()::class.java))
        val tmpSwagIdentityModification = identityCreationConverter.convertToSwag(thriftIdentityCreationModification)
        val resultThriftIdentityCreation = identityCreationConverter.convertToThrift(tmpSwagIdentityModification)
        assertEquals(
            thriftIdentityCreationModification,
            resultThriftIdentityCreation,
            "Thrift objects 'IdentityCreationModification' not equals"
        )
    }

    @Test
    fun identityModificationUnitConverterTest() {
        val converter = IdentityModificationUnitConverter(IdentityModificationCreationConverter())
        val swagIdentityModificationUnit = testSwagIdentityModificationUnit()
        val tmpThriftIdentityModificationUnit = converter.convertToThrift(swagIdentityModificationUnit)
        val swagIdentityModificationUnitResult = converter.convertToSwag(tmpThriftIdentityModificationUnit)
        assertEquals(
            swagIdentityModificationUnit, swagIdentityModificationUnitResult,
            "Swag objects 'IdentityModificationUnit' not equals"
        )
        val thriftIdentityModificationUnit = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(
                testThriftIdentityModificationUnit(),
                TBaseHandler(ThriftIdentityModificationUnit()::class.java)
            )
        val thriftModification = Modification().apply { identityModification = thriftIdentityModificationUnit }
        val resultThriftModification = converter.convertToThrift(
            converter.convertToSwag(thriftModification)
        )
        assertEquals(
            thriftModification, resultThriftModification,
            "Thrift objects 'IdentityModificationUnit' not equals"
        )
    }

    private fun testSwagIdentityModificationUnit(): SwagIdentityModificationUnit {
        return SwagIdentityModificationUnit().apply {
            id = "test"
            modification = testSwagIdentityCreationModification()
            ModificationTypeEnum.IDENTITYMODIFICATIONUNIT
        }
    }

    private fun testSwagIdentityCreationModification(): SwagIdentityCreationModification {
        return SwagIdentityCreationModification()
            .apply {
                partyID = "testPartyId"
                name = "testName"
                provider = "testProvider"
                metadata = testMetadata()
                identityModificationType = IdentityModificationTypeEnum.IDENTITYCREATIONMODIFICATION
            }
    }

    private fun testThriftIdentityModificationUnit(): ThriftIdentityModificationUnit {
        return ThriftIdentityModificationUnit().apply {
            id = "test"
            modification = testThriftIdentityModification()
        }
    }

    private fun testThriftIdentityModification(): ThriftIdentityModification {
        return ThriftIdentityModification().apply {
            creation = testThriftIdentityParams()
        }
    }

    private fun testThriftIdentityParams(): ThriftIdentityParams {
        return ThriftIdentityParams().apply {
            partyId = "testPartyId"
            name = "testName"
            provider = "testProvider"
            metadata = testMetadata()
        }
    }

    private fun testMetadata(): MutableMap<String, Value> {
        val metadata: MutableMap<String, Value> = HashMap()
        metadata["test_key"] = Value()
        return metadata
    }

}
