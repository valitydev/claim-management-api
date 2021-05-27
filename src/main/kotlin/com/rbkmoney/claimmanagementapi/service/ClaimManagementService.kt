package com.rbkmoney.claimmanagementapi.service

import com.rbkmoney.claimmanagementapi.converter.ClaimManagementConverter
import com.rbkmoney.damsel.claim_management.ClaimManagementSrv
import com.rbkmoney.swag.claim_management.model.Claim
import com.rbkmoney.swag.claim_management.model.InlineResponse200
import com.rbkmoney.swag.claim_management.model.Modification
import org.springframework.stereotype.Service

@Service
class ClaimManagementService(
    private val claimManagementClient: ClaimManagementSrv.Iface,
    private val claimManagementConverter: ClaimManagementConverter
) {

    fun createClaim(
        partyId: String,
        changeset: List<Modification>
    ): Claim {
        val modificationList = claimManagementConverter.convertModificationUnitToThrift(changeset)
        val claim = claimManagementClient.createClaim(partyId, modificationList)
        return claimManagementConverter.convertClaimToSwag(claim)
    }

    fun getClaimById(partyId: String, claimId: Long): Claim {
        val claim = claimManagementClient.getClaim(partyId, claimId)
        return claimManagementConverter.convertClaimToSwag(claim)
    }

    fun revokeClaimById(partyId: String, claimId: Long, claimRevision: Int, reason: String?) =
        claimManagementClient.revokeClaim(partyId, claimId, claimRevision, reason)

    fun requestClaimReviewById(partyId: String, claimId: Long, claimRevision: Int) {
        claimManagementClient.requestClaimReview(partyId, claimId, claimRevision)
    }

    fun searchClaims(
        partyId: String,
        limit: Int,
        continuationToken: String?,
        claimId: Long?,
        claimStatuses: List<String>?
    ): InlineResponse200 {
        val claimSearchQuery = claimManagementConverter.convertSearchClaimsToThrift(
            partyId,
            claimId,
            limit,
            continuationToken,
            claimStatuses
        )
        val claimSearchResponse = claimManagementClient.searchClaims(claimSearchQuery)
        return InlineResponse200()
            .result(claimManagementConverter.convertClaimListToSwag(claimSearchResponse.getResult()))
            .continuationToken(claimSearchResponse.continuationToken)
    }

    fun updateClaimById(
        partyId: String,
        claimId: Long,
        claimRevision: Int,
        changeset: List<Modification>
    ) {
        val modificationList = claimManagementConverter.convertModificationUnitToThrift(changeset)
        claimManagementClient.updateClaim(partyId, claimId, claimRevision, modificationList)
    }
}
