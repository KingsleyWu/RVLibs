package kingsley.rvlibs;

import android.view.View;
import android.widget.Toast;

import java.util.List;

import kingsley.rvlibrary.base.RVBaseAdapter;
import kingsley.rvlibrary.base.ViewHolder;

/**
 * class name : RVLibs
 * author : Kingsley
 * created date : on 2017/9/11 18:25
 * file change date : on 2017/9/11 18:25
 * version: 1.0
 */

public class RecyclerViewAdapter extends RVBaseAdapter<String> {

    public RecyclerViewAdapter(int layoutResId, List<String> datas) {
        super(layoutResId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, final String item) {
        holder.setText(R.id.text,item);
        holder.setImageView(R.id.iv,R.mipmap.ic_launcher);
        holder.setOnClickListener(R.id.iv, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, item, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
