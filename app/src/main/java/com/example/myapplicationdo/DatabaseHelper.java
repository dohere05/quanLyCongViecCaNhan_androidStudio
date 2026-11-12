package com.example.myapplicationdo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TaskManager.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DUE_DATE = "due_date";
    public static final String COLUMN_DUE_TIMESTAMP = "due_timestamp";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_IS_COMPLETED = "is_completed";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_DUE_DATE + " TEXT, "
                + COLUMN_DUE_TIMESTAMP + " INTEGER, "
                + COLUMN_PRIORITY + " TEXT, "
                + COLUMN_CATEGORY + " TEXT, "
                + COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0" + ")";
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    // Thêm công việc mới
    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_DUE_TIMESTAMP, task.getDueTimestamp());
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_CATEGORY, task.getCategory());
        values.put(COLUMN_IS_COMPLETED, task.getIsCompleted());

        long id = db.insert(TABLE_TASKS, null, values);
        db.close();
        return id;
    }

    // Sửa công việc
    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_DUE_TIMESTAMP, task.getDueTimestamp());
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_CATEGORY, task.getCategory());
        values.put(COLUMN_IS_COMPLETED, task.getIsCompleted());

        int result = db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
        return result;
    }

    // Xóa công việc
    public int deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    // Cập nhật trạng thái hoàn thành
    public int updateTaskStatus(int id, int isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_COMPLETED, isCompleted);
        int result = db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    // Lấy chi tiết 1 công việc
    public Task getTaskById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        Task task = null;
        if (cursor != null && cursor.moveToFirst()) {
            task = new Task(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DUE_TIMESTAMP)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED))
            );
            cursor.close();
        }
        db.close();
        return task;
    }

    // Lấy tất cả danh sách công việc
    public List<Task> getAllTasks() {
        return filterTasks("", "ALL", "Tất cả");
    }

    // Lọc và Tìm kiếm công việc
    public List<Task> filterTasks(String keyword, String statusFilter, String categoryFilter) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder query = new StringBuilder("SELECT * FROM " + TABLE_TASKS + " WHERE 1=1");
        List<String> argsList = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.append(" AND (").append(COLUMN_TITLE).append(" LIKE ? OR ").append(COLUMN_DESCRIPTION).append(" LIKE ?)");
            argsList.add("%" + keyword.trim() + "%");
            argsList.add("%" + keyword.trim() + "%");
        }

        if ("DONE".equalsIgnoreCase(statusFilter)) {
            query.append(" AND ").append(COLUMN_IS_COMPLETED).append(" = 1");
        } else if ("PENDING".equalsIgnoreCase(statusFilter)) {
            query.append(" AND ").append(COLUMN_IS_COMPLETED).append(" = 0");
        }

        if (categoryFilter != null && !categoryFilter.equals("Tất cả") && !categoryFilter.isEmpty()) {
            query.append(" AND ").append(COLUMN_CATEGORY).append(" = ?");
            argsList.add(categoryFilter);
        }

        query.append(" ORDER BY ").append(COLUMN_DUE_TIMESTAMP).append(" ASC");

        String[] args = argsList.toArray(new String[0]);
        Cursor cursor = db.rawQuery(query.toString(), args);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DUE_TIMESTAMP)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED))
                );
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }

    // Thống kê: Tổng số việc
    public int getTotalTaskCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TASKS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    // Thống kê: Số việc đã hoàn thành
    public int getCompletedTaskCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TASKS + " WHERE " + COLUMN_IS_COMPLETED + " = 1", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    // Thống kê: Số việc chưa làm
    public int getPendingTaskCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TASKS + " WHERE " + COLUMN_IS_COMPLETED + " = 0", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    // Thống kê: Số việc trễ hạn (chưa làm và đã qua timestamp)
    public int getOverdueTaskCount(long currentTimestamp) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TASKS + " WHERE " + COLUMN_IS_COMPLETED + " = 0 AND " + COLUMN_DUE_TIMESTAMP + " > 0 AND " + COLUMN_DUE_TIMESTAMP + " < ?", new String[]{String.valueOf(currentTimestamp)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
}