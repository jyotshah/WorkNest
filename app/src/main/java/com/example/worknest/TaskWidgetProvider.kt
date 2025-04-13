package com.example.worknest

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.worknest.database.DatabaseManager

class TaskWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.task_widget)

            // Get upcoming tasks
            val dbManager = DatabaseManager(context)
            val tasks = dbManager.getAllTasks().take(3)

            val taskListText = tasks.joinToString("\n") { "- ${it.name}" }
            views.setTextViewText(R.id.widget_task_list, if (taskListText.isEmpty()) "No tasks." else taskListText)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
