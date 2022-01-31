package com.rbkmoney.claimmanagementapi.converter.party.contract

import com.rbkmoney.claimmanagementapi.converter.DarkApiConverter
import dev.vality.damsel.domain.ArticlesOfAssociation
import dev.vality.damsel.domain.LegalAgreement
import org.springframework.stereotype.Component
import dev.vality.damsel.domain.RepresentativeDocument as ThriftRepresentativeDocument
import dev.vality.swag.claim_management.model.ArticlesOfAssociation as SwagArticlesOfAssociation
import dev.vality.swag.claim_management.model.PowerOfAttorney as SwagPowerOfAttorney
import dev.vality.swag.claim_management.model.RepresentativeDocument as SwagRepresentativeDocument
import dev.vality.swag.claim_management.model.RepresentativeDocument.DocumentTypeEnum as SwagDocumentTypeEnum

@Component
class RepresentativeDocumentConverter : DarkApiConverter<ThriftRepresentativeDocument, SwagRepresentativeDocument> {

    override fun convertToThrift(value: SwagRepresentativeDocument): ThriftRepresentativeDocument {
        val signerDocument = ThriftRepresentativeDocument()
        when (value.documentType) {
            SwagDocumentTypeEnum.ARTICLESOFASSOCIATION -> signerDocument.articlesOfAssociation = ArticlesOfAssociation()
            SwagDocumentTypeEnum.POWEROFATTORNEY -> {
                val swagPowerOfAttorney: SwagPowerOfAttorney = value as SwagPowerOfAttorney
                val powerOfAttorney = LegalAgreement()
                    .setLegalAgreementId(swagPowerOfAttorney.legalAgreementID)
                    .setSignedAt(swagPowerOfAttorney.signedAt)
                    .setValidUntil(swagPowerOfAttorney.validUntil)
                signerDocument.powerOfAttorney = powerOfAttorney
            }
            else -> throw IllegalArgumentException("Unknown report preferences document type: $value")
        }
        return signerDocument
    }

    override fun convertToSwag(value: ThriftRepresentativeDocument): SwagRepresentativeDocument {
        return when {
            value.isSetArticlesOfAssociation -> {
                SwagArticlesOfAssociation().apply {
                    documentType = SwagDocumentTypeEnum.ARTICLESOFASSOCIATION
                }
            }
            value.isSetPowerOfAttorney -> {
                val powerOfAttorney = value.powerOfAttorney
                return SwagPowerOfAttorney().apply {
                    documentType = SwagDocumentTypeEnum.POWEROFATTORNEY
                    legalAgreementID = powerOfAttorney.legalAgreementId
                    signedAt = powerOfAttorney.signedAt
                    validUntil = powerOfAttorney.validUntil
                }
            }
            else -> {
                throw IllegalArgumentException("Unknown representative document type!")
            }
        }
    }
}
