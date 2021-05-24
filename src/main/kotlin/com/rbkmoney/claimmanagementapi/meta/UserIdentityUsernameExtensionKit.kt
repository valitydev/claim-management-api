package com.rbkmoney.claimmanagementapi.meta

class UserIdentityUsernameExtensionKit
private constructor(key: String) : AbstractUserIdentityExtensionKit(KEY) {

    companion object {
        const val KEY = "user-identity.username"
        val INSTANCE = UserIdentityUsernameExtensionKit(KEY)
    }
}
