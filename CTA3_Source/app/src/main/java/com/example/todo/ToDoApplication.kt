package com.example.todo

import android.app.Application
import com.example.todo.data.TaskRepository
import com.example.todo.data.ToDoDatabase

class ToDoApplication : Application() {
    val database by lazy {
        ToDoDatabase.getDatabase(this)
    }

    val repository by lazy {
        TaskRepository(database.taskDao())
    }
}
