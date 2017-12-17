package adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lwz.smartpillow.R;

import java.util.Calendar;
import java.util.List;

import entity.calendarData;

/**
 * Created by Li Wenzhao on 2017/7/21.
 */

public class CalendarRecycleViewAdapter extends RecyclerView.Adapter<CalendarRecycleViewAdapter.ViewHolder> {
    private List<calendarData> calendarDataList;
    private OnItemClickListener mOnItemClickListener = null;
    private Context context;
    private int year, month;

    public CalendarRecycleViewAdapter(Context context, List<calendarData> calendarDataList, int year, int month) {
        this.calendarDataList = calendarDataList;
        this.context = context;
        this.year = year;
        this.month = month;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //绑定布局
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cal_item, parent, false);
        //创建ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        itemLayoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, (int) v.getTag());
                }
            }
        });

        itemLayoutView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemLongClick(v, (int) v.getTag());
                }
                return false;
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_day.setBackgroundResource(R.drawable.other_day_bg);
        holder.tv_day.setText(calendarDataList.get(position).getDay());
        holder.tv_useTime.setText(calendarDataList.get(position).getUseTime());
        if(!holder.tv_day.equals("")) {
            if(isToday(holder.tv_day.getText().toString())) {
                holder.tv_day.setTextColor(context.getResources().getColor(R.color.white));
                holder.tv_day.setBackgroundResource(R.drawable.current_day_bg);
                holder.tv_useTime.setTextColor(context.getResources().getColor(R.color.select_red));
            }
        }
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return calendarDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_day;
        public TextView tv_useTime;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            tv_day = (TextView) itemLayoutView.findViewById(R.id.tv_day);
            tv_useTime = (TextView) itemLayoutView.findViewById(R.id.tv_useTime);
        }
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view , int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    private boolean isToday(String day) {
        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.YEAR) == year && (calendar.get(Calendar.MONTH) + 1) == month && (calendar.get(Calendar.DAY_OF_MONTH) + "").equals(day))
            return true;
        return false;
    }
}
