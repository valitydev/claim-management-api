package com.rbkmoney.claimmanagementapi.converter.claim

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.swag.claim_management.model.ClaimModificationType.ClaimModificationTypeEnum
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.claim_management.ClaimModification as ThriftClaimModification
import com.rbkmoney.damsel.claim_management.Modification as ThriftModification
import com.rbkmoney.swag.claim_management.model.ClaimModification as SwagClaimModification
import com.rbkmoney.swag.claim_management.model.CommentModificationUnit as SwagCommentModificationUnit
import com.rbkmoney.swag.claim_management.model.DocumentModificationUnit as SwagDocumentModificationUnit
import com.rbkmoney.swag.claim_management.model.FileModificationUnit as SwagFileModificationUnit
import com.rbkmoney.swag.claim_management.model.Modification as SwagModification
import com.rbkmoney.swag.claim_management.model.StatusModificationUnit as SwagStatusModificationUnit

@Component
class ClaimModificationConverter(
    private val documentModificationUnitConverter: ClaimDocumentModificationUnitConverter,
    private val commentModificationUnitConverter: ClaimCommentModificationUnitConverter,
    private val statusModificationUnitConverter: ClaimStatusModificationUnitConverter,
    private val fileModificationUnitConverter: ClaimFileModificationUnitConverter
) : DarkApiConverter<ThriftModification, SwagClaimModification> {

    override fun convertToThrift(value: SwagClaimModification): ThriftModification {
        val modification = ThriftModification()
        val claimModification = ThriftClaimModification() // todo: напилить проверку на null
        val swagClaimModificationType = value.claimModificationType
        when (swagClaimModificationType.claimModificationType) {
            ClaimModificationTypeEnum.DOCUMENTMODIFICATIONUNIT -> {
                val swagDocModification = swagClaimModificationType as SwagDocumentModificationUnit
                claimModification.documentModification =
                    documentModificationUnitConverter.convertToThrift(swagDocModification)
            }
            ClaimModificationTypeEnum.COMMENTMODIFICATIONUNIT -> {
                val commentModificationUnit = swagClaimModificationType as SwagCommentModificationUnit
                claimModification.commentModification =
                    commentModificationUnitConverter.convertToThrift(commentModificationUnit)
            }
            ClaimModificationTypeEnum.STATUSMODIFICATIONUNIT -> {
                val swagStatusModificationUnit = swagClaimModificationType as SwagStatusModificationUnit
                claimModification.statusModification =
                    statusModificationUnitConverter.convertToThrift(swagStatusModificationUnit)
            }
            ClaimModificationTypeEnum.FILEMODIFICATIONUNIT -> {
                val fileModificationUnit = swagClaimModificationType as SwagFileModificationUnit
                claimModification.fileModification = fileModificationUnitConverter.convertToThrift(fileModificationUnit)
            }
            else -> throw IllegalArgumentException("Unknown claim modification type: $swagClaimModificationType")
        }
        modification.claimModification = claimModification
        return modification
    }

    override fun convertToSwag(value: ThriftModification): SwagClaimModification {
        val swagClaimModification = SwagClaimModification()
        val claimModification = value.claimModification
        when {
            claimModification.isSetDocumentModification -> {
                val documentModification = claimModification.documentModification
                swagClaimModification.claimModificationType =
                    documentModificationUnitConverter.convertToSwag(documentModification)
            }
            claimModification.isSetCommentModification -> {
                val commentModification = claimModification.commentModification
                swagClaimModification.claimModificationType =
                    commentModificationUnitConverter.convertToSwag(commentModification)
            }
            claimModification.isSetStatusModification -> {
                val statusModification = claimModification.statusModification
                swagClaimModification.claimModificationType =
                    statusModificationUnitConverter.convertToSwag(statusModification)
            }
            claimModification.isSetFileModification -> {
                val fileModificationUnit = claimModification.fileModification
                swagClaimModification.claimModificationType =
                    fileModificationUnitConverter.convertToSwag(fileModificationUnit)
            }
            else -> throw IllegalArgumentException("Unknown claim modification type")
        }
        swagClaimModification.modificationType = SwagModification.ModificationTypeEnum.CLAIMMODIFICATION
        return swagClaimModification
    }
}
