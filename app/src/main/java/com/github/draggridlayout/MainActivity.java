package com.github.draggridlayout;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
 * Copyright：${TODO}有限公司版权所有 (c) 2017
 * Author：   AllenIverson
 * Email：    815712739@qq.com
 * GitHub：   https://github.com/JackChen1999
 * GitBook：  https://www.gitbook.com/@alleniverson
 * 博客：     http://blog.csdn.net/axi295309066
 * 微博：     AndroidDeveloper
 * <p>
 * Project_Name：DragGridLayout
 * Package_Name：com.github.draggridlayout
 * Version：1.0
 * time：2017/3/26 9:31
 * des ：${TODO}
 * gitVersion：2.12.0.windows.1
 * updateAuthor：$Author$
 * updateDate：$Date$
 * updateDes：${TODO}
 * ============================================================
 */

public class MainActivity extends AppCompatActivity {

    private GridLayout mGridLayout;
    private int        index;
    private View       dragedView;

    private View.OnLongClickListener olcl = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            //长按时，开始拖拽操作，显示出阴影
            //被拖拽的视图其实就是v参数
            dragedView = v;
            v.startDrag(null, new View.DragShadowBuilder(v), null, 0);
            v.setEnabled(false);
            // v.startDragAndDrop(null, new View.DragShadowBuilder(v), null, 0);
            return false;
        }
    };
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
            String dragEventAction = getDragEventAction(event);
            System.out.println(dragEventAction);
            //  Rect rect = new Rect();
            //  rect.contains()
            switch (event.getAction()) {
                //当拖拽事件开始时，创建出与子控件对应的矩形数组
                case DragEvent.ACTION_DRAG_STARTED:
                    initRects();
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    //手指移动时，实时判断触摸是否进入了某一个子控件
                    int touchIndex = getTouchIndex(event);
                    //说明触摸点进入了某一个子控件,判断被拖拽的视图与进入的子控件对象不是同一个的时候才进行删除添加操作

                    if (touchIndex > -1 && dragedView != null&&dragedView != mGridLayout.getChildAt(touchIndex)) {
                        mGridLayout.removeView(dragedView);
                        mGridLayout.addView(dragedView,touchIndex);
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
    private DragGridlayout mDragGridlayout;
    private DragGridlayout mHidegridlayout;

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
        mRects = new Rect[mGridLayout.getChildCount()];
        for (int i = 0; i < mGridLayout.getChildCount(); i++) {
            View childView = mGridLayout.getChildAt(i);
            //创建与每个子控件对应矩形对象
            Rect rect = new Rect(childView.getLeft(), childView.getTop(), childView.getRight(), childView.getBottom());
            mRects[i] = rect;
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

    public static String getDragEventAction(DragEvent de){
        return dragEventType.get(de.getAction());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGridLayout = (GridLayout) findViewById(R.id.gridlayout);
        mGridLayout.setOnDragListener(odl);

        //获取自定义的控件
        mDragGridlayout = (DragGridlayout) findViewById(R.id.draggridlayout);
        //定义出频道数据
        List<String> titles = new ArrayList<>();
        titles.add("头条");
        titles.add("要闻");
        titles.add("娱乐");
        titles.add("体育");
        titles.add("科技");
        titles.add("直播");
        titles.add("视频");
        titles.add("财经");
        titles.add("时尚");
        titles.add("图片");
        titles.add("汽车");
        titles.add("热点");
        titles.add("跟帖");
        titles.add("航空");
        titles.add("历史");
        titles.add("家居");
        titles.add("游戏");
        titles.add("漫画");
        titles.add("彩票");
        titles.add("美女");
        mDragGridlayout.setAllowDrag(true);
        mDragGridlayout.setItems(titles);
        mHidegridlayout = (DragGridlayout) findViewById(R.id.hidegridlayout);
        //定义出频道数据
        List<String> titles1 = new ArrayList<>();
        titles1.add("NBA");
        titles1.add("社会");
        titles1.add("云课堂");
        titles1.add("轻松一刻");
        titles1.add("段子");
        titles1.add("股票");
        titles1.add("独家");
        titles1.add("军事");
        titles1.add("国际足球");
        titles1.add("中国足球");
        titles1.add("CBA");
        titles1.add("手机");
        titles1.add("数码");
        titles1.add("旅游");
        titles1.add("艺术");
        titles1.add("亲子");
        titles1.add("二次元");
        mHidegridlayout.setAllowDrag(false);
        mHidegridlayout.setItems(titles1);
    }

    public void addItem(View view) {
        //点击时，往GridLayout添加频道栏目
        TextView tv = new TextView(MainActivity.this);
        tv.setText(index+"");
        int margin = 5;
        tv.setBackgroundResource(R.drawable.selector_tv_bg);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels/4 - 2*margin;//宽为屏幕宽的4分之一
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.setMargins(margin,margin,margin,margin);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(layoutParams);
        index++;

        tv.setOnLongClickListener(olcl);

        mGridLayout.addView(tv,0);
    }

    /** dip转换px */
    public int dip2px(int dip) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }
}
