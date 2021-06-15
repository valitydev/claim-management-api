package com.rbkmoney.claimmanagementapi.converter.claim

import com.rbkmoney.geck.serializer.kit.mock.MockMode
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler
import com.rbkmoney.swag.claim_management.model.ClaimModificationType.ClaimModificationTypeEnum
import com.rbkmoney.swag.claim_management.model.Modification.ModificationTypeEnum
import com.rbkmoney.swag.claim_management.model.StatusModificationUnit.StatusEnum.DENIED
import com.rbkmoney.swag.claim_management.model.StatusModificationUnit.StatusEnum.REVOKED
import io.github.benas.randombeans.api.EnhancedRandom
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import com.rbkmoney.damsel.claim_management.ClaimModification as ThriftClaimModification
import com.rbkmoney.damsel.claim_management.ClaimStatus as ThriftClaimStatus
import com.rbkmoney.damsel.claim_management.CommentModificationUnit as ThriftCommentModificationUnit
import com.rbkmoney.damsel.claim_management.DocumentModificationUnit as ThriftDocumentModificationUnit
import com.rbkmoney.damsel.claim_management.FileModificationUnit as ThriftFileModificationUnit
import com.rbkmoney.damsel.claim_management.Modification as ThriftModification
import com.rbkmoney.damsel.claim_management.StatusModificationUnit as ThriftStatusModificationUnit
import com.rbkmoney.swag.claim_management.model.ClaimModification as SwagClaimModification
import com.rbkmoney.swag.claim_management.model.CommentModificationUnit as SwagCommentModificationUnit
import com.rbkmoney.swag.claim_management.model.DocumentModificationUnit as SwagDocumentModificationUnit
import com.rbkmoney.swag.claim_management.model.FileModificationUnit as SwagFileModificationUnit
import com.rbkmoney.swag.claim_management.model.StatusModification as SwagStatusModification1
import com.rbkmoney.swag.claim_management.model.StatusModificationUnit as SwagStatusModificationUnit

class ClaimConvertersTest {

    @Test
    fun claimStatusConverterTest() {
        val statusConverter = ClaimStatusModificationConverter()
        val swagStatusModificationUnit = EnhancedRandom.random(SwagStatusModificationUnit::class.java)
            .apply { claimModificationType = ClaimModificationTypeEnum.STATUSMODIFICATIONUNIT }
        if (swagStatusModificationUnit.status != DENIED && swagStatusModificationUnit.status != REVOKED) {
            swagStatusModificationUnit.reason = null
        }
        val tmpThriftClaimStatus = statusConverter.convertToThrift(swagStatusModificationUnit)
        val resultSwagStatusModificationUnit = statusConverter.convertToSwag(tmpThriftClaimStatus)
        assertEquals(
            swagStatusModificationUnit, resultSwagStatusModificationUnit,
            "Swag objects 'StatusModificationUnit' not equals"
        )
        val thriftClaimStatus = MockTBaseProcessor(MockMode.ALL)
            .process(ThriftClaimStatus(), TBaseHandler(ThriftClaimStatus::class.java))
        val tmpSwagStatusModificationUnit = statusConverter.convertToSwag(thriftClaimStatus)
        val resultThriftClaimStatus = statusConverter.convertToThrift(tmpSwagStatusModificationUnit)
        assertEquals(thriftClaimStatus, resultThriftClaimStatus, "Thrift objects 'ClaimStatus' not equals")
    }

    @Test
    fun claimStatusModificationUnitConverterTest() {
        val converter = ClaimStatusModificationUnitConverter(ClaimStatusModificationConverter())
        val swagStatusModificationUnit = EnhancedRandom.random(SwagStatusModificationUnit::class.java)
            .apply { claimModificationType = ClaimModificationTypeEnum.STATUSMODIFICATIONUNIT }
        if (swagStatusModificationUnit.status != DENIED && swagStatusModificationUnit.status != REVOKED) {
            swagStatusModificationUnit.reason = null
        }
        val tmpThriftStatusModificationUnit = converter.convertToThrift(swagStatusModificationUnit)
        val swagStatusModificationUnitResult = converter.convertToSwag(tmpThriftStatusModificationUnit)
        assertEquals(
            swagStatusModificationUnit, swagStatusModificationUnitResult,
            "Swag objects 'StatusModificationUnit' not equals"
        )
        val thriftStatusModificationUnit = MockTBaseProcessor(MockMode.ALL)
            .process(
                ThriftStatusModificationUnit(),
                TBaseHandler(ThriftStatusModificationUnit::class.java)
            )
        val tmpSwagStatusModificationUnit = converter.convertToSwag(thriftStatusModificationUnit)
        val resultThriftStatusModificationUnit = converter.convertToThrift(tmpSwagStatusModificationUnit)
        assertEquals(
            thriftStatusModificationUnit, resultThriftStatusModificationUnit,
            "Thrift objects 'StatusModificationUnit' not equals"
        )
    }

