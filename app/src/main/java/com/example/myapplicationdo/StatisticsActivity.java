package com.example.myapplicationdo;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class StatisticsActivity extends AppCompatActivity {

    private TextView tvTotalTasks, tvCompletedTasks, tvPendingTasks, tvOverdueTasks, tvPercent, tvAdvice;
    private ProgressBar progressBarCompletion;
    private DatabaseHelper dbHelper;

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

        tvTotalTasks = findViewById(R.id.tvTotalTasks);
        tvCompletedTasks = findViewById(R.id.tvCompletedTasks);
        tvPendingTasks = findViewById(R.id.tvPendingTasks);
        tvOverdueTasks = findViewById(R.id.tvOverdueTasks);
        tvPercent = findViewById(R.id.tvPercent);
        tvAdvice = findViewById(R.id.tvAdvice);
        progressBarCompletion = findViewById(R.id.progressBarCompletion);

        loadStatistics();
    }

    private void loadStatistics() {
        int total = dbHelper.getTotalTaskCount();
        int completed = dbHelper.getCompletedTaskCount();
        int pending = dbHelper.getPendingTaskCount();
        int overdue = dbHelper.getOverdueTaskCount(System.currentTimeMillis());

        tvTotalTasks.setText(String.valueOf(total));
        tvCompletedTasks.setText(String.valueOf(completed));
        tvPendingTasks.setText(String.valueOf(pending));
        tvOverdueTasks.setText(String.valueOf(overdue));

        int percentage = total > 0 ? (completed * 100) / total : 0;
        tvPercent.setText(percentage + "%");
        progressBarCompletion.setProgress(percentage);

        if (total == 0) {
            tvAdvice.setText("Hãy tạo công việc đầu tiên để bắt đầu theo dõi tiến độ!");
        } else if (percentage == 100) {
            tvAdvice.setText("🎉 Tuyệt vời! Bạn đã hoàn thành 100% công việc!");
        } else if (percentage >= 50) {
            tvAdvice.setText("👍 Bạn đang làm rất tốt, tiếp tục phát huy nhé!");
        } else {
            tvAdvice.setText("💪 Cố gắng lên! Còn nhiều công việc cần hoàn thành.");
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