package com.rbkmoney.claimmanagementapi.converter

import com.rbkmoney.damsel.claim_management.Claim
import com.rbkmoney.damsel.claim_management.ClaimSearchQuery
import com.rbkmoney.damsel.claim_management.ClaimStatus
import com.rbkmoney.damsel.claim_management.ModificationUnit
import com.rbkmoney.swag.claim_management.model.ClaimChangeset
import com.rbkmoney.swag.claim_management.model.Modification

interface ClaimManagementConverter {
    fun convertClaimToSwag(sourceClaim: Claim): com.rbkmoney.swag.claim_management.model.Claim
    fun convertClaimListToSwag(sourceClaimList: List<Claim>): List<com.rbkmoney.swag.claim_management.model.Claim>
    fun convertClaimChangesetToSwag(changeset: List<ModificationUnit>): ClaimChangeset
    fun convertModificationUnitToThrift(unitModifications: List<Modification>): List<com.rbkmoney.damsel.claim_management.Modification>
    fun convertSearchClaimsToThrift(
        partyId: String?,
        claimId: Long?,
        limit: Int,
        continuationToken: String?,
        claimStatuses: List<String>?
    ): ClaimSearchQuery

    fun convertStatusesToThrift(sourceClaimStatuses: List<String>?): List<ClaimStatus>?
}
