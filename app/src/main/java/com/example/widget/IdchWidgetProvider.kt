package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.preferences.ApiKeyPreferences
import com.example.data.repository.VmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class IdchWidgetProvider : AppWidgetProvider() {

    private val widgetScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_START_VM -> {
                val prefs = ApiKeyPreferences(context)
                val uuid = prefs.getWidgetVmUuid()
                val region = prefs.getRegion()

                if (!uuid.isNullOrBlank()) {
                    prefs.updateWidgetVmStatus("STARTING...")
                    updateAllWidgets(context)

                    widgetScope.launch {
                        val repository = VmRepository(context)
                        val res = repository.startVm(region, uuid)
                        if (res.isSuccess) {
                            prefs.updateWidgetVmStatus("RUNNING")
                        } else {
                            prefs.updateWidgetVmStatus("ERROR")
                        }
                        updateAllWidgets(context)
                    }
                }
            }

            ACTION_STOP_VM -> {
                val prefs = ApiKeyPreferences(context)
                val uuid = prefs.getWidgetVmUuid()
                val region = prefs.getRegion()

                if (!uuid.isNullOrBlank()) {
                    prefs.updateWidgetVmStatus("STOPPING...")
                    updateAllWidgets(context)

                    widgetScope.launch {
                        val repository = VmRepository(context)
                        val res = repository.stopVm(region, uuid, force = false)
                        if (res.isSuccess) {
                            prefs.updateWidgetVmStatus("STOPPED")
                        } else {
                            prefs.updateWidgetVmStatus("ERROR")
                        }
                        updateAllWidgets(context)
                    }
                }
            }

            ACTION_REFRESH_WIDGET -> {
                val prefs = ApiKeyPreferences(context)
                val uuid = prefs.getWidgetVmUuid()
                val region = prefs.getRegion()

                if (!uuid.isNullOrBlank()) {
                    widgetScope.launch {
                        val repository = VmRepository(context)
                        val res = repository.getVmList(region)
                        val list = res.getOrNull()
                        val currentVm = list?.find { it.uuid == uuid }
                        if (currentVm != null) {
                            prefs.saveWidgetVm(currentVm)
                        }
                        updateAllWidgets(context)
                    }
                }
            }
        }
    }

    companion object {
        const val ACTION_START_VM = "com.example.ACTION_START_VM"
        const val ACTION_STOP_VM = "com.example.ACTION_STOP_VM"
        const val ACTION_REFRESH_WIDGET = "com.example.ACTION_REFRESH_WIDGET"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val prefs = ApiKeyPreferences(context)
            val vmName = prefs.getWidgetVmName()
            val vmStatus = prefs.getWidgetVmStatus()
            val vmIp = prefs.getWidgetVmIp()
            val vmSpec = prefs.getWidgetVmSpec()

            val views = RemoteViews(context.packageName, R.layout.widget_vm_status)

            views.setTextViewText(R.id.widget_tv_vm_name, vmName)
            views.setTextViewText(R.id.widget_tv_status, vmStatus)
            views.setTextViewText(R.id.widget_tv_ip, "IP: $vmIp")
            views.setTextViewText(R.id.widget_tv_spec, vmSpec)

            // Adjust status color
            when (vmStatus.uppercase()) {
                "RUNNING" -> {
                    views.setInt(R.id.widget_tv_status, "setBackgroundResource", R.drawable.btn_widget_start)
                    views.setTextColor(R.id.widget_tv_status, Color.parseColor("#121212"))
                }
                "STOPPED" -> {
                    views.setInt(R.id.widget_tv_status, "setBackgroundResource", R.drawable.btn_widget_stop)
                    views.setTextColor(R.id.widget_tv_status, Color.parseColor("#121212"))
                }
                else -> {
                    views.setInt(R.id.widget_tv_status, "setBackgroundResource", R.drawable.btn_widget_refresh)
                    views.setTextColor(R.id.widget_tv_status, Color.parseColor("#121212"))
                }
            }

            // Launch App intent on root click
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pLaunchIntent = PendingIntent.getActivity(
                context, 0, launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pLaunchIntent)

            // Start VM Action PendingIntent
            val startIntent = Intent(context, IdchWidgetProvider::class.java).apply {
                action = ACTION_START_VM
            }
            val pStartIntent = PendingIntent.getBroadcast(
                context, 1, startIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_start, pStartIntent)

            // Stop VM Action PendingIntent
            val stopIntent = Intent(context, IdchWidgetProvider::class.java).apply {
                action = ACTION_STOP_VM
            }
            val pStopIntent = PendingIntent.getBroadcast(
                context, 2, stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_stop, pStopIntent)

            // Refresh Widget Action PendingIntent
            val refreshIntent = Intent(context, IdchWidgetProvider::class.java).apply {
                action = ACTION_REFRESH_WIDGET
            }
            val pRefreshIntent = PendingIntent.getBroadcast(
                context, 3, refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_refresh, pRefreshIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, IdchWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (widgetId in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, widgetId)
            }
        }
    }
}
