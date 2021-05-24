package com.rbkmoney.claimmanagementapi.meta

class UserIdentityRealmExtensionKit
private constructor(key: String) : AbstractUserIdentityExtensionKit(KEY) {

    companion object {
        const val KEY = "user-identity.realm"
        val INSTANCE = UserIdentityRealmExtensionKit(KEY)
    }
}
