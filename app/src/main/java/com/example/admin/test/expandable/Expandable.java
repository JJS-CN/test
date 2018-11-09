package com.example.admin.test.expandable;

import java.util.List;

/**
 * 说明：
 * Created by jjs on 2018/11/9.
 */

public interface Expandable {
    int getItemType();//分类

    boolean canSwitch();//能否操作

    boolean isSwitch();//是否打开

    void setSwitch(boolean isSwitch);//设置

    List<? extends Expandable> getSubItems();
}
