package com.rbkmoney.claimmanagementapi.converter.party.shop

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.swag.claim_management.model.ShopModification
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.claim_management.ScheduleModification as ThriftScheduleModification
import com.rbkmoney.damsel.domain.BusinessScheduleRef as ThriftBusinessScheduleRef
import com.rbkmoney.swag.claim_management.model.BusinessScheduleRef as SwagBusinessScheduleRef
import com.rbkmoney.swag.claim_management.model.ShopPayoutScheduleModification as SwagShopPayoutScheduleModification

@Component
class ShopPayoutScheduleModificationConverter :
    DarkApiConverter<ThriftScheduleModification, SwagShopPayoutScheduleModification> {

    override fun convertToThrift(value: SwagShopPayoutScheduleModification): ThriftScheduleModification =
        ThriftScheduleModification()
            .setSchedule(ThriftBusinessScheduleRef((value.schedule.id)))

    override fun convertToSwag(value: ThriftScheduleModification): SwagShopPayoutScheduleModification =
        SwagShopPayoutScheduleModification().apply {
            shopModificationType = ShopModification.ShopModificationTypeEnum.SHOPPAYOUTSCHEDULEMODIFICATION
            schedule = SwagBusinessScheduleRef().id(value.schedule.id)
        }
}
