package com.github.draggridlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

    private DragGridlayout mSelectedChannel;
    private DragGridlayout mUnSelectedChannel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initData();
        initEvent();
    }

    private void initViews() {
        setContentView(R.layout.activity_main);
        mSelectedChannel = (DragGridlayout) findViewById(R.id.selectedChannel);
        mUnSelectedChannel = (DragGridlayout) findViewById(R.id.unSelectedChannel);
        mSelectedChannel.setAllowDrag(true);
        mUnSelectedChannel.setAllowDrag(true);
    }

    private void initData() {
        List<String> selectedChannel = new ArrayList<>();
        selectedChannel.add("头条");
        selectedChannel.add("要闻");
        selectedChannel.add("娱乐");
        selectedChannel.add("体育");
        selectedChannel.add("科技");
        selectedChannel.add("直播");
        selectedChannel.add("视频");
        selectedChannel.add("财经");
        selectedChannel.add("时尚");
        selectedChannel.add("图片");
        selectedChannel.add("汽车");
        selectedChannel.add("热点");
        selectedChannel.add("跟帖");
        selectedChannel.add("航空");
        selectedChannel.add("历史");
        selectedChannel.add("家居");
        selectedChannel.add("游戏");
        selectedChannel.add("漫画");
        selectedChannel.add("彩票");
        selectedChannel.add("美女");
        mSelectedChannel.setItems(selectedChannel);

        List<String> unSelectedChannel = new ArrayList<>();
        unSelectedChannel.add("NBA");
        unSelectedChannel.add("社会");
        unSelectedChannel.add("云课堂");
        unSelectedChannel.add("轻松一刻");
        unSelectedChannel.add("段子");
        unSelectedChannel.add("股票");
        unSelectedChannel.add("独家");
        unSelectedChannel.add("军事");
        unSelectedChannel.add("国际足球");
        unSelectedChannel.add("中国足球");
        unSelectedChannel.add("CBA");
        unSelectedChannel.add("手机");
        unSelectedChannel.add("数码");
        unSelectedChannel.add("旅游");
        unSelectedChannel.add("艺术");
        unSelectedChannel.add("亲子");
        unSelectedChannel.add("二次元");
        mUnSelectedChannel.setItems(unSelectedChannel);
    }

    public void initEvent(){
        //设置条目点击监听
        mSelectedChannel.setOnDragItemClickListener(new DragGridlayout.OnDragItemClickListener() {
            @Override
            public void onDragItemClick(TextView tv) {
                //移除点击的条目，把条目添加到下面的Gridlayout
                mSelectedChannel.removeView(tv);//移除是需要时间,不能直接添加
                mUnSelectedChannel.addItem(tv.getText().toString(),0);
            }
        });

        mUnSelectedChannel.setOnDragItemClickListener(new DragGridlayout.OnDragItemClickListener() {
            @Override
            public void onDragItemClick(TextView tv) {
                //移除点击的条目，把条目添加到上面的Gridlayout
                mUnSelectedChannel.removeView(tv);//移除是需要时间,不能直接添加
                mSelectedChannel.addItem(tv.getText().toString());
            }
        });
    }

    private int index = 0;

    public void addItem(View view) {
        mSelectedChannel.addItem("频道" + index++,0);
    }

}
