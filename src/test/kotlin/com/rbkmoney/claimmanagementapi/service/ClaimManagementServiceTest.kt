package com.rbkmoney.claimmanagementapi.service

import com.rbkmoney.damsel.claim_management.Claim
import com.rbkmoney.damsel.claim_management.ClaimAccepted
import com.rbkmoney.damsel.claim_management.ClaimManagementSrv
import com.rbkmoney.damsel.claim_management.ClaimSearchResponse
import com.rbkmoney.damsel.claim_management.ClaimStatus
import com.rbkmoney.damsel.claim_management.DocumentCreated
import com.rbkmoney.damsel.claim_management.InternalUser
import com.rbkmoney.damsel.claim_management.UserType
import com.rbkmoney.damsel.msgpack.Value
import com.rbkmoney.swag.claim_management.model.ClaimChangeset
import com.rbkmoney.swag.claim_management.model.ClaimModification
import com.rbkmoney.swag.claim_management.model.ClaimModificationType.ClaimModificationTypeEnum
import com.rbkmoney.swag.claim_management.model.DocumentModification
import com.rbkmoney.swag.claim_management.model.DocumentModification.DocumentModificationTypeEnum
import com.rbkmoney.swag.claim_management.model.DocumentModificationUnit
import com.rbkmoney.swag.claim_management.model.Modification.ModificationTypeEnum
import com.rbkmoney.swag.claim_management.model.UserInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime
import kotlin.collections.set
import com.rbkmoney.damsel.claim_management.ClaimModification as ThriftClaimModification
import com.rbkmoney.damsel.claim_management.DocumentModification as ThriftDocumentModification
import com.rbkmoney.damsel.claim_management.DocumentModificationUnit as ThriftDocumentModificationUnit
import com.rbkmoney.damsel.claim_management.Modification as ThriftModification
import com.rbkmoney.damsel.claim_management.ModificationUnit as ThriftModificationUnit
import com.rbkmoney.damsel.claim_management.UserInfo as ThriftUserInfo
import com.rbkmoney.swag.claim_management.model.Modification as SwagModification

@SpringBootTest
@ExtendWith(SpringExtension::class)
class ClaimManagementServiceTest {

    @Autowired
    private lateinit var claimManagementService: ClaimManagementService

    @MockBean
    private lateinit var claimManagementClient: ClaimManagementSrv.Iface

    @MockBean
    private lateinit var partyManagementService: PartyManagementService

    @BeforeEach
    fun setUp() {
        doNothing().whenever(partyManagementService).checkStatus(any())
    }

    @Test
    fun test() {
        whenever(
            claimManagementClient.createClaim(any(), any())
        ).thenReturn(testCreateClaim)
        whenever(
            claimManagementClient.getClaim(any(), any())
        ).thenReturn(testClaimById)
        whenever(
            claimManagementClient.searchClaims(any())
        ).thenReturn(
            ClaimSearchResponse(testSearchClaim).setContinuationToken("continuation_token")
        )
        val requestClaim = claimManagementService.createClaim("test_request_1", modifications)
        assertEquals(
            testAnswerCreateClaim.toString(), requestClaim.toString(),
            "Swag objects 'Claim' (create) not equals"
        )
        val claimById = claimManagementService.getClaimById("test_request_1", 1L)
        assertEquals(
            testAnswerCreateClaim.toString(), claimById.toString(),
            "Swag objects 'Claim' (by id) not equals"
        )
        val response = claimManagementService.searchClaims("test_request_1", 1, "token", 123L, ArrayList())
        assertEquals(
            testAnswerCreateClaim.toString(), response.result[0].toString(), "Swag objects 'Claim' (search) not equals"
        )
        assertEquals("continuation_token", response.continuationToken)
    }

