package com.likhith.taskmanagerapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val name = intent.getStringExtra("USERNAME")
        val tv = findViewById<TextView>(R.id.tvWelcome)
        tv.text = "Welcome $name"

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val btnAdd = findViewById<Button>(R.id.btnAdd)

        val dataStore = TaskDataStore(this)

        val taskList = mutableListOf<Task>()
        val adapter = TaskAdapter(taskList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // ✅ LOAD SAVED TASKS
        GlobalScope.launch {
            val savedTasks = dataStore.getTasks()

            runOnUiThread {
                if (savedTasks.isNotEmpty()) {
                    taskList.addAll(savedTasks)
                } else {
                    taskList.addAll(
                        listOf(
                            Task("Learn Android"),
                            Task("Build App"),
                            Task("Get Internship")
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            }
        }

        // 🌐 FETCH API TASKS
        GlobalScope.launch {
            try {
                val apiTasks = RetrofitInstance.api.getTasks()

                runOnUiThread {
                    apiTasks.take(5).forEach {
                        taskList.add(Task(it.title))
                    }
                    adapter.notifyDataSetChanged()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // ➕ Add Task Button
        btnAdd.setOnClickListener {

            val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
            val etTask = dialogView.findViewById<EditText>(R.id.etTask)

            AlertDialog.Builder(this)
                .setTitle("Add Task")
                .setView(dialogView)
                .setPositiveButton("Add") { _, _ ->
                    val taskText = etTask.text.toString()

                    if (taskText.isNotEmpty()) {
                        taskList.add(Task(taskText))
                        adapter.notifyDataSetChanged()

                        // 💾 SAVE TASKS
                        GlobalScope.launch {
                            dataStore.saveTasks(taskList)
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // ⏰ WORKMANAGER (OUTSIDE BUTTON)
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }
}