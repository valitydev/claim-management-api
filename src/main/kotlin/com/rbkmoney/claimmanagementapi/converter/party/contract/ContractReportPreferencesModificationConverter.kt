package com.rbkmoney.claimmanagementapi.converter.party.contract

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.damsel.domain.Representative
import dev.vality.swag.claim_management.model.ContractModification
import org.springframework.stereotype.Component
import dev.vality.damsel.domain.BusinessScheduleRef as ThriftBusinessScheduleRef
import dev.vality.damsel.domain.ReportPreferences as ThriftReportPreferences
import dev.vality.damsel.domain.ServiceAcceptanceActPreferences as ThriftServiceAcceptanceActPreferences
import dev.vality.swag.claim_management.model.BusinessScheduleRef as SwagBusinessScheduleRef
import dev.vality.swag.claim_management.model.ContractReportPreferencesModification as SwagContractReportPreferencesModification
import dev.vality.swag.claim_management.model.ReportPreferences as SwagReportPreferences
import dev.vality.swag.claim_management.model.Representative as SwagRepresentative
import dev.vality.swag.claim_management.model.ServiceAcceptanceActPreferences as SwagServiceAcceptanceActPreferences

@Component
class ContractReportPreferencesModificationConverter(
    private val representativeDocumentConverter: RepresentativeDocumentConverter
) : DarkApiConverter<ThriftReportPreferences, SwagContractReportPreferencesModification> {

    override fun convertToThrift(value: SwagContractReportPreferencesModification): ThriftReportPreferences {
        val actPreferences = ThriftServiceAcceptanceActPreferences()
        value.reportPreferences?.serviceAcceptanceActPreferences?.let {
            actPreferences.setSchedule(ThriftBusinessScheduleRef(it.schedule.id))
            val swagSigner = it.signer
            val signer = Representative()
                .setFullName(swagSigner.fullName)
                .setPosition(swagSigner.position)
                .setDocument(representativeDocumentConverter.convertToThrift(swagSigner.document))
            actPreferences.setSigner(signer)
        }

        return ThriftReportPreferences().apply { serviceAcceptanceActPreferences = actPreferences }
    }

    override fun convertToSwag(value: ThriftReportPreferences): SwagContractReportPreferencesModification {
        val swagReportPreferences = SwagReportPreferences()
        if (value.isSetServiceAcceptanceActPreferences) {
            val thriftServiceAcceptanceActPreferences = value.serviceAcceptanceActPreferences

            val swagSchedule = SwagBusinessScheduleRef().id(thriftServiceAcceptanceActPreferences.schedule.id)
            val thriftSigner = thriftServiceAcceptanceActPreferences.getSigner()
            val swagSigner = SwagRepresentative().apply {
                fullName = thriftSigner.fullName
                position = thriftSigner.getPosition()
                document = representativeDocumentConverter.convertToSwag(thriftSigner.document)
            }
            swagReportPreferences.serviceAcceptanceActPreferences = SwagServiceAcceptanceActPreferences()
                .apply {
                    schedule = swagSchedule
                    signer = swagSigner
                }
        }

        return SwagContractReportPreferencesModification().apply {
            contractModificationType =
                ContractModification.ContractModificationTypeEnum.CONTRACTREPORTPREFERENCESMODIFICATION
            reportPreferences = swagReportPreferences
        }
    }
}
