package com.rose.taskassignmenttest.viewmodels.idaos

import com.rose.taskassignmenttest.data.STATUS_DONE
import com.rose.taskassignmenttest.data.STATUS_IN_PROGRESS
import com.rose.taskassignmenttest.data.STATUS_NOT_STARTED
import com.rose.taskassignmenttest.data.Task
import com.rose.taskassignmenttest.viewmodels.daos.TaskDao
import java.util.stream.Collectors

class FakeTaskDao : TaskDao {
    companion object {
        private val sList: MutableList<Task> = ArrayList<Task>().apply {
            add(
                Task(
                    1, "Go shopping", System.currentTimeMillis(), System.currentTimeMillis(),
                    false, STATUS_IN_PROGRESS, -1
                )
            )
            add(
                Task(
                    2, "Have Dinner", System.currentTimeMillis(), System.currentTimeMillis(),
                    false, STATUS_NOT_STARTED, -1
                )
            )
            add(
                Task(
                    3, "Team meeting", System.currentTimeMillis(), System.currentTimeMillis(),
                    false, STATUS_NOT_STARTED, -1
                )
            )
            add(
                Task(
                    4, "Workout", System.currentTimeMillis(), System.currentTimeMillis(),
                    false, STATUS_DONE, -1
                )
            )
        }
    }

    override fun getAllTasks(): MutableList<Task> {
        return ArrayList<Task>().apply { addAll(sList) }
    }

    override fun getTask(taskId: Int): Task {
        return sList.find { t -> t.id == taskId } ?: Task.newTask()
    }

    override fun updateTask(task: Task) {
        val list = sList.stream().filter { t -> t.id != task.id }.collect(Collectors.toList())
        sList.clear()
        sList.addAll(list)
        sList.add(task)
    }

    override fun insertTask(task: Task) {
        val id: Int = sList.stream().mapToInt { t -> t.id }.max().orElse(1)
        val currentTime = System.currentTimeMillis()
        sList.add(
            Task(
                id,
                task.title,
                currentTime,
                currentTime,
                task.completed,
                task.status,
                task.deadLine
            )
        )
    }

    override fun deleteTask(taskId: Int) {
        val list = sList.stream().filter { t -> t.id != taskId }.collect(Collectors.toList())
        sList.clear()
        sList.addAll(list)
    }
}