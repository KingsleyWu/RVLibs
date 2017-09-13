package kingsley.rvlibrary.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * class name : Timer
 * author : Kingsley
 * created date : on 2017/7/21 20:43
 * file change date : on 2017/7/21 20:43
 * version: 1.0
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private View mItemView;
    private Context mContext;

    public ViewHolder(Context context,View itemView) {
        super(itemView);
        mContext = context;
        mViews = new SparseArray<>();
        mItemView = itemView;
    }

    public ViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        mViews = new SparseArray<>();
        mItemView = itemView;
    }

    public static ViewHolder createViewHolder(Context context,View itemView){
        return new ViewHolder(context,itemView);
    }

    public static ViewHolder createViewHolder(ViewGroup parent, int layoutId){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId,parent,false);
        return new ViewHolder(parent.getContext(),itemView);
    }

    /**
     * 通过viewId获取控件
     * @param viewId
     * @return
     */
    @SuppressWarnings("uncheckd")
    private <V extends View> V getView(int viewId){
        View view = mViews.get(viewId);
        if (view == null){
            view = mItemView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return (V) view;
    }

    /*辅助方法*/

    public View getItemView(){
        return mItemView;
    }

    public TextView getTextView(int viewId){
        return getView(viewId);
    }

    public ImageView getImageView(int viewId){
        return getView(viewId);
    }

    public Button getButton(int viewId){
        return getView(viewId);
    }

    public ViewHolder setText(int viewId,CharSequence text){
        getTextView(viewId).setText(text);
        return this;
    }

    public ViewHolder setText(int resId,int strId){
        getTextView(resId).setText(strId);
        return this;
    }

    public ViewHolder setImageView(int viewId, Drawable drawable){
        getImageView(viewId).setImageDrawable(drawable);
        return this;
    }

    public ViewHolder setImageView(int viewId, Bitmap bitmap){
        getImageView(viewId).setImageBitmap(bitmap);
        return this;
    }

    public ViewHolder setImageView(int viewId,int resId){
        getImageView(viewId).setImageResource(resId);
        return this;
    }

    public ViewHolder setBackgroundColor(int viewId, int color) {
        getView(viewId).setBackgroundColor(color);
        return this;
    }

    public ViewHolder setBackgroundRes(int viewId, int backgroundRes) {
        getView(viewId).setBackgroundResource(backgroundRes);
        return this;
    }

    public ViewHolder setTextColor(int viewId, int textColor) {
        TextView view = getView(viewId);
        view.setTextColor(textColor);
        return this;
    }

    public ViewHolder setTextColorRes(int viewId, int textColorRes) {
        TextView view = getView(viewId);
        view.setTextColor(mContext.getResources().getColor(textColorRes));
        return this;
    }

    public ViewHolder setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView(viewId).setAlpha(value);
        } else {
            // Pre-honeycomb hack to set Alpha value
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            getView(viewId).startAnimation(alpha);
        }
        return this;
    }

    public ViewHolder setGone(@IdRes int viewId, boolean visible) {
        getView(viewId).setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public ViewHolder setVisible(@IdRes int viewId, boolean visible) {
        getView(viewId).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        return this;
    }

    public ViewHolder linkify(@IdRes int viewId) {
        TextView view = getView(viewId);
        Linkify.addLinks(view, Linkify.ALL);
        return this;
    }

    public ViewHolder setTypeface(@IdRes int viewId, Typeface typeface) {
        TextView view = getView(viewId);
        view.setTypeface(typeface);
        view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        return this;
    }

    public ViewHolder setTypeface(Typeface typeface, int... viewIds) {
        for (int viewId : viewIds) {
            TextView view = getView(viewId);
            view.setTypeface(typeface);
            view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
        return this;
    }

    public ViewHolder setProgress(@IdRes int viewId, int progress) {
        ProgressBar view = getView(viewId);
        view.setProgress(progress);
        return this;
    }

    public ViewHolder setProgress(@IdRes int viewId, int progress, int max) {
        ProgressBar view = getView(viewId);
        view.setMax(max);
        view.setProgress(progress);
        return this;
    }

    public ViewHolder setMax(@IdRes int viewId, int max) {
        ProgressBar view = getView(viewId);
        view.setMax(max);
        return this;
    }

    public ViewHolder setRating(int viewId, float rating) {
        RatingBar view = getView(viewId);
        view.setRating(rating);
        return this;
    }

    public ViewHolder setRating(@IdRes int viewId, float rating, int max) {
        RatingBar view = getView(viewId);
        view.setMax(max);
        view.setRating(rating);
        return this;
    }

    public ViewHolder setTag(@IdRes int viewId, Object tag) {
        getView(viewId).setTag(tag);
        return this;
    }

    public ViewHolder setTag(@IdRes int viewId, int key, Object tag) {
        getView(viewId).setTag(key, tag);
        return this;
    }

    public ViewHolder setChecked(@IdRes int viewId, boolean checked) {
        Checkable view = getView(viewId);
        view.setChecked(checked);
        return this;
    }

    /**
     * 监听事件
     */
    public ViewHolder setOnClickListener(@IdRes int viewId,View.OnClickListener listener) {
        getView(viewId).setOnClickListener(listener);
        return this;
    }

    public ViewHolder setOnClickListener(View.OnClickListener listener,@IdRes int... viewIds) {
        for (int viewId : viewIds) {
            getView(viewId).setOnClickListener(listener);
        }
        return this;
    }

    public ViewHolder setOnTouchListener(@IdRes int viewId,View.OnTouchListener listener) {
        getView(viewId).setOnTouchListener(listener);
        return this;
    }

    public ViewHolder setOnTouchListener(View.OnTouchListener listener,@IdRes int... viewIds) {
        for (int viewId : viewIds) {
            getView(viewId).setOnTouchListener(listener);
        }
        return this;
    }

    public ViewHolder setOnLongClickListener(@IdRes int viewId, View.OnLongClickListener listener) {
        getView(viewId).setOnLongClickListener(listener);
        return this;
    }

    public ViewHolder setOnLongClickListener(View.OnLongClickListener listener,@IdRes int... viewIds) {
        for (int viewId : viewIds) {
            getView(viewId).setOnLongClickListener(listener);
        }
        return this;
    }

    public ViewHolder setOnItemClickListener(View.OnClickListener listener) {
        mItemView.setOnClickListener(listener);
        return this;
    }

    public ViewHolder setOnItemLongClickListener(View.OnLongClickListener listener) {
        mItemView.setOnLongClickListener(listener);
        return this;
    }

    public ViewHolder setOnItemTouchListener(View.OnTouchListener listener) {
        mItemView.setOnTouchListener(listener);
        return this;
    }
}
