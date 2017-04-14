package com.hahaxueche.ui.fragment.homepage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Banner;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.student.Contact;
import com.hahaxueche.presenter.homepage.HomepagePresenter;
import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.base.BaseWebViewActivity;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.activity.community.ExamLibraryActivity;
import com.hahaxueche.ui.activity.findCoach.PaySuccessActivity;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.activity.myPage.MyInsuranceActivity;
import com.hahaxueche.ui.activity.myPage.PurchaseInsuranceActivity;
import com.hahaxueche.ui.activity.myPage.ReferFriendsActivity;
import com.hahaxueche.ui.activity.myPage.StudentReferActivity;
import com.hahaxueche.ui.activity.myPage.UploadIdCardActivity;
import com.hahaxueche.ui.dialog.BaseAlertDialog;
import com.hahaxueche.ui.dialog.login.CityChoseDialog;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.homepage.HomepageView;
import com.hahaxueche.ui.widget.bannerView.NetworkImageHolderView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.RequestCode;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wangshirui on 16/9/13.
 */
public class HomepageFragment extends HHBaseFragment implements ViewPager.OnPageChangeListener, OnItemClickListener, HomepageView {
    private MainActivity mActivity;
    private HomepagePresenter mPresenter;

    @BindView(R.id.crl_main)
    CoordinatorLayout mClyMain;
    @BindView(R.id.iv_find_driving_school)
    SimpleDraweeView mIvFindDrivingSchool;
    @BindView(R.id.iv_find_coach)
    SimpleDraweeView mIvFindCoach;
    @BindView(R.id.rcy_hot_driving_school)
    RecyclerView mRcyHotDrivingSchool;
    @BindView(R.id.rcy_near_coach)
    RecyclerView mRcyNearCoach;

