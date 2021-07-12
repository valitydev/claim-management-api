package com.rbkmoney.claimmanagementapi.meta

class UserIdentityUsernameExtensionKit
private constructor() : AbstractUserIdentityExtensionKit(KEY) {

    companion object {
        const val KEY = "user-identity.username"
        val INSTANCE = UserIdentityUsernameExtensionKit()
    }
}
