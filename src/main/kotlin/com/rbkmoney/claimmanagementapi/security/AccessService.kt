package com.rbkmoney.claimmanagementapi.security

interface AccessService {
    fun checkAccess(operationId: String, partyId: String? = null)
}
