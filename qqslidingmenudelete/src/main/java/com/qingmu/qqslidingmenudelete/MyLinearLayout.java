package com.qingmu.qqslidingmenudelete;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 当SlideMenu处于打开的状态的时候，能够拦截并消费掉所有的触摸事件
 * Created by lxj on 2016/7/12.
 */
public class MyLinearLayout extends LinearLayout{
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private SlideMenu slideMenu;
    public void setSlideMenu(SlideMenu slideMenu){
        this.slideMenu = slideMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //如果Slidemenu处于打开的状态
        if(slideMenu!=null && slideMenu.getDragState()== SlideMenu.DragState.Open){
            //需要拦截
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //如果Slidemenu处于打开的状态,则消费掉事件
        if(slideMenu!=null && slideMenu.getDragState()== SlideMenu.DragState.Open){
            //需要消费掉
            return true;
        }
        return super.onTouchEvent(event);
    }
}
