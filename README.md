# DragGridLayout

自定义GridLayout控件，实现新闻频道等拖拽管理的炫酷功能

<img src="./img/gridlayout.jpg" width="400" />

自定义GridLayout控件，可以在新闻咨询类APP中的管理页面使用到，也可以应用在类别管理中，总之，可以帮助我们设计更加规范和炫酷的手机页面。

新闻类app是最常见的应用之一，而频道管理又是其必不可少的功能，该自定义控件不仅可以带我们实现炫酷的频道管理功能，还可以让我们学习如何使用Android拖拽框架实现我们想要的多种功能，以及让我们对自定义控件会有更多的理解。

## **知识点**

1. GridLayout的使用

   - 从Google官方文档学习GridLayout的功能以及用法
   - 使用GridLayout实现子控件排列显示

2. View的拖拽功能实现

   - 通过查看Google文档，学会调用view的拖拽方法
   - 拖拽事件的处理
   - 使用View的拖拽框架实现实现频道切换位置效果

3. 自定义GridLayout控件

   自定义GridLayout控件，实现拖拽功能，继而实现频道管理操作

4. Rect类的使用

   使用Rect类确定被触摸到的子控件

## View.OnLongClickListener

```java
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
```

## 拖拽

- startDragAndDrop()
- startDrag(ClipData data, DragShadowBuilder shadowBuilder, Object myLocalState, int flag)

参数1：ClipData data 拖拽过程中可以transferred的数据，可以为空

参数2：DragShadowBuilder shadowBuilder，拖拽阴影效果创建者

参数3：Object myLocalState，拖拽状态

参数4：int flag，可以控制拖拽操作的flag，未定义，传0即可

## View.OnDragListener

```java
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
            //            Rect rect = new Rect();
            //            rect.contains()
            switch (event.getAction()) {
                //当拖拽事件开始时，创建出与子控件对应的矩形数组
                case DragEvent.ACTION_DRAG_STARTED:
                    initRects();
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    //手指移动时，实时判断触摸是否进入了某一个子控件
                    int touchIndex = getTouchIndex(event);
                    //说明触摸点进入了某一个子控件,判断被拖拽的视图与进入的子控件对象不是同一个的时候才进行删除添加操作

                    if (touchIndex > -1&&dragedView != null&&dragedView != mGridLayout.getChildAt(touchIndex)) {
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
```

### DragEvent

| 拖拽事件                           | 说明                               |
| :----------------------------- | :------------------------------- |
| ACTION_DRAG_STARTED            | 当拖拽操作执行时，就会执行一次                  |
| DragEvent.ACTION_DRAG_ENDED    | 当拖拽事件结束，手指抬起时，就是执行一次             |
| DragEvent.ACTION_DRAG_ENTERED  | 当手指进入设置了拖拽监听的控件范围内的瞬间执行一次        |
| DragEvent.ACTION_DRAG_EXITED   | 当手指离开设置了拖拽监听的控件范围内的瞬间执行一次        |
| DragEvent.ACTION_DRAG_LOCATION | 当手指在设置了拖拽监听的控件范围内，移动时，实时会执行，执行N次 |
| DragEvent.ACTION_DROP          | 当手指在设置了拖拽监听的控件范围内松开时，执行一次        |

## 当拖拽事件开始时，创建出与子控件对应的矩形数组

```java
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
```

## 手指移动时，实时判断触摸是否进入了某一个子控件

```java
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
```

## 是否允许拖拽

```java
public void setAllowDrag(boolean allowDrag) {
        this.allowdrag = allowDrag;
        if (this.allowdrag) {
            this.setOnDragListener(odl);
        } else {
            this.setOnDragListener(null);
        }

    }
```

## 设置列数和动画

```java
//初始化方法
private void init() {
    // android:columnCount="4"
    // android:animateLayoutChanges="true"
    this.setColumnCount(columnCount);
    this.setLayoutTransition(new LayoutTransition());
}
```

## DragGridlayout

```java
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
```

## 摩天轮控件分析

![摩天轮控件分析](img/摩天轮控件分析.png)
