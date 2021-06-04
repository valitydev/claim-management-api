package com.rbkmoney.claimmanagementapi.converter.claim

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.claim_management.ExternalInfoModificationUnit as ThriftExternalInfoModificationUnit
import com.rbkmoney.swag.claim_management.model.ClaimModificationType as SwagClaimModificationType
import com.rbkmoney.swag.claim_management.model.ExternalInfoModificationUnit as SwagExternalInfoModificationUnit

@Component
class ClaimExternalInfoModificationUnitConverter :
    DarkApiConverter<ThriftExternalInfoModificationUnit, SwagExternalInfoModificationUnit> {

    override fun convertToThrift(value: SwagExternalInfoModificationUnit) =
        ThriftExternalInfoModificationUnit().apply {
            documentId = value.documentId
            roistatId = value.roistatId
        }

    override fun convertToSwag(value: ThriftExternalInfoModificationUnit) =
        SwagExternalInfoModificationUnit().apply {
            documentId = value.documentId
            roistatId = value.roistatId
            claimModificationType = SwagClaimModificationType.ClaimModificationTypeEnum.EXTERNALINFOMODIFICATIONUNIT
        }
}
