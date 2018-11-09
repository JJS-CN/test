package com.example.admin.test.expandable;

/**
 * 说明：
 * Created by jjs on 2018/11/9.
 */

public abstract class ExpandableItem implements Expandable {
    private boolean isSwitch = false;//用于标识当前分组是否已经展开

    @Override
    public boolean isSwitch() {
        return isSwitch;
    }

    @Override
    public void setSwitch(boolean isSwitch) {
        this.isSwitch = isSwitch;
    }

}
