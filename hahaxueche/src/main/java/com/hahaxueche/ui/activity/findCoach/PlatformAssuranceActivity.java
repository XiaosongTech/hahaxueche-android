package com.hahaxueche.ui.activity.findCoach;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.user.coach.AssuranceProperty;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.adapter.findCoach.AssuranceAdapter;
import com.hahaxueche.ui.widget.recyclerView.DividerItemDecoration;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/12/26.
 */

public class PlatformAssuranceActivity extends HHBaseActivity {
    @BindView(R.id.lv_assurance)
    RecyclerView mLvAssurance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_assurance);
        ButterKnife.bind(this);
        initActionBar();
        Intent intent = getIntent();
        loadAssurances(intent.getBooleanExtra("isGolden", false), intent.getBooleanExtra("isCashPledge", false));
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("平台保障");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlatformAssuranceActivity.this.finish();
            }
        });
    }

    public void loadAssurances(boolean isGolden, boolean isCashPledge) {
        ArrayList<AssuranceProperty> list = new ArrayList<>();
        if (isGolden) {
            list.add(new AssuranceProperty(R.drawable.ic_jinpaijiaolian2, "教练认证", "教练在平台教学数据与学员评价名列前茅，已通过平台金牌教练认证及培训。", "已认证"));
        } else {
            list.add(new AssuranceProperty(R.drawable.ic_jiaolianrenzheng2, "教练认证", "该教练已完成实名认证，已备案。教学技术专业，爱岗敬业，热爱驾培行业，收费透明。", "已备案"));
        }
        if (!isCashPledge) {
            list.add(new AssuranceProperty(R.drawable.ic_mianfeishixue2, "免费试学", "在平台注册的用户都可享有教练一次免费试学的机会，和教练面对面交流，打消你报名前的疑虑。", "已开通"));
        }
        list.add(new AssuranceProperty(R.drawable.ic_fenduandakuan2, "分段打款", "教练支持使用分阶段打款，学费将由平台提供担保，在用户确认打款后由平台打款给教练。", "已开通"));
        list.add(new AssuranceProperty(R.drawable.ic_fenqidakuan2, "分期付款", "该教练支持使用分期乐等分期付款。", "已开通"));
        list.add(new AssuranceProperty(R.drawable.ic_cheliangbaoxian2, "车辆保险", "该教练拥有可用于教学的车辆，并提供用于教学的训练场地。教练教学所用车辆已购买相关保险。", "已备案"));
        if (isCashPledge) {
            list.add(new AssuranceProperty(R.drawable.ic_xianxiangpeifu2, "先行赔付", "该教练已缴纳平台保证金。在教学过程中如果产生任何纠纷，在确定为教练责任的情况下平台先行赔付。", "已开通"));
        }
        mLvAssurance.setLayoutManager(new LinearLayoutManager(this));
        mLvAssurance.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST,
                Utils.instence(this).dip2px(20)));
        AssuranceAdapter adapter = new AssuranceAdapter(this, list);
        mLvAssurance.setAdapter(adapter);
    }
}
