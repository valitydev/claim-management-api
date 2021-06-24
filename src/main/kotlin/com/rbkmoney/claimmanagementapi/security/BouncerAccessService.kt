package com.rbkmoney.claimmanagementapi.security

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class BouncerAccessService(
    private val bouncerService: BouncerService,
    @Value("\${bouncer.auth.enabled}") private val authEnabled: Boolean = false
) : AccessService {

    private val log = KotlinLogging.logger { }

    override fun checkAccess(operationId: String, partyId: String?) {
        log.info { "Check the user's rights to perform the operation $operationId" }
        val bouncerContext = BouncerContextDto(operationId, partyId)
        if (bouncerService.havePrivileges(bouncerContext).not()) {
            if (authEnabled) {
                throw AccessDeniedException("No rights to perform $operationId")
            } else {
                log.warn { "No rights to perform $operationId" }
            }
        }
    }
}
