package com.rbkmoney.claimmanagementapi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.rbkmoney.claimmanagementapi.config.AbstractKeycloakOpenIdAsWiremockConfig
import com.rbkmoney.claimmanagementapi.security.BouncerAccessService
import com.rbkmoney.claimmanagementapi.service.ClaimManagementService
import com.rbkmoney.claimmanagementapi.service.ClaimManagementServiceTest
import com.rbkmoney.claimmanagementapi.service.PartyManagementService
import com.rbkmoney.damsel.claim_management.ChangesetConflict
import com.rbkmoney.damsel.claim_management.ClaimNotFound
import com.rbkmoney.damsel.claim_management.InvalidChangeset
import com.rbkmoney.damsel.claim_management.InvalidClaimRevision
import com.rbkmoney.damsel.claim_management.InvalidClaimStatus
import org.apache.thrift.TException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class ErrorControllerTest : AbstractKeycloakOpenIdAsWiremockConfig() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var partyManagementService: PartyManagementService

    @MockBean
    private lateinit var claimManagementService: ClaimManagementService

    @MockBean
    private lateinit var bouncerAccessService: BouncerAccessService

    @BeforeEach
    fun setUp() {
        doNothing().whenever(partyManagementService).checkStatus(any())
        doNothing().whenever(partyManagementService).checkStatus()
        doNothing().whenever(bouncerAccessService).checkAccess(any(), anyOrNull())
    }

    @Test
    fun testThenBouncerForbidOperation() {
        whenever(bouncerAccessService.checkAccess(any(), anyOrNull()))
            .doAnswer { throw AccessDeniedException("") }
        mockMvc.perform(
            MockMvcRequestBuilders.get(eq("/processing/claims/{claimID}"), any<Long>())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + generateReadJwt())
                .header("X-Request-ID", string())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
        ).andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun testThenClaimManagementClientThrowingExceptions() {
        whenever(claimManagementService.getClaimById(any(), any()))
            .doAnswer { throw ClaimNotFound() }
        mockMvc.perform(
            MockMvcRequestBuilders.get(eq("/processing/claims/{claimID}"), any<Long>())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + generateReadJwt())
                .header("X-Request-ID", string())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
        reset(claimManagementService)
        whenever(
            claimManagementService.updateClaimById(any(), any(), any(), any())
        ).doAnswer { throw ClaimNotFound() }
        mockMvc.perform(
            MockMvcRequestBuilders.put(eq("/processing/claims/{claimID}/update"), any<Long>())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + generateWriteJwt())
                .header("X-Request-ID", string())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .param("claimRevision", 123.toString())
                .param("partyID", 123.toString())
                .content(objectMapper.writeValueAsBytes(ClaimManagementServiceTest.modifications))
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
        reset(claimManagementService)
        whenever(
            claimManagementService.updateClaimById(any(), any(), any(), any())
        ).doAnswer { throw InvalidClaimStatus() }
        mockMvc.perform(
            MockMvcRequestBuilders.put(eq("/processing/claims/{claimID}/update"), any<Long>())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + generateWriteJwt())
                .header("X-Request-ID", string())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .param("claimRevision", 123.toString())
                .param("partyID", 123.toString())
                .content(objectMapper.writeValueAsBytes(ClaimManagementServiceTest.modifications))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
        reset(claimManagementService)
        whenever(
            claimManagementService.updateClaimById(any(), any(), any(), any())
        ).doAnswer { throw InvalidClaimRevision() }
        mockMvc.perform(
            MockMvcRequestBuilders.put(eq("/processing/claims/{claimID}/update"), any<Long>())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + generateWriteJwt())
                .header("X-Request-ID", string())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .param("claimRevision", 123.toString())
                .param("partyID", 123.toString())
                .content(objectMapper.writeValueAsBytes(ClaimManagementServiceTest.modifications))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
        reset(claimManagementService)
        whenever(
            claimManagementService.updateClaimById(any(), any(), any(), any())
        ).doAnswer { throw ChangesetConflict() }
        mockMvc.perform(
            MockMvcRequestBuilders.put(eq("/processing/claims/{claimID}/update"), any<Long>())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + generateWriteJwt())
                .header("X-Request-ID", string())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .param("claimRevision", 123.toString())
                .param("partyID", 123.toString())
                .content(objectMapper.writeValueAsBytes(ClaimManagementServiceTest.modifications))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
        reset(claimManagementService)
        whenever(
            claimManagementService.updateClaimById(any(), any(), any(), any())
        ).doAnswer { throw InvalidChangeset() }
        mockMvc.perform(
            MockMvcRequestBuilders.put(eq("/processing/claims/{claimID}/update"), any<Long>())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + generateWriteJwt())
                .header("X-Request-ID", string())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .param("claimRevision", 123.toString())
                .param("partyID", 123.toString())
                .content(objectMapper.writeValueAsBytes(ClaimManagementServiceTest.modifications))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
        reset(claimManagementService)
        whenever(
            claimManagementService.updateClaimById(any(), any(), any(), any())
        ).doAnswer { throw TException() }
        mockMvc.perform(
            MockMvcRequestBuilders.put(eq("/processing/claims/{claimID}/update"), any<Long>())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + generateWriteJwt())
                .header("X-Request-ID", string())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .param("claimRevision", 123.toString())
                .param("partyID", 123.toString())
                .content(objectMapper.writeValueAsBytes(ClaimManagementServiceTest.modifications))
        ).andExpect(MockMvcResultMatchers.status().isInternalServerError)
    }

    private fun string() = UUID.randomUUID().toString()
}
