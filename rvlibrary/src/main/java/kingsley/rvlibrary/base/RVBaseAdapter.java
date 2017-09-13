package kingsley.rvlibrary.base;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kingsley.rvlibrary.Constant;
import kingsley.rvlibrary.R;
import kingsley.rvlibrary.loading.LoadingView;

/**
 * class name : RVLibrary
 * author : Kingsley
 * created date : on 2017/7/21 22:12
 * file change date : on 2017/7/21 22:12
 * version: 1.0
 */

public abstract class RVBaseAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    protected Context mContext;
    protected List<T> mDatas;
    private RecyclerView mRecyclerView;
    private int mLayoutResId;
    private int mEmptyLayoutResId;
    private LoadingView mLoadingView = new LoadingView();
    private FrameLayout mEmptyView;
    private OnItemClickListener mOnItemClickListener;
    private OnLoadingListener mOnLoadingListener;
    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();
    private boolean mIsUseEmpty = true;
    private boolean mIsLoading;
    private boolean mLoadingEnable;
    private boolean mNextLoadEnable;
    private boolean mEnableLoadingEndClick;
    private int mPreLoadNumber = 1;

    public RVBaseAdapter(int layoutResId, List<T> data) {
        mDatas = data == null ? new ArrayList<T>() : data;
        if (layoutResId != 0) mLayoutResId = layoutResId;
    }

    public RVBaseAdapter(List<T> data) {
        mDatas = data == null ? new ArrayList<T>() : data;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    if (mHeaderViews.get(viewType) != null) {
                        return gridLayoutManager.getSpanCount();
                    } else if (mFootViews.get(viewType) != null) {
                        return gridLayoutManager.getSpanCount();
                    }
                    if (spanSizeLookup != null)
                        return spanSizeLookup.getSpanSize(position);
                    return 1;
                }
            });
            gridLayoutManager.setSpanCount(gridLayoutManager.getSpanCount());
        }
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int viewType = holder.getItemViewType();
        if (viewType == Constant.EMPTY_TYPE || mHeaderViews.get(viewType) != null ||
                mFootViews.get(viewType) != null || viewType == Constant.LOADING_TYPE) {
            setFullSpan(holder);
        }
    }

    private void setFullSpan(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams LayoutParams = (StaggeredGridLayoutManager.LayoutParams) params;
            LayoutParams.setFullSpan(true);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder;
        mContext = parent.getContext();
        if (mHeaderViews.get(viewType) != null) {
            holder = ViewHolder.createViewHolder(mContext, mHeaderViews.get(viewType));
        } else if (mFootViews.get(viewType) != null) {
            holder = ViewHolder.createViewHolder(mContext, mFootViews.get(viewType));
        } else if (viewType == Constant.EMPTY_TYPE) {
            if (mEmptyView != null)
                holder = ViewHolder.createViewHolder(mContext, mEmptyView);
            else
                holder = ViewHolder.createViewHolder(parent, mEmptyLayoutResId);
        } else if (viewType == Constant.LOADING_TYPE) {
            if (mLoadingView.getLoadingLayoutId() != 0)
                holder = createLoadingViewHolder(parent);
            else
                throw new NullPointerException("LoadingView can't be empty !! ");
        } else {
            holder = createDefViewHolder(parent, viewType);
            setListener(holder, viewType);
        }
        onViewHolderCreated(holder, holder.getItemView());
        return holder;
    }

    protected ViewHolder createDefViewHolder(ViewGroup parent, int viewType) {
        return ViewHolder.createViewHolder(parent, mLayoutResId);
    }

    protected ViewHolder createLoadingViewHolder(ViewGroup parent) {
        ViewHolder holder = ViewHolder.createViewHolder(parent, mLoadingView.getLoadingLayoutId());
        holder.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLoadingView.getLoadingStatus() == Constant.FAILED_STATUS) {
                    notifyLoading();
                }
                if (mEnableLoadingEndClick && mLoadingView.getLoadingStatus() == Constant.END_STATUS) {
                    notifyLoading();
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (isHeaderPos(position)) return;
        if (isFooterPos(position)) return;
        if (isLoadingPos(position)) {
            mLoadingView.showLoadingView(holder);
            return;
        }
        convert(holder, mDatas.get(position - getHeadersCount()));
    }

    @Override
    public int getItemViewType(int position) {
        autoLoading(position);
        if (isLoadingPos(position)) {
            return Constant.LOADING_TYPE;
        } else if (getEmptyViewCount() == 1) {
            return Constant.EMPTY_TYPE;
        } else if (mHeaderViews.size() >= 1 && isHeaderPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (mFootViews.size() >= 1 && isFooterPos(position)) {
            return mFootViews.keyAt(position - getHeadersCount() - getRealItemCount());
        } else {
            return getDefItemViewType(position - getHeadersCount());
        }
    }

    protected int getDefItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        int count;
        if (getEmptyViewCount() == 1) count = 1;
        else
            count = getHeadersCount() + getRealItemCount() + getFootersCount() + getLoadingViewCount();
        return count;
    }

    public void notifyLoading() {
        if (mLoadingView.getLoadingStatus() == Constant.LOADING_STATUS) {
            return;
        }
        mLoadingView.setLoadingStatus(Constant.DEFAULT_STATUS);
        notifyItemChanged(getLoadingViewPosition());
    }

    public void setEnableLoadEndClick(boolean enable) {
        mEnableLoadingEndClick = enable;
    }

    public void setPreLoadNumber(int preLoadNumber) {
        if (preLoadNumber > 1) {
            mPreLoadNumber = preLoadNumber;
        }
    }

    private int getRealItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    private int getHeadersCount() {
        return mHeaderViews.size();
    }

    private int getFootersCount() {
        return mFootViews.size();
    }

    private int getLoadingViewCount() {
        if (mOnLoadingListener == null || !mLoadingEnable) return 0;
        if (!mNextLoadEnable && mLoadingView.isLoadEnd()) return 0;
        if (mDatas.size() == 0) return 0;
        return 1;
    }

    private int getEmptyViewCount() {
        if (mEmptyView == null || mEmptyLayoutResId != 0) return 0;
        if (!mIsUseEmpty) return 0;
        if (mDatas.size() != 0) return 0;
        return 1;
    }

    private int getLoadingViewPosition() {
        return getHeadersCount() + mDatas.size() + getFootersCount();
    }

    public void isUseEmpty(boolean isUseEmpty) {
        mIsUseEmpty = isUseEmpty;
    }

    private boolean isHeaderPos(int position) {
        return position < getHeadersCount();
    }

    private boolean isFooterPos(int position) {
        return position >= (getHeadersCount() + getRealItemCount()) && !isLoadingPos(position);
    }

    private boolean isLoadingPos(int position) {
        return position >= getLoadingViewPosition();
    }

    private boolean hasLoading() {
        return mLoadingView != null && mLoadingView.getLoadingLayoutId() != 0;
    }

    private boolean hasEmptyView() {
        return mEmptyView != null || mEmptyLayoutResId != 0;
    }

    private boolean isEmpty() {
        return hasEmptyView() && getItemCount() == 0;
    }

    public void setEmptyLayoutId(int emptyLayoutId) {
        mEmptyLayoutResId = emptyLayoutId;
    }

    public void setEmptyView(View emptyView) {
        boolean insert = false;
        if (mEmptyView == null) {
            mEmptyView = new FrameLayout(emptyView.getContext());
            final RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
            final ViewGroup.LayoutParams lp = emptyView.getLayoutParams();
            if (lp != null) {
                layoutParams.width = lp.width;
                layoutParams.height = lp.height;
            }
            mEmptyView.setLayoutParams(layoutParams);
            insert = true;
        }
        mEmptyView.removeAllViews();
        mEmptyView.addView(emptyView);
        mIsUseEmpty = true;
        if (insert) {
            if (getEmptyViewCount() == 1) {
                int position = 0;
                if (getHeadersCount() != 0) {
                    position++;
                }
                notifyItemInserted(position);
            }
        }
    }

    public void addHeaderView(View view) {
        mHeaderViews.put(mHeaderViews.size() + Constant.HEADER_TYPE, view);
    }

    public void addFooterView(View view) {
        mFootViews.put(mFootViews.size() + Constant.FOOTER_TYPE, view);
    }

    public void useDefLoadingView(Boolean useDefLoadingView) {
        if (useDefLoadingView) {
            mLoadingView = new LoadingView(R.layout.default_loading_view, R.id.loading_view,
                    R.id.loading_fail_view, R.id.loading_end_view);
        }
    }

    /*自动加载*/
    private void autoLoading(int position) {
        if (getLoadingViewCount() == 0) return;
        if (position < getItemCount() - mPreLoadNumber) return;
        if (mLoadingView.getLoadingStatus() != Constant.DEFAULT_STATUS) return;
        mLoadingView.setLoadingStatus(Constant.LOADING_STATUS);
        if (!mIsLoading) {
            mIsLoading = true;
            if (getRecyclerView() != null) {
                getRecyclerView().post(new Runnable() {
                    @Override
                    public void run() {
                        mOnLoadingListener.onLoading();
                    }
                });
            } else {
                mOnLoadingListener.onLoading();
            }
        }
    }

    public void loadEnd() {
        loadEnd(false);
    }

    public void loadEnd(boolean isLoadEnd) {
        if (getLoadingViewCount() == 0) return;
        mIsLoading = false;
        mNextLoadEnable = false;
        mLoadingView.setLoadingEnd(isLoadEnd);
        if (isLoadEnd) {
            notifyItemRemoved(getLoadingViewPosition());
        } else {
            mLoadingView.setLoadingStatus(Constant.END_STATUS);
            notifyItemChanged(getLoadingViewPosition());
        }
    }

    public void loadingComplete() {
        if (getLoadingViewPosition() == 0) {
            return;
        }
        mIsLoading = false;
        mNextLoadEnable = true;
        mLoadingView.setLoadingStatus(Constant.DEFAULT_STATUS);
        notifyItemChanged(getLoadingViewPosition());
    }

    public void loadingFailed() {
        if (getLoadingViewCount() == 0) {
            return;
        }
        mIsLoading = false;
        mLoadingView.setLoadingStatus(Constant.FAILED_STATUS);
        notifyItemChanged(getLoadingViewPosition());
    }

    public void setEnableLoading(boolean enable) {
        int oldLoadingCount = getLoadingViewCount();
        mLoadingEnable = enable;
        int newLoadMoreCount = getLoadingViewCount();
        if (oldLoadingCount == 1) {
            if (newLoadMoreCount == 0) {
                notifyItemRemoved(getLoadingViewPosition());
            }
        } else {
            if (newLoadMoreCount == 1) {
                mLoadingView.setLoadingStatus(Constant.DEFAULT_STATUS);
                notifyItemInserted(getLoadingViewPosition());
            }
        }
    }

    public boolean isLoadingEnable() {
        return mLoadingEnable;
    }

    public void setLoadingView(LoadingView loadingView) {
        mLoadingView = loadingView;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setNewData(@Nullable List<T> data) {
        this.mDatas = data == null ? new ArrayList<T>() : data;
        if (mOnLoadingListener != null) {
            mNextLoadEnable = true;
            mLoadingEnable = true;
            mIsLoading = false;
            mLoadingView.setLoadingStatus(Constant.DEFAULT_STATUS);
        }
        notifyDataSetChanged();
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public void addData(List<T> data) {
        if (data == null || data.size() == 0) return;
        mDatas.addAll(data);
        notifyItemRangeChanged(mDatas.size() - data.size(), mDatas.size());
    }

    public void addData(@IntRange(from = 0) int position, @NonNull Collection<? extends T> newData) {
        mDatas.addAll(position, newData);
        notifyItemRangeInserted(position + getHeadersCount(), newData.size());
        compatibilityDataSizeChanged(newData.size());
    }

    public void addData(@NonNull Collection<? extends T> newData) {
        mDatas.addAll(newData);
        notifyItemRangeInserted(mDatas.size() - newData.size() + getHeadersCount(), newData.size());
        compatibilityDataSizeChanged(newData.size());
    }

    public void replaceData(@NonNull Collection<? extends T> data) {
        // 不是同一个引用才清空列表
        if (data != mDatas) {
            mDatas.clear();
            mDatas.addAll(data);
        }
        notifyDataSetChanged();
    }

    private void compatibilityDataSizeChanged(int size) {
        final int dataSize = mDatas == null ? 0 : mDatas.size();
        if (dataSize == size) {
            notifyDataSetChanged();
        }
    }

    public void addAll(int index, List<T> data) {
        if (data == null || data.size() == 0) return;
        mDatas.addAll(index, data);
        notifyItemRangeChanged(index, index + data.size());
    }

    public void addAll(List<T> data, boolean isClear) {
        if (data == null || data.size() == 0) return;
        if (isClear) mDatas.clear();
        mDatas.addAll(data);
        notifyDataSetChanged();
    }

    /*清除数据*/
    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    /**
     * 通过index移除数据 @param index
     */
    public void remove(int index) {
        mDatas.remove(index);
        notifyItemRemoved(index);
    }

    /**
     * 对itemView的item进行操作 @param holder @param item
     */
    protected abstract void convert(ViewHolder holder, T item);

    protected boolean isEnabled(int viewType) {
        return true;
    }

    /**
     * 如有必要可以重写此方法进行itemView的业务逻辑
     */
    protected void onViewHolderCreated(ViewHolder holder, View itemView) {
    }

    /**
     * 设置itemView的监听
     */
    private void setListener(final ViewHolder holder, int viewType) {
        if (!isEnabled(viewType)) return;
        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(view, holder, position);
                }
            }
        });
        holder.getItemView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnItemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    mOnItemClickListener.onItemLongClick(view, holder, position);
                }
                return false;
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);

        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener != null)
            mOnItemClickListener = onItemClickListener;
    }

    public interface OnLoadingListener {
        void onLoading();
    }

    public void setOnLoadingListener(OnLoadingListener loadingListener) {
        if (loadingListener != null)
            mOnLoadingListener = loadingListener;
        mNextLoadEnable = true;
        mLoadingEnable = true;
        mIsLoading = false;
    }
}
