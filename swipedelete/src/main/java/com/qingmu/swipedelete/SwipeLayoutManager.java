package com.qingmu.swipedelete;

/**
 * 负责记录打开的条目，
 * 负责关闭已经打开的条目，
 * 负责判断当前是否有打开的条目
 * Created by lxj on 2016/7/12.
 */
public class SwipeLayoutManager {
    private SwipeLayout currentSwipeLayout;//用来记录当前已经打开的SwipeLayout

    private static SwipeLayoutManager mInstance = new SwipeLayoutManager();
    private SwipeLayoutManager(){
    }
    public static SwipeLayoutManager getInstance(){
        return mInstance;
    }

    /**
     * 记录打开的条目
     * @param swipeLayout
     */
    public void setSwipeLayout(SwipeLayout swipeLayout){
        this.currentSwipeLayout = swipeLayout;
    }

    /**
     * 是否满足滑动条目的条件
     */
    public boolean shouldSwipe(SwipeLayout touchSwipeLayout){
        if(currentSwipeLayout==null){
            //说明没有打开的，那么可以滑动
            return true;
        }else {
            //说明有打开的条目，那么则判断按下的和打开的是否是同一个
            //如果是同一个，则可以滑动，否则不可以滑动
            return currentSwipeLayout==touchSwipeLayout;
        }
    }

    /**
     * 关闭已经打开的条目
     */
    public void closeSwipeLayout(){
        if(currentSwipeLayout!=null){
            currentSwipeLayout.close();
        }
    }
    public void clearSwipeLayout(){
        currentSwipeLayout = null;
    }


}
