package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.List;

/**
 * Created by wangshirui on 2016/10/26.
 */

public interface MyCoachDetailView extends HHBaseView {
    void showMessage(String message);

    void enableFollow(boolean enable);

    void showFollow(boolean isFollow);

    void enableApplaud(boolean enable);

    void showApplaud(boolean isApplaud);

    void setApplaudCount(int count);

    void startApplaudAnimation();

    void setCoachName(String name);

    void setCoachBio(String bio);

    void setCoachAvatar(String avatarUrl);

    void setCoachImages(List<String> images);

    void setCoachPhone(String phone);

    void setTrainingLocation(String trainingLocation);

    void setPeerCoaches(List<Coach> peerCoaches);

    void setDrivingSchool(String drivingSchool);

    void setCourseName(String courseName);
}
