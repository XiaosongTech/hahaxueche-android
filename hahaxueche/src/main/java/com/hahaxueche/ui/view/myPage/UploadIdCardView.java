package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2016/11/25.
 */

public interface UploadIdCardView extends HHBaseView {
    void showMessage(String message);

    void navigateToUserContract(String pdfUrl, String studentId);

    void setFaceImage(String imageUrl);

    void setFaceBackImage(String imageUrl);
}
