package com.likhith.taskmanagerapp

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore("task_prefs")

class TaskDataStore(private val context: Context) {

    private val TASK_KEY = stringPreferencesKey("tasks")

    suspend fun saveTasks(tasks: List<Task>) {
        val taskString = tasks.joinToString(",") { it.title }

        context.dataStore.edit {
            it[TASK_KEY] = taskString
        }
    }

    suspend fun getTasks(): List<Task> {
        val prefs = context.dataStore.data.first()
        val taskString = prefs[TASK_KEY] ?: ""

        return if (taskString.isEmpty()) {
            emptyList()
        } else {
            taskString.split(",").map { Task(it) }
        }
    }
}