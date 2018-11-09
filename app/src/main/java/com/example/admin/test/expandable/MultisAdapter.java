package com.example.admin.test.expandable;

import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.List;

/**
 * 说明：多级分组adapter
 * Created by jjs on 2018/11/9.
 */

public abstract class MultisAdapter<T extends Expandable> extends RecyclerView.Adapter<MultisViewHolder> {
    private List<T> mData;
    private SparseIntArray mLayoutArrays;

    public MultisAdapter(List<T> mData) {
        this.mData = mData;
        mLayoutArrays = new SparseIntArray();
    }

    //添加itemType到集合中，此处需要和entity中对应
    public void addItemType(int itemType, @LayoutRes int layoutId) {
        mLayoutArrays.put(itemType, layoutId);
    }

    @Override
    public MultisViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(mLayoutArrays.get(viewType), parent, false);
        return new MultisViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MultisViewHolder holder, final int position) {
        convert(holder, mData.get(position));
    }

    abstract void convert(MultisViewHolder holder, Expandable item);


    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getItemType();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //根据当前状态，自行切换
    public void expandOrcollapse(int position) {
        if (mData.get(position).isSwitch()) {
            collapse(position);
        } else {
            expand(position);
        }
    }

    //展开（添加view）
    public void expand(int position) {
        Expandable item = mData.get(position);
        if (item.canSwitch()) {
            item.setSwitch(true);
            mData.addAll(position + 1, (Collection<? extends T>) mData.get(position).getSubItems());
            notifyDataSetChanged();
        }

    }

    //折叠
    public void collapse(int position) {
        Expandable item = mData.get(position);
        if (item.canSwitch()) {
            item.setSwitch(false);
            recursiveCollapse(position);
            notifyDataSetChanged();
        }

    }

    //清除view
    private int recursiveCollapse(@IntRange(from = 0) int position) {
        Expandable item = mData.get(position);
        if (!item.canSwitch()) {
            return 0;
        }
        int subItemCount = 0;
        if (item.canSwitch()) {
            List<T> subItems = (List<T>) item.getSubItems();
            if (null == subItems)
                return 0;

            for (int i = subItems.size() - 1; i >= 0; i--) {
                T subItem = subItems.get(i);
                int pos = getItemPosition(subItems.get(i));
                if (pos < 0) {
                    continue;
                }
                if (subItem.isSwitch()) {
                    subItem.setSwitch(false);
                    subItemCount += recursiveCollapse(pos);
                }
                mData.remove(pos);
                subItemCount++;
            }
        }
        return subItemCount;
    }

    private int getItemPosition(T item) {
        return item != null && mData != null && !mData.isEmpty() ? mData.indexOf(item) : -1;
    }
}