    private CityChoseDialog mCityChoseDialog;
    private List<DrivingSchool> mHotDrivingSchool;
    private ArrayList<Coach> mNearCoaches;
    private DrivingSchoolAdapter mDrivingSchoolAdapter;
    private NearCoachAdapter mNearCoachAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mPresenter = new HomepagePresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        ButterKnife.bind(this, view);
        mPresenter.attachView(this);
        Uri uriFindSchool = Uri.parse("res://com.hahaxueche)/" + R.drawable.bt_chooseschool);
        DraweeController dcFindSchool =
                Fresco.newDraweeControllerBuilder()
                        .setUri(uriFindSchool)
                        .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
                        .build();
        mIvFindDrivingSchool.setController(dcFindSchool);
        GenericDraweeHierarchy hyFindDrivingSchool = mIvFindDrivingSchool.getHierarchy();
        hyFindDrivingSchool.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        Uri uriFindCoach = Uri.parse("res://com.hahaxueche)/" + R.drawable.bt_choosecoach);
        DraweeController dcFindCoach =
                Fresco.newDraweeControllerBuilder()
                        .setUri(uriFindCoach)
                        .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
                        .build();
        mIvFindCoach.setController(dcFindCoach);
        GenericDraweeHierarchy hyFindCoach = mIvFindCoach.getHierarchy();
        hyFindCoach.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);

        mPresenter.getCityConstants();
        mPresenter.getNearCoaches();

        if (mPresenter.isNeedUpdate()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    (mActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || mActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || mActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            || mActivity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_CONTACTS},
                        RequestCode.PERMISSIONS_REQUEST_SDCARD_CONTACTS_HOMEPAGE);
            } else {
                mPresenter.alertToUpdate(getContext());
                readContacts();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mActivity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, RequestCode.PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {
                readContacts();
            }
        }

        return view;
    }

    @Override
    public void navigateToReferFriends() {
        startActivity(new Intent(getContext(), ReferFriendsActivity.class));
    }

    @Override
    public void navigateToExamLibrary() {
        startActivityForResult(new Intent(getContext(), ExamLibraryActivity.class), RequestCode.REQUEST_CODE_EXAM_LIBRARY);
    }

    @Override
    public void navigateToStudentRefer() {
        startActivity(new Intent(getContext(), StudentReferActivity.class));
    }

    @Override
    public void navigateToMyInsurance() {
        startActivityForResult(new Intent(getContext(), MyInsuranceActivity.class), RequestCode.REQUEST_CODE_MY_INSURANCE);
    }

    @Override
    public void loadHotDrivingSchools(List<DrivingSchool> drivingSchoolList) {
        mHotDrivingSchool = drivingSchoolList;
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置布局管理器
        mRcyHotDrivingSchool.setLayoutManager(layoutManager);
        mDrivingSchoolAdapter = new DrivingSchoolAdapter();
        mRcyHotDrivingSchool.setAdapter(mDrivingSchoolAdapter);
    }

    @Override
    public void loadNearCoaches(ArrayList<Coach> coaches) {
        mNearCoaches = coaches;
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置布局管理器
        mRcyNearCoach.setLayoutManager(layoutManager);
        mNearCoachAdapter = new NearCoachAdapter();
        mRcyNearCoach.setAdapter(mNearCoachAdapter);
    }

    @OnClick({R.id.iv_procedure,
            R.id.tv_online_ask,
            R.id.tv_group_buy,
            R.id.tv_test_lib})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_procedure:
                mPresenter.openProcedure();
                break;
            case R.id.tv_online_ask:
                mPresenter.onlineAsk();
                break;
            case R.id.tv_group_buy:
                mPresenter.openGroupBuy();
                break;
            case R.id.tv_test_lib:
                mPresenter.clickTestLib();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContacts();
            }
        } else if (requestCode == RequestCode.PERMISSIONS_REQUEST_SDCARD_CONTACTS_HOMEPAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                mPresenter.alertToUpdate(getContext());
            } else {
                showMessage("请允许读写sdcard权限，不然无法下载最新的安装包");
            }
            if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                readContacts();
            }
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mClyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showCityChoseDialog() {
        if (mCityChoseDialog == null) {
            mCityChoseDialog = new CityChoseDialog(getContext(), new CityChoseDialog.onConfirmListener() {
                @Override
                public boolean selectCity(City city) {
                    if (city != null) {
                        mPresenter.selectCity(city.id);
                    }
                    return true;
                }
            });
        }
        mCityChoseDialog.show();
    }

    @Override
    public void openWebView(String url) {
        Intent intent = new Intent(getContext(), BaseWebViewActivity.class);
        Bundle bundle = new Bundle();
        HHLog.v("webview url -> " + url);
        bundle.putString("url", url);
        intent.putExtras(bundle);
        startActivityForResult(intent, RequestCode.REQUEST_CODE_WEBVIEW);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.REQUEST_CODE_WEBVIEW) {
            if (resultCode == RESULT_OK && null != data) {
                if (data.getBooleanExtra("peifubao", false)) {
                    navigateToMyInsurance();
                } else {
                    int tab = data.getIntExtra("showTab", 1);
                    mActivity.selectTab(tab);
                }
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_EXAM_LIBRARY) {
            if (resultCode == RESULT_OK && null != data) {
                if (data.getBooleanExtra("toFindCoach", false)) {
                    mActivity.selectTab(1);
                }
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_MY_INSURANCE) {
            if (resultCode == RESULT_OK && null != data) {
                if (data.getBooleanExtra("toUploadInfo", false)) {
                    Intent intent = new Intent(getContext(), UploadIdCardActivity.class);
                    intent.putExtra("isFromPaySuccess", false);
                    intent.putExtra("isInsurance", true);
                    startActivityForResult(intent, RequestCode.REQUEST_CODE_UPLOAD_ID_CARD);
                } else if (data.getBooleanExtra("toFindCoach", false)) {
                    mActivity.selectTab(1);
                } else {
                    Intent intent = new Intent(getContext(), PurchaseInsuranceActivity.class);
                    intent.putExtra("insuranceType", data.getIntExtra("insuranceType", Common.PURCHASE_INSURANCE_TYPE_WITHOUT_COACH));
                    startActivityForResult(intent, RequestCode.REQUEST_CODE_PURCHASE_INSURANCE);
                }
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_PURCHASE_INSURANCE) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(getContext(), PaySuccessActivity.class);
                intent.putExtra("isPurchasedInsurance", true);
                intent.putExtra("isFromPurchaseInsurance", true);
                startActivityForResult(intent, RequestCode.REQUEST_CODE_PAY_SUCCESS);
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_PAY_SUCCESS) {
            Intent intent = new Intent(getContext(), UploadIdCardActivity.class);
            intent.putExtra("isFromPaySuccess", false);
            intent.putExtra("isInsurance", true);
            startActivityForResult(intent, RequestCode.REQUEST_CODE_UPLOAD_ID_CARD);
        } else if (requestCode == RequestCode.REQUEST_CODE_UPLOAD_ID_CARD) {
            mPresenter.toReferFriends();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onItemClick(int i) {
        mPresenter.bannerClick(i);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    /**
     * 读取通讯录
     */
    private void readContacts() {
        Cursor cursor = null;
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            cursor = mActivity.getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Contact contact = new Contact();
                    contact.name = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    contact.number = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "").replace("-", "");
                    contacts.add(contact);
                }
            }
        } catch (Exception e) {
            HHLog.e(e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            mPresenter.uploadContacts(contacts);
        }
    }

    class DrivingSchoolAdapter extends RecyclerView.Adapter<DrivingSchoolAdapter.DrivingSchoolHolder> {

        @Override
        public DrivingSchoolHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            DrivingSchoolHolder holder = new DrivingSchoolHolder(LayoutInflater.from(
                    getContext()).inflate(R.layout.adapter_hot_driving_school, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(DrivingSchoolHolder holder, int position) {
            String text = Utils.getMoney(mHotDrivingSchool.get(position).lowest_price) + "起";
            SpannableString ss = new SpannableString(text);
            ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.haha_gray)), text.indexOf("起"), text.indexOf("起") + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new AbsoluteSizeSpan(Utils.instence(getContext()).sp2px(12)), text.indexOf("起"), text.indexOf("起") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.mTvPrice.setText(ss);
            holder.mIvAvatar.setImageURI(mHotDrivingSchool.get(position).avatar);
        }

        @Override
        public int getItemCount() {
            return mHotDrivingSchool.size();
        }

        class DrivingSchoolHolder extends RecyclerView.ViewHolder {

            TextView mTvPrice;
            SimpleDraweeView mIvAvatar;

            public DrivingSchoolHolder(View view) {
                super(view);

                mTvPrice = (TextView) view.findViewById(R.id.tv_price);
                mIvAvatar = (SimpleDraweeView) view.findViewById(R.id.iv_avatar);
            }
        }
    }

    class NearCoachAdapter extends RecyclerView.Adapter<NearCoachAdapter.NearCoachHolder> {

        @Override
        public NearCoachHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            NearCoachHolder holder = new NearCoachHolder(LayoutInflater.from(
                    getContext()).inflate(R.layout.adapter_near_coach, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(NearCoachHolder holder, int position) {
            Coach coach = mNearCoaches.get(position);
            holder.mTvPrice.setText(Utils.getMoney(coach.coach_group.training_cost));
            holder.mIvAvatar.setImageURI(coach.avatar);
            holder.mTvName.setText(coach.name);
            holder.mTvLocation.setText("洪山区");
        }

        @Override
        public int getItemCount() {
            return mNearCoaches.size();
        }

        class NearCoachHolder extends RecyclerView.ViewHolder {

            TextView mTvPrice;
            SimpleDraweeView mIvAvatar;
            TextView mTvName;
            TextView mTvLocation;

            public NearCoachHolder(View view) {
                super(view);
                mTvPrice = (TextView) view.findViewById(R.id.tv_price);
                mIvAvatar = (SimpleDraweeView) view.findViewById(R.id.iv_avatar);
                mTvName = (TextView) view.findViewById(R.id.tv_name);
                mTvLocation = (TextView) view.findViewById(R.id.tv_location);
            }
        }
    }
}
