package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.responseList.ReviewResponseList;
import com.hahaxueche.model.user.coach.ClassType;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.List;

/**
 * Created by wangshirui on 16/10/5.
 */

public interface CoachDetailView extends HHBaseView {
    void showMessage(String message);

    void showNoReview(String coachName);

    void showReviews(ReviewResponseList responseList);

    void enableFollow(boolean enable);

    void showFollow(boolean isFollow);

    void enableApplaud(boolean enable);

    void showApplaud(boolean isApplaud);

    void setApplaudCount(int count);

    void startApplaudAnimation();

    void navigateToPurchaseCoach(Coach coach, ClassType classType);

    void initShareData(Coach coach);

    void alertToLogin(String alertMessage);

    void setCoachBadge(boolean isGolden);

    void setPayBadge(boolean isCashPledge);

    void navigationToPlatformAssurance(boolean isGolden, boolean isCashPledge);

    void navigateToStudentRefer();

    void navigateToReferFriends();

    void setLicenseTab(boolean isShowTitleC1, boolean isShowTitleC2);

    void addClassType(ClassType classType);

    void clearClassType();

    void showC1Tab(boolean isLight);

    void showC2Tab(boolean isLight);

    void setCoachName(String name);

    void setCoachBio(String bio);

    void setCoachAvatar(String avatarUrl);

    void setCoachImages(List<String> images);

    void setCoachGolden(boolean isGolden);

    void setCoachPledge(boolean hasPledge);

    void setCoachSatisfaction(String satisfactionRate);

    void setCoachSkillLevel(String skillLevel);

    void setCoachAveragePassDays(String passDays);

    void setCoachPassRate(String passRate);

    void setTrainingLocation(String trainingLocation);

    void setPeerCoaches(List<Coach> peerCoaches);

    void setCommentCount(String commentCount);

    void setCoachAverageRating(String averageRating);

    void setDrivingSchool(String drivingSchool);

    void startToShare(int shareType, String shortenUrl);
}
