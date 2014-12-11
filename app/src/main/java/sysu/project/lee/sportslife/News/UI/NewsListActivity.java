package sysu.project.lee.sportslife.News.UI;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import java.io.File;
import java.util.ArrayList;

import sysu.project.lee.sportslife.News.Utils.ActionLabelUtils;
import sysu.project.lee.sportslife.News.Utils.SectionHelper;
import sysu.project.lee.sportslife.News.Utils.SeriaHelper;
import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.Utils.ToastUtils;

/**
 * Created by lee on 14年12月1日.
 */
public class NewsListActivity extends Activity {

    private PullToRefreshListView mListView;
    private ArrayList<FeedItem> mItems = new ArrayList<FeedItem>();
    private ItemListAdapter mAdapter = null;
    private SeriaHelper seriaHelper;
    private String sectionUrl;
    private ImageView btn_navi_back = null;
    private String sectionTitle = null;
    private BroadcastReceiver mReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news_list_layout);
        Intent intent = getIntent();
        sectionUrl = intent.getStringExtra("url");
        sectionTitle = "NEONAN";
        initView();
        initData();
        initBroadeCast();

    }

    private void initView() {
        btn_navi_back = (ImageView) findViewById(R.id.navi_back);
        mListView = (PullToRefreshListView) findViewById(R.id.news_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                FeedItem item = mItems.get(position - 1);

                final String link = item.getLink();
                // 改变阅读状态
                if (!item.isReaded())
                {
                    item.setReaded(true);
                    mAdapter.notifyDataSetChanged();

                    new Thread()
                    {
                        @Override
                        public void run()
                        {
                            SeriaHelper helper = SeriaHelper.newInstance();
                            File cache = SectionHelper.getSdCache(sectionUrl);
                            ItemListEntity entity = new ItemListEntity();
                            for (FeedItem i : mItems)
                            {
                                if (i.getLink().equals(link))
                                {
                                    i.setReaded(true);
                                }
                            }
                            entity.setItemList(mItems);
                            helper.saveObject(entity, cache);
                        }

                    }.start();
                }
                String title = item.getTitle();
                String contentEncoded = item.getContent();
                String pubdate = item.getPubdate();
                boolean isFavorite = item.isFavorite();
                String firstImgUrl = item.getFirstImageUrl();
                if (contentEncoded != null && contentEncoded.length() != 0)
                {
                    intent.putExtra("item_detail", contentEncoded);
                }
                intent.putExtra("section_title", sectionTitle);
                intent.putExtra("section_url", sectionUrl);
                intent.putExtra("title", title);
                intent.putExtra("pubdate", pubdate);
                intent.putExtra("link", link);
                intent.putExtra("is_favorite", isFavorite);
                intent.putExtra("first_img_url", firstImgUrl);
                intent.setClass(NewsListActivity.this, ItemDetail.class);
                startActivity(intent);

            }
        });
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                new RefreshTask().execute(sectionUrl);
            }
        });

        btn_navi_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initData() {
//        mAdapter = new ItemListAdapter(NewsListActivity.this, dataList);
//        mListView.setAdapter(mAdapter);
        File file = SectionHelper.getSdCache(sectionUrl);
        Log.i("FILEpath",file.getAbsolutePath());
        if (file.exists())
        {
            seriaHelper = SeriaHelper.newInstance();
            ItemListEntity itemListEntity = (ItemListEntity) seriaHelper
                    .readObject(file);
            mItems = itemListEntity.getItemList();
            if (mItems != null)
            {
                mAdapter = new ItemListAdapter(this, mItems, false);
                mListView.setAdapter(mAdapter);
            }
        }
    }


//    /**
//     * 显示进度框
//     */
//    private void showProgressDialog() {
//        if (progressDialog == null){
//            progressDialog = new ProgressDialog(this);
//            progressDialog.setMessage("正在加载……");
//            progressDialog.setCancelable(false);
//        }
//        progressDialog.show();
//
//    }
//
//    /**
//     * 关闭进度框
//     */
//
//    private void closeProgressDialog() {
//        if (progressDialog != null){
//            progressDialog.dismiss();
//        }
//    }


    private class RefreshTask extends AsyncTask<String, Integer, ItemListEntity>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ItemListEntity result)
        {
            if (result == null)
            {
                mListView.onRefreshComplete();
                ToastUtils.show(NewsListActivity.this, "网错错误，加载失败");
                return;
            }
            ArrayList<FeedItem> newItems = new ArrayList<FeedItem>();
            File cache = SectionHelper.getSdCache(sectionUrl);
            SeriaHelper helper = SeriaHelper.newInstance();
            ArrayList<FeedItem> items = result.getItemList();
            ItemListEntity old = (ItemListEntity) helper.readObject(cache);
            String oldFirstDate = old.getFirstItem().getPubdate();
            String oldFirstFeedItemTitle = old.getFirstItem().getTitle();
            int newCount = 0;
            for (FeedItem i : items)
            {
                if (i.getTitle().equals(oldFirstFeedItemTitle))
                {
                    mListView.onRefreshComplete();
                    ToastUtils.show(NewsListActivity.this, "已经是最新的数据了");
                    return;
                }
                newCount++;
                newItems.add(i);
            }
            helper.saveObject(result, cache);
            mAdapter.addItemsToHead(newItems);
            ToastUtils.show(NewsListActivity.this, "更新了"+newCount+"条新资讯！");
//            Toast.makeText(ItemList.this, "更新了" + newCount + "条",Toast.LENGTH_SHORT).show();
            mListView.onRefreshComplete();
        }

        @Override
        protected ItemListEntity doInBackground(String... params)
        {
            ItemListEntityParser parser = new ItemListEntityParser();
            return parser.parse(params[0]);
        }
    }

    private void initBroadeCast()
    {
        mReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String link = intent.getStringExtra("link");
                boolean isFavorite = intent.getBooleanExtra("is_favorite",
                        false);
                for (FeedItem i : mItems)
                {
                    if (i.getLink().equals(link))
                    {
                        i.setFavorite(isFavorite);
                        break;
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ActionLabelUtils.FAVORITE_ACTION);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
