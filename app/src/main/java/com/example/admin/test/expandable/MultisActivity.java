package com.example.admin.test.expandable;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.admin.test.R;
import com.google.gson.Gson;

/**
 * 说明：
 * Created by jjs on 2018/11/9.
 */

public class MultisActivity extends Activity {
    RecyclerView mRv;
    MultisAdapter mAdapter;

    String test = "{\"code\":\"000000\",\"message\":\"\\u6210\\u529f\",\"result\":[{\"id\":\"100211\",\"user_name\":\"\\u963f\\u98de\",\"avatar\":\"http:\\/\\/thirdwx.qlogo.cn\\/mmopen\\/vi_32\\/Q0j4TwGTfTJEMrRUnBSh2cic3BwiaZ3wiab0qPOLjTiaRcJrCwWCVN7pkiaqgKO4kFk5QXj6EplpDwtEZvZiaH2icDr3w\\/132\",\"children\":[{\"id\":\"100212\",\"user_name\":\"15625732879\",\"avatar\":null,\"children\":[{\"id\":\"100208\",\"user_name\":\"18575518402\",\"avatar\":null}]}]},{\"id\":\"100213\",\"user_name\":\"13192051428\",\"avatar\":null,\"children\":[]}]}";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multis);
        mRv = findViewById(R.id.rv);

        UserDataEntity entity = new Gson().fromJson(test, UserDataEntity.class);
        mAdapter = new MultisAdapter<UserDataEntity.ResultEntityA>(entity.result) {
            {
                addItemType(0, R.layout.item_province);
                addItemType(1, R.layout.item_city);
                addItemType(2, R.layout.item_area);
            }

            @Override
            void convert(final MultisViewHolder holder, Expandable item) {
                switch (item.getItemType()) {
                    case 0:
                        TextView mTv1 = holder.itemView.findViewById(R.id.tv_province);
                        mTv1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                expandOrcollapse(holder.getAdapterPosition());
                            }
                        });
                        break;
                    case 1:
                        TextView mTv2 = holder.itemView.findViewById(R.id.tv_city);
                        mTv2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                expandOrcollapse(holder.getAdapterPosition());
                            }
                        });
                        break;
                    case 2:
                        break;
                }
            }
        };
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(mAdapter);
    }
}
