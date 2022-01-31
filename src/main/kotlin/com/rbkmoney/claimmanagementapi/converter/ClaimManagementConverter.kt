package com.rbkmoney.claimmanagementapi.converter

import dev.vality.damsel.claim_management.Claim
import dev.vality.damsel.claim_management.ClaimSearchQuery
import dev.vality.damsel.claim_management.ClaimStatus
import dev.vality.damsel.claim_management.ModificationUnit
import dev.vality.swag.claim_management.model.ClaimChangeset
import dev.vality.swag.claim_management.model.Modification

interface ClaimManagementConverter {
    fun convertClaimToSwag(sourceClaim: Claim): dev.vality.swag.claim_management.model.Claim
    fun convertClaimListToSwag(sourceClaimList: List<Claim>): List<dev.vality.swag.claim_management.model.Claim>
    fun convertClaimChangesetToSwag(changeset: List<ModificationUnit>): ClaimChangeset
    fun convertModificationUnitToThrift(unitModifications: List<Modification>): List<dev.vality.damsel.claim_management.Modification>
    fun convertSearchClaimsToThrift(
        partyId: String?,
        claimId: Long?,
        limit: Int,
        continuationToken: String?,
        claimStatuses: List<String>?
    ): ClaimSearchQuery

    fun convertStatusesToThrift(sourceClaimStatuses: List<String>?): List<ClaimStatus>?
}
