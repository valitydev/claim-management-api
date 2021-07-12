package com.rbkmoney.claimmanagementapi.meta

class UserIdentityIdExtensionKit
private constructor() : AbstractUserIdentityExtensionKit(KEY) {

    companion object {
        const val KEY = "user-identity.id"
        val INSTANCE = UserIdentityIdExtensionKit()
    }
}
