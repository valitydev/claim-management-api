package com.rbkmoney.claimmanagementapi.service

import com.rbkmoney.claimmanagementapi.exception.client.ForbiddenException
import com.rbkmoney.claimmanagementapi.exception.server.DarkApi5xxException
import com.rbkmoney.claimmanagementapi.service.security.KeycloakService
import com.rbkmoney.damsel.payment_processing.InternalUser
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv
import com.rbkmoney.damsel.payment_processing.UserInfo
import com.rbkmoney.damsel.payment_processing.UserType
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class PartyManagementService(
    private val partyManagementClient: PartyManagementSrv.Iface,
    private val keycloakService: KeycloakService
) {

    private val log = KotlinLogging.logger { }

    fun checkStatus(xRequestId: String? = null) {
        val partyId = keycloakService.partyId
        log.info {
            "Trying to get request on party-management service for party-status, " +
                "xRequestId='$xRequestId', partyId='$partyId'"
        }
        val status = getPartyStatus(xRequestId, partyId)
        if (status.getBlocking().isSetBlocked) {
            val blocked = status.getBlocking().blocked
            throw ForbiddenException(
                "Party is blocked xRequestId=$xRequestId, since=${blocked.getSince()}, reason=${blocked.getReason()}"
            )
        }
        log.info {
            "Request has been got on party-management service, " +
                "party-status=unblocked, xRequestId='$xRequestId', partyId='$partyId'"
        }
    }

    private fun getPartyStatus(xRequestId: String?, partyId: String) =
        try {
            partyManagementClient.getStatus(userInfo, partyId)
        } catch (ex: Exception) {
            throw DarkApi5xxException(
                "Some Exception while requesting api='party-management', method='getStatus', xRequestId=$xRequestId",
                ex
            )
        }

    companion object {
        private val userInfo = UserInfo("dark-api", UserType.internal_user(InternalUser()))
    }
}
