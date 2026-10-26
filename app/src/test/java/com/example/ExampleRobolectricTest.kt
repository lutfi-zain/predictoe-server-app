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
}
