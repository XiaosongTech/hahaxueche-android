package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.model.payment.WithdrawRecord;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/11/1.
 */

public interface WithdrawRecordsView extends HHBaseView {
    void loadWithdrawRecords(ArrayList<WithdrawRecord> withdrawRecords);
}
