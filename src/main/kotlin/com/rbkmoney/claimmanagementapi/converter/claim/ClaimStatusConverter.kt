package com.rbkmoney.claimmanagementapi.converter.claim

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.claimmanagementapi.converter.claim.ThriftClaimStatus.ACCEPTED
import com.rbkmoney.claimmanagementapi.converter.claim.ThriftClaimStatus.DENIED
import com.rbkmoney.claimmanagementapi.converter.claim.ThriftClaimStatus.PENDING
import com.rbkmoney.claimmanagementapi.converter.claim.ThriftClaimStatus.PENDING_ACCEPTANCE
import com.rbkmoney.claimmanagementapi.converter.claim.ThriftClaimStatus.REVIEW
import com.rbkmoney.claimmanagementapi.converter.claim.ThriftClaimStatus.REVOKED
import dev.vality.damsel.claim_management.*
import org.springframework.stereotype.Component

@Component
class ClaimStatusConverter : DarkApiConverter<ClaimStatus, String> {

    override fun convertToThrift(value: String): ClaimStatus {
        val status = ClaimStatus()
        when (value) {
            PENDING -> status.pending = ClaimPending()
            REVIEW -> status.review = ClaimReview()
            PENDING_ACCEPTANCE -> status.pendingAcceptance = ClaimPendingAcceptance()
            ACCEPTED -> status.accepted = ClaimAccepted()
            DENIED -> status.denied = ClaimDenied()
            REVOKED -> status.revoked = ClaimRevoked()
            else -> throw IllegalArgumentException("Unknown ClaimStatus $value")
        }
        return status
    }

    override fun convertToSwag(value: ClaimStatus): String =
        when {
            value.isSetPending -> PENDING
            value.isSetReview -> REVIEW
            value.isSetPendingAcceptance -> PENDING_ACCEPTANCE
            value.isSetAccepted -> ACCEPTED
            value.isSetDenied -> DENIED
            value.isSetRevoked -> REVOKED
            else -> throw IllegalArgumentException("Claim status not found: $value")
        }
}
