package com.github.draggridlayout;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.List;

/**
 * ============================================================
 * Copyright：${TODO}有限公司版权所有 (c) 2017
 * Author：   AllenIverson
 * Email：    815712739@qq.com
 * GitHub：   https://github.com/JackChen1999
 * 博客：     http://blog.csdn.net/axi295309066
 * 微博：     AndroidDeveloper
 * GitBook：  https://www.gitbook.com/@alleniverson
 * <p>
 * Project_Name：DragGridLayout
 * Package_Name：com.github.draggridlayout
 * Version：1.0
 * time：2017/3/26 9:26
 * des ：1.定义出一个setItems，动态地将每个频道的标题数据集合传递进来之后，按照数据进行展示即可
 *       2.定义出一个setAllowDrag方法，控制控件是否可以进行拖拽操作
 * gitVersion：2.12.0.windows.1
 * updateAuthor：$Author$
 * updateDate：$Date$
 * updateDes：${TODO}
 * ============================================================
 */
public class DragGridlayout extends GridLayout{
    private  static final int columnCount = 4;//列数
    private boolean allowdrag;//记录当前控件是否可以进行拖拽操作

    public DragGridlayout(Context context) {
        this(context,null);
    }

    public DragGridlayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DragGridlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //初始化方法
    private void init() {
        //  android:columnCount="4"
        //  android:animateLayoutChanges="true"

        this.setColumnCount(columnCount);
        this.setLayoutTransition(new LayoutTransition());
    }

    public void setItems(List<String> items) {
        for (String item : items) {
            addItem(item);
        }
    }

    private void addItem(String item) {
        TextView textView = newItemView();
        textView.setText(item);
        this.addView(textView);
    }

    private TextView newItemView() {
        TextView tv = new TextView(getContext());
        int margin = dip2px(5);
        tv.setBackgroundResource(R.drawable.selector_tv_bg);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels/4 - 2*margin;//宽为屏幕宽的4分之一
        layoutParams.height = dip2px(26);
        layoutParams.setMargins(margin,margin,margin,margin);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(layoutParams);

        if (this.allowdrag) {
            tv.setOnLongClickListener(olcl);
        } else {
            tv.setOnLongClickListener(null);
        }

        return tv;
    }

    /** dip转换px */
    public int dip2px(int dip) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    private View dragedView;//被拖拽的视图
    private View.OnLongClickListener olcl = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            //长按时，开始拖拽操作，显示出阴影
            //被拖拽的视图其实就是v参数
            dragedView = v;
            v.startDrag(null, new View.DragShadowBuilder(v), null, 0);
            v.setEnabled(false);
            //v.startDragAndDrop(null, new View.DragShadowBuilder(v), null, 0);
            return false;
        }
    };


    public void setAllowDrag(boolean allowDrag) {
        this.allowdrag = allowDrag;
        if (this.allowdrag) {
            this.setOnDragListener(odl);
        } else {
            this.setOnDragListener(null);
        }

    }



    private View.OnDragListener odl =  new View.OnDragListener() {
        /**
         * ACTION_DRAG_STARTED:当拖拽操作执行时，就会执行一次
         * DragEvent.ACTION_DRAG_ENDED：当拖拽事件结束，手指抬起时，就是执行一次
         * DragEvent.ACTION_DRAG_ENTERED：当手指进入设置了拖拽监听的控件范围内的瞬间执行一次
         * DragEvent.ACTION_DRAG_EXITED：当手指离开设置了拖拽监听的控件范围内的瞬间执行一次
         * DragEvent.ACTION_DRAG_LOCATION：当手指在设置了拖拽监听的控件范围内，移动时，实时会执行，执行N次
         * DragEvent.ACTION_DROP：当手指在设置了拖拽监听的控件范围内松开时，执行一次
         *
         *
         * @param v 当前监听拖拽事件的view(其实就是mGridLayout)
         * @param event 拖拽事件
         * @return
         */
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                //当拖拽事件开始时，创建出与子控件对应的矩形数组
                case DragEvent.ACTION_DRAG_STARTED:
                    initRects();
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    //手指移动时，实时判断触摸是否进入了某一个子控件
                    int touchIndex = getTouchIndex(event);
                    //说明触摸点进入了某一个子控件,判断被拖拽的视图与进入的子控件对象不是同一个的时候才进行删除添加操作

                    if (touchIndex > -1&&dragedView != null&&dragedView != DragGridlayout.this.getChildAt(touchIndex)) {
                        DragGridlayout.this.removeView(dragedView);
                        DragGridlayout.this.addView(dragedView,touchIndex);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //拖拽事件结束后，让被拖拽的view设置为可用，否则背景变红，并且长按事件会失效
                    if (dragedView != null) {
                        dragedView.setEnabled(true);
                    }
                    break;
            }

            return true;
        }
    };

    //手指移动时，实时判断触摸是否进入了某一个子控件
    private int getTouchIndex(DragEvent event) {
        //遍历所有的数组，如果包含了当前的触摸点返回索引即可
        for (int i = 0; i < mRects.length; i++) {
            Rect rect = mRects[i];
            if (rect.contains((int)event.getX(), (int)event.getY())) {
                return i;
            }
        }
        return -1;
    }


    //当拖拽事件开始时，创建出与子控件对应的矩形数组
    private Rect[] mRects;

    private void initRects() {
        mRects = new Rect[this.getChildCount()];
        for (int i = 0; i < this.getChildCount(); i++) {
            View childView = this.getChildAt(i);
            //创建与每个子控件对应矩形对象
            Rect rect = new Rect(childView.getLeft(), childView.getTop(), childView.getRight(), childView.getBottom());
            mRects[i] = rect;
        }
    }
}
