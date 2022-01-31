package com.rbkmoney.claimmanagementapi.converter.claim

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.damsel.claim_management.DocumentChanged
import dev.vality.damsel.claim_management.DocumentCreated
import dev.vality.swag.claim_management.model.ClaimModificationType.ClaimModificationTypeEnum
import dev.vality.swag.claim_management.model.DocumentModification.DocumentModificationTypeEnum
import org.springframework.stereotype.Component
import dev.vality.damsel.claim_management.DocumentModification as ThriftDocumentModification
import dev.vality.damsel.claim_management.DocumentModificationUnit as ThriftDocumentModificationUnit
import dev.vality.swag.claim_management.model.DocumentModification as SwagDocumentModification
import dev.vality.swag.claim_management.model.DocumentModificationUnit as SwagDocumentModificationUnit

@Component
class ClaimDocumentModificationUnitConverter :
    DarkApiConverter<ThriftDocumentModificationUnit, SwagDocumentModificationUnit> {

    override fun convertToThrift(value: SwagDocumentModificationUnit) =
        ThriftDocumentModificationUnit(value.documentId, resolveDocumentModification(value.documentModification))

    override fun convertToSwag(value: ThriftDocumentModificationUnit) =
        SwagDocumentModificationUnit().apply {
            documentId = value.getId()
            claimModificationType = ClaimModificationTypeEnum.DOCUMENTMODIFICATIONUNIT
            documentModification = resolveDocumentModification(value.getModification())
        }

    private fun resolveDocumentModification(value: ThriftDocumentModification): SwagDocumentModification =
        when {
            value.isSetCreation -> DocumentModificationTypeEnum.DOCUMENTCREATED
            value.isSetChanged -> DocumentModificationTypeEnum.DOCUMENTCHANGED
            else -> throw IllegalArgumentException(
                "Unknown DocumentModificationType in ThriftDocumentModificationUnit!"
            )
        }.let {
            SwagDocumentModification().apply { documentModificationType = it }
        }

    private fun resolveDocumentModification(value: SwagDocumentModification): ThriftDocumentModification =
        when (value.documentModificationType) {
            DocumentModificationTypeEnum.DOCUMENTCREATED -> ThriftDocumentModification.creation(DocumentCreated())
            DocumentModificationTypeEnum.DOCUMENTCHANGED -> ThriftDocumentModification.changed(DocumentChanged())
            else -> throw IllegalArgumentException(
                "Unknown DocumentModificationType ${value.documentModificationType} in SwagDocumentModificationUnit!"
            )
        }
}
