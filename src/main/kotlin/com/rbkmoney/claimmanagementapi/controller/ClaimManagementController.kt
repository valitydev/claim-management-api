package com.rbkmoney.claimmanagementapi.controller

import com.rbkmoney.claimmanagementapi.exception.DeadlineException
import com.rbkmoney.claimmanagementapi.exception.client.BadRequestException
import com.rbkmoney.claimmanagementapi.exception.client.NotFoundException
import com.rbkmoney.claimmanagementapi.exception.server.DarkApi5xxException
import com.rbkmoney.claimmanagementapi.service.ClaimManagementService
import com.rbkmoney.claimmanagementapi.service.PartyManagementService
import com.rbkmoney.claimmanagementapi.service.security.KeycloakService
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
import com.rbkmoney.swag.claim_management.model.InlineResponse200
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
        @NotNull @Valid partyId: String?,
        @NotNull @Valid changeset: List<Modification>?,
        @Size(min = 1, max = 40) xRequestDeadline: String?
    ): ResponseEntity<Claim> =
        performRequest("createClaim", xRequestId!!) {
            log.info { "Process 'createClaim' get started, xRequestId=$xRequestId" }
            partyManagementService.checkStatus(xRequestId)
            deadlineChecker.checkDeadline(xRequestDeadline, xRequestId)
            val claim = claimManagementService.createClaim(keycloakService.partyId, changeset!!)
            log.info { "Claim created, xRequestId=$xRequestId, claimId=${claim.id}" }
            ResponseEntity.ok(claim)
        }

    @PreAuthorize("hasAuthority('party:read')")
    override fun getClaimByID(
        @NotNull @Size(min = 1, max = 40) xRequestId: String?,
        @NotNull @Valid claimId: Long?,
        @Size(min = 1, max = 40) xRequestDeadline: String?
    ): ResponseEntity<Claim> =
        performRequest("getClaimByID", xRequestId!!, claimId!!) {
            log.info { "Process 'getClaimByID' get started, xRequestId=$xRequestId, claimId=$claimId" }
            partyManagementService.checkStatus(xRequestId)
            deadlineChecker.checkDeadline(xRequestDeadline, xRequestId)
            val claim = claimManagementService.getClaimById(keycloakService.partyId, claimId)
            log.info { "Got a claim, xRequestId=$xRequestId, claimId=$claimId" }

            ResponseEntity.ok(claim)
        }

    @PreAuthorize("hasAuthority('party:write')")
    override fun revokeClaimByID(
        @NotNull @Size(min = 1, max = 40) xRequestId: String?,
        @NotNull @Valid partyId: String?,
        @NotNull @Valid claimId: Long?,
        @NotNull @Valid claimRevision: Int?,
        @Size(min = 1, max = 40) xRequestDeadline: String?,
        @Valid reason: String?
    ): ResponseEntity<Void> =
        performRequest("revokeClaimByID", xRequestId!!, claimId!!, claimRevision!!) {
            log.info { "Process 'revokeClaimByID' get started, xRequestId=$xRequestId, claimId=$claimId" }
            partyManagementService.checkStatus(xRequestId)
            deadlineChecker.checkDeadline(xRequestDeadline, xRequestId)
            claimManagementService.revokeClaimById(keycloakService.partyId, claimId, claimRevision, reason)
            log.info { "Successful revoke claim, xRequestId=$xRequestId, claimId=$claimId" }

            ResponseEntity<Void>(HttpStatus.OK)
        }

    @PreAuthorize("hasAuthority('party:write')")
    override fun requestReviewClaimByID(
        @NotNull @Size(min = 1, max = 40) xRequestId: String?,
        @NotNull @Valid partyId: String?,
        @NotNull @Valid claimId: Long?,
        @NotNull @Valid claimRevision: Int?,
        @Size(min = 1, max = 40) xRequestDeadline: String?
    ): ResponseEntity<Void> =
        performRequest("requestClaimReviewById", xRequestId!!, claimId!!, claimRevision!!) {
            log.info { "Process 'requestReviewClaimByID' get started, xRequestId=$xRequestId, claimId=$claimId" }
            partyManagementService.checkStatus(xRequestId)
            deadlineChecker.checkDeadline(xRequestDeadline, xRequestId)
            claimManagementService.requestClaimReviewById(keycloakService.partyId, claimId, claimRevision)
            log.info { "Successful request claim review, xRequestId=$xRequestId, claimId=$claimId" }

            ResponseEntity<Void>(HttpStatus.OK)
        }

    @PreAuthorize("hasAuthority('party:read')")
    override fun searchClaims(
        @NotNull @Size(min = 1, max = 40) xRequestId: String?,
        @NotNull @Min(1L) @Max(1000L) @Valid limit: Int?,
        @Size(min = 1, max = 40) xRequestDeadline: String?,
        @Size(min = 1, max = 40) continuationToken: String?,
        @Valid claimId: Long?,
        @Valid claimStatuses: List<String>?
    ): ResponseEntity<InlineResponse200> =
        performRequest("searchClaims", xRequestId!!, claimId!!) {
            log.info { "Process 'searchClaims' get started, xRequestId=$xRequestId, claimId=$claimId" }
            partyManagementService.checkStatus(xRequestId)
            deadlineChecker.checkDeadline(xRequestDeadline, xRequestId)
            val response = claimManagementService.searchClaims(
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
        }

    @PreAuthorize("hasAuthority('party:write')")
    override fun updateClaimByID(
        @NotNull @Size(min = 1, max = 40) xRequestId: String?,
        @NotNull @Valid partyId: String?,
        @NotNull @Valid claimId: Long?,
        @NotNull @Valid claimRevision: Int?,
        @NotNull @Valid changeset: List<Modification>?,
        @Size(min = 1, max = 40) xRequestDeadline: String?
    ): ResponseEntity<Void> =
        performRequest("updateClaimByID", xRequestId!!, claimId!!, claimRevision!!) {
            log.info { "Process 'updateClaimByID' get started, requestId=$xRequestId, claimId=$claimId" }
            partyManagementService.checkStatus(xRequestId)
            deadlineChecker.checkDeadline(xRequestDeadline, xRequestId)
            claimManagementService.updateClaimById(keycloakService.partyId, claimId, claimRevision, changeset!!)
            log.info { "Successful update claim, xRequestId=$xRequestId, claimId=$claimId" }

            ResponseEntity<Void>(HttpStatus.OK)
        }

    private fun <T> performRequest(
        methodName: String,
        xRequestId: String,
        claimId: Long? = null,
        claimRevision: Int? = null,
        operation: () -> ResponseEntity<T>
    ): ResponseEntity<T> =
        try {
            operation.invoke()
        } catch (ex: DeadlineException) {
            val msg = ex.message
            val response = ErrorData(ErrorData.Code.INVALIDDEADLINE, msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: InvalidClaimStatus) {
            val msg = "Invalid claim status, xRequestId=$xRequestId, status=${ex.status}"
            val response = ErrorData(ErrorData.Code.INVALIDCLAIMSTATUS, msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: InvalidClaimRevision) {
            val msg = "Invalid claim revision, xRequestId=$xRequestId, claimRevision=$claimRevision"
            val response = ErrorData(ErrorData.Code.INVALIDCLAIMREVISION, msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: ChangesetConflict) {
            val msg = "Changeset conflict, xRequestId=$xRequestId, conflictedId=${ex.conflictedId}"
            val response = ErrorData(ErrorData.Code.CHANGESETCONFLICT, msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: InvalidChangeset) {
            val msg = "Invalid changeset, xRequestId=$xRequestId, reason='${ex.reason}'"
            val response = ErrorData(ErrorData.Code.INVALIDCHANGESET, msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: LimitExceeded) {
            val msg = "Limit exceeded, xRequestId=$xRequestId"
            val response = ErrorData(ErrorData.Code.LIMITEXCEEDED, msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: BadContinuationToken) {
            val msg = "Bad continuation token, xRequestId=$xRequestId, reason=${ex.reason}"
            val response = ErrorData(ErrorData.Code.BADCONTINUATIONTOKEN, msg)
            throw BadRequestException(msg, ex, response)
        } catch (ex: ClaimNotFound) {
            val msg = "Claim not found, claimId=$claimId, xRequestId=$xRequestId"
            val response = ErrorData(message = msg)
            throw NotFoundException(msg, ex, response)
        } catch (ex: TException) {
            throw DarkApi5xxException(
                "Some TException while requesting api='$API_NAME', method='$methodName', xRequestId=$xRequestId",
                ex
            )
        }

    companion object {
        const val API_NAME = "claim-management"
    }
}
