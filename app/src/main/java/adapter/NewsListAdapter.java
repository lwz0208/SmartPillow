package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lwz.smartpillow.R;

import java.util.List;

import entity.NewsData;

/**
 * Created by Li Wenzhao on 2017/12/4.
 */

public class NewsListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<NewsData.DataBean.ItemsBean> newsList;

    public NewsListAdapter(Context context, List<NewsData.DataBean.ItemsBean> newsList) {
        this.newsList = newsList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    private class ViewHolder {
        public TextView tv_newsTitle;
        public TextView tv_newsTime;
        public ImageView iv_newsPic;
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder myViewHolder = null;
        if (convertView == null)
        {
            myViewHolder = new ViewHolder();
            // 获取list_item布局文件的视图
            convertView = layoutInflater.inflate(R.layout.news_item, null);
            // 获取控件对象
            myViewHolder.tv_newsTitle = (TextView) convertView
                    .findViewById(R.id.tv_newsTitle);
            myViewHolder.tv_newsTime = (TextView) convertView
                    .findViewById(R.id.tv_newsTime);
            myViewHolder.iv_newsPic = (ImageView) convertView
                    .findViewById(R.id.iv_newsPic);
            // 设置控件集到convertView
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (ViewHolder) convertView.getTag();
        }

        myViewHolder.tv_newsTitle.setText(newsList.get(position).getNewsTitle());
        myViewHolder.tv_newsTime.setText(newsList.get(position).getNewsDate().substring(0,10));
        Glide.with(context).load("http://cheerstech.cn:8063" + newsList.get(position).getNewsPicture()).placeholder(R.drawable.unload_bg).into(myViewHolder.iv_newsPic);

        return convertView;
    }
}
