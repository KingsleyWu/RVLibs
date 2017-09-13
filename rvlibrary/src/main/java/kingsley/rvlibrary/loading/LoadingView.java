package kingsley.rvlibrary.loading;

import android.support.annotation.IdRes;
import android.util.Log;

import kingsley.rvlibrary.Constant;
import kingsley.rvlibrary.R;
import kingsley.rvlibrary.base.ViewHolder;

public class LoadingView {

    private int mLoadingStatus = Constant.DEFAULT_STATUS;
    private boolean mLoadingEnd = false;
    private int mLoadingLayoutId;
    private int mLoadingViewId;
    private int mLoadFailedViewId;
    private int mLoadEndViewId;

    public LoadingView(){
        this.mLoadingLayoutId = R.layout.default_loading_view;
        this.mLoadingViewId = R.id.loading_view;
        this.mLoadFailedViewId = R.id.loading_fail_view;
        this.mLoadEndViewId = R.id.loading_end_view;
    }

    public LoadingView(int loadingLayoutId, int loadingViewId, int loadFailedViewId, int loadEndViewId) {
        this.mLoadingLayoutId = loadingLayoutId;
        this.mLoadingViewId = loadingViewId;
        this.mLoadFailedViewId = loadFailedViewId;
        this.mLoadEndViewId = loadEndViewId;
    }

    public void showLoadingView(ViewHolder holder) {
        switch (mLoadingStatus) {
            case Constant.LOADING_STATUS:
                Log.i("TAG", "showLoadingView: LOADING_STATUS");
                Loading(holder);
                break;
            case Constant.FAILED_STATUS:
                Log.i("TAG", "showLoadingView: FAILED_STATUS");
                LoadFailed(holder);
                break;
            case Constant.END_STATUS:
                Log.i("TAG", "showLoadingView: END_STATUS");
                LoadEnd(holder);
                break;
            case Constant.DEFAULT_STATUS:
                Log.i("TAG", "showLoadingView: DEFAULT_STATUS");
                defaultLoad(holder);
                break;
        }
    }

    private void Loading(ViewHolder holder) {
        holder.setVisible(getLoadingViewId(), true);
        holder.setVisible(getLoadFailViewId(), false);
        holder.setVisible(getLoadEndViewId(), false);
    }

    private void LoadFailed(ViewHolder holder) {
        holder.setVisible(getLoadingViewId(), false);
        holder.setVisible(getLoadFailViewId(), true);
        holder.setVisible(getLoadEndViewId(), false);
    }

    private void LoadEnd(ViewHolder holder) {
        holder.setVisible(getLoadingViewId(), false);
        holder.setVisible(getLoadFailViewId(), false);
        final int loadEndViewId = getLoadEndViewId();
        if (loadEndViewId != 0) {
            holder.setVisible(loadEndViewId, true);
        }
    }

    private void defaultLoad(ViewHolder holder) {
        holder.setVisible(getLoadingViewId(), false);
        holder.setVisible(getLoadFailViewId(), false);
        holder.setVisible(getLoadEndViewId(), false);
    }

    public final void setLoadingEnd(boolean isLoadingEnd) {
        this.mLoadingEnd = isLoadingEnd;
    }

    public final boolean isLoadEnd() {
        return getLoadEndViewId() == 0 || mLoadingEnd;
    }

    public int getLoadingLayoutId() {
        return mLoadingLayoutId;
    }

    public
    @IdRes
    int getLoadingViewId() {
        return mLoadingViewId;
    }

    public
    @IdRes
    int getLoadFailViewId() {
        return mLoadFailedViewId;
    }

    public
    @IdRes
    int getLoadEndViewId() {
        return mLoadEndViewId;
    }

    public void setLoadingStatus(int loadingStatus) {
        this.mLoadingStatus = loadingStatus;
    }

    public int getLoadingStatus() {
        return mLoadingStatus;
    }

    public void setLoadingLayoutId(int loadingLayoutId) {
        mLoadingLayoutId = loadingLayoutId;
    }

    public void setLoadingViewId(int loadingViewId) {
        mLoadingViewId = loadingViewId;
    }

    public void setLoadFailedViewId(int loadFailedViewId) {
        mLoadFailedViewId = loadFailedViewId;
    }

    public void setLoadEndViewId(int loadEndViewId) {
        mLoadEndViewId = loadEndViewId;
    }
}