    @Test
    fun claimFileModificationUnitConverterTest() {
        val converter = ClaimFileModificationUnitConverter()
        val swagFileModificationUnit = EnhancedRandom.random(SwagFileModificationUnit::class.java)
            .apply { claimModificationType = ClaimModificationTypeEnum.FILEMODIFICATIONUNIT }
        val resultSwagFileModificationUnit = converter.convertToSwag(
            converter.convertToThrift(swagFileModificationUnit)
        )
        assertEquals(
            swagFileModificationUnit, resultSwagFileModificationUnit,
            "Swag objects 'FileModificationUnit' not equals"
        )
        val thriftFileModificationUnit = MockTBaseProcessor(MockMode.ALL)
            .process(
                ThriftFileModificationUnit(),
                TBaseHandler(ThriftFileModificationUnit::class.java)
            )
        val resultThriftFileModificationUnit = converter.convertToThrift(
            converter.convertToSwag(thriftFileModificationUnit)
        )
        assertEquals(
            thriftFileModificationUnit, resultThriftFileModificationUnit,
            "Thrift objects 'FileModificationUnit' not equals"
        )
    }

    @Test
    fun claimDocumentModificationConverterTest() {
        val converter = ClaimDocumentModificationUnitConverter()
        val swagDocumentModificationUnit = EnhancedRandom.random(SwagDocumentModificationUnit::class.java)
            .apply { claimModificationType = ClaimModificationTypeEnum.DOCUMENTMODIFICATIONUNIT }
        val resultDocumentModificationUnit = converter.convertToSwag(
            converter.convertToThrift(swagDocumentModificationUnit)
        )
        assertEquals(
            swagDocumentModificationUnit, resultDocumentModificationUnit,
            "Swag objects 'DocumentModificationUnit' not equals"
        )
        val thriftDocumentModificationUnit = MockTBaseProcessor(MockMode.ALL)
            .process(ThriftDocumentModificationUnit(), TBaseHandler(ThriftDocumentModificationUnit::class.java))
        thriftDocumentModificationUnit.setType(null)
        val resultThriftDocumentModificationUnit = converter.convertToThrift(
            converter.convertToSwag(thriftDocumentModificationUnit)
        )
        assertEquals(
            thriftDocumentModificationUnit, resultThriftDocumentModificationUnit,
            "Thrift objects 'DocumentModificationUnit' not equals"
        )
    }

    @Test
    fun claimCommentModificationUnitConverterTest() {
        val converter = ClaimCommentModificationUnitConverter()
        val swagCommentModificationUnit = EnhancedRandom.random(SwagCommentModificationUnit::class.java)
            .apply { claimModificationType = ClaimModificationTypeEnum.COMMENTMODIFICATIONUNIT }
        val resultSwagCommentModificationUnit = converter.convertToSwag(
            converter.convertToThrift(swagCommentModificationUnit)
        )
        assertEquals(
            swagCommentModificationUnit, resultSwagCommentModificationUnit,
            "Swag objects 'CommentModificationUnit' not equals"
        )
        val thriftCommentModificationUnit = MockTBaseProcessor(MockMode.ALL)
            .process(
                ThriftCommentModificationUnit(),
                TBaseHandler(ThriftCommentModificationUnit::class.java)
            )
        val resultThriftCommentModificationUnit = converter.convertToThrift(
            converter.convertToSwag(thriftCommentModificationUnit)
        )
        assertEquals(
            thriftCommentModificationUnit, resultThriftCommentModificationUnit,
            "Thrift objects 'DocumentModificationUnit' not equals"
        )
    }

    @Test
    fun claimModificationConverterTest() {
        val converter = ClaimModificationConverter(
            ClaimDocumentModificationUnitConverter(),
            ClaimCommentModificationUnitConverter(),
            ClaimStatusModificationUnitConverter(ClaimStatusModificationConverter()),
            ClaimFileModificationUnitConverter(),
            ClaimExternalInfoModificationUnitConverter()
        )
        val swagStatusModUnit = SwagStatusModificationUnit().apply {
            claimModificationType = ClaimModificationTypeEnum.STATUSMODIFICATIONUNIT
            reason = "testReason"
            status = DENIED
            statusModification = EnhancedRandom.random(SwagStatusModification1::class.java)
        }
        val swagClaimModification = SwagClaimModification().apply {
            claimModificationType = swagStatusModUnit
            modificationType = ModificationTypeEnum.CLAIMMODIFICATION
        }
        val resultSwagClaimModification = converter.convertToSwag(converter.convertToThrift(swagClaimModification))
        assertEquals(swagClaimModification, resultSwagClaimModification, "Swag objects 'ClaimModification' not equals")
        val thriftClaimModification = MockTBaseProcessor(MockMode.REQUIRED_ONLY)
            .process(
                ThriftClaimModification(),
                TBaseHandler(ThriftClaimModification::class.java)
            )
        val thriftModification = ThriftModification().apply { claimModification = thriftClaimModification }
        val resultThriftModification = converter.convertToThrift(
            converter.convertToSwag(thriftModification)
        )
        assertEquals(thriftModification, resultThriftModification, "Thrift objects 'Modification' not equals")
    }
}
