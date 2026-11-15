package com.example.myapplicationdo;

import java.util.ArrayList;
import java.util.List;

public class TaskSeries {
    private String monthYear; // e.g. "Tháng 11/2026"
    private int totalTasks;
    private int completedTasks;
    private List<Task> taskList;

    public TaskSeries(String monthYear) {
        this.monthYear = monthYear;
        this.taskList = new ArrayList<>();
        this.totalTasks = 0;
        this.completedTasks = 0;
    }

    public void addTask(Task task) {
        this.taskList.add(task);
        this.totalTasks++;
        if (task.isCompleted()) {
            this.completedTasks++;
        }
    }

    public String getMonthYear() {
        return monthYear;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public int getPendingTasks() {
        return totalTasks - completedTasks;
    }

    public int getProgressPercentage() {
        return totalTasks > 0 ? (completedTasks * 100) / totalTasks : 0;
    }

    public List<Task> getTaskList() {
        return taskList;
    }
}