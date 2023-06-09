package com.rbkmoney.claimmanagementapi.converter.identity

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.damsel.msgpack.Value
import dev.vality.swag.claim_management.model.IdentityModification.IdentityModificationTypeEnum
import org.springframework.stereotype.Component
import dev.vality.damsel.claim_management.IdentityParams as ThriftIdentityParams
import dev.vality.swag.claim_management.model.IdentityCreationModification as SwagIdentityCreationModification

@Component
class IdentityModificationCreationConverter : DarkApiConverter<ThriftIdentityParams, SwagIdentityCreationModification> {

    override fun convertToThrift(value: SwagIdentityCreationModification): ThriftIdentityParams {
        return ThriftIdentityParams().apply {
            partyId = value.partyID
            name = value.name
            provider = value.provider
            metadata = if (value.metadata != null) value.metadata as MutableMap<String, Value> else emptyMap()
        }
    }

    override fun convertToSwag(value: ThriftIdentityParams): SwagIdentityCreationModification {
        val swagIdentityParams = SwagIdentityCreationModification().apply {
            partyID = value.partyId
            name = value.name
            provider = value.provider
            metadata = value.metadata
            identityModificationType = IdentityModificationTypeEnum.IDENTITYCREATIONMODIFICATION
        }
        return swagIdentityParams
    }
}
