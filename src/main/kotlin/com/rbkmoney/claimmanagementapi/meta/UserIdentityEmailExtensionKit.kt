package com.rbkmoney.claimmanagementapi.meta

class UserIdentityEmailExtensionKit
private constructor() : AbstractUserIdentityExtensionKit(KEY) {

    companion object {
        const val KEY = "user-identity.email"
        val INSTANCE = UserIdentityEmailExtensionKit()
    }
}
