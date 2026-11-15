package com.example.myapplicationdo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;
import java.util.Locale;

public class DetailEditTaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";

    private EditText etTitle, etDescription;
    private Button btnSelectDate, btnSelectTime, btnUpdateTask, btnDeleteTask;
    private TextView tvSelectedDateTime, tvCompletedStatus;
    private CheckBox cbCompleted;
    private Spinner spinnerPriority, spinnerCategory;

    private DatabaseHelper dbHelper;
    private Task currentTask;
    private Calendar selectedCalendar;
    private int taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_edit_task);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        selectedCalendar = Calendar.getInstance();

        taskId = getIntent().getIntExtra(EXTRA_TASK_ID, -1);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnUpdateTask = findViewById(R.id.btnUpdateTask);
        btnDeleteTask = findViewById(R.id.btnDeleteTask);
        tvSelectedDateTime = findViewById(R.id.tvSelectedDateTime);
        tvCompletedStatus = findViewById(R.id.tvCompletedStatus);
        cbCompleted = findViewById(R.id.cbCompleted);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        String[] priorities = {"Cao", "Trung bình", "Thấp"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, priorities);
        spinnerPriority.setAdapter(priorityAdapter);

        String[] categories = {"Học tập", "Cá nhân", "Công việc"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(categoryAdapter);

        loadTaskData();

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnUpdateTask.setOnClickListener(v -> updateTask());
        btnDeleteTask.setOnClickListener(v -> confirmDeleteTask());

        cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tvCompletedStatus.setText("Đã hoàn thành công việc này");
            } else {
                tvCompletedStatus.setText("Chưa hoàn thành công việc này");
            }
        });
    }

    private void loadTaskData() {
        if (taskId == -1) {
            Toast.makeText(this, "Không tìm thấy công việc!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentTask = dbHelper.getTaskById(taskId);
        if (currentTask == null) {
            Toast.makeText(this, "Công việc đã bị xóa!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etTitle.setText(currentTask.getTitle());
        etDescription.setText(currentTask.getDescription());
        cbCompleted.setChecked(currentTask.isCompleted());
        tvCompletedStatus.setText(currentTask.isCompleted() ? "Đã hoàn thành công việc này" : "Chưa hoàn thành công việc này");

        if (currentTask.getDueTimestamp() > 0) {
            selectedCalendar.setTimeInMillis(currentTask.getDueTimestamp());
        }
        updateDateTimeLabel();

        if ("Cao".equalsIgnoreCase(currentTask.getPriority())) {
            spinnerPriority.setSelection(0);
        } else if ("Thấp".equalsIgnoreCase(currentTask.getPriority())) {
            spinnerPriority.setSelection(2);
        } else {
            spinnerPriority.setSelection(1);
        }

        if ("Cá nhân".equalsIgnoreCase(currentTask.getCategory())) {
            spinnerCategory.setSelection(1);
        } else if ("Công việc".equalsIgnoreCase(currentTask.getCategory())) {
            spinnerCategory.setSelection(2);
        } else {
            spinnerCategory.setSelection(0);
        }
    }

    private void showDatePicker() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            selectedCalendar.set(Calendar.YEAR, selectedYear);
            selectedCalendar.set(Calendar.MONTH, selectedMonth);
            selectedCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);
            updateDateTimeLabel();
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        int hour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = selectedCalendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            selectedCalendar.set(Calendar.MINUTE, selectedMinute);
            selectedCalendar.set(Calendar.SECOND, 0);
            updateDateTimeLabel();
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void updateDateTimeLabel() {
        String formatted = String.format(Locale.getDefault(), "%02d:%02d - %02d/%02d/%d",
                selectedCalendar.get(Calendar.HOUR_OF_DAY),
                selectedCalendar.get(Calendar.MINUTE),
                selectedCalendar.get(Calendar.DAY_OF_MONTH),
                selectedCalendar.get(Calendar.MONTH) + 1,
                selectedCalendar.get(Calendar.YEAR));
        tvSelectedDateTime.setText("Thời hạn đã chọn: " + formatted);
    }

    private void updateTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priority = spinnerPriority.getSelectedItem().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        boolean isCompleted = cbCompleted.isChecked();

        if (title.isEmpty()) {
            etTitle.setError("Vui lòng nhập tiêu đề!");
            etTitle.requestFocus();
            return;
        }

        String formattedDate = String.format(Locale.getDefault(), "%02d:%02d - %02d/%02d/%d",
                selectedCalendar.get(Calendar.HOUR_OF_DAY),
                selectedCalendar.get(Calendar.MINUTE),
                selectedCalendar.get(Calendar.DAY_OF_MONTH),
                selectedCalendar.get(Calendar.MONTH) + 1,
                selectedCalendar.get(Calendar.YEAR));

        long timestamp = selectedCalendar.getTimeInMillis();

        currentTask.setTitle(title);
        currentTask.setDescription(description);
        currentTask.setDueDate(formattedDate);
        currentTask.setDueTimestamp(timestamp);
        currentTask.setPriority(priority);
        currentTask.setCategory(category);
        currentTask.setIsCompleted(isCompleted ? 1 : 0);

        int rows = dbHelper.updateTask(currentTask);
        if (rows > 0) {
            if (isCompleted) {
                NotificationReceiver.cancelNotification(this, currentTask.getId());
            } else {
                NotificationReceiver.scheduleNotification(this, currentTask);
            }
            Toast.makeText(this, "Cập nhật công việc thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteTask() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa công việc \"" + currentTask.getTitle() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.deleteTask(currentTask.getId());
                    NotificationReceiver.cancelNotification(DetailEditTaskActivity.this, currentTask.getId());
                    Toast.makeText(DetailEditTaskActivity.this, "Đã xóa công việc!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}