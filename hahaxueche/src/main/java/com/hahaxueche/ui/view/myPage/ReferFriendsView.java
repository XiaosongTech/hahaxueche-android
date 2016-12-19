package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 16/9/21.
 */

public interface ReferFriendsView extends HHBaseView {
    void showMessage(String message);

    void navigateToMyRefer();

    void showShareDialog();

    void initShareData(String shareUrl);
}
