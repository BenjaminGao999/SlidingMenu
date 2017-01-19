package com.qingmu.qqslidingmenudelete;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * 一般情况下，我们要自定义ViewGroup的时候可以选择继承FrameLayout，而不是去继承ViewGroup，
 * 原因是FrameLayout已经帮我们实现了onMeasure，并且FrameLayout是最轻量级的
 * Created by lxj on 2016/7/12.
 */
public class SlideMenu extends FrameLayout {
    private static final String TAG = "SlideMenu";

    private View menuView;
    private View mainView;
    private ViewDragHelper viewDragHelper;
    private int mainWidth;
    private int menuWidth;
    private int dragRange;
    private FloatEvaluator floatEvaluator;
    private ArgbEvaluator argbEvaluator;

    public enum DragState{
        Open,Close
    }
    private DragState mState = DragState.Close;//默认是关闭状态

    public DragState getDragState(){
        return mState;
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private Scroller scroller;
    /**
     * 初始化的方法
     */
    private void init(){
        scroller = new Scroller(getContext());
        argbEvaluator = new ArgbEvaluator();
        floatEvaluator = new FloatEvaluator();
        viewDragHelper = ViewDragHelper.create(this,callback);
    }

    /**
     * 当前完成布局填充之后调用，该方法调用的时候就知道自己又几个子View
     * 但是注意，该方法在onMeasure之前执行，所以在该方法中是获取不到子View的
     * 宽高
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }

    //    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        //测量子View
//        for (int i=0;i<getChildCount();i++){
//            View view = getChildAt(i);
//
//            measureChild(view,widthMeasureSpec,heightMeasureSpec);
//            measureChild(view,widthMeasureSpec,heightMeasureSpec);
//        }
//    }

    /**
     * 当onMeasure执行之后执行，所以一般可以在该方法中获取宽高
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mainWidth = mainView.getMeasuredWidth();
        menuWidth = menuView.getMeasuredWidth();
        //计算水平拖拽的范围
        dragRange = (int) (getMeasuredWidth() * 0.6f);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //让ViewDragHelper帮我判断是否应该拦截
        boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //让ViewDragHelper帮我处理触摸事件
        viewDragHelper.processTouchEvent(event);

        return true;
    }

    ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 判断是否需要捕获当前所触摸child的触摸事件
         * @param child 当前所触摸的子View
         * @param pointerId 触摸点的索引
         * @return 返回true，表示需要捕获，false就是不需要捕获
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child==mainView || child==menuView;
        }
        /**
         * 当chilid被开始捕获触摸事件的时候执行
         * @param capturedChild
         * @param activePointerId
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
//            Log.e(TAG, "onViewCaptured: " );
        }

        /**
         * 鸡肋的方法
         * 注意：该方法的返回值不要小于0，期返回值用来作为是否是水平方向滑动的条件之一
         * 。并且还会用来计算松开手指平滑动画执行的动画时间上
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return dragRange;
        }

        /**
         * 控制子View在水平方向的移动
         * @param child 当前触摸的子View
         * @param left  表示ViewDragHelper认为你想让child的left变成的值，并且帮你事先计算好了，
         *              left=child.getLeft()+dx
         * @param dx    表示手指水平移动的距离
         * @return 返回的值，表示你真正想让child的left变成的值。返回的值真正起决定作用
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //只对mainView的left进行限制
            if(child==mainView){
                left = fixLeft(left);
            }
//            else if(child==menuView){
//                //对menuView进行限制
//                left = 0;//让menuView原地不动
//            }
            return left;
        }
//        /**
//         * 控制子View在垂直方向的移动
//         * @param child 当前触摸的子View
//         * @param top  表示ViewDragHelper认为你想让child的top变成的值，并且帮你事先计算好了，
//         *              top=child.getTop()+dy
//         * @param dy    表示手指垂直移动的距离
//         * @return 返回的值，表示你真正想让child的top变成的值。返回的值真正起决定作用
//         */
//        @Override
//        public int clampViewPositionVertical(View child, int top, int dy) {
//            if(top>100){
//                top = 100;
//            }
//            return top;
//        }

        /**
         * 当child的位置改变之后调用,一般用让子View进行伴随移动
         * @param changedView  移动的子View
         * @param left  移动之后最新的left
         * @param top   移动之后最新的top
         * @param dx    水平移动的距离
         * @param dy    垂直移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
//            Log.e(TAG, "onViewPositionChanged: left"+left +"   dx:"+dx);
            //根据menuView滑动的距离让mainView进行伴随移动
            if(changedView==menuView){
                //手动让menuView固定住位置
                menuView.layout(0,0,menuWidth,menuView.getBottom());

                //让mainView进行伴随移动
                int newLeft = mainView.getLeft()+dx;
                //对newLeft进行判断
                newLeft = fixLeft(newLeft);

                mainView.layout(newLeft,mainView.getTop(),newLeft+mainWidth,
                        mainView.getBottom());
            }

            //当view滑动的时候进行伴随动画
            //1.计算mainView的滑动进度
            float fraction = mainView.getLeft()*1f/dragRange;
            //2.根据滑动的百分比执行一系列的伴随动画
            executeAnim(fraction);

            //3.回调接口的方法
            if(mainView.getLeft()==dragRange && mState!=DragState.Open){
                mState = DragState.Open;
                if(listener!=null){
                    listener.onOpen();
                }
            }else if(mainView.getLeft()==0 && mState!=DragState.Close){
                mState = DragState.Close;
                if(listener!=null){
                    listener.onClose();
                }
            }
            //直接回调拖拽中的方法
            if(listener!=null){
                listener.onDragging(fraction);
            }
        }

        /**
         * 手指抬起的时候会执行
         * @param releasedChild 抬起的子View
         * @param xvel  x方向滑动的速度
         * @param yvel  y方向滑动的速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            //参考mainView的left来进行判断
            if(mainView.getLeft()>dragRange/2){
                //在右半边,应该向右边滑动
                open();
            }else {
                //在左半边，应该向左边滑动
                close();
            }
        }
    };

    /**
     * 关闭的方法
     */
    public void close() {
        viewDragHelper.smoothSlideViewTo(mainView,0,0);
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    /**
     * 打开菜单的方法
     */
    public void open() {
        viewDragHelper.smoothSlideViewTo(mainView, dragRange, 0);
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);

        //scroller的写法
//       scroller.startScroll();
//       invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //scroller的写法
//        if(scroller.computeScrollOffset()){
//            int currX = scroller.getCurrX();
//            int currY = scroller.getCurrY();
//            scrollTo(currX,currY);
//            invalidate();
//        }

        //ViewDragHelper的写法
        if(viewDragHelper.continueSettling(true)){//如果返回true表示动画还没有结束
            //继续执行动画
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    /**
     * 执行伴随动画
     * @param fraction
     */
    private void executeAnim(float fraction) {
        //1.让mainView进行缩放
        //fraction: 0 -> 1
        //scale: 1 -> 0.6
        //0  -> 0.1 -> 0.5 -> 1
        //10 -> 20  ->  60 -> 110
        //value: startValue + (endValue-startValue)*fraction
//        float scale = 1f + (0.8f-1f)*fraction;
        mainView.setScaleY(floatEvaluator.evaluate(fraction,1f,0.8f));
        mainView.setScaleX(floatEvaluator.evaluate(fraction,1f,0.8f));

        //2.让menuView执行动画,缩放动画和平移动画
        menuView.setScaleX(floatEvaluator.evaluate(fraction,0.3f,1f));
        menuView.setScaleY(floatEvaluator.evaluate(fraction,0.3f,1f));

        menuView.setTranslationX(floatEvaluator.evaluate(fraction,-menuWidth/2,0));

        //3D旋转效果
//        menuView.setRotationY(floatEvaluator.evaluate(fraction,-90,0));
//        mainView.setRotationY(floatEvaluator.evaluate(fraction,0,90));

        //3.给SlideMenu的背景图片添加颜色的遮罩效果
        if(getBackground()!=null){
//            getBackground().setColorFilter((Integer) argbEvaluator.evaluate(fraction, Color.RED,Color.GREEN)
//                    ,PorterDuff.Mode.SRC_OVER);
            getBackground().setColorFilter((Integer) argbEvaluator.evaluate(fraction, Color.BLACK,Color.TRANSPARENT)
                    ,PorterDuff.Mode.SRC_OVER);
        }
    }

    /**
     * 对left进行值的判断
     * @param newLeft
     * @return
     */
    private int fixLeft(int newLeft) {
        if(newLeft>dragRange){
            newLeft = dragRange;
        }
        if(newLeft<0){
            newLeft = 0;
        }
        return newLeft;
    }

    private OnDragListener listener;
    public void setDragListener(OnDragListener listener){
        this.listener = listener;
    }

    /**
     * 定义菜单滑动的接口回调
     */
    public interface OnDragListener{
        void onOpen();
        void onClose();

        /**
         * 拖拽百分比的回调
         * @param fraction
         */
        void onDragging(float fraction);
    }
}
