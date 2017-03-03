package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2017/2/18.
 */

public interface StudentReferView extends HHBaseView {
    void showShareDialog();

    void startToShare(int shareType, String shareUrl);

    void showMessage(String message);

    void initShareData(String shareUrl);
}
