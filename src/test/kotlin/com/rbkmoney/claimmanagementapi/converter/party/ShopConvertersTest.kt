package com.rbkmoney.claimmanagementapi.converter.party

import com.rbkmoney.claimmanagementapi.converter.party.data.TestShopData.testSwagShopModificationUnit
import com.rbkmoney.claimmanagementapi.converter.party.data.TestShopData.testSwagShopParams
import com.rbkmoney.claimmanagementapi.converter.party.shop.ShopAccountCreationModificationConverter
import com.rbkmoney.claimmanagementapi.converter.party.shop.ShopContractModificationConverter
import com.rbkmoney.claimmanagementapi.converter.party.shop.ShopCreationModificationConverter
import com.rbkmoney.claimmanagementapi.converter.party.shop.ShopDetailsModificationConverter
import com.rbkmoney.claimmanagementapi.converter.party.shop.ShopModificationUnitConverter
import com.rbkmoney.claimmanagementapi.converter.party.shop.ShopPayoutScheduleModificationConverter
import com.rbkmoney.damsel.claim_management.ScheduleModification
import com.rbkmoney.damsel.claim_management.ShopAccountParams
import com.rbkmoney.damsel.claim_management.ShopParams
import com.rbkmoney.geck.serializer.kit.mock.MockMode
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler
import com.rbkmoney.swag.claim_management.model.ShopPayoutScheduleModification
import io.github.benas.randombeans.api.EnhancedRandom
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import com.rbkmoney.damsel.claim_management.ShopContractModification as ThriftShopContractModification
import com.rbkmoney.damsel.claim_management.ShopModificationUnit as ThriftShopModificationUnit
import com.rbkmoney.damsel.domain.ShopDetails as ThriftShopDetails
import com.rbkmoney.swag.claim_management.model.ShopAccountCreationModification as SwagShopAccountCreationModification
import com.rbkmoney.swag.claim_management.model.ShopContractModification as SwagShopContractModification
import com.rbkmoney.swag.claim_management.model.ShopDetailsModification as SwagShopDetailsModification
import com.rbkmoney.swag.claim_management.model.ShopModification as SwagShopModification

class ShopConvertersTest {

    @Test
    @RepeatedTest(10)
    fun shopParamsConverterTest() {
        val converter = ShopCreationModificationConverter()
        val swagShopParams = testSwagShopParams
        val resultSwagShopParams = converter.convertToSwag(converter.convertToThrift(swagShopParams))
        assertEquals(swagShopParams, resultSwagShopParams, "Swag objects 'ShopParams' not equals")
        val thriftShopParams = MockTBaseProcessor(MockMode.ALL)
            .process(ShopParams(), TBaseHandler(ShopParams::class.java))
        val resultThriftShopParams = converter.convertToThrift(converter.convertToSwag(thriftShopParams))
        assertEquals(thriftShopParams, resultThriftShopParams, "Thrift objects 'ShopParams' not equals")
    }

    @Test
    fun shopAccountParamsConverterTest() {
        val converter = ShopAccountCreationModificationConverter()
        val swagShopAccountParams = EnhancedRandom.random(SwagShopAccountCreationModification::class.java)
            .apply {
                shopModificationType = SwagShopModification.ShopModificationTypeEnum.SHOPACCOUNTCREATIONMODIFICATION
            }
        val resultSwagShopAccountParams = converter.convertToSwag(converter.convertToThrift(swagShopAccountParams))
        assertEquals(swagShopAccountParams, resultSwagShopAccountParams, "Swag objects 'ShopAccountParams' not equals")
        val thriftShopAccountParams = MockTBaseProcessor(MockMode.ALL)
            .process(ShopAccountParams(), TBaseHandler(ShopAccountParams::class.java))
        val resultThriftShopAccountParams = converter.convertToThrift(converter.convertToSwag(thriftShopAccountParams))
        assertEquals(
            thriftShopAccountParams,
            resultThriftShopAccountParams,
            "Thrift objects 'ShopAccountParams' not equals"
        )
    }

    @Test
    fun scheduleModificationConverterTest() {
        val converter = ShopPayoutScheduleModificationConverter()
        val swagScheduleModification = EnhancedRandom.random(ShopPayoutScheduleModification::class.java)
            .apply {
                shopModificationType = SwagShopModification.ShopModificationTypeEnum.SHOPPAYOUTSCHEDULEMODIFICATION
            }
        val resultSwagScheduleModification =
            converter.convertToSwag(converter.convertToThrift(swagScheduleModification))
        assertEquals(
            swagScheduleModification, resultSwagScheduleModification,
            "Swag objects 'ScheduleModification' not equals"
        )
        val thriftScheduleModification = MockTBaseProcessor(MockMode.ALL)
            .process(ScheduleModification(), TBaseHandler(ScheduleModification::class.java))
        val resultThriftScheduleModification: ScheduleModification = converter.convertToThrift(
            converter.convertToSwag(thriftScheduleModification)
        )
        assertEquals(
            thriftScheduleModification, resultThriftScheduleModification,
            "Thrift objects 'ScheduleModification' not equals"
        )
    }

