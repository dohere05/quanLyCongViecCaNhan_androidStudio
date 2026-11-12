package com.example.myapplicationdo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private static final int PERMISSION_REQUEST_CODE = 101;

    private RecyclerView rvTasks;
    private TextView tvEmpty;
    private EditText etSearch;
    private ChipGroup chipGroupStatus;
    private Spinner spinnerCategoryFilter;
    private FloatingActionButton fabAddTask;

    private DatabaseHelper dbHelper;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    private String currentKeyword = "";
    private String currentStatusFilter = "ALL"; // ALL, PENDING, DONE
    private String currentCategoryFilter = "Tất cả";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Đăng ký cấp quyền thông báo nếu dùng Android 13+
        requestNotificationPermission();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);
        taskList = new ArrayList<>();

        rvTasks = findViewById(R.id.rvTasks);
        tvEmpty = findViewById(R.id.tvEmpty);
        etSearch = findViewById(R.id.etSearch);
        chipGroupStatus = findViewById(R.id.chipGroupStatus);
        spinnerCategoryFilter = findViewById(R.id.spinnerCategoryFilter);
        fabAddTask = findViewById(R.id.fabAddTask);

        // Thiết lập RecyclerView
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, taskList, this);
        rvTasks.setAdapter(taskAdapter);

        // Cấu hình Spinner Danh mục
        String[] categories = {"Tất cả", "Học tập", "Cá nhân", "Công việc"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategoryFilter.setAdapter(spinnerAdapter);

        // Sự kiện lọc Danh mục
        spinnerCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCategoryFilter = categories[position];
                loadTasks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Sự kiện lọc Trạng thái (ChipGroup)
        chipGroupStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipDone) {
                currentStatusFilter = "DONE";
            } else if (checkedId == R.id.chipPending) {
                currentStatusFilter = "PENDING";
            } else {
                currentStatusFilter = "ALL";
            }
            loadTasks();
        });

        // Sự kiện Tìm kiếm
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentKeyword = s.toString();
                loadTasks();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Nút thêm mới công việc
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        taskList = dbHelper.filterTasks(currentKeyword, currentStatusFilter, currentCategoryFilter);
        taskAdapter.setTaskList(taskList);

        if (taskList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvTasks.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvTasks.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_statistics) {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(MainActivity.this, DetailEditTaskActivity.class);
        intent.putExtra(DetailEditTaskActivity.EXTRA_TASK_ID, task.getId());
        startActivity(intent);
    }

    @Override
    public void onTaskStatusChanged(Task task, boolean isCompleted) {
        dbHelper.updateTaskStatus(task.getId(), isCompleted ? 1 : 0);
        if (isCompleted) {
            NotificationReceiver.cancelNotification(this, task.getId());
        } else {
            NotificationReceiver.scheduleNotification(this, task);
        }
        loadTasks();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }
    }
}