package com.rbkmoney.claimmanagementapi.meta

class UserIdentityEmailExtensionKit
private constructor(key: String) : AbstractUserIdentityExtensionKit(KEY) {

    companion object {
        const val KEY = "user-identity.email"
        val INSTANCE = UserIdentityEmailExtensionKit(KEY)
    }
}
