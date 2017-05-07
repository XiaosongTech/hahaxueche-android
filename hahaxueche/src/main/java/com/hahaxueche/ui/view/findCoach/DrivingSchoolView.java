package com.hahaxueche.ui.view.findCoach;

import android.net.Uri;
import android.text.SpannableString;

import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.user.coach.ClassType;
import com.hahaxueche.model.user.coach.Review;
import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2017/5/7.
 */

public interface DrivingSchoolView extends HHBaseView {
    void showMessage(String message);

    void showNoReview();

    void addReview(Review review);

    void setImage(Uri uri);

    void setName(String drivingSchoolName);

    void setConsultantCount(SpannableString text);

    void setLowestPrice(SpannableString text);

    void setFieldCount(SpannableString text);

    void setPassRate(SpannableString text);

    void setSatisfactionRate(SpannableString text);

    void setCoachCount(SpannableString text);

    void setBio(String bio);

    void addClassType(ClassType classType);

    void setGroupBuyCount(SpannableString text);

    void addFieldView(Field field);

}
