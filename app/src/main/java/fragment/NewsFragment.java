package fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lwz.smartpillow.NewsDetailActivity;
import com.lwz.smartpillow.R;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.header.FalsifyHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import adapter.NewsListAdapter;
import entity.NewsData;
import okhttp3.Call;
import utils.CalculateSignature;
import utils.URL_UNIVERSAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {
    private SmartRefreshLayout refreshLayout;
    private ListView listView;
    private NewsData newsData;
    private NewsListAdapter adapter;
    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_news, container, false);

        refreshLayout = (SmartRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new WaterDropHeader(getContext()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getNews();
                refreshLayout.finishRefresh();
            }
        });
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("data", newsData.getData().getItems().get(i));
                startActivity(intent);
            }
        });
        refreshLayout.autoRefresh();
        return view;
    }

    private void getNews() {
        OkHttpUtils.get().url(URL_UNIVERSAL.NEWS_URL)
                .addParams("userid", "15671618162")
                .addParams("newsType", "health_product")
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        Log.i("getNews", "接口访问失败：" + call + "---" + e);
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.i("getNews", "接口访问成功：" + response);
                        JSONObject jsonObject = JSON.parseObject(response);
                        newsData = JSON.parseObject(jsonObject.toJSONString(), NewsData.class);
                        adapter = new NewsListAdapter(getContext(), newsData.getData().getItems());
                        listView.setAdapter(adapter);
                    }
                });
    }
}
