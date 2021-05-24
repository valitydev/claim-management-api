package com.rbkmoney.claimmanagementapi.converter.claim

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.damsel.claim_management.ClaimAccepted
import com.rbkmoney.damsel.claim_management.ClaimDenied
import com.rbkmoney.damsel.claim_management.ClaimPending
import com.rbkmoney.damsel.claim_management.ClaimPendingAcceptance
import com.rbkmoney.damsel.claim_management.ClaimReview
import com.rbkmoney.damsel.claim_management.ClaimRevoked
import com.rbkmoney.swag.claim_management.model.ClaimModificationType.ClaimModificationTypeEnum
import com.rbkmoney.swag.claim_management.model.StatusModification.StatusModificationTypeEnum
import com.rbkmoney.swag.claim_management.model.StatusModificationUnit.StatusEnum
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.claim_management.ClaimStatus as ThriftClaimStatus
import com.rbkmoney.swag.claim_management.model.StatusModification as SwagStatusModification
import com.rbkmoney.swag.claim_management.model.StatusModificationUnit as SwagStatusModificationUnit

@Component
class ClaimStatusModificationConverter : DarkApiConverter<ThriftClaimStatus, SwagStatusModificationUnit> {

    override fun convertToThrift(value: SwagStatusModificationUnit): ThriftClaimStatus {
        val status = ThriftClaimStatus()
        when (value.status) {
            StatusEnum.PENDING -> status.pending = ClaimPending()
            StatusEnum.REVIEW -> status.review = ClaimReview()
            StatusEnum.PENDINGACCEPTANCE -> status.pendingAcceptance = ClaimPendingAcceptance()
            StatusEnum.ACCEPTED -> status.accepted = ClaimAccepted()
            StatusEnum.DENIED -> {
                status.denied = ClaimDenied()
                status.denied.setReason(value.reason)
            }
            StatusEnum.REVOKED -> {
                status.revoked = ClaimRevoked()
                status.revoked.setReason(value.reason)
            }
            else -> throw IllegalArgumentException("Unknown status ${value.status} in SwagStatusModificationUnit!")
        }
        return status
    }

    override fun convertToSwag(value: ThriftClaimStatus): SwagStatusModificationUnit {
        val status = SwagStatusModificationUnit()
        when {
            value.isSetPending -> status.status = StatusEnum.PENDING
            value.isSetReview -> status.status = StatusEnum.REVIEW
            value.isSetPendingAcceptance -> status.status = StatusEnum.PENDINGACCEPTANCE
            value.isSetAccepted -> status.status = StatusEnum.ACCEPTED
            value.isSetDenied -> {
                status.status = StatusEnum.DENIED
                status.reason = value.denied.getReason()
            }
            value.isSetRevoked -> {
                status.status = StatusEnum.REVOKED
                status.reason = value.revoked.getReason()
            }
            else -> {
                throw IllegalArgumentException("Unknown status in ThriftClaimStatus!")
            }
        }
        val statusModification = SwagStatusModification().apply {
            statusModificationType = StatusModificationTypeEnum.STATUSCHANGED
        }
        status.apply {
            claimModificationType = ClaimModificationTypeEnum.STATUSMODIFICATIONUNIT
            this.statusModification = statusModification
        }
        return status
    }
}
