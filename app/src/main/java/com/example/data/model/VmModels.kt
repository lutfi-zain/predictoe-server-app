package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationItem(
    @Json(name = "slug") val slug: String,
    @Json(name = "name") val name: String = slug,
    @Json(name = "country") val country: String? = "Indonesia"
)

@JsonClass(generateAdapter = true)
data class VmItem(
    @Json(name = "uuid") val uuid: String,
    @Json(name = "name") val name: String,
    @Json(name = "status") val status: String = "STOPPED",
    @Json(name = "vcpu") val vcpu: Int = 1,
    @Json(name = "memory") val memory: Int? = null,
    @Json(name = "ram") val ramRaw: Int? = null,
    @Json(name = "disks") val disks: Int? = 20,
    @Json(name = "storage") val storage: Int? = null,
    @Json(name = "primary_ip") val primaryIp: String? = null,
    @Json(name = "private_ip") val privateIp: String? = null,
    @Json(name = "ip_address") val ipAddress: String? = null,
    @Json(name = "os_name") val osName: String? = "linux",
    @Json(name = "os_version") val osVersion: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "billing_account_id") val billingAccountId: Long? = null
) {
    val ram: Int
        get() = ramRaw ?: memory ?: 1024

    val diskSize: Int
        get() = storage ?: disks ?: 20

    val displayIp: String
        get() = primaryIp ?: ipAddress ?: privateIp ?: "No IP assigned"

    val isRunning: Boolean
        get() = status.equals("running", ignoreCase = true) ||
                status.equals("active", ignoreCase = true)

    val isStopped: Boolean
        get() = status.equals("stopped", ignoreCase = true) ||
                status.equals("shutoff", ignoreCase = true) ||
                status.equals("inactive", ignoreCase = true)

    val ramFormatted: String
        get() = if (ram >= 1024) "${ram / 1024} GB" else "$ram MB"
}

@JsonClass(generateAdapter = true)
data class VmActionRequest(
    @Json(name = "uuid") val uuid: String,
    @Json(name = "force") val force: Boolean? = false
)

@JsonClass(generateAdapter = true)
data class VmModifyRequest(
    @Json(name = "uuid") val uuid: String,
    @Json(name = "name") val name: String? = null,
    @Json(name = "vcpu") val vcpu: Int? = null,
    @Json(name = "ram") val ram: Int? = null,
    @Json(name = "memory") val memory: Int? = null
)
