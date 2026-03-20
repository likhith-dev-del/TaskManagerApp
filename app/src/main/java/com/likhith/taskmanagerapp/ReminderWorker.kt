package com.likhith.taskmanagerapp

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, params: WorkerParameters)
    : Worker(context, params) {

    override fun doWork(): Result {
        Log.d("WORK_MANAGER", "Reminder: Complete your tasks!")
        return Result.success()
    }
}