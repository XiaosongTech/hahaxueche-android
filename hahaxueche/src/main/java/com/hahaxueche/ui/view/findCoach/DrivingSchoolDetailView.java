package com.hahaxueche.ui.view.findCoach;

import android.net.Uri;
import android.text.SpannableString;

import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.user.coach.ClassType;
import com.hahaxueche.model.user.coach.Review;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.List;

/**
 * Created by wangshirui on 2017/5/7.
 */

public interface DrivingSchoolDetailView extends HHBaseView {
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

    void addFieldView(Field field);

    void startToShare(int shareType, String shortenUrl);

    void initShareData(DrivingSchool mDrivingSchool);

    void setCommentCount(String text);

    void setGroupBuyPhone(String cellPhone);

    void navigateToMapSearch(int drivingSchoolId);
}
