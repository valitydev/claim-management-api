package com.rbkmoney.claimmanagementapi.converter.party.contractor

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.damsel.domain.CountryCode
import com.rbkmoney.damsel.domain.CountryRef
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.domain.InternationalLegalEntity as ThriftInternationalLegalEntity
import com.rbkmoney.swag.claim_management.model.InternationalLegalEntity as SwagInternationalLegalEntity
import com.rbkmoney.swag.claim_management.model.LegalEntityType as SwagLegalEntityType

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
