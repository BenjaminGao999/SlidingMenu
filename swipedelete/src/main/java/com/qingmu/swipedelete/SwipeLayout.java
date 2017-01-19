package com.qingmu.swipedelete;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;


/**
 * Created by lxj on 2016/7/12.
 */
public class SwipeLayout extends FrameLayout{

    private View contentView;
    private View deleteView;
    private int contentWidth;
    private int contentHeight;
    private int deleteWidth;
    private int deleteHeight;

    private ViewDragHelper viewDragHelper;
    private int touchSlop;

    public SwipeLayout(Context context) {
        super(context);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public enum SwipeState{
        Open,Close
    }
    private SwipeState mState = SwipeState.Close;//默认是关闭的状态

    private void init(){
        //获取触摸的介于滚动和点击之间的界限值
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        viewDragHelper = ViewDragHelper.create(this,callback);
    }

    /**
     * 完成填充
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        deleteView = getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        contentWidth = contentView.getMeasuredWidth();
        contentHeight = contentView.getMeasuredHeight();
        deleteWidth = deleteView.getMeasuredWidth();
        deleteHeight = deleteView.getMeasuredHeight();
    }

    //重写onLayout
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);

        contentView.layout(0,0,contentWidth,contentHeight);
        deleteView.layout(contentWidth,0,contentWidth+deleteWidth,deleteHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
        //每次触摸的时候判断当前是否满足可以滑动的条件
        if(!SwipeLayoutManager.getInstance().shouldSwipe(this)){
            //需要关闭已经打开的
            SwipeLayoutManager.getInstance().closeSwipeLayout();

            result = true;//消费掉事件，不让滑动
        }
        return result;
    }

    private float downX,downY;
    private long downTime;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //每次触摸的时候判断当前是否满足可以滑动的条件
        if(!SwipeLayoutManager.getInstance().shouldSwipe(this)){
            //让listview不能滑动
            requestDisallowInterceptTouchEvent(true);//子View请求父View不拦截
            return true;//消费掉事件，不让滑动
        }

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //1.记录按下的坐标
                downX = event.getX();
                downY = event.getY();
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                //2.记录移动的坐标
                float moveX = event.getX();
                float moveY = event.getY();
                //3.计算x方向和y方向滑动的距离
                float deltaX = moveX - downX;
                float deltaY = moveY - downY;
                //4.判断滑动的方向是偏向于水平还是垂直
                if(Math.abs(deltaX)>Math.abs(deltaY)){
                    //说明是偏向于水平方向，那我们则认为用户向滑动的是条目，而不是listview，
                    //此时不应该让listview拦截事件
                    requestDisallowInterceptTouchEvent(true);//子View请求父View不拦截
                }

                break;
            case MotionEvent.ACTION_UP:
                //记录抬起的坐标
                float upX = event.getX();
                float upY = event.getY();
                //计算按下和抬起的移动的距离
                float dX = upX - downX;
                float dy = upY - downY;
                //计算两点之间的直线距离
                double distance = Math.sqrt(Math.pow(dX, 2) + Math.pow(dy, 2));
                //计算抬起的时间
                long time = System.currentTimeMillis() - downTime;
                if(distance<touchSlop && time<400){
                    //认为是触发点击事件
                    if(listener!=null){
                        listener.onClick();
                    }
                }

                break;
        }
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child==contentView || child==deleteView;
        }

        /**
         * 鸡肋方法，返回值需要大于0
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return deleteWidth;
        }

        /**
         * 控制水平移动
         * @param child
         * @param left
         * @param dx
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if(child==contentView){
                //限制contentView
                if(left>0){
                    left = 0;
                }else if(left<-deleteWidth){
                    left = -deleteWidth;
                }
            }else if(child==deleteView){
                //限制deleteView
                if(left>contentWidth){
                    left = contentWidth;
                }else if(left<(contentWidth-deleteWidth)){
                    left = contentWidth-deleteWidth;
                }
            }
            return left;
        }

        /**
         * 当view的位置改变的回调，一般用来实现view的伴随移动
         * @param changedView
         * @param left
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if(changedView==contentView){
                //让deleteView进行伴随移动
                int newLeft = deleteView.getLeft()+dx;
                deleteView.layout(newLeft,0,newLeft+deleteWidth,deleteHeight);
            }else if(changedView==deleteView){
                //让contentView进行伴随移动
                int newLeft = contentView.getLeft()+dx;
                contentView.layout(newLeft,0,newLeft+contentWidth,contentHeight);
            }

            //判断打开和关闭的逻辑
            if(contentView.getLeft()==-deleteWidth && mState!=SwipeState.Open){
                mState = SwipeState.Open;
                //说明是打开的,需要记录
                SwipeLayoutManager.getInstance().setSwipeLayout(SwipeLayout.this);
            }else if(contentView.getLeft()==0 && mState!=SwipeState.Close){
                mState = SwipeState.Close;
                //说明是关闭的，需要清除
                SwipeLayoutManager.getInstance().clearSwipeLayout();
            }
        }

        /**
         * 当手指松开的时候执行
         * @param releasedChild
         * @param xvel
         * @param yvel
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if(contentView.getLeft()>-deleteWidth/2){
                //应该关闭
                close();
            }else {
                //应该打开
                open();
            }
        }
    };

    /**
     * 打开的方法
     */
    public void open() {
        viewDragHelper.smoothSlideViewTo(contentView,-deleteWidth,0);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * 关闭
     */
    public void close() {
        viewDragHelper.smoothSlideViewTo(contentView,0,0);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
    OnSwipeLayoutClickListener listener;
    public void setOnSwipeLayoutClickListener(OnSwipeLayoutClickListener listener){
        this.listener = listener;
    }
    public interface OnSwipeLayoutClickListener{
        void onClick();
    }
}
