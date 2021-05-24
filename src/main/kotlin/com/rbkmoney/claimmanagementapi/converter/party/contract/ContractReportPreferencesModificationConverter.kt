package com.rbkmoney.claimmanagementapi.converter.party.contract

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import com.rbkmoney.damsel.domain.Representative
import com.rbkmoney.swag.claim_management.model.ContractModification
import org.springframework.stereotype.Component
import com.rbkmoney.damsel.domain.BusinessScheduleRef as ThriftBusinessScheduleRef
import com.rbkmoney.damsel.domain.ReportPreferences as ThriftReportPreferences
import com.rbkmoney.damsel.domain.ServiceAcceptanceActPreferences as ThriftServiceAcceptanceActPreferences
import com.rbkmoney.swag.claim_management.model.BusinessScheduleRef as SwagBusinessScheduleRef
import com.rbkmoney.swag.claim_management.model.ContractReportPreferencesModification as SwagContractReportPreferencesModification
import com.rbkmoney.swag.claim_management.model.ReportPreferences as SwagReportPreferences
import com.rbkmoney.swag.claim_management.model.Representative as SwagRepresentative
import com.rbkmoney.swag.claim_management.model.ServiceAcceptanceActPreferences as SwagServiceAcceptanceActPreferences

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
