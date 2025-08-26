package com.onlyanh.wdemo

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import java.text.SimpleDateFormat
import java.util.Date

object AppWidgetDemo : GlanceAppWidget() {
    val COUNTER_KEY: Preferences.Key<Int> = intPreferencesKey("counter")
    val LAST_UPDATE_TIME_KEY: Preferences.Key<Long> = longPreferencesKey("lastUpdateTime")

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            val prefs = currentState<Preferences>()
            val count = prefs[COUNTER_KEY] ?: 0
            val time = prefs[LAST_UPDATE_TIME_KEY] ?: System.currentTimeMillis()

            Content(
                time = time,
                count = count
            )
        }
    }

    suspend fun updateLevel(context: Context) {
        val glanceIds = androidx.glance.appwidget.GlanceAppWidgetManager(context)
            .getGlanceIds(AppWidgetDemo::class.java)

        glanceIds.forEach { id ->
            updateAppWidgetState(context, id) { prefs ->
                prefs[COUNTER_KEY] = (prefs[COUNTER_KEY] ?: 0) + 1
                prefs[LAST_UPDATE_TIME_KEY] = System.currentTimeMillis()
            }
        }

        updateAll(context)
    }
}

class AppWidgetDemoReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = AppWidgetDemo
}

@Composable
fun Content(
    modifier: GlanceModifier = GlanceModifier,
    time: Long = System.currentTimeMillis(),
    count: Int = 0,
) {
    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        // Ảnh nền
        Image(
            provider = ImageProvider(R.drawable.chan_del),
            contentDescription = null,
            modifier = GlanceModifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay mờ
        Box(
            modifier = GlanceModifier.fillMaxSize().background(
                color = Color.Black.copy(alpha = 0.4f)
            )
        ) {}

        // Nội dung chính
        Column(
            modifier = GlanceModifier
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.6f))
                .cornerRadius(16.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            // Thời gian update
            Text(
                text = "Last updated: ${convertLongToTime(time)}",
                style = TextStyle(
                    color = ColorProvider(Color.White, Color.White),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(GlanceModifier.height(12.dp))

            // Counter
            Text(
                text = "Count: $count",
                style = TextStyle(
                    color = ColorProvider(Color.White, Color.White),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(GlanceModifier.height(16.dp))

            // Button
            Button(
                text = "Increment",
                onClick = actionRunCallback(IncrementActionCallback::class.java),
                modifier = GlanceModifier.cornerRadius(12.dp)
            )
        }
    }
}


fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
    return format.format(date)
}

class IncrementActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        AppWidgetDemo.updateLevel(context)
    }
}