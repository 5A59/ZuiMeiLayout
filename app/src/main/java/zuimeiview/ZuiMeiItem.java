package zuimeiview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by zy on 16-3-14.
 */
public class ZuiMeiItem extends FrameLayout{

    public ZuiMeiItem(Context context) {
        super(context);
    }

    public ZuiMeiItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZuiMeiItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setItemWidth(int width){
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.getLayoutParams();
        layoutParams.width = width;
        this.setLayoutParams(layoutParams);
    }
}
