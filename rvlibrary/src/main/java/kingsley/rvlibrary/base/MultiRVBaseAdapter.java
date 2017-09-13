package kingsley.rvlibrary.base;

import android.support.annotation.LayoutRes;
import android.util.SparseIntArray;
import android.view.ViewGroup;

import java.util.List;

import kingsley.rvlibrary.entity.MultiItemType;

/**
 * class name : RVLibs
 * author : Kingsley
 * created date : on 2017/9/11 21:09
 * file change date : on 2017/9/11 21:09
 * version: 1.0
 */

public abstract class MultiRVBaseAdapter<T extends MultiItemType> extends RVBaseAdapter<T> {

    private SparseIntArray mLayouts;
    private final int TYPE_NOT_FOUND = -0X010101;
    private final int DEFAULT_TYPE = -0X999999;

    public MultiRVBaseAdapter(List<T> datas) {
        super(datas);
    }

    public MultiRVBaseAdapter(List<T> datas, int[] viewType, int[] layoutResId) {
        super(datas);
        addItemType(viewType,layoutResId);
    }

    @Override
    protected ViewHolder createDefViewHolder(ViewGroup parent, int viewType) {
        return ViewHolder.createViewHolder(parent, getLayoutId(viewType));
    }

    @Override
    protected int getDefItemViewType(int position) {
        return mDatas.get(position).getType();
    }

    private int getLayoutId(int viewType) {
        return mLayouts.get(viewType, TYPE_NOT_FOUND);
    }

    public void addItemType(int[] viewTypes, @LayoutRes int[] layoutResIds) {
        if (mLayouts == null) {
            mLayouts = new SparseIntArray();
        }
        for (int i = 0; i < layoutResIds.length; i++) {
            mLayouts.put(viewTypes[i],layoutResIds[i]);
        }
    }

    public void addItemType(int viewType, @LayoutRes int layoutResId) {
        if (mLayouts == null) {
            mLayouts = new SparseIntArray();
        }
        mLayouts.put(viewType,layoutResId);
    }

}
