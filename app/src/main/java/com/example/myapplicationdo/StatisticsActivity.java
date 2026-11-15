package com.example.myapplicationdo;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity implements TaskSeriesAdapter.OnSeriesClickListener {

    private TextView tvTotalTasks, tvCompletedTasks, tvPendingTasks, tvOverdueTasks, tvPercent, tvAdvice, tvEmptySeries;
    private ProgressBar progressBarCompletion;
    private Spinner spinnerTimeRange;
    private RecyclerView rvTaskSeries;

    private DatabaseHelper dbHelper;
    private TaskSeriesAdapter seriesAdapter;
    private List<TaskSeries> seriesList;

    private int selectedTimeRangeIndex = 0; // 0: Tất cả, 1: 1 ngày, 2: 1 tuần, 3: 1 tháng, 4: 1 năm, 5: Hơn 1 năm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        seriesList = new ArrayList<>();

        tvTotalTasks = findViewById(R.id.tvTotalTasks);
        tvCompletedTasks = findViewById(R.id.tvCompletedTasks);
        tvPendingTasks = findViewById(R.id.tvPendingTasks);
        tvOverdueTasks = findViewById(R.id.tvOverdueTasks);
        tvPercent = findViewById(R.id.tvPercent);
        tvAdvice = findViewById(R.id.tvAdvice);
        tvEmptySeries = findViewById(R.id.tvEmptySeries);
        progressBarCompletion = findViewById(R.id.progressBarCompletion);
        spinnerTimeRange = findViewById(R.id.spinnerTimeRange);
        rvTaskSeries = findViewById(R.id.rvTaskSeries);

        // Thiết lập RecyclerView cho Seri Công Việc
        rvTaskSeries.setLayoutManager(new LinearLayoutManager(this));
        seriesAdapter = new TaskSeriesAdapter(this, seriesList, this);
        rvTaskSeries.setAdapter(seriesAdapter);

        // Cấu hình Spinner Khung thời gian
        String[] timeRanges = {
                "Tất cả thời gian",
                "Hôm nay (1 ngày)",
                "Trong 1 tuần (7 ngày)",
                "Trong 1 tháng (30 ngày)",
                "Trong 1 năm (365 ngày)",
                "Hơn 1 năm"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timeRanges);
        spinnerTimeRange.setAdapter(adapter);

        spinnerTimeRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTimeRangeIndex = position;
                loadStatistics();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        loadMonthlySeries();
    }

    private void loadStatistics() {
        List<Task> filteredTasks = dbHelper.getTasksByTimeRange(selectedTimeRangeIndex);

        int total = filteredTasks.size();
        int completed = 0;
        int pending = 0;
        int overdue = 0;
        long currentTime = System.currentTimeMillis();

        for (Task task : filteredTasks) {
            if (task.isCompleted()) {
                completed++;
            } else {
                pending++;
                if (task.getDueTimestamp() > 0 && task.getDueTimestamp() < currentTime) {
                    overdue++;
                }
            }
        }

        tvTotalTasks.setText(String.valueOf(total));
        tvCompletedTasks.setText(String.valueOf(completed));
        tvPendingTasks.setText(String.valueOf(pending));
        tvOverdueTasks.setText(String.valueOf(overdue));

        int percentage = total > 0 ? (completed * 100) / total : 0;
        tvPercent.setText(percentage + "%");
        progressBarCompletion.setProgress(percentage);

        if (total == 0) {
            tvAdvice.setText("Không có công việc nào trong khung thời gian này!");
        } else if (percentage == 100) {
            tvAdvice.setText("🎉 Tuyệt vời! Bạn đã hoàn thành 100% công việc trong mốc này!");
        } else if (percentage >= 50) {
            tvAdvice.setText("👍 Bạn đang làm rất tốt, tiếp tục hoàn thành số việc còn lại nhé!");
        } else {
            tvAdvice.setText("💪 Cố gắng lên! Hãy ưu tiên các công việc quan trọng trước.");
        }
    }

    private void loadMonthlySeries() {
        seriesList = dbHelper.getMonthlyTaskSeries();
        seriesAdapter.setSeriesList(seriesList);

        if (seriesList.isEmpty()) {
            tvEmptySeries.setVisibility(View.VISIBLE);
            rvTaskSeries.setVisibility(View.GONE);
        } else {
            tvEmptySeries.setVisibility(View.GONE);
            rvTaskSeries.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSeriesClick(TaskSeries series) {
        // Mở dialog danh sách công việc chi tiết trong Seri này
        StringBuilder builder = new StringBuilder();
        builder.append("📊 Tiến độ: ").append(series.getProgressPercentage()).append("% (")
                .append(series.getCompletedTasks()).append("/").append(series.getTotalTasks()).append(" việc)\n\n");

        for (int i = 0; i < series.getTaskList().size(); i++) {
            Task task = series.getTaskList().get(i);
            String statusIcon = task.isCompleted() ? "✅" : "⏳";
            builder.append(i + 1).append(". ").append(statusIcon).append(" ")
                    .append(task.getTitle()).append(" (").append(task.getDueDate()).append(")\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("📂 Seri " + series.getMonthYear())
                .setMessage(builder.toString())
                .setPositiveButton("Đóng", null)
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