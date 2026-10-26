package com.example.data.model

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

@JsonClass(generateAdapter = true)
data class LocationItem(
    @Json(name = "slug") val slug: String,
    @Json(name = "name") val name: String = slug,
    @Json(name = "country") val country: String? = "Indonesia"
)

@JsonClass(generateAdapter = true)
data class VmStorageItem(
    @Json(name = "uuid") val uuid: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "size") val size: Int? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "primary") val primary: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class VmItem(
    @Json(name = "uuid") val uuid: String,
    @Json(name = "name") val name: String = "",
    @Json(name = "status") val status: String = "STOPPED",
    @Json(name = "vcpu") val vcpu: Int = 1,
    @Json(name = "memory") val memory: Int? = null,
    @Json(name = "ram") val ramRaw: Int? = null,
    @Json(name = "storage") val storage: List<VmStorageItem>? = null,
    @Json(name = "private_ipv4") val privateIpv4: String? = null,
    @Json(name = "public_ipv4") val publicIpv4: String? = null,
    @Json(name = "public_ipv6") val publicIpv6: String? = null,
    @Json(name = "primary_ip") val primaryIp: String? = null,
    @Json(name = "private_ip") val privateIp: String? = null,
    @Json(name = "ip_address") val ipAddress: String? = null,
    @Json(name = "os_name") val osName: String? = "linux",
    @Json(name = "os_version") val osVersion: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "billing_account") val billingAccount: Long? = null,
    @Json(name = "billing_account_id") val billingAccountId: Long? = null
) {
    val ram: Int
        get() = memory ?: ramRaw ?: 1024

    val diskSize: Int
        get() {
            val total = storage?.sumOf { it.size ?: 0 } ?: 0
            return if (total > 0) total else 20
        }

    val displayIp: String
        get() = publicIpv4 ?: primaryIp ?: ipAddress ?: privateIpv4 ?: privateIp ?: "No IP assigned"

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

class VmStorageListAdapter {

    @FromJson
    fun fromJson(reader: JsonReader): List<VmStorageItem> {
        val list = mutableListOf<VmStorageItem>()
        when (reader.peek()) {
            JsonReader.Token.BEGIN_ARRAY -> {
                reader.beginArray()
                while (reader.hasNext()) {
                    if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                        var uuid: String? = null
                        var name: String? = null
                        var size: Int? = null
                        var type: String? = null
                        var primary: Boolean? = null

                        reader.beginObject()
                        while (reader.hasNext()) {
                            when (reader.nextName()) {
                                "uuid" -> uuid = runCatching { reader.nextString() }.getOrNull()
                                "name" -> name = runCatching { reader.nextString() }.getOrNull()
                                "size" -> {
                                    size = try {
                                        reader.nextInt()
                                    } catch (e: Exception) {
                                        try {
                                            reader.nextDouble().toInt()
                                        } catch (e2: Exception) {
                                            reader.nextString().toIntOrNull()
                                        }
                                    }
                                }
                                "type" -> type = runCatching { reader.nextString() }.getOrNull()
                                "primary" -> primary = runCatching { reader.nextBoolean() }.getOrNull()
                                else -> reader.skipValue()
                            }
                        }
                        reader.endObject()
                        list.add(VmStorageItem(uuid = uuid, name = name, size = size, type = type, primary = primary))
                    } else if (reader.peek() == JsonReader.Token.NUMBER) {
                        list.add(VmStorageItem(size = reader.nextInt()))
                    } else {
                        reader.skipValue()
                    }
                }
                reader.endArray()
            }
            JsonReader.Token.NUMBER -> {
                list.add(VmStorageItem(size = reader.nextInt()))
            }
            JsonReader.Token.BEGIN_OBJECT -> {
                var size: Int? = null
                var uuid: String? = null
                var name: String? = null
                var type: String? = null
                var primary: Boolean? = null

                reader.beginObject()
                while (reader.hasNext()) {
                    when (reader.nextName()) {
                        "size" -> size = runCatching { reader.nextInt() }.getOrNull()
                        "uuid" -> uuid = runCatching { reader.nextString() }.getOrNull()
                        "name" -> name = runCatching { reader.nextString() }.getOrNull()
                        "type" -> type = runCatching { reader.nextString() }.getOrNull()
                        "primary" -> primary = runCatching { reader.nextBoolean() }.getOrNull()
                        else -> reader.skipValue()
                    }
                }
                reader.endObject()
                list.add(VmStorageItem(uuid = uuid, name = name, size = size, type = type, primary = primary))
            }
            JsonReader.Token.NULL -> {
                reader.nextNull<Unit>()
            }
            else -> {
                reader.skipValue()
            }
        }
        return list
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: List<VmStorageItem>?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.beginArray()
        for (item in value) {
            writer.beginObject()
            item.uuid?.let { writer.name("uuid").value(it) }
            item.name?.let { writer.name("name").value(it) }
            item.size?.let { writer.name("size").value(it) }
            item.type?.let { writer.name("type").value(it) }
            item.primary?.let { writer.name("primary").value(it) }
            writer.endObject()
        }
        writer.endArray()
    }
}
