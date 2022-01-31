package com.rbkmoney.claimmanagementapi.converter.party.shop

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.swag.claim_management.model.ShopModification
import org.springframework.stereotype.Component
import dev.vality.damsel.claim_management.ScheduleModification as ThriftScheduleModification
import dev.vality.damsel.domain.BusinessScheduleRef as ThriftBusinessScheduleRef
import dev.vality.swag.claim_management.model.BusinessScheduleRef as SwagBusinessScheduleRef
import dev.vality.swag.claim_management.model.ShopPayoutScheduleModification as SwagShopPayoutScheduleModification

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
