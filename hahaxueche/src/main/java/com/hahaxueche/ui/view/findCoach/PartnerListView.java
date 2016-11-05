package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.user.coach.Partner;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/10/19.
 */

public interface PartnerListView extends HHBaseView{
    void setPullLoadEnable(boolean enable);

    /**
     * 刷新教练列表
     *
     * @param partnerArrayList
     */
    void refreshPartnerList(ArrayList<Partner> partnerArrayList);

    /**
     * 加载更多教练
     *
     * @param partnerArrayList
     */
    void addMorePartnerList(ArrayList<Partner> partnerArrayList);

    void showMessage(String message);
}
