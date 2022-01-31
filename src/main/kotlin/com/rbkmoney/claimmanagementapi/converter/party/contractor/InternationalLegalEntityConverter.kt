package com.rbkmoney.claimmanagementapi.converter.party.contractor

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.damsel.domain.CountryCode
import dev.vality.damsel.domain.CountryRef
import org.springframework.stereotype.Component
import dev.vality.damsel.domain.InternationalLegalEntity as ThriftInternationalLegalEntity
import dev.vality.swag.claim_management.model.InternationalLegalEntity as SwagInternationalLegalEntity
import dev.vality.swag.claim_management.model.LegalEntityType as SwagLegalEntityType

@Component
class InternationalLegalEntityConverter :
    DarkApiConverter<ThriftInternationalLegalEntity, SwagInternationalLegalEntity> {

    override fun convertToThrift(
        value: SwagInternationalLegalEntity
    ): ThriftInternationalLegalEntity =
        ThriftInternationalLegalEntity()
            .setLegalName(value.legalName)
            .setActualAddress(value.actualAddress)
            .setRegisteredAddress(value.registeredAddress)
            .setRegisteredNumber(value.registeredNumber)
            .setTradingName(value.tradingName)
            .setCountry(
                if (!value.country.isNullOrEmpty())
                    CountryRef(CountryCode.valueOf(value.country))
                else null
            )

    override fun convertToSwag(
        value: ThriftInternationalLegalEntity
    ) = SwagInternationalLegalEntity().apply {
        legalEntityType = SwagLegalEntityType.LegalEntityTypeEnum.INTERNATIONALLEGALENTITY
        legalName = value.legalName
        tradingName = value.tradingName
        actualAddress = value.actualAddress
        registeredAddress = value.registeredAddress
        registeredNumber = value.registeredNumber
        country = if (value.isSetCountry) value.country.id.name else null
    }
}
