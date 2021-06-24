package com.rbkmoney.claimmanagementapi.meta

import com.rbkmoney.woody.api.trace.Metadata
import com.rbkmoney.woody.api.trace.context.metadata.MetadataConverter
import com.rbkmoney.woody.api.trace.context.metadata.MetadataExtension
import com.rbkmoney.woody.api.trace.context.metadata.MetadataExtensionKitImpl

abstract class AbstractUserIdentityExtensionKit(val key: String) : MetadataExtensionKitImpl<String>(

    object : MetadataExtension<String> {
        override fun getValue(metadata: Metadata): String = metadata.getValue(key)

        override fun setValue(value: String, metadata: Metadata) {
            metadata.putValue<Any>(key, value)
        }
    },

    object : MetadataConverter<String> {
        override fun convertToObject(key: String, value: String): String = value

        override fun convertToString(key: String, value: String): String = value

        override fun apply(key: String): Boolean = key.equals(key, ignoreCase = true)
    }
)
