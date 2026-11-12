package com.example.myapplicationdo;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskStatusChanged(Task task, boolean isCompleted);
    }

    private Context context;
    private List<Task> taskList;
    private OnTaskClickListener listener;

    public TaskAdapter(Context context, List<Task> taskList, OnTaskClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.tvTitle.setText(task.getTitle());
        holder.tvDueDate.setText("⏰ " + task.getDueDate());
        holder.tvCategory.setText(task.getCategory());
        holder.tvPriority.setText(task.getPriority());

        // Ưu tiên: đổi màu indicator và badge
        int priorityColor;
        int priorityBg;
        if ("Cao".equalsIgnoreCase(task.getPriority())) {
            priorityColor = ContextCompat.getColor(context, R.color.priority_high);
            priorityBg = R.drawable.bg_priority_high;
        } else if ("Trung bình".equalsIgnoreCase(task.getPriority())) {
            priorityColor = ContextCompat.getColor(context, R.color.priority_medium);
            priorityBg = R.drawable.bg_priority_medium;
        } else {
            priorityColor = ContextCompat.getColor(context, R.color.priority_low);
            priorityBg = R.drawable.bg_priority_low;
        }
        holder.viewPriorityIndicator.setBackgroundColor(priorityColor);
        holder.tvPriority.setTextColor(priorityColor);
        holder.tvPriority.setBackgroundResource(priorityBg);

        // Trạng thái hoàn thành & Gạch ngang chữ (Strikethrough)
        holder.cbCompleted.setOnCheckedChangeListener(null);
        holder.cbCompleted.setChecked(task.isCompleted());

        if (task.isCompleted()) {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.gray));
        } else {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        // Kiểm tra trễ hạn (overdue)
        long currentTime = System.currentTimeMillis();
        if (!task.isCompleted() && task.getDueTimestamp() > 0 && task.getDueTimestamp() < currentTime) {
            holder.tvDueDate.setTextColor(ContextCompat.getColor(context, R.color.overdue_red));
            holder.tvDueDate.setText("⚠️ Trễ hạn: " + task.getDueDate());
        } else {
            holder.tvDueDate.setTextColor(ContextCompat.getColor(context, R.color.gray));
        }

        // Sự kiện tick hoàn thành
        holder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setIsCompleted(isChecked ? 1 : 0);
            if (isChecked) {
                holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.gray));
            } else {
                holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.black));
            }
            if (listener != null) {
                listener.onTaskStatusChanged(task, isChecked);
            }
        });

        // Click xem chi tiết / sửa
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        View viewPriorityIndicator;
        CheckBox cbCompleted;
        TextView tvTitle, tvDueDate, tvCategory, tvPriority;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            viewPriorityIndicator = itemView.findViewById(R.id.viewPriorityIndicator);
            cbCompleted = itemView.findViewById(R.id.cbCompleted);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPriority = itemView.findViewById(R.id.tvPriority);
        }
    }
}