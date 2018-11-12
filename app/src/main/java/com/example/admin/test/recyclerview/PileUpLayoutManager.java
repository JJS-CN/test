package com.example.admin.test.recyclerview;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 说明：上滑时依次层叠item
 * Created by jjs on 2018/8/22.
 */

public class PileUpLayoutManager extends RecyclerView.LayoutManager {
    private int mOrientation = LinearLayout.VERTICAL;
    private LayoutState mLayoutStatus = new LayoutState();
    private int mScrollOffsetDy;
    private int TopMarginForPx = 40;


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {//没有Item，界面空着吧
            removeAndRecycleAllViews(recycler);
            return;
        }
        detachAndScrapAttachedViews(recycler);
        fillViews(recycler, state);
    }

    private void fillViews(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.v("性能测试", "child:" + getChildCount() + "     ScrapSize:" + recycler.getScrapList().size());
        if (getChildCount() == 0) {
            //第一次加载
            mLayoutStatus.recyHeight = getHeight() - getPaddingTop() - getPaddingBottom();
            //更新data时，此时recyclerView已经移动，进行偏移量计算。当每次更新data需要重置时，将mScrollOffsetDy重置为0
            int lastBottom = -mScrollOffsetDy;
            for (int i = 0; i < getItemCount(); i++) {
                View view = recycler.getViewForPosition(i);
                addView(view);
                measureChildWithMargins(view, 0, 0);
                int childWidth = getDecoratedMeasurementHorizontal(view);
                int childHeight = getDecoratedMeasurementVertical(view);
                int childTop = lastBottom;
                int childBottom = childTop + childHeight;
                lastBottom = childBottom;
                layoutDecoratedWithMargins(view, 0, childTop, childWidth, childBottom);
                //当前itemBottom已经超出底部时，不再进行绘制
                if (lastBottom > mLayoutStatus.recyHeight) {
                    break;
                }
            }
        } else {
            //先删除
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View child = getChildAt(i);
                //item顶部超出recy下边界时，删除
                if (mLayoutStatus.orientation == LayoutState.OrientationEnd && getDecoratedTop(child) - getParams(child).topMargin + mLayoutStatus.dt > mLayoutStatus.recyHeight) {
                    removeAndRecycleView(child, recycler);
                }
            }
            //再执行添加
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                int position = getPosition(child);
                //当前item需要是最后一个child，当前item不能是最后一个dataView
                if (mLayoutStatus.orientation == LayoutState.OrientationStart && getDecoratedBottom(child) + getParams(child).bottomMargin - mLayoutStatus.dt < mLayoutStatus.recyHeight && position < getItemCount() - 1 && i == getChildCount() - 1) {
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
        int lastBottom = 0;

        int scroll = mScrollOffsetDy;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                child.setTranslationZ(i * 2);
            }
            int childWidth = getDecoratedMeasurementHorizontal(child);
            int childHeight = getDecoratedMeasurementVertical(child);
            int childTop = lastBottom;
            int childBottom = childTop + childHeight;
            int pile = Math.min(scroll, childHeight - TopMarginForPx);
            lastBottom = childBottom - pile;
            scroll = scroll - pile;
            layoutDecoratedWithMargins(child, 0, childTop, childWidth, childBottom);
        }
        mScrollOffsetDy = mScrollOffsetDy - scroll;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {

        //当处于这些情况时，不执行
        if (dy == 0 || getChildCount() == 0 || mOrientation == LinearLayout.HORIZONTAL) {
            return 0;
        }


        //进行边界限制，垂直滚动时上边界限制
        if (mScrollOffsetDy + dy < 0) {
            dy = 0;
            mScrollOffsetDy = 0;
        }
        //不知为何，这行代码从上面挪下来之后就不会多移除顶部item了。 可能是由于这个判断 if (gap > 0) {
        mLayoutStatus.orientation = dy < 0 ? LayoutState.OrientationEnd : LayoutState.OrientationStart;
        mLayoutStatus.dt = Math.abs(dy);


        fillViews(recycler, state);
        //移动所有child 同时提供fly效果
        //offsetChildrenVertical(-dy);
        //进行滑动记录
        mScrollOffsetDy += dy;
        //返回dy与初始不同时，认为到达边界，出现边界阴影效果
        return dy;
    }


    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (dx == 0 || getChildCount() == 0 || mOrientation == LinearLayout.VERTICAL) {
            return 0;
        }
        mLayoutStatus.orientation = dx < 0 ? LayoutState.OrientationEnd : LayoutState.OrientationStart;
        mLayoutStatus.dt = Math.abs(dx);
        return dx;
    }


    private class LayoutState {
        public static final int OrientationStart = 0;
        public static final int OrientationEnd = 1;
        int orientation;//滑动方向
        int dt;//滑动距离
        int recyHeight;//控件高度
    }

    @Override
    public boolean canScrollHorizontally() {
        //能否进行水平滚动
        return mOrientation == LinearLayout.HORIZONTAL;
    }

    @Override
    public boolean canScrollVertically() {
        //是否能进行垂直滚动
        return mOrientation == LinearLayout.VERTICAL;
    }

    //获取某个childView在水平方向所占的空间
    public int getDecoratedMeasurementHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin
                + params.rightMargin;
    }

    //获取某个childView在竖直方向所占的空间
    public int getDecoratedMeasurementVertical(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin
                + params.bottomMargin;
    }


    private RecyclerView.LayoutParams getParams(View view) {
        return (RecyclerView.LayoutParams) view.getLayoutParams();
    }


}
