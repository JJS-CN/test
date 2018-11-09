package com.example.admin.test.list;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 说明：
 * recyclerview继承于viewGroup
 * tools:listitem 设置item layout，可以在xml中预览item布局
 * app:layoutManager="android.support.v7.widget.LinearLayoutManager" 在xml中设置布局管理器，可同步预览；本质是通过反射实例化类对象，且AS不进行代码提示，不推荐
 * Created by jjs on 2018/11/5.
 */

public class LinLayoutManager extends RecyclerView.LayoutManager {
    //通过传参设置滑动方向
    private int mOrientation = LinearLayout.VERTICAL;
    //滑动由监听onTouch来实现，记录总的滑动距离，通过限定此参数的边界，可控制manager的滑动边界位置
    private int mScrollOffsetDy;
    //当前竖向滑动
    private int mVerticallyBy;
    private boolean mVerticallyToDown;//是否向下滑动

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        /**
         * 此处设定的lp会在recycler.getViewForPosition(i)时调用到
         * 但是通过xml正常设置item布局，此方法未被调用，所以尚不清除使用场景
         * RecyclerView.LayoutParams继承于android.view.ViewGroup.MarginLayoutParams
         * 所以可以处理xml布局的margin类参数
         */
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollHorizontally() {
        //设置可以横向滑动
        return mOrientation == LinearLayout.HORIZONTAL;
    }

    @Override
    public boolean canScrollVertically() {
        //设置可以竖向滑动
        return mOrientation == LinearLayout.VERTICAL;
    }

