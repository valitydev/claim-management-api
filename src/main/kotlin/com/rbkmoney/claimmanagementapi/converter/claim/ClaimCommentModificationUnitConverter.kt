package com.rbkmoney.claimmanagementapi.converter.claim

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.damsel.claim_management.CommentChanged
import com.rbkmoney.damsel.claim_management.CommentCreated
import com.rbkmoney.damsel.claim_management.CommentDeleted
import com.rbkmoney.swag.claim_management.model.ClaimModificationType.ClaimModificationTypeEnum
import com.rbkmoney.swag.claim_management.model.CommentModification.CommentModificationTypeEnum
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.claim_management.CommentModification as ThriftCommentModification
import com.rbkmoney.damsel.claim_management.CommentModificationUnit as ThriftCommentModificationUnit
import com.rbkmoney.swag.claim_management.model.CommentModification as SwagCommentModification
import com.rbkmoney.swag.claim_management.model.CommentModificationUnit as SwagCommentModificationUnit

@Component
class ClaimCommentModificationUnitConverter :
    DarkApiConverter<ThriftCommentModificationUnit, SwagCommentModificationUnit> {

    override fun convertToThrift(value: SwagCommentModificationUnit) =
        ThriftCommentModificationUnit(value.commentId, resolveCommentModification(value.commentModification))

    override fun convertToSwag(value: ThriftCommentModificationUnit) =
        SwagCommentModificationUnit().apply {
            commentId = value.getId()
            claimModificationType = ClaimModificationTypeEnum.COMMENTMODIFICATIONUNIT
            commentModification = resolveCommentModification(value.getModification())
        }

    private fun resolveCommentModification(value: ThriftCommentModification): SwagCommentModification =
        when {
            value.isSetCreation -> CommentModificationTypeEnum.COMMENTCREATED
            value.isSetDeletion -> CommentModificationTypeEnum.COMMENTDELETED
            value.isSetChanged -> CommentModificationTypeEnum.COMMENTCHANGED
            else -> throw IllegalArgumentException("Unknown CommentModificationType in ThriftCommentModificationUnit!")
        }.let {
            SwagCommentModification().apply { commentModificationType = it }
        }

    private fun resolveCommentModification(value: SwagCommentModification): ThriftCommentModification =
        when (value.commentModificationType) {
            CommentModificationTypeEnum.COMMENTCREATED -> ThriftCommentModification.creation(CommentCreated())
            CommentModificationTypeEnum.COMMENTDELETED -> ThriftCommentModification.deletion(CommentDeleted())
            CommentModificationTypeEnum.COMMENTCHANGED -> ThriftCommentModification.changed(CommentChanged())
            else -> throw IllegalArgumentException(
                "Unknown CommentModificationType ${value.commentModificationType} in SwagCommentModificationUnit!"
            )
        }
}
