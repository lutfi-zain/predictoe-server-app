package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.VmItem
import com.example.data.preferences.ApiKeyPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("IDCloud VM", appName)
    }

    @Test
    fun `vmItem helper properties calculate correctly`() {
        val vm = VmItem(
            uuid = "test-uuid-1234",
            name = "db-production",
            status = "RUNNING",
            vcpu = 4,
            memory = 4096,
            primaryIp = "103.150.190.5"
        )

        assertTrue(vm.isRunning)
        assertEquals("4 GB", vm.ramFormatted)
        assertEquals("103.150.190.5", vm.displayIp)
    }

    @Test
    fun `apiKeyPreferences persists and retrieves correctly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = ApiKeyPreferences(context)

        prefs.saveApiKey("secret-api-key-test")
        assertEquals("secret-api-key-test", prefs.getApiKey())

        prefs.saveRegion("jkt02")
        assertEquals("jkt02", prefs.getRegion())
    }

    @Test
    fun `vmItem parses idcloudhost json with storage array correctly`() {
        val json = """
        [
          {
            "uuid": "fc880f74-cf03-4a7a-93da-74c506157023",
            "name": "Ubuntu-Server",
            "status": "running",
            "vcpu": 2,
            "memory": 2048,
            "private_ipv4": "10.123.8.223",
            "public_ipv4": "103.150.190.1",
            "storage": [
              {
                "id": 8,
                "name": "sda",
                "primary": true,
                "size": 20,
                "uuid": "fc880f74-cf03-4a7a-93da-74c506157023"
              }
            ]
          }
        ]
        """.trimIndent()

        val moshi = com.squareup.moshi.Moshi.Builder()
            .add(com.example.data.model.VmStorageListAdapter())
            .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
            .build()

        val type = com.squareup.moshi.Types.newParameterizedType(List::class.java, VmItem::class.java)
        val adapter = moshi.adapter<List<VmItem>>(type)
        val list = adapter.fromJson(json)

        assertEquals(1, list?.size)
        val item = list?.first()
        assertEquals("Ubuntu-Server", item?.name)
        assertEquals("103.150.190.1", item?.displayIp)
        assertEquals(20, item?.diskSize)
        assertTrue(item?.isRunning == true)
    }
}
