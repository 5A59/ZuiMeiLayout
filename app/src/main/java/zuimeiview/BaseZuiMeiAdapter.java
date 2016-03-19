package zuimeiview;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import zy.com.zuimeidemo.R;

/**
 * Created by zy on 16-3-19.
 */
public class BaseZuiMeiAdapter extends ZuiMeiAdapter{

    private Context context;
    private List<Integer> imgList;

    public BaseZuiMeiAdapter(Context context, List<Integer> imgList){
        this.context = context;
        this.imgList = imgList;
    }

    @Override
    public View getView(int pos) {
        BaseZuiMeiAdapter.ViewHolder viewHolder = new ViewHolder();
        View view = LayoutInflater.from(context).inflate(R.layout.item_zuimei, null);
        viewHolder.imageView = (ImageView) view.findViewById(R.id.img_item);
        viewHolder.imageView.setImageResource(imgList.get(pos));
        return view;
    }

    @Override
    public int getCount() {
        return imgList.size();
    }

    public static class ViewHolder{
        ImageView imageView;
    }
}