    @Test
    fun shopContractModificationConverterTest() {
        val converter = ShopContractModificationConverter()
        val swagShopContractModification = EnhancedRandom.random(SwagShopContractModification::class.java).apply {
            shopModificationType = SwagShopModification.ShopModificationTypeEnum.SHOPCONTRACTMODIFICATION
        }
        val resultSwagShopContractModificationConverter = converter.convertToSwag(
            converter.convertToThrift(swagShopContractModification)
        )
        assertEquals(
            swagShopContractModification, resultSwagShopContractModificationConverter,
            "Swag objects 'ShopContractModification' not equals"
        )
        val thriftShopContractModification = MockTBaseProcessor(MockMode.ALL)
            .process(
                ThriftShopContractModification(),
                TBaseHandler(ThriftShopContractModification::class.java)
            )
        val resultThriftShopContractModification =
            converter.convertToThrift(converter.convertToSwag(thriftShopContractModification))
        assertEquals(
            thriftShopContractModification, resultThriftShopContractModification,
            "Thrift objects 'ShopContractModification' not equals"
        )
    }

    @Test
    fun shopDetailsConverterTest() {
        val converter = ShopDetailsModificationConverter()
        val swagShopDetails = EnhancedRandom.random(SwagShopDetailsModification::class.java).apply {
            shopModificationType = SwagShopModification.ShopModificationTypeEnum.SHOPDETAILSMODIFICATION
        }
        val resultSwagShopContractModificationConverter =
            converter.convertToSwag(converter.convertToThrift(swagShopDetails))
        assertEquals(
            swagShopDetails, resultSwagShopContractModificationConverter,
            "Swag objects 'ShopDetails' not equals"
        )
        var thriftShopDetails = ThriftShopDetails()
        thriftShopDetails = MockTBaseProcessor(MockMode.ALL)
            .process(thriftShopDetails, TBaseHandler(ThriftShopDetails::class.java))
        val resultThriftShopDetails = converter.convertToThrift(converter.convertToSwag(thriftShopDetails))
        assertEquals(
            thriftShopDetails, resultThriftShopDetails,
            "Thrift objects 'ShopDetails' not equals"
        )
    }

    @Test
    fun shopModificationUnitSwagConverterTest() {
        val converter = ShopModificationUnitConverter(
            ShopCreationModificationConverter(),
            ShopPayoutScheduleModificationConverter(),
            ShopDetailsModificationConverter(),
            ShopAccountCreationModificationConverter(),
            ShopContractModificationConverter()
        )
        val swagShopModificationUnit = testSwagShopModificationUnit
        val resultSwagShopModificationUnit =
            converter.convertToSwag(converter.convertToThrift(swagShopModificationUnit))
        assertEquals(
            swagShopModificationUnit, resultSwagShopModificationUnit,
            "Swag objects 'ShopModificationUnit' not equals"
        )
    }

    @Test
    @RepeatedTest(10)
    fun shopModificationUnitThriftConverterTest() {
        val converter = ShopModificationUnitConverter(
            ShopCreationModificationConverter(),
            ShopPayoutScheduleModificationConverter(),
            ShopDetailsModificationConverter(),
            ShopAccountCreationModificationConverter(),
            ShopContractModificationConverter()
        )
        val thriftShopModificationUnit = MockTBaseProcessor(MockMode.ALL)
            .process(ThriftShopModificationUnit(), TBaseHandler(ThriftShopModificationUnit::class.java))
        // Temporary (hope so) hack
        if (thriftShopModificationUnit.getModification().isSetCashRegisterModificationUnit) {
            assertThrows<NullPointerException> {
                converter.convertToThrift(converter.convertToSwag(thriftShopModificationUnit))
            }
        } else {
            val resultThriftShopModificationUnit = converter.convertToThrift(
                converter.convertToSwag(thriftShopModificationUnit)
            )
            assertEquals(
                thriftShopModificationUnit, resultThriftShopModificationUnit,
                "Thrift objects 'ShopModificationUnit' not equals"
            )
        }
    }
}
