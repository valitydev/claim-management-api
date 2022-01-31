package com.rbkmoney.claimmanagementapi.converter.party.contract

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.damsel.claim_management.PayoutToolModification
import dev.vality.damsel.claim_management.PayoutToolParams
import dev.vality.damsel.domain.CurrencyRef
import dev.vality.swag.claim_management.model.ContractPayoutToolModification.PayoutToolModificationTypeEnum.CONTRACTPAYOUTTOOLCREATIONMODIFICATION
import dev.vality.swag.claim_management.model.ContractPayoutToolModification.PayoutToolModificationTypeEnum.CONTRACTPAYOUTTOOLINFOMODIFICATION
import org.springframework.stereotype.Component
import dev.vality.damsel.claim_management.PayoutToolModificationUnit as ThriftPayoutToolModificationUnit
import dev.vality.swag.claim_management.model.ContractModification as SwagContractModification
import dev.vality.swag.claim_management.model.ContractPayoutToolCreationModification as SwagContractPayoutToolCreationModification
import dev.vality.swag.claim_management.model.ContractPayoutToolInfoModification as SwagContractPayoutToolInfoModification
import dev.vality.swag.claim_management.model.ContractPayoutToolModificationUnit as SwagContractPayoutToolModificationUnit
import dev.vality.swag.claim_management.model.CurrencyRef as SwagCurrencyRef

@Component
class PayoutToolModificationUnitConverter(
    private val payoutToolInfoConverter: PayoutToolInfoConverter
) : DarkApiConverter<ThriftPayoutToolModificationUnit, SwagContractPayoutToolModificationUnit> {

    override fun convertToThrift(value: SwagContractPayoutToolModificationUnit): ThriftPayoutToolModificationUnit {
        val thriftPayoutToolModification = PayoutToolModification()
        val swagPayoutToolModification = value.modification
        when (swagPayoutToolModification.payoutToolModificationType) {
            CONTRACTPAYOUTTOOLCREATIONMODIFICATION -> {
                val swagContractPayoutToolCreation =
                    swagPayoutToolModification as SwagContractPayoutToolCreationModification
                val creation = PayoutToolParams().apply {
                    currency = CurrencyRef().setSymbolicCode(swagContractPayoutToolCreation.currency.symbolicCode)
                    toolInfo = payoutToolInfoConverter.convertToThrift(swagContractPayoutToolCreation.toolInfo)
                }
                thriftPayoutToolModification.creation = creation
            }
            CONTRACTPAYOUTTOOLINFOMODIFICATION -> {
                val swagPayoutToolInfo = swagPayoutToolModification as SwagContractPayoutToolInfoModification
                thriftPayoutToolModification.infoModification =
                    payoutToolInfoConverter.convertToThrift(swagPayoutToolInfo.payoutToolInfo)
            }
            else -> throw IllegalArgumentException(
                "Unknown PayoutTool modification type: ${swagPayoutToolModification.payoutToolModificationType}"
            )
        }

        return ThriftPayoutToolModificationUnit().apply {
            payoutToolId = value.payoutToolID
            modification = thriftPayoutToolModification
        }
    }

    override fun convertToSwag(value: ThriftPayoutToolModificationUnit): SwagContractPayoutToolModificationUnit {
        val thriftPayoutToolModification = value.getModification()
        val swagPayoutToolModification = when {
            thriftPayoutToolModification.isSetCreation -> {
                val thriftCreation = thriftPayoutToolModification.creation
                SwagContractPayoutToolCreationModification().apply {
                    payoutToolModificationType = CONTRACTPAYOUTTOOLCREATIONMODIFICATION
                    currency = SwagCurrencyRef().symbolicCode(thriftCreation.currency.symbolicCode)
                    toolInfo = payoutToolInfoConverter.convertToSwag(thriftCreation.toolInfo)
                }
            }
            thriftPayoutToolModification.isSetInfoModification -> {
                val swagPayoutToolInfo =
                    payoutToolInfoConverter.convertToSwag(thriftPayoutToolModification.infoModification)

                SwagContractPayoutToolInfoModification()
                    .payoutToolInfo(swagPayoutToolInfo)
                    .payoutToolModificationType(
                        CONTRACTPAYOUTTOOLINFOMODIFICATION
                    )
            }
            else -> {
                throw IllegalArgumentException("Unknown PayoutTool modification type!")
            }
        }

        return SwagContractPayoutToolModificationUnit().apply {
            payoutToolID = value.payoutToolId
            contractModificationType =
                SwagContractModification.ContractModificationTypeEnum.CONTRACTPAYOUTTOOLMODIFICATIONUNIT
            modification = swagPayoutToolModification
        }
    }
}
