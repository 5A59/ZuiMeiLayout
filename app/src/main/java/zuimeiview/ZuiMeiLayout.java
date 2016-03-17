package zuimeiview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import utils.Logger;
import zy.com.zuimeidemo.R;

/**
 * Created by zy on 16-3-14.
 */
public class ZuiMeiLayout extends HorizontalScrollView{
    private static final int SHOW_ITEM_NUM = 7;
    private static final int OUT_ITEM_NUM = 3;

    private Context context;

    private int subHeight;
    private int initHeight;
    private int itemWidth;
    private int itemHeight;

    private int itemCount;
    private int head;
    private int tail;
    private int showHead;
    private int showTail;

    private int lastPos;

    private float initY;
    private float initDownY;
    private float lastX;
    private float lastY;

    private boolean initView;

    private Adapter adapter;
    private LinearLayout innerLayout;
    private List<ZuiMeiItem> itemList;
    private List<ZuiMeiItem> outItemList;

    public ZuiMeiLayout(Context context) {
        this(context, null);
    }

    public ZuiMeiLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZuiMeiLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;
        getAttrs(attrs);
        setData();
        setInnerView();
        this.setWillNotDraw(false);
        this.setFillViewport(true);
    }

    public void getAttrs(AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZuiMeiLayout);
        itemCount = typedArray.getInt(R.styleable.ZuiMeiLayout_item_count, SHOW_ITEM_NUM);
    }

    public void setData(){
        initView = true;
        initHeight = 20;
        initDownY = 10;
        subHeight = 10;
        lastPos = 0;
        showHead = 0;
        showTail = SHOW_ITEM_NUM - 1;
        itemList = new ArrayList<>();
        outItemList = new ArrayList<>();
    }

    public void setInnerView(){
        innerLayout = new LinearLayout(context);
        innerLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
//        itemParams.weight = 1;
        for (int i = 0; i < SHOW_ITEM_NUM + OUT_ITEM_NUM; i ++){
            ZuiMeiItem zuiMeiItem = new ZuiMeiItem(context);
            zuiMeiItem.setText("test");
            zuiMeiItem.setY(initHeight);
            innerLayout.addView(zuiMeiItem, itemParams);
            if (i < SHOW_ITEM_NUM){
                itemList.add(zuiMeiItem);
            }else {
                outItemList.add(zuiMeiItem);
            }
        }
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(innerLayout, layoutParams);
    }

    public void setAdapter(Adapter adapter){
        this.adapter = adapter;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (initView){
//            itemWidth = innerLayout.getChildAt(0).getWidth();
            itemWidth = this.getWidth() / (SHOW_ITEM_NUM);
            itemHeight = innerLayout.getChildAt(0).getHeight();
            subHeight = (itemHeight - initHeight) / (SHOW_ITEM_NUM - 1);
            initY = innerLayout.getChildAt(0).getY();
            initDownY = itemHeight - initHeight;

            innerLayout.setMinimumWidth(itemWidth * (SHOW_ITEM_NUM + OUT_ITEM_NUM));

            itemList.get(0).setWidth(itemWidth);
            for (int i = 1; i < SHOW_ITEM_NUM + OUT_ITEM_NUM; i ++){
                ZuiMeiItem item;
                if (i >= SHOW_ITEM_NUM){
                    item = outItemList.get(i - SHOW_ITEM_NUM);
                }else {
                    item = itemList.get(i);
                }
                item.setY(initDownY);
                item.setWidth(itemWidth);
            }
//            for (int i = 0; i < OUT_ITEM_NUM; i ++){
//                ZuiMeiItem item = new ZuiMeiItem(context);
//                item.setText("new add");
//                item.setWidth(itemWidth);
//                item.setY(initDownY);
//            }
            initView = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int pos = getEventPos(x, y);
        Logger.d("curPos  " + pos);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastPos = pos;
                setItemsHeight(pos);
                break;
            case MotionEvent.ACTION_MOVE:
                if (pos != lastPos){
                    lastPos = pos;
                }
                setItemsHeight(pos);
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                setItemsDown(pos);
                moveInnerLayout(pos);
                break;
        }

        return true;
    }

    public int getEventPos(float x, float y){
        int pos = (int) x / itemWidth;
        if (pos >= SHOW_ITEM_NUM){
            pos = SHOW_ITEM_NUM - 1;
        }
        if (pos < 0){
            pos = 0;
        }
        return pos;
    }

    public void setItemsHeight(int pos){
        Logger.d("item list " + itemList.size());
        itemList.get(pos).setY(initY);
        for (int i = pos + 1; i < itemList.size(); i ++){
            int s = i - pos;
            itemList.get(i).setY(initY + s * subHeight);
        }

        for (int i = pos - 1; i >= 0; i --){
            int s = pos - i;
            itemList.get(i).setY(initY + s * subHeight);
        }
        invalidate();
    }

    public void setItemsDown(int pos){
        for (int i = 0; i < itemList.size(); i ++){
            if (pos == i){
                itemList.get(i).setY(initY);
                continue;
            }
            itemList.get(i).setY(initDownY);
        }
    }

    public void moveInnerLayout(int pos){
        Logger.d("------------------");
        Logger.d("show tail  " + showTail);
        Logger.d("show head  " + showHead);
        Logger.d("pos  " + (showHead + pos));
        Logger.d("------------------");
        if (pos == SHOW_ITEM_NUM / 2){
            return ;
        }
        int sub = 0;
        lastPos = SHOW_ITEM_NUM / 2 - 1;
        int middle = SHOW_ITEM_NUM /2;
        if (pos > middle){
            sub = Math.min(pos - SHOW_ITEM_NUM / 2, itemCount - 1 - showTail);
            Logger.d("------------------");
            Logger.d("sub is  :  " + sub);
            Logger.d("------------------");
            if (sub > 0){
                showTail += sub;
                showHead += sub;
                for (ZuiMeiItem i : itemList){
                    Logger.d("before  itemList  :  " + i.getX() + "  " + i.getY());
                }
                float rx = itemList.get(itemList.size() - 1).getRight();
                Logger.d("right x  " + rx);
                Logger.d("innerlayout x  " + innerLayout.getX());
                for (int i = 0; i < sub; i ++){
                    ZuiMeiItem item = outItemList.get(i);
                    innerLayout.removeView(item);
                    innerLayout.addView(item);
                    item.setText("ne" + i + " add");
                    item.setY(initDownY);
                    item.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                    item.setX(rx + i * itemWidth);

                    outItemList.add(itemList.get(i));
                    itemList.add(item);
                    Logger.d("new add item  x  " + item.getX() + "   y  " + item.getY());
                }
                for (ZuiMeiItem i : outItemList){
                    Logger.d("-- before remove outItemList  :  " + i.getX() + "  " + i.getY());
                }
                for (ZuiMeiItem i : itemList){
                    Logger.d("-- before remove itemList  :  " + i.getX() + "  " + i.getY());
                }

                for (int i = 0; i < sub; i ++){
                    outItemList.remove(0);
                    itemList.remove(0);
                }

                for (ZuiMeiItem i : outItemList){
                    Logger.d("outItemList  :  " + i.getX() + "  " + i.getY());
                }
                for (ZuiMeiItem i : itemList){
                    Logger.d("itemList  :  " + i.getX() + "  " + i.toString() + "  " + i.getY()
                            + "    view index  " + innerLayout.indexOfChild(i));
                }

                Logger.d("move  " + (sub * itemWidth) + "  innerlayout  x  " + innerLayout.getX()
                        + "  move x  " + (innerLayout.getX() - (sub * itemWidth)));
                ObjectAnimator animator = ObjectAnimator.ofFloat(innerLayout, "translateX", - (sub * itemWidth));
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animator);
                animatorSet.setDuration(500);
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        Logger.d("start innerlayout width  " + innerLayout.getWidth());
                        Logger.d("stop innerlayout width  " + innerLayout.getWidth() + "  x " + innerLayout.getX()
                                + "  right " + innerLayout.getRight() + "   left  " + innerLayout.getLeft());
                        for (int i = 0; i < innerLayout.getChildCount(); i ++){
                            Logger.d("start innerlayout child  " + i + "  x  " + innerLayout.getChildAt(i).getX());
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Logger.d("stop innerlayout width  " + innerLayout.getWidth() + "  x " + innerLayout.getX()
                                + "  right " + innerLayout.getRight() + "   left  " + innerLayout.getLeft());
                        for (int i = 0; i < innerLayout.getChildCount(); i ++){
                            Logger.d("stop innerlayout child  " + i + "  x  " + innerLayout.getChildAt(i).getX());
                        }

                        for (ZuiMeiItem i : itemList){
                            Logger.d("after animation stop itemList  :  " + i.toString() + "  " + i.getX() + "  " + i.getY());
                        }

                        for (int i = 1; i < itemList.size(); i ++){
                            itemList.get(i).setX(itemList.get(i - 1).getRight());
                        }
                        for (ZuiMeiItem i : itemList){
                            Logger.d("after refresh animation stop itemList  :  " + i.getX() + "  " + i.getY());
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animatorSet.start();
            }
        }
//        if (pos < middle){
//            sub = Math.min(pos - SHOW_ITEM_NUM / 2, showHead);
//            if (sub > 0){
//                showHead -= sub;
//                showTail -= sub;
//
//                for (int i = 0; i < sub; i ++){
//                    ZuiMeiItem item = outItemList.get(i);
//                    item.setHeight(itemHeight);
//                    item.setWidth(itemWidth);
//                    item.setY(initY);
//                    item.setX(this.getX() + i * itemWidth);
//                    innerLayout.removeView(item);
//                    innerLayout.addView(item);
//                    outItemList.add(itemList.get(i));
//                    itemList.add(item);
//                }
//                invalidate();
//                for (int i = 0; i < sub; i ++){
//                    outItemList.remove(0);
//                    itemList.remove(0);
//                }
//                ObjectAnimator.ofFloat(innerLayout, "translationX", (sub * itemWidth)).start();
//            }
//        }
    }
}
