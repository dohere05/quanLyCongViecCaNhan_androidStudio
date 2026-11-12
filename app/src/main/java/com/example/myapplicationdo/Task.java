package com.example.myapplicationdo;

public class Task {
    private int id;
    private String title;
    private String description;
    private String dueDate;
    private long dueTimestamp;
    private String priority;
    private String category;
    private int isCompleted; // 0: chưa hoàn thành, 1: đã hoàn thành

    public Task() {
    }

    public Task(int id, String title, String description, String dueDate, long dueTimestamp, String priority, String category, int isCompleted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.dueTimestamp = dueTimestamp;
        this.priority = priority;
        this.category = category;
        this.isCompleted = isCompleted;
    }

    public Task(String title, String description, String dueDate, long dueTimestamp, String priority, String category, int isCompleted) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.dueTimestamp = dueTimestamp;
        this.priority = priority;
        this.category = category;
        this.isCompleted = isCompleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public long getDueTimestamp() {
        return dueTimestamp;
    }

    public void setDueTimestamp(long dueTimestamp) {
        this.dueTimestamp = dueTimestamp;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    public boolean isCompleted() {
        return isCompleted == 1;
    }
}