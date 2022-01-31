package com.rbkmoney.claimmanagementapi.converter.party.contractor

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import org.springframework.stereotype.Component
import dev.vality.damsel.domain.LegalEntity as ThriftLegalEntity
import dev.vality.swag.claim_management.model.ContractorType as SwagContractorType
import dev.vality.swag.claim_management.model.InternationalLegalEntity as SwagInternationalLegalEntity
import dev.vality.swag.claim_management.model.LegalEntity as SwagLegalEntity
import dev.vality.swag.claim_management.model.LegalEntityType as SwagLegalEntityType
import dev.vality.swag.claim_management.model.RussianLegalEntity as SwagRussianLegalEntity

@Component
class ClaimLegalEntityConverter(
    private val internationalLegalEntityConverter: InternationalLegalEntityConverter,
    private val russianLegalEntityConverter: RussianLegalEntityConverter
) : DarkApiConverter<ThriftLegalEntity, SwagLegalEntity> {

    override fun convertToThrift(value: SwagLegalEntity): ThriftLegalEntity {
        val thriftLegalEntity = ThriftLegalEntity()
        val swagLegalEntityType = value.legalEntityType
        when (swagLegalEntityType.legalEntityType) {
            SwagLegalEntityType.LegalEntityTypeEnum.RUSSIANLEGALENTITY -> {
                val swagRussianLegalEntity = swagLegalEntityType as SwagRussianLegalEntity
                thriftLegalEntity.russianLegalEntity =
                    russianLegalEntityConverter.convertToThrift(swagRussianLegalEntity)
            }
            SwagLegalEntityType.LegalEntityTypeEnum.INTERNATIONALLEGALENTITY -> {
                val swagInternationalLegalEntity = swagLegalEntityType as SwagInternationalLegalEntity
                thriftLegalEntity.internationalLegalEntity =
                    internationalLegalEntityConverter.convertToThrift(swagInternationalLegalEntity)
            }
            else -> throw IllegalArgumentException("Unknown legal entity type: $swagLegalEntityType")
        }
        return thriftLegalEntity
    }

    override fun convertToSwag(value: ThriftLegalEntity): SwagLegalEntity {
        val swagLegalEntityType = when {
            value.isSetRussianLegalEntity -> {
                val russianLegalEntity = value.russianLegalEntity
                russianLegalEntityConverter.convertToSwag(russianLegalEntity)
            }
            value.isSetInternationalLegalEntity -> {
                val internationalLegalEntity = value.internationalLegalEntity
                internationalLegalEntityConverter.convertToSwag(internationalLegalEntity)
            }
            else -> {
                throw IllegalArgumentException("Unknown legal entity type!")
            }
        }
        return SwagLegalEntity().apply {
            contractorType = SwagContractorType.ContractorTypeEnum.LEGALENTITY
            legalEntityType = swagLegalEntityType
        }
    }
}
