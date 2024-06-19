package com.rbkmoney.claimmanagementapi.converter.party.additional

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import org.springframework.stereotype.Component
import dev.vality.damsel.claim_management.AdditionalInfoModificationUnit as ThriftAdditionalInfoModificationUnit
import dev.vality.swag.claim_management.model.AdditionalInfoModificationUnit as SwagAdditionalInfoModificationUnit

@Component
class AdditionalInfoModificationConverter :
    DarkApiConverter<ThriftAdditionalInfoModificationUnit, SwagAdditionalInfoModificationUnit> {

    override fun convertToThrift(value: SwagAdditionalInfoModificationUnit) =
        ThriftAdditionalInfoModificationUnit()
            .setComment(value.comment)
            .setPartyName(value.partyName)
            .setManagerContactEmails(value.managerContactEmails)

    override fun convertToSwag(value: ThriftAdditionalInfoModificationUnit): SwagAdditionalInfoModificationUnit =
        SwagAdditionalInfoModificationUnit().apply {
            comment = value.comment
            partyName = value.partyName
            managerContactEmails = value.managerContactEmails
        }
}
