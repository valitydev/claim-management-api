package com.rbkmoney.claimmanagementapi.converter.claim

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.damsel.claim_management.FileChanged
import com.rbkmoney.damsel.claim_management.FileCreated
import com.rbkmoney.damsel.claim_management.FileDeleted
import com.rbkmoney.swag.claim_management.model.ClaimModificationType.ClaimModificationTypeEnum
import com.rbkmoney.swag.claim_management.model.FileModification.FileModificationTypeEnum
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.claim_management.FileModification as ThriftFileModification
import com.rbkmoney.damsel.claim_management.FileModificationUnit as ThriftFileModificationUnit
import com.rbkmoney.swag.claim_management.model.FileModification as SwagFileModification
import com.rbkmoney.swag.claim_management.model.FileModificationUnit as SwagFileModificationUnit

@Component
class ClaimFileModificationUnitConverter : DarkApiConverter<ThriftFileModificationUnit, SwagFileModificationUnit> {

    override fun convertToThrift(value: SwagFileModificationUnit) =
        ThriftFileModificationUnit(value.fileId, resolveFileModification(value.fileModification))

    override fun convertToSwag(value: ThriftFileModificationUnit) =
        SwagFileModificationUnit().apply {
            fileId = value.getId()
            claimModificationType = ClaimModificationTypeEnum.FILEMODIFICATIONUNIT
            fileModification = resolveFileModification(value.modification)
        }

    private fun resolveFileModification(value: ThriftFileModification) =
        when {
            value.isSetCreation -> FileModificationTypeEnum.FILECREATED
            value.isSetDeletion -> FileModificationTypeEnum.FILEDELETED
            value.isSetChanged -> FileModificationTypeEnum.FILECHANGED
            else -> throw IllegalArgumentException("Unknown FileModificationType in ThriftFileModificationUnit!")
        }.let {
            SwagFileModification().apply { fileModificationType = it }
        }

    private fun resolveFileModification(value: SwagFileModification): ThriftFileModification =
        when (value.fileModificationType) {
            FileModificationTypeEnum.FILECREATED -> ThriftFileModification.creation(FileCreated())
            FileModificationTypeEnum.FILECHANGED -> ThriftFileModification.changed(FileChanged())
            FileModificationTypeEnum.FILEDELETED -> ThriftFileModification.deletion(FileDeleted())
            else -> throw IllegalArgumentException(
                "Unknown FileModificationType ${value.fileModificationType} in SwagFileModificationUnit!"
            )
        }
}
