package com.rbkmoney.claimmanagementapi.converter.claim

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.damsel.claim_management.StatusChanged
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.claim_management.StatusModification as ThriftStatusModification
import com.rbkmoney.damsel.claim_management.StatusModificationUnit as ThriftStatusModificationUnit
import com.rbkmoney.swag.claim_management.model.StatusModification as SwagStatusModification
import com.rbkmoney.swag.claim_management.model.StatusModificationUnit as SwagStatusModificationUnit

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
