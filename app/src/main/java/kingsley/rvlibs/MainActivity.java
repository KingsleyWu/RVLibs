package kingsley.rvlibs;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kingsley.rvlibrary.base.RVBaseAdapter;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, RVBaseAdapter.OnLoadingListener {

    private RecyclerView mRecyclerView;
    private List<String> mDatas;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerViewAdapter adapter;
    private static final int PAGE_SIZE = 6;
    private boolean isErr;
    private int mCurrentCounter;
    private static final int TOTAL_COUNTER = 18;
    private boolean mLoadEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatas = new ArrayList<>();
        refresh();
        initView();
    }

    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //mRecyclerView.setHasFixedSize(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        adapter = new RecyclerViewAdapter(R.layout.item_layout,mDatas);
        /*final GridLayoutManager manager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(manager);*/
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        View headerView = getLayoutInflater().inflate(R.layout.header_layout, (ViewGroup) mRecyclerView.getParent(), false);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoadEnd = true;
                mRecyclerView.setAdapter(adapter);
                Toast.makeText(MainActivity.this, "更改完成", Toast.LENGTH_LONG).show();
            }
        });
        adapter.addHeaderView(headerView);
        adapter.addFooterView(getLayoutInflater().inflate(R.layout.footer_layout, (ViewGroup) mRecyclerView.getParent(), false));
        adapter.setEmptyLayoutId(R.layout.empty_layout);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(new RVBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Toast.makeText(MainActivity.this, "position = " + position +"  getAdapterPosition = "+holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        adapter.setOnLoadingListener(this);
        mCurrentCounter = adapter.getDatas().size();
    }

    private void refresh() {
        for (int i = 0; i < 10; i++) {
            mDatas.add("data" + i);
        }
    }

    private List<String> reRefresh(int length) {
        List<String> data= new ArrayList<>();
        for (int i = 0; i < length; i++) {
            data.add("data" + i);
        }
        return data;
    }

    @Override
    public void onRefresh() {
        adapter.setEnableLoading(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setNewData(reRefresh(PAGE_SIZE));
                isErr = false;
                mCurrentCounter = PAGE_SIZE;
                mSwipeRefreshLayout.setRefreshing(false);
                adapter.setEnableLoading(true);
            }
        }, 2000);
    }

    @Override
    public void onLoading() {
        mSwipeRefreshLayout.setEnabled(false);
        if (adapter.getDatas().size() < PAGE_SIZE){
            adapter.loadEnd(true);
        }else {
            if (mCurrentCounter >= TOTAL_COUNTER){
                adapter.loadEnd(mLoadEnd);
            }else {
                if (isErr) {
                    adapter.addData(reRefresh(PAGE_SIZE));
                    mCurrentCounter = adapter.getDatas().size();
                    adapter.loadingComplete();
                }else {
                    isErr = true;
                    Toast.makeText(MainActivity.this, "模拟网络错误", Toast.LENGTH_LONG).show();
                    adapter.loadingFailed();
                }
            }
            mSwipeRefreshLayout.setEnabled(true);
        }
    }
}