    /**
     * 在setAdapter 或 adapter.notifyDataSetChanged 等数据更新时，此方法会被调用
     * 最主要是控制itemView的复用。这是recyclerview的精髓
     * <p>
     * recyclerview的缓存有3种，其中一种是在onlayout预布局使用的缓存，对于用户来说大致分为2类:
     * 1：轻量级回收scrap，view复用时不会重新bindViewHolder>>>>detach类方法会根据情况，将原来的Item View放入Scrap Heap或Recycle Pool；
     * 2：重量级回收Recycle，view复用时重新bindViewHolder>>>>remove类方法，只会将view放入Recycle Pool中；
     * by:https://blog.csdn.net/user11223344abc/article/details/78080671
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        mVerticallyBy=0;
        if (getItemCount() == 0) {
            //当数据为0时，清空所有的itemview,这里有使用remove类方法，？？使之复用时可以重走bindViewholder绑定新数据
            removeAndRecycleAllViews(recycler);
            return;
        }
        //如果数据不为空时，将所有item放入缓存中，然后重走布局方法
        detachAndScrapAttachedViews(recycler);

        //对items进行布局，由于滑动时也需要重新布局items，所以定义方法处理
        fillViews(recycler, state);
    }

    private void fillViews(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.e("性能测试start", "child:" + getChildCount() + "     ScrapSize:" + recycler.getScrapList().size());
        //getChildCount()获取的是当前recyclerview所拥有的子view内容，因为recyclerView是继承于viewGroup
        //对应的getItemCount() 获取的是adapter的集合size
        if (getChildCount() == 0) {
            //当子view为0时，表示是onLayoutChildren触发的布局，此时可能已经滑动过，所以初始绘制位置需要考虑滑动距离
            int lastBottom = -mScrollOffsetDy;
            for (int i = 0; i < getItemCount(); i++) {
                //获取对应position的itemview，recycler会从
                View view = recycler.getViewForPosition(i);
                //测量view的大小，会将margin考虑
                measureChildWithMargins(view, 0, 0);
                //自定义方法，考虑margin,否则给予itemview的宽高都将不足，会被挤压
                int childWidth = getDecoratedMeasurementHorizontal(view);
                int childHeight = getDecoratedMeasurementVertical(view);
                int childTop = lastBottom;
                int childBottom = childTop + childHeight;
                lastBottom = childBottom;
                if (lastBottom <= 0) {
                    //此处为了适配adapter.notifyDataSetChanged进入。此时可能滑动过，之前移出屏幕的view不应展示了
                    continue;
                }
                //将view添加到viewgroup
                addView(view);
                //布局view
                layoutDecoratedWithMargins(view, 0, childTop, childWidth, childBottom);
                //当前itemBottom已经超出底部时，不再进行绘制
                if (lastBottom > getHeight()) {
                    break;
                }
            }
        } else {
            //先删除，从最后一个开始往前遍历；因为从前开始的话，删除itemview会引起下标混乱
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View child = getChildAt(i);
                //item底部超出recy上边界时，删除
                if (!mVerticallyToDown && getDecoratedBottom(child) + getParams(child).bottomMargin <= 0) {
                    removeAndRecycleView(child, recycler);
                }
                //item顶部超出recy下边界时，删除
                if (mVerticallyToDown && getDecoratedTop(child) - getParams(child).topMargin + mVerticallyBy > getHeight()) {
                    removeAndRecycleView(child, recycler);
                }
            }
            //再执行添加
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                int position = getPosition(child);
                //item顶部大于recy上边界0时，添加item，并i--执行position回复，重复判断
                if (mVerticallyToDown && getDecoratedTop(child) - getParams(child).topMargin + mVerticallyBy > 0 && position > 0 && i == 0) {
                    View view = recycler.getViewForPosition(position - 1);
                    addView(view, 0);
                    measureChildWithMargins(view, 0, 0);
                    int childWidth = getDecoratedMeasurementHorizontal(view);
                    int childHeight = getDecoratedMeasurementVertical(view);
                    int childBottom = getDecoratedTop(child) - getParams(child).topMargin;
                    int childTop = childBottom - childHeight;
                    layoutDecoratedWithMargins(view, 0, childTop, childWidth, childBottom);
                    i--;
                }
                //item底部小于recy下边界时，添加item
                //当前item需要是最后一个child，当前item不能是最后一个dataView
                if (!mVerticallyToDown && getDecoratedBottom(child) + getParams(child).bottomMargin - mVerticallyBy < getHeight() && position < getItemCount() - 1 && i == getChildCount() - 1) {

                    View view = recycler.getViewForPosition(position + 1);
                    addView(view);
                    measureChildWithMargins(view, 0, 0);
                    int childWidth = getDecoratedMeasurementHorizontal(view);
                    int childHeight = getDecoratedMeasurementVertical(view);
                    int childTop = getDecoratedBottom(child) + getParams(child).bottomMargin;
                    int childBottom = childTop + childHeight;
                    layoutDecoratedWithMargins(view, 0, childTop, childWidth, childBottom);
                }
            }
        }
        Log.e("性能测试end", "child:" + getChildCount() + "     ScrapSize:" + recycler.getScrapList().size());
    }


    /**
     * 竖向滑动，在onTouch中被回调，由canScrollVertically控制
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {

        //当处于这些情况时，不执行
        if (dy == 0 || getChildCount() == 0 || mOrientation == LinearLayout.HORIZONTAL) {
            mVerticallyBy=0;
            return 0;
        }


        //进行边界限制，垂直滚动时上边界限制
        if (mScrollOffsetDy + dy < 0) {
            dy = -mScrollOffsetDy;
        } else if (dy > 0) {//下边界
            //利用最后一个子View比较修正
            View lastChild = getChildAt(getChildCount() - 1);
            if (getPosition(lastChild) == getItemCount() - 1) {
                int gap = getHeight() - getPaddingBottom() - getDecoratedBottom(lastChild) - getParams(lastChild).bottomMargin;
                if (gap > 0) {
                    dy = -gap;
                } else if (gap == 0) {
                    return 0;
                } else {
                    dy = Math.min(dy, -gap);
                }
            }
        }
        //不知为何，这行代码从上面挪下来之后就不会多移除顶部item了。 可能是由于这个判断 if (gap > 0) {

        mVerticallyToDown = dy < 0;
        mVerticallyBy = Math.abs(dy);

        fillViews(recycler, state);
        //移动所有child 同时提供fly效果
        offsetChildrenVertical(-dy);
        //进行滑动记录
        mScrollOffsetDy += dy;
        //返回dy与初始不同时，认为到达边界，出现边界阴影效果
        return dy;
    }

    /**
     * 横向滑动，在onTouch中被回调，由canScrollHorizontally控制
     */
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return super.scrollHorizontallyBy(dx, recycler, state);
        //横向滑动距离，同scrollVerticallyBy处理
    }

    //获取某个childView在水平方向所占的空间
    public int getDecoratedMeasurementHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        //getDecoratedMeasuredWidth在测量时考虑了ItemDecoration，我们要额外考虑itemview的margin属性
        return getDecoratedMeasuredWidth(view) + params.leftMargin
                + params.rightMargin;
    }

    //获取某个childView在竖直方向所占的空间
    public int getDecoratedMeasurementVertical(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        //getDecoratedMeasuredHeight在测量时考虑了ItemDecoration，我们要额外考虑itemview的margin属性
        return getDecoratedMeasuredHeight(view) + params.topMargin
                + params.bottomMargin;
    }

    private RecyclerView.LayoutParams getParams(View view) {
        return (RecyclerView.LayoutParams) view.getLayoutParams();
    }

}