    companion object {
        private val testAnswerCreateClaim: com.rbkmoney.swag.claim_management.model.Claim
            get() {
                val testClaim = com.rbkmoney.swag.claim_management.model.Claim()
                testClaim.id = 1L
                testClaim.status = "accepted"
                testClaim.createdAt = OffsetDateTime.parse("2019-08-21T12:09:32.449571+03:00")
                testClaim.revision = 1
                testClaim.updatedAt = OffsetDateTime.parse("2019-08-21T12:09:32.449571+03:00")
                testClaim.changeset = changeset
                val claimMetadata: MutableMap<String, Value> = HashMap()
                claimMetadata["test_key"] = Value()
                testClaim.metadata = claimMetadata
                return testClaim
            }
        private val changeset: ClaimChangeset
            get() {
                val changeset = ClaimChangeset()
                val modificationUnit = com.rbkmoney.swag.claim_management.model.ModificationUnit()
                val swagClaimModification = ClaimModification()
                swagClaimModification.modificationType = ModificationTypeEnum.CLAIMMODIFICATION
                modificationUnit.modificationID = 1L
                modificationUnit.createdAt = OffsetDateTime.parse("2019-08-21T12:09:32.449571+03:00")
                val documentModificationUnit = DocumentModificationUnit()
                documentModificationUnit.claimModificationType = ClaimModificationTypeEnum.DOCUMENTMODIFICATIONUNIT
                documentModificationUnit.documentId = "id_1"
                val documentModification = DocumentModification()
                documentModification.documentModificationType = DocumentModificationTypeEnum.DOCUMENTCREATED
                documentModificationUnit.documentModification = documentModification
                swagClaimModification.claimModificationType = documentModificationUnit
                modificationUnit.modification = swagClaimModification
                modificationUnit.userInfo = UserInfo()
                    .userId("ID")
                    .username("username")
                    .email("email")
                    .userType(UserInfo.UserTypeEnum.INTERNAL_USER)
                changeset.add(modificationUnit)
                return changeset
            }

        val modifications: List<SwagModification>
            get() {
                val documentModification = DocumentModification()
                    .apply { documentModificationType = DocumentModificationTypeEnum.DOCUMENTCREATED }
                return listOf(
                    ClaimModification()
                        .claimModificationType(
                            DocumentModificationUnit()
                                .documentId("document_id")
                                .documentModification(documentModification)
                                .claimModificationType(ClaimModificationTypeEnum.DOCUMENTMODIFICATIONUNIT)
                        )
                        .modificationType(ModificationTypeEnum.CLAIMMODIFICATION)
                )
            }

        private val testCreateClaim: Claim
            get() {
                val claim = Claim()
                claim.createdAt = "2019-08-21T12:09:32.449571+03:00"
                claim.setId(1L)
                claim.setRevision(1)
                claim.setStatus(ClaimStatus.accepted(ClaimAccepted()))
                val claimMetadata: MutableMap<String, Value> = HashMap()
                claimMetadata["test_key"] = Value()
                claim.setMetadata(claimMetadata)
                claim.updatedAt = "2019-08-21T12:09:32.449571+03:00"
                val changeset: MutableList<ThriftModificationUnit> = ArrayList()
                val documentModification = ThriftDocumentModification()
                documentModification.creation = DocumentCreated()
                val claimModification = ThriftClaimModification()
                claimModification.documentModification = ThriftDocumentModificationUnit()
                    .setId("id_1")
                    .setModification(documentModification)
                val modification = ThriftModification()
                modification.claimModification = claimModification
                val thriftModificationUnit = ThriftModificationUnit()
                thriftModificationUnit.createdAt = "2019-08-21T12:09:32.449571+03:00"
                thriftModificationUnit.modificationId = 1L
                thriftModificationUnit.userInfo = ThriftUserInfo()
                    .setId("ID")
                    .setUsername("username")
                    .setEmail("email")
                    .setType(UserType.internal_user(InternalUser()))
                thriftModificationUnit.setModification(modification)
                changeset.add(thriftModificationUnit)
                claim.setChangeset(changeset)
                return claim
            }
        private val testClaimById: Claim
            get() = testCreateClaim
        private val testSearchClaim: List<Claim>
            get() = listOf(testCreateClaim)
    }
}
