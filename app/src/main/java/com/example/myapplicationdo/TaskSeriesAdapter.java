package com.example.myapplicationdo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskSeriesAdapter extends RecyclerView.Adapter<TaskSeriesAdapter.SeriesViewHolder> {

    public interface OnSeriesClickListener {
        void onSeriesClick(TaskSeries series);
    }

    private Context context;
    private List<TaskSeries> seriesList;
    private OnSeriesClickListener listener;

    public TaskSeriesAdapter(Context context, List<TaskSeries> seriesList, OnSeriesClickListener listener) {
        this.context = context;
        this.seriesList = seriesList;
        this.listener = listener;
    }

    public void setSeriesList(List<TaskSeries> seriesList) {
        this.seriesList = seriesList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_series, parent, false);
        return new SeriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesViewHolder holder, int position) {
        TaskSeries series = seriesList.get(position);

        holder.tvSeriesTitle.setText("📅 Seri " + series.getMonthYear());
        holder.tvSeriesDetail.setText(series.getTotalTasks() + " công việc • "
                + series.getCompletedTasks() + " đã làm • "
                + series.getPendingTasks() + " chưa làm");

        int percent = series.getProgressPercentage();
        holder.tvSeriesPercent.setText(percent + "%");
        holder.progressBarSeries.setProgress(percent);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSeriesClick(series);
            }
        });
    }

    @Override
    public int getItemCount() {
        return seriesList != null ? seriesList.size() : 0;
    }

    static class SeriesViewHolder extends RecyclerView.ViewHolder {
        TextView tvSeriesTitle, tvSeriesDetail, tvSeriesPercent;
        ProgressBar progressBarSeries;

        public SeriesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSeriesTitle = itemView.findViewById(R.id.tvSeriesTitle);
            tvSeriesDetail = itemView.findViewById(R.id.tvSeriesDetail);
            tvSeriesPercent = itemView.findViewById(R.id.tvSeriesPercent);
            progressBarSeries = itemView.findViewById(R.id.progressBarSeries);
        }
    }
}