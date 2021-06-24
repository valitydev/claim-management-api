package com.rbkmoney.claimmanagementapi.security

import com.rbkmoney.bouncer.ctx.ContextFragment
import com.rbkmoney.orgmanagement.AuthContextProviderSrv
import org.springframework.stereotype.Service

@Service
class UserAuthContextProvider(
    private val authContextProvider: AuthContextProviderSrv.Iface
) {

    fun getUserAuthContext(userId: String): ContextFragment =
        try {
            authContextProvider.getUserContext(userId) // ToDo: cache it
        } catch (ex: Exception) {
            throw RuntimeException("Can't get user auth context: userId = $userId", ex)
        }
}
