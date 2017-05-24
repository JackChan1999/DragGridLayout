package com.github.draggridlayout;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * Copyright：JackChan和他的朋友们有限公司版权所有 (c) 2017
 * Author：   JackChan
 * Email：    815712739@qq.com
 * GitHub：   https://github.com/JackChan1999
 * GitBook：  https://www.gitbook.com/@alleniverson
 * CSDN博客： http://blog.csdn.net/axi295309066
 * 个人博客： https://jackchan1999.github.io/
 * 微博：     AndroidDeveloper
 * <p>
 * Project_Name：DragGridLayout
 * Package_Name：com.github.draggridlayout
 * Version：1.0
 * time：2017/5/24 16:39
 * des ：新闻频道管理的炫酷实现
 * gitVersion：2.12.0.windows.1
 * updateAuthor：AllenIverson
 * updateDate：2017/5/24 16:39
 * updateDes：${TODO}
 * ============================================================
 */
public class DragGridLayout2 extends GridLayout{
    //列数
    private final int columnCount = 4;

    //是否进行拖拽
    private boolean hasCanDrag;

    //记录拖拽的View
    private View       dragView;
    //条目的矩形区域集合
    private List<Rect> rects;

    private List<String> items;
    //从外部设置条目的内容
    public void setItems(List<String> items) {
        this.items = items;
        createItems(items);
    }

    //创建所有的条目
    private void createItems(List<String> items) {
        //创建条目
        for (String item : items) {
            addItem(item);
        }
    }

    public void setHasCanDrag(boolean hasCanDrag) {
        this.hasCanDrag = hasCanDrag;
        if(hasCanDrag){
            setOnDragListener(onDragListener);
        }else{
            setOnDragListener(null);
        }
    }
    private View.OnDragListener onDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            //Log.i(TAG,getDragEventAction(event));
            switch (event.getAction()){
                case DragEvent.ACTION_DRAG_STARTED://按下
                    dragView.setEnabled(false);
                    initRects();
                    break;
                case DragEvent.ACTION_DRAG_LOCATION://移动
                    //获取要交换条目的下标
                    int exchangeItemPosition = getExchangeItemPosition(event);
                    if(exchangeItemPosition > -1 && dragView != getChildAt(exchangeItemPosition)){
                        //移除
                        removeView(dragView);
                        //添加
                        addView(dragView,exchangeItemPosition);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED://弹起
                    dragView.setEnabled(true);
                    break;
            }
            return true;//拖拽事件成功的处理
        }
    };

    //获取要交换条目的下标
    private int getExchangeItemPosition(DragEvent event) {
        for (int i = 0; i < rects.size(); i++) {
            Rect rect = rects.get(i);
            if(rect.contains((int)event.getX(),(int)event.getY())){
                return i;
            }
        }
        return -1;
    }

    public DragGridLayout2(Context context) {
        this(context,null);
    }

    public DragGridLayout2(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DragGridLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //        android:columnCount="4"
        //        android:animateLayoutChanges="true"
        //设置列数
        setColumnCount(columnCount);
        //开启动画
        setLayoutTransition(new LayoutTransition());
    }

    //初始化条目的矩形区域
    private void initRects() {
        rects = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            //创建矩形
            Rect rect = new Rect(view.getLeft(),view.getTop(),view.getRight(),view.getBottom());
            rects.add(rect);
        }
    }

    //SparseArray<String> 相当于hashmap,但更高效，谷歌官方推荐
    static SparseArray<String> dragEventType = new SparseArray<String>();
    static{
        dragEventType.put(DragEvent.ACTION_DRAG_STARTED, "STARTED");
        dragEventType.put(DragEvent.ACTION_DRAG_ENDED, "ENDED");
        dragEventType.put(DragEvent.ACTION_DRAG_ENTERED, "ENTERED");
        dragEventType.put(DragEvent.ACTION_DRAG_EXITED, "EXITED");
        dragEventType.put(DragEvent.ACTION_DRAG_LOCATION, "LOCATION");
        dragEventType.put(DragEvent.ACTION_DROP, "DROP");
    }

    /*
    DragEvent.ACTION_DRAG_STARTED 手指按下
    DragEvent.ACTION_DRAG_ENTERED  手指按下|从GridLayout外部进入
    DragEvent.ACTION_DRAG_LOCATION 在Gridlayout的区间内移动
    DragEvent.ACTION_DRAG_EXITED   离开GridLayout的区间
    DragEvent.ACTION_DROP      松手
    DragEvent.ACTION_DRAG_ENDED  松手

     */
    public static String getDragEventAction(DragEvent de){
        return dragEventType.get(de.getAction());
    }



    private int margin = 5;

    public void addItem(String content){
        //添加条目
        TextView tv = new TextView(getContext());
        //设置布局参数
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.width = getResources().getDisplayMetrics().widthPixels/4 - margin*2;
        //设置外边距
        params.setMargins(margin,margin,margin,margin);
        tv.setLayoutParams(params);
        //设置内边距
        tv.setPadding(0,margin,0,margin);
        tv.setGravity(Gravity.CENTER);
        tv.setText(content);
        //设置背景资源
        tv.setBackgroundResource(R.drawable.selector_tv_bg);
        addView(tv,0);

        if(hasCanDrag){
            //给条目设置长按点击事件
            tv.setOnLongClickListener(onLongClickListener);
        }else{
            //给条目设置长按点击事件
            tv.setOnLongClickListener(null);
        }
        //设置条目的点击事件
        tv.setOnClickListener(onClickListener);
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(onDragItemClickListener != null){
                onDragItemClickListener.onDragItemClick((TextView) v);
            }
        }
    };

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            dragView = v;
            //产生浮动的阴影效果
            //1.传递数据  2.产生阴影的    3.传递数据  4.状态
            v.startDrag(null,new View.DragShadowBuilder(v),null,0);
            return true;//处理长按事件
        }
    };

    //接口
    public interface OnDragItemClickListener{
        public void onDragItemClick(TextView tv);
    }

    private OnDragItemClickListener onDragItemClickListener;

    public void setOnDragItemClickListener(OnDragItemClickListener onDragItemClickListener) {
        this.onDragItemClickListener = onDragItemClickListener;
    }
}
