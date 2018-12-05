package com.example.admin.test.recyclerview.other;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.admin.test.R;

import java.util.Arrays;

/**
 * 说明：
 * Created by jjs on 2018/11/23
 */

public class ListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        final RecyclerView mRv = findViewById(R.id.rv);
        BaseQuickAdapter adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.itemsss, Arrays.asList(new String[]{"1", "2", "3", "4", "1", "2", "3", "4", "1", "2", "3", "4", "1", "2", "3", "4", "1", "2", "3", "4", "1", "2", "3", "4"})) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv1, item);
              /*  ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) helper.itemView.getLayoutParams();
                Log.e("top0", helper.getAdapterPosition() + "==" + mRv.getHeight() / 2 + " - " + helper.itemView.getHeight() / 2);
                if (helper.getAdapterPosition() == 0) {

                    lp.topMargin = mRv.getHeight() / 2 - helper.itemView.getHeight() / 2;
                } else if (helper.getAdapterPosition() == getItemCount() - 1) {
                    lp.topMargin = 0;
                    lp.bottomMargin = mRv.getHeight() / 2 - helper.itemView.getHeight() / 2;
                } else {
                    lp.topMargin = 0;
                    lp.bottomMargin = 0;
                }
                helper.itemView.setLayoutParams(lp);*/
            }
        };
      /*
        new LinearSnapHelper().attachToRecyclerView(mRv);

        mRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = parent.getHeight() / 2 - ConvertUtils.dp2px(100) / 2;

                } else if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
                    outRect.bottom = parent.getHeight() / 2 - ConvertUtils.dp2px(100) / 2;
                }
            }
        });*/
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(adapter);

        WheelSnapHelper helper = new WheelSnapHelper(WheelSnapHelper.VERTICAL, 100);

        helper.setOnSnapListener(new WheelSnapHelper.OnSnapListener() {
            @Override
            public void onSnaped(int position) {
                Log.e("eeee", position + "");
            }
        });
        helper.attachToRecyclerView(mRv);



    }
}
