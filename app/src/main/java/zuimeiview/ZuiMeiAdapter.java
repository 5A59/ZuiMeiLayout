package zuimeiview;

import android.view.View;

/**
 * Created by zy on 16-3-19.
 */
public abstract class ZuiMeiAdapter {
    public abstract View getView(int pos);
    public abstract int getCount();
}
