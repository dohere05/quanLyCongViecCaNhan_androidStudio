package com.example.myapplicationdo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private Button btnSelectDate, btnSelectTime, btnSaveTask;
    private TextView tvSelectedDateTime;
    private Spinner spinnerPriority, spinnerCategory;

    private DatabaseHelper dbHelper;

    private Calendar selectedCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        selectedCalendar = Calendar.getInstance();

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        tvSelectedDateTime = findViewById(R.id.tvSelectedDateTime);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        // Thiết lập Spinner Ưu tiên
        String[] priorities = {"Cao", "Trung bình", "Thấp"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, priorities);
        spinnerPriority.setAdapter(priorityAdapter);
        spinnerPriority.setSelection(1); // Mặc định Trung bình

        // Thiết lập Spinner Danh mục
        String[] categories = {"Học tập", "Cá nhân", "Công việc"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(categoryAdapter);

        // Nút chọn Ngày
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Nút chọn Giờ
        btnSelectTime.setOnClickListener(v -> showTimePicker());

        // Nút Lưu
        btnSaveTask.setOnClickListener(v -> saveTask());

        updateDateTimeLabel();
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

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priority = spinnerPriority.getSelectedItem().toString();
        String category = spinnerCategory.getSelectedItem().toString();

        if (title.isEmpty()) {
            etTitle.setError("Vui lòng nhập tiêu đề công việc!");
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

        Task task = new Task(title, description, formattedDate, timestamp, priority, category, 0);
        long id = dbHelper.addTask(task);

        if (id > 0) {
            task.setId((int) id);
            // Lên lịch nhắc nhở qua AlarmManager
            NotificationReceiver.scheduleNotification(this, task);

            Toast.makeText(this, "Thêm công việc mới thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi lưu công việc!", Toast.LENGTH_SHORT).show();
        }
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