package zuimeiview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import utils.Logger;
import zy.com.zuimeidemo.R;

/**
 * Created by zy on 16-3-14.
 */
public class ZuiMeiLayout extends HorizontalScrollView{
    private static final int SHOW_ITEM_NUM = 7;
    private static final int OUT_ITEM_NUM = 3;
    private static final int DURATION = 500;

    private Context context;

    private int subHeight;
    private int initHeight;
    private int itemWidth;
    private int itemHeight;

    private int itemCount;
    private int showHead;
    private int showTail;

    private int lastPos;

    private float initY;
    private float initDownY;
    private float lastX;
    private float lastY;

    private boolean initView;

    private ZuiMeiAdapter adapter;
    private LinearLayout innerLayout;

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
    }

    public void setInnerView(){
        innerLayout = new LinearLayout(context);
        innerLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < SHOW_ITEM_NUM + OUT_ITEM_NUM * 2; i ++){
            ZuiMeiItem zuiMeiItem = new ZuiMeiItem(context);
            zuiMeiItem.setY(initHeight);
            innerLayout.addView(zuiMeiItem, itemParams);
        }
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(innerLayout, layoutParams);
    }

    public void setAdapter(ZuiMeiAdapter adapter){
        if (adapter != null){
            this.adapter = adapter;
            this.itemCount = adapter.getCount();
            refreshView();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (initView){
            itemWidth = this.getWidth() / (SHOW_ITEM_NUM);
            itemHeight = innerLayout.getChildAt(0).getHeight();
            subHeight = (itemHeight - initHeight) / (SHOW_ITEM_NUM - 1);
            initY = innerLayout.getChildAt(0).getY();
            initDownY = itemHeight - initHeight;

            innerLayout.setMinimumWidth(itemWidth * (SHOW_ITEM_NUM + OUT_ITEM_NUM));
            innerLayout.setX(- (OUT_ITEM_NUM * itemWidth));

            for (int i = 0; i < SHOW_ITEM_NUM + OUT_ITEM_NUM * 2; i ++){
                ZuiMeiItem item = (ZuiMeiItem) innerLayout.getChildAt(i);
                item.setItemWidth(itemWidth);
                if (i > OUT_ITEM_NUM){
                    item.removeAllViews();
                    item.addView(getAdapterView(i));
                }
                if (i == OUT_ITEM_NUM){
                    item.setY(initY);
                }else {
                    item.setY(initDownY);
                }
            }

            initView = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int pos = getEventPos(x, y);

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
        int inPos = getRealPos(pos);
        innerLayout.getChildAt(inPos).setY(initY);
        for (int i = inPos + 1; i < innerLayout.getChildCount(); i ++){
            int s = i - inPos;
            float shouldY = initY + s * subHeight;
            innerLayout.getChildAt(i).setY(shouldY > initDownY ? initDownY : shouldY);
        }

        for (int i = inPos - 1; i >= 0; i --){
            int s = inPos - i;
            float shouldY = initY + s * subHeight;
            innerLayout.getChildAt(i).setY(shouldY > initDownY ? initDownY : shouldY);
        }

        invalidate();
    }

    public void setItemsDown(int pos){
        int inPos = getRealPos(pos);
        for (int i = 0; i < innerLayout.getChildCount(); i ++){
            if (inPos == i){
                innerLayout.getChildAt(i).setY(initY);
                continue;
            }
            innerLayout.getChildAt(i).setY(initDownY);
        }
    }

    private void moveInnerLayout(int p){
        int pos = getRealPos(p);
        int middle = SHOW_ITEM_NUM / 2 + OUT_ITEM_NUM;
        Logger.d("pos is : " + pos + "  middle  " + middle);
        if (pos == middle){
            return ;
        }
        int sub = 0;

        if (pos > middle){
            sub = Math.min(pos - middle, itemCount - 1 - showTail);
            if (sub > 0){
                showTail += sub;
                showHead += sub;

                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(innerLayout, "translationX",
                        innerLayout.getX(), innerLayout.getX() - (sub * itemWidth));
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(objectAnimator);
                animatorSet.setDuration(getDuration(sub));
                final int finalSub = sub;
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        for (int i = 0; i < finalSub; i ++){
                            ZuiMeiItem item = (ZuiMeiItem) innerLayout.getChildAt(0);
                            innerLayout.removeView(item);
                            innerLayout.setX(innerLayout.getX() + itemWidth);
                            item.setY(initDownY);
                            if (showTail + i + 1 < itemCount){
                                Logger.d("remove views  " + showTail + "  " + (showTail + i + 1));
                                item.removeAllViews();
                                item.addView(getAdapterView(showTail + i + 1));
                            }
                            innerLayout.addView(item, innerLayout.getChildCount());
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
            return ;
        }

        if (pos < middle){
            sub = Math.min(middle - pos, showHead);
            Logger.d("sub is  " + sub);
            if (sub > 0){
                showTail -= sub;
                showHead -= sub;

                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(innerLayout, "translationX",
                        innerLayout.getX(), innerLayout.getX() + (sub * itemWidth));
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(objectAnimator);
                animatorSet.setDuration(getDuration(sub));
                final int finalSub = sub;
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        for (int i = 0; i < finalSub; i ++){
                            ZuiMeiItem item = (ZuiMeiItem) innerLayout.getChildAt(innerLayout.getChildCount() - 1);
                            innerLayout.removeView(item);
                            innerLayout.setX(innerLayout.getX() - itemWidth);
                            item.setY(initDownY);
                            item.removeAllViews();
                            if (showHead - i >= 0){
                                item.addView(getAdapterView(showHead - i));
                            }
                            innerLayout.addView(item, 0);
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
    }

    private int getDuration(int sub){
        return DURATION;
    }

    private int getRealPos(int pos){
        return pos + OUT_ITEM_NUM;
    }

    private View getAdapterView(int pos){
        if (adapter == null || pos >= adapter.getCount()){
            TextView textView = new TextView(context);
            textView.setText("null");
            return textView;
        }
        return adapter.getView(pos);
    }

    private void refreshView(){
        for (int i = 0; i < SHOW_ITEM_NUM; i ++){
            ZuiMeiItem item = (ZuiMeiItem) innerLayout.getChildAt(i + showHead + OUT_ITEM_NUM);
            item.removeAllViews();
            item.addView(adapter.getView(i + showHead));
            item.setItemWidth(itemWidth);
        }
    }
}
