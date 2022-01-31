package com.rbkmoney.claimmanagementapi.converter

import com.rbkmoney.claimmanagementapi.converter.claim.ClaimStatusConverter
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import dev.vality.damsel.claim_management.Claim as ThriftClaim
import dev.vality.damsel.claim_management.ClaimSearchQuery as ThriftClaimSearchQuery
import dev.vality.damsel.claim_management.ClaimStatus as ThriftClaimStatus
import dev.vality.damsel.claim_management.Modification as ThriftModification
import dev.vality.damsel.claim_management.ModificationUnit as ThriftModificationUnit
import dev.vality.swag.claim_management.model.Claim as SwagClaim
import dev.vality.swag.claim_management.model.ClaimChangeset as SwagClaimChangeset
import dev.vality.swag.claim_management.model.Modification as SwagModification
import dev.vality.swag.claim_management.model.ModificationUnit as SwagModificationUnit
import dev.vality.swag.claim_management.model.UserInfo as SwagUserInfo

@Component
class ClaimManagementConverterImpl(
    private val claimStatusConverter: ClaimStatusConverter,
    private val modificationConverter: ModificationConverter
) : ClaimManagementConverter {

    override fun convertClaimToSwag(sourceClaim: ThriftClaim) =
        SwagClaim().apply {
            id = sourceClaim.getId()
            status = claimStatusConverter.convertToSwag(sourceClaim.getStatus())
            changeset = convertClaimChangesetToSwag(sourceClaim.getChangeset())
            createdAt = OffsetDateTime.parse(sourceClaim.createdAt)
            updatedAt = sourceClaim.updatedAt?.let {
                OffsetDateTime.parse(sourceClaim.updatedAt)
            }
            revision = sourceClaim.getRevision()
            // TODO: метадата это MAP - правильно ли ее так передавать?
            metadata = sourceClaim.getMetadata()
        }

    override fun convertClaimListToSwag(sourceClaimList: List<ThriftClaim>) =
        sourceClaimList.map { convertClaimToSwag(it) }

    override fun convertClaimChangesetToSwag(changeset: List<ThriftModificationUnit>) =
        changeset.map { convertModificationUnit(it) }.toCollection(SwagClaimChangeset())

    private fun convertModificationUnit(unit: ThriftModificationUnit): SwagModificationUnit {
        val thriftUserInfo = unit.userInfo
        val swagUserInfo = SwagUserInfo()
            .userId(thriftUserInfo.id)
            .email(thriftUserInfo.email)
            .username(thriftUserInfo.username)
            .userType(SwagUserInfo.UserTypeEnum.fromValue(thriftUserInfo.type.setField.fieldName))

        return SwagModificationUnit().apply {
            modificationID = unit.modificationId
            createdAt = OffsetDateTime.parse(unit.createdAt)
            userInfo = swagUserInfo
            modification = modificationConverter.convertToSwag(unit.modification)
        }
    }

    override fun convertModificationUnitToThrift(unitModifications: List<SwagModification>): List<ThriftModification> =
        unitModifications.map { modificationConverter.convertToThrift(it) }

    override fun convertSearchClaimsToThrift(
        partyId: String?,
        claimId: Long?,
        limit: Int,
        continuationToken: String?,
        claimStatuses: List<String>?
    ): ThriftClaimSearchQuery {
        val claimSearchQuery = ThriftClaimSearchQuery().apply {
            this.partyId = partyId
            this.limit = limit
            this.continuationToken = continuationToken
            this.statuses = convertStatusesToThrift(claimStatuses)
        }
        claimId?.let {
            claimSearchQuery.claimId = claimId
        }
        return claimSearchQuery
    }

    override fun convertStatusesToThrift(sourceClaimStatuses: List<String>?): List<ThriftClaimStatus>? =
        sourceClaimStatuses?.map { claimStatusConverter.convertToThrift(it) }
}
