package com.rbkmoney.claimmanagementapi.converter.claim

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.damsel.claim_management.StatusChanged
import org.springframework.stereotype.Component
import dev.vality.damsel.claim_management.StatusModification as ThriftStatusModification
import dev.vality.damsel.claim_management.StatusModificationUnit as ThriftStatusModificationUnit
import dev.vality.swag.claim_management.model.StatusModification as SwagStatusModification
import dev.vality.swag.claim_management.model.StatusModificationUnit as SwagStatusModificationUnit

@Component
class ClaimStatusModificationUnitConverter(
    private val claimStatusModificationConverter: ClaimStatusModificationConverter
) : DarkApiConverter<ThriftStatusModificationUnit, SwagStatusModificationUnit> {

    override fun convertToThrift(value: SwagStatusModificationUnit): ThriftStatusModificationUnit {
        if (value.statusModification.statusModificationType != SwagStatusModification.StatusModificationTypeEnum.STATUSCHANGED) {
            throw IllegalArgumentException("Change field for SwagStatusModificationUnit must be set!")
        }
        val statusModification = ThriftStatusModification.change(StatusChanged())
        return ThriftStatusModificationUnit().apply {
            status = claimStatusModificationConverter.convertToThrift(value)
            modification = statusModification
        }
    }

    override fun convertToSwag(value: ThriftStatusModificationUnit): SwagStatusModificationUnit {
        if (!value.getModification().isSetChange) {
            throw IllegalArgumentException("Unknown status modification type!")
        }
        return claimStatusModificationConverter.convertToSwag(value.getStatus())
    }
}
