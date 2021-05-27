package com.rbkmoney.claimmanagementapi.controller

import com.rbkmoney.claimmanagementapi.exception.DeadlineException
import com.rbkmoney.claimmanagementapi.exception.client.BadRequestException
import com.rbkmoney.claimmanagementapi.exception.client.NotFoundException
import com.rbkmoney.claimmanagementapi.exception.server.DarkApi5xxException
import com.rbkmoney.claimmanagementapi.service.ClaimManagementService
import com.rbkmoney.claimmanagementapi.service.KeycloakService
import com.rbkmoney.claimmanagementapi.service.PartyManagementService
import com.rbkmoney.claimmanagementapi.util.DeadlineChecker
import com.rbkmoney.damsel.claim_management.BadContinuationToken
import com.rbkmoney.damsel.claim_management.ChangesetConflict
import com.rbkmoney.damsel.claim_management.ClaimNotFound
import com.rbkmoney.damsel.claim_management.InvalidChangeset
import com.rbkmoney.damsel.claim_management.InvalidClaimRevision
import com.rbkmoney.damsel.claim_management.InvalidClaimStatus
import com.rbkmoney.damsel.claim_management.LimitExceeded
import com.rbkmoney.swag.claim_management.api.ProcessingApi
import com.rbkmoney.swag.claim_management.model.Claim
import com.rbkmoney.swag.claim_management.model.GeneralError
import com.rbkmoney.swag.claim_management.model.InlineResponse200
import com.rbkmoney.swag.claim_management.model.InlineResponse400
import com.rbkmoney.swag.claim_management.model.InlineResponse4001
import com.rbkmoney.swag.claim_management.model.InlineResponse4002
import com.rbkmoney.swag.claim_management.model.InlineResponse4003
import com.rbkmoney.swag.claim_management.model.Modification
import mu.KotlinLogging
import org.apache.thrift.TException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@RestController
@RequestMapping("")
class ClaimManagementController(
    private val claimManagementService: ClaimManagementService,
    private val partyManagementService: PartyManagementService,
    private val keycloakService: KeycloakService,
    private val deadlineChecker: DeadlineChecker
) : ProcessingApi {

    private val log = KotlinLogging.logger { }

    @PreAuthorize("hasAuthority('party:read')")
    override fun createClaim(
        @NotNull @Size(min = 1, max = 40) xRequestId: String?,
        @NotNull @Valid changeset: List<Modification>?,
        @Size(min = 1, max = 40) xRequestDeadline: String?
    ): ResponseEntity<Claim> {
        return try {
            log.info { "Process 'createClaim' get started, xRequestId=$xRequestId" }
            partyManagementService.checkStatus(xRequestId)
            deadlineChecker.checkDeadline(xRequestDeadline, xRequestId)
            val claim = claimManagementService.createClaim(keycloakService.partyId, changeset!!)
            log.info { "Claim created, xRequestId=$xRequestId, claimId=${claim.id}" }
            ResponseEntity.ok(claim)
        } catch (ex: DeadlineException) {
            val msg = ex.message
            val response: InlineResponse4001 = InlineResponse4001()
                .code(InlineResponse4001.CodeEnum.INVALIDDEADLINE)
                .message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: InvalidChangeset) {
            val msg = "Invalid changeset, xRequestId=$xRequestId, reason='${ex.reason}'"
            val response: InlineResponse4001 = InlineResponse4001()
                .code(InlineResponse4001.CodeEnum.INVALIDCHANGESET)
                .message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: TException) {
            throw buildDarkApi5xxException("createClaim", xRequestId, ex)
        }
    }

    @PreAuthorize("hasAuthority('party:read')")
    override fun getClaimByID(
        @NotNull @Size(min = 1, max = 40) xRequestId: String?,
        @NotNull @Valid claimId: Long?,
        @Size(min = 1, max = 40) xRequestDeadline: String?
    ): ResponseEntity<Claim> {
        return try {
            log.info { "Process 'getClaimByID' get started, xRequestId=$xRequestId, claimId=$claimId" }
            partyManagementService.checkStatus(xRequestId)
            deadlineChecker.checkDeadline(xRequestDeadline, xRequestId)
            val claim = claimManagementService.getClaimById(keycloakService.partyId, claimId!!)
            log.info { "Got a claim, xRequestId=$xRequestId, claimId=$claimId" }
            ResponseEntity.ok(claim)
        } catch (ex: DeadlineException) {
            val msg = ex.message
            val response: GeneralError = GeneralError().message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: ClaimNotFound) {
            throw buildNotFoundException(xRequestId!!, claimId!!, ex)
        } catch (ex: TException) {
            throw buildDarkApi5xxException("getClaimByID", xRequestId, ex)
        }
    }

    @PreAuthorize("hasAuthority('party:write')")
    override fun revokeClaimByID(
        @NotNull @Size(min = 1, max = 40) xRequestId: String?,
        @NotNull @Valid claimId: Long?,
        @NotNull @Valid claimRevision: Int?,
        @Size(min = 1, max = 40) xRequestDeadline: String?,
        @Valid reason: String?
    ): ResponseEntity<Void> {
        return try {
            log.info { "Process 'revokeClaimByID' get started, xRequestId=$xRequestId, claimId=$claimId" }
            partyManagementService.checkStatus(xRequestId)
            deadlineChecker.checkDeadline(xRequestDeadline, xRequestId)
            claimManagementService.revokeClaimById(keycloakService.partyId, claimId!!, claimRevision!!, reason)
            log.info { "Successful revoke claim, xRequestId=$xRequestId, claimId=$claimId" }
            ResponseEntity<Void>(HttpStatus.OK)
        } catch (ex: DeadlineException) {
            val msg = ex.message
            val response: InlineResponse4002 = InlineResponse4002()
                .code(InlineResponse4002.CodeEnum.INVALIDDEADLINE)
                .message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: InvalidClaimStatus) {
            val msg = "Invalid claim status, xRequestId=$xRequestId, status=${ex.status}"
            val response: InlineResponse4002 = InlineResponse4002()
                .code(InlineResponse4002.CodeEnum.INVALIDCLAIMSTATUS)
                .message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: InvalidClaimRevision) {
            val msg = "Invalid claim revision, xRequestId=$xRequestId, claimRevision=$claimRevision"
            val response: InlineResponse4002 = InlineResponse4002()
                .code(InlineResponse4002.CodeEnum.INVALIDCLAIMREVISION)
                .message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: ClaimNotFound) {
            throw buildNotFoundException(xRequestId!!, claimId!!, ex)
        } catch (ex: TException) {
            throw buildDarkApi5xxException("revokeClaimByID", xRequestId, ex)
        }
    }

    @PreAuthorize("hasAuthority('party:write')")
    override fun requestReviewClaimByID(
        @NotNull @Size(min = 1, max = 40) xRequestId: String?,
        @NotNull @Valid claimId: Long?,
        @NotNull @Valid claimRevision: Int?,
        @Size(min = 1, max = 40) xRequestDeadline: String?
    ): ResponseEntity<Void> {
        return try {
            log.info { "Process 'requestReviewClaimByID' get started, xRequestId=$xRequestId, claimId=$claimId" }
            partyManagementService.checkStatus(xRequestId)
            deadlineChecker.checkDeadline(xRequestDeadline, xRequestId)
            claimManagementService.requestClaimReviewById(keycloakService.partyId, claimId!!, claimRevision!!)
            log.info { "Successful request claim review, xRequestId=$xRequestId, claimId=$claimId" }
            ResponseEntity<Void>(HttpStatus.OK)
        } catch (ex: DeadlineException) {
            val msg = ex.message
            val response: InlineResponse4002 = InlineResponse4002()
                .code(InlineResponse4002.CodeEnum.INVALIDDEADLINE)
                .message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: InvalidClaimStatus) {
            val msg = "Invalid claim status, xRequestId=$xRequestId, status=${ex.status}"
            val response: InlineResponse4002 = InlineResponse4002()
                .code(InlineResponse4002.CodeEnum.INVALIDCLAIMSTATUS)
                .message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: InvalidClaimRevision) {
            val msg = "Invalid claim revision, xRequestId=$xRequestId, claimRevision=$claimRevision"
            val response: InlineResponse4002 = InlineResponse4002()
                .code(InlineResponse4002.CodeEnum.INVALIDCLAIMREVISION)
                .message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: ClaimNotFound) {
            throw buildNotFoundException(xRequestId!!, claimId!!, ex)
        } catch (ex: TException) {
            throw buildDarkApi5xxException("requestClaimReviewById", xRequestId, ex)
        }
    }

    @PreAuthorize("hasAuthority('party:read')")
    override fun searchClaims(
        @NotNull @Size(min = 1, max = 40) xRequestId: String?,
        @NotNull @Min(1L) @Max(1000L) @Valid limit: Int?,
        @Size(min = 1, max = 40) xRequestDeadline: String?,
        @Size(min = 1, max = 40) continuationToken: String?,
        @Valid claimId: Long?,
        @Valid claimStatuses: List<String>?
    ): ResponseEntity<InlineResponse200> {
        return try {
            log.info { "Process 'searchClaims' get started, xRequestId=$xRequestId, claimId=$claimId" }
            partyManagementService.checkStatus(xRequestId)
            deadlineChecker.checkDeadline(xRequestDeadline, xRequestId)
            val response: InlineResponse200 = claimManagementService.searchClaims(
                keycloakService.partyId,
                limit!!,
                continuationToken,
                claimId,
                claimStatuses
            )
            log.info {
                "For status list, xRequestId=$xRequestId, claimId=$claimId, list statuses=$claimStatuses, " +
                    "size results=${response.result.size}"
            }
            ResponseEntity.ok(response)
        } catch (ex: DeadlineException) {
            val msg = ex.message
            val response: InlineResponse400 = InlineResponse400()
                .code(InlineResponse400.CodeEnum.INVALIDDEADLINE)
                .message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: LimitExceeded) {
            val msg = "Limit exceeded, xRequestId=$xRequestId"
            val response: InlineResponse400 = InlineResponse400()
                .code(InlineResponse400.CodeEnum.LIMITEXCEEDED)
                .message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: BadContinuationToken) {
            val msg = "Bad continuation token, xRequestId=$xRequestId, reason=${ex.reason}"
            val response: InlineResponse400 = InlineResponse400()
                .code(InlineResponse400.CodeEnum.BADCONTINUATIONTOKEN)
                .message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: TException) {
            throw buildDarkApi5xxException("searchClaims", xRequestId, ex)
        }
    }

    @PreAuthorize("hasAuthority('party:write')")
    override fun updateClaimByID(
        @NotNull @Size(min = 1, max = 40) xRequestId: String?,
        @NotNull @Valid claimId: Long,
        @NotNull @Valid claimRevision: Int?,
        @NotNull @Valid changeset: List<Modification>?,
        @Size(min = 1, max = 40) xRequestDeadline: String?
    ): ResponseEntity<Void> {
        return try {
            log.info { "Process 'updateClaimByID' get started, requestId=$xRequestId, claimId=$claimId" }
            partyManagementService.checkStatus(xRequestId)
            deadlineChecker.checkDeadline(xRequestDeadline, xRequestId)
            claimManagementService.updateClaimById(keycloakService.partyId, claimId, claimRevision!!, changeset!!)
            log.info { "Successful update claim, xRequestId=$xRequestId, claimId=$claimId" }
            ResponseEntity<Void>(HttpStatus.OK)
        } catch (ex: DeadlineException) {
            val msg = ex.message
            val response: InlineResponse4003 = InlineResponse4003()
                .code(InlineResponse4003.CodeEnum.INVALIDDEADLINE)
                .message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: InvalidClaimStatus) {
            val msg = "Invalid claim status, xRequestId=$xRequestId, status=${ex.status}"
            val response: InlineResponse4003 = InlineResponse4003()
                .code(InlineResponse4003.CodeEnum.INVALIDCLAIMSTATUS)
                .message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: InvalidClaimRevision) {
            val msg = "Invalid claim revision, xRequestId=$xRequestId, claimRevision=$claimRevision"
            val response: InlineResponse4003 = InlineResponse4003()
                .code(InlineResponse4003.CodeEnum.INVALIDCLAIMREVISION)
                .message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: ChangesetConflict) {
            val msg = "Changeset conflict, xRequestId=$xRequestId, conflictedId=${ex.conflictedId}"
            val response: InlineResponse4003 = InlineResponse4003()
                .code(InlineResponse4003.CodeEnum.CHANGESETCONFLICT)
                .message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: InvalidChangeset) {
            val msg = "Invalid changeset, xRequestId=$xRequestId, reason='${ex.reason}'"
            val response: InlineResponse4003 = InlineResponse4003()
                .code(InlineResponse4003.CodeEnum.INVALIDCHANGESET)
                .message(msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: ClaimNotFound) {
            throw buildNotFoundException(xRequestId!!, claimId, ex)
        } catch (ex: TException) {
            throw buildDarkApi5xxException("updateClaimByID", xRequestId, ex)
        }
    }

    private fun buildNotFoundException(xRequestId: String, claimId: Long, ex: ClaimNotFound): NotFoundException {
        val msg = "Claim not found, claimId=$claimId, xRequestId=$xRequestId"
        val response = GeneralError().message(msg)
        return NotFoundException(msg, ex, response)
    }

    private fun buildDarkApi5xxException(
        methodName: String?,
        xRequestId: String?,
        ex: TException?
    ) = DarkApi5xxException(
        "Some TException while requesting api='$API_NAME', method='$methodName', xRequestId=$xRequestId",
        ex
    )

    companion object {
        const val API_NAME = "claim-management"
    }
}
