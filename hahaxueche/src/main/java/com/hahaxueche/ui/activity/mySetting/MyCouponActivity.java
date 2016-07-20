package com.hahaxueche.ui.activity.mySetting;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.student.Coupon;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.presenter.signupLogin.SLCallbackListener;
import com.hahaxueche.share.ShareConstants;
import com.hahaxueche.ui.activity.signupLogin.AgreementActivity;
import com.hahaxueche.ui.dialog.BaseAlertDialog;
import com.hahaxueche.ui.dialog.mySetting.ActiveCouponDialog;
import com.hahaxueche.ui.dialog.mySetting.AddCouponDialog;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.tencent.tauth.Tencent;

/**
 * Created by wangshirui on 16/7/13.
 */
public class MyCouponActivity extends MSBaseActivity {
    private TextView mTvHowGetCoupon;//如何获取礼金券文字说明
    private TextView mTvFenqileUsage;//分期乐使用说明
    private TextView mTvContactTel;
    private TextView mTvContactQQ;
    private Tencent mTencent;//QQ
    private ImageButton mIbtnBack;
    private ImageView mIvCoupon;
    private TextView mTvCouponTitle;
    private TextView mTvCouponPremise;
    private TextView mTvCouponContent;
    private TextView mTvCouponActive;
    private TextView mTvCouponStatus;
    private TextView mTvAdd;//添加按钮
    private ProgressDialog pd;//进度框
    private SwipeRefreshLayout mSrlMySetting;//下拉刷新
    private ActiveCouponDialog mActiveCouponDialog;//激活优惠券对话框
    private AddCouponDialog mAddCouponDialog;//添加优惠券对话框
    private BaseAlertDialog mReceiveCouponDialog;//领取优惠券对话框

    private boolean isDisplayFreeTry = true;//是否显示免费试学
    private boolean isDisplayAdd = false;//是否显示添加按钮
    private boolean isRefresh = false;//是否刷新中

    private static final int PERMISSIONS_REQUEST_CELL_PHONE = 601;

    private SharedPreferencesUtil spUtil;
    private Coupon mCoupon;//status 0 1 2 分别对应: 未激活 待领取 已领取(激活)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupon);
        mTencent = Tencent.createInstance(ShareConstants.APP_ID_QQ, MyCouponActivity.this);
        initViews();
        spUtil = new SharedPreferencesUtil(MyCouponActivity.this);
        initEvent();
        refreshStudent();
        loadTextViews();
    }

    private void refreshUI() {
        //判断是否显示免费试学券
        if (spUtil.getUser().getStudent().getCoupons() != null && spUtil.getUser().getStudent().getCoupons().size() > 0) {
            mCoupon = spUtil.getUser().getStudent().getCoupons().get(0);
        }
        if (mCoupon != null && mCoupon.getContent() != null && mCoupon.getContent().size() > 0) {
            isDisplayFreeTry = false;
        }
        //加载优惠券
        loadCoupon();
        //判断是否显示添加按钮
        if (mCoupon == null && !spUtil.getUser().getStudent().hasPurchasedService()) {
            isDisplayAdd = true;
        }
        //加载添加按钮
        loadAddButton();
    }

    private void initViews() {
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mTvHowGetCoupon = Util.instence(this).$(this, R.id.tv_how_get_coupon);
        mTvFenqileUsage = Util.instence(this).$(this, R.id.tv_fenqile_usage);
        mTvContactTel = Util.instence(this).$(this, R.id.tv_contact_tel);
        mTvContactQQ = Util.instence(this).$(this, R.id.tv_contact_qq);
        mIvCoupon = Util.instence(this).$(this, R.id.iv_coupon);
        mTvCouponTitle = Util.instence(this).$(this, R.id.tv_coupon_title);
        mTvCouponPremise = Util.instence(this).$(this, R.id.tv_coupon_premise);
        mTvCouponContent = Util.instence(this).$(this, R.id.tv_coupon_content);
        mTvCouponActive = Util.instence(this).$(this, R.id.tv_coupon_active);
        mTvCouponStatus = Util.instence(this).$(this, R.id.tv_coupon_status);
        mTvAdd = Util.instence(this).$(this, R.id.tv_add);
        mSrlMySetting = Util.instence(this).$(this, R.id.srl_my_coupon);
    }

    private void initEvent() {
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyCouponActivity.this.finish();
            }
        });
        mSrlMySetting.setOnRefreshListener(mRefreshListener);
        mSrlMySetting.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }

    SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!isRefresh) {
                isRefresh = true;
                /*new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mSrlMySetting.setRefreshing(false);
                        refreshStudent();
                        isRefresh = false;

                    }
                }, 1500);*/
                refreshStudent();
            }
        }
    };

    private void loadCoupon() {
        if (isDisplayFreeTry) {
            mIvCoupon.setImageDrawable(ContextCompat.getDrawable(MyCouponActivity.this, R.drawable.ic_ticket));
            mTvCouponTitle.setText("哈哈学车免费试学券");
            mTvCouponPremise.setVisibility(View.GONE);
            mTvCouponContent.setText("使用后我们会致电联系接送事宜,\n优质服务提前体验,试学过程100%免费");
            mTvCouponActive.setText("立即\n使用");
            mTvCouponActive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigateToFreeTry();
                }
            });
            mTvCouponStatus.setVisibility(View.GONE);
        } else {
            mTvCouponTitle.setText(mCoupon.getChannel_name() + "优惠券");
            String content = "";
            for (String perContent : mCoupon.getContent()) {
                content += "- " + perContent + "\n";
            }
            mTvCouponContent.setText(content);
            if (mCoupon.getStatus() == 0) {
                mIvCoupon.setImageDrawable(ContextCompat.getDrawable(MyCouponActivity.this, R.drawable.ic_ticket));
                mTvCouponStatus.setVisibility(View.VISIBLE);
                mTvCouponStatus.setText("未激活");
                mTvCouponStatus.setTextColor(ContextCompat.getColor(MyCouponActivity.this, R.color.haha_green));
                mTvCouponStatus.setBackgroundResource(R.drawable.rect_bg_transparent_bd_green);
                mTvCouponActive.setText("立即\n激活");
                mTvCouponActive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activate();
                    }
                });
            } else if (mCoupon.getStatus() == 1) {
                mIvCoupon.setImageDrawable(ContextCompat.getDrawable(MyCouponActivity.this, R.drawable.ic_ticket));
                mTvCouponStatus.setVisibility(View.VISIBLE);
                mTvCouponStatus.setText("待领取");
                mTvCouponStatus.setTextColor(ContextCompat.getColor(MyCouponActivity.this, R.color.app_theme_color));
                mTvCouponStatus.setBackgroundResource(R.drawable.rect_bg_transparent_bd_appcolor);
                mTvCouponActive.setText("立即\n领取");
                mTvCouponActive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        receive();
                    }
                });
            } else {
                mIvCoupon.setImageDrawable(ContextCompat.getDrawable(MyCouponActivity.this, R.drawable.ic_ticket_gray));
                mTvCouponStatus.setVisibility(View.GONE);
                mTvCouponActive.setText("已\n激活");
                mTvCouponActive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //do nothing
                    }
                });
            }
        }
    }

    private void loadAddButton() {
        if (isDisplayAdd) {
            mTvAdd.setVisibility(View.VISIBLE);
            mTvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addCoupon();
                }
            });
        } else {
            mTvAdd.setVisibility(View.GONE);
        }
    }

    /**
     * 加载部分文字说明
     */
    private void loadTextViews() {
        //礼金券文字说明
        //部分变色 可点击
        CharSequence howToGetCouponStr = getText(R.string.howToGetCoupon);
        SpannableString spHowGetCoupon = new SpannableString(howToGetCouponStr);
        spHowGetCoupon.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(context, AgreementActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(context, R.color.app_theme_color));
                ds.setUnderlineText(false);
                ds.clearShadowLayer();
            }
        }, howToGetCouponStr.length() - 12, howToGetCouponStr.length() - 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spHowGetCoupon.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.haha_blue)), howToGetCouponStr.length() - 12, howToGetCouponStr.length() - 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvHowGetCoupon.setText(spHowGetCoupon);
        mTvHowGetCoupon.setHighlightColor(ContextCompat.getColor(context, R.color.haha_blue));
        mTvHowGetCoupon.setMovementMethod(LinkMovementMethod.getInstance());
        //分期乐文字说明
        CharSequence fenqileUsageStr = getText(R.string.fenqileUsage);
        SpannableString spFenqileUsageStr = new SpannableString(fenqileUsageStr);
        spFenqileUsageStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(context, AgreementActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(context, R.color.app_theme_color));
                ds.setUnderlineText(false);
                ds.clearShadowLayer();
            }
        }, fenqileUsageStr.length() - 12, fenqileUsageStr.length() - 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spFenqileUsageStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.haha_blue)), fenqileUsageStr.length() - 12, fenqileUsageStr.length() - 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvFenqileUsage.setText(spFenqileUsageStr);
        mTvFenqileUsage.setHighlightColor(ContextCompat.getColor(context, R.color.haha_blue));
        mTvFenqileUsage.setMovementMethod(LinkMovementMethod.getInstance());
        //联系客服电话
        mTvContactTel.setText(Html.fromHtml(getResources().getString(R.string.contactTel)));
        mTvContactTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CELL_PHONE);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    // Android version is lesser than 6.0 or the permission is already granted.
                    contactTel();
                }
            }
        });
        mTvContactQQ.setText(Html.fromHtml(getResources().getString(R.string.contactQQ)));
        mTvContactQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactQQ();
            }
        });

    }

    /**
     * 联系客服电话
     */
    private void contactTel() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4000016006"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    private void contactQQ() {
        int ret = mTencent.startWPAConversation(MyCouponActivity.this, ShareConstants.CUSTOMER_SERVICE_QQ, "");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CELL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactTel();
            } else {
                Toast.makeText(this, "请允许拨打电话权限，不然无法直接拨号联系客服", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 跳转到免费试学
     */
    private void navigateToFreeTry() {
        //免费试学URL
        String url = "http://m.hahaxueche.com/free_trial";
        if (spUtil.getUser() != null && spUtil.getUser().getStudent() != null) {
            if (!TextUtils.isEmpty(spUtil.getUser().getStudent().getCity_id())) {
                url += "?city_id=" + spUtil.getUser().getStudent().getCity_id();
            }
            if (!TextUtils.isEmpty(spUtil.getUser().getStudent().getName())) {
                if (url.indexOf("?") > 0) {
                    url += "&name=" + spUtil.getUser().getStudent().getName();
                } else {
                    url += "?name=" + spUtil.getUser().getStudent().getName();
                }
            }
            if (!TextUtils.isEmpty(spUtil.getUser().getStudent().getCell_phone())) {
                if (url.indexOf("?") > 0) {
                    url += "&phone=" + spUtil.getUser().getStudent().getCell_phone();
                } else {
                    url += "?phone=" + spUtil.getUser().getStudent().getCell_phone();
                }
            }

        }
        Log.v("gibxin", "free try url -> " + url);
        openWebView(url);
    }

    /**
     * 激活
     */
    private void activate() {
        if (mActiveCouponDialog == null) {
            mActiveCouponDialog = new ActiveCouponDialog(MyCouponActivity.this, new ActiveCouponDialog.OnFreeTryListener() {
                @Override
                public boolean freeTry() {
                    navigateToFreeTry();
                    return true;
                }
            });
        }
        mActiveCouponDialog.show();
    }

    /**
     * 添加优惠券
     */
    private void addCoupon() {
        if (mAddCouponDialog == null) {
            mAddCouponDialog = new AddCouponDialog(MyCouponActivity.this, new AddCouponDialog.OnAddCouponSaveListener() {
                @Override
                public boolean saveCoupon(String coupon) {
                    Student student = spUtil.getUser().getStudent();
                    slPresenter.completeStuInfo(student.getId(), student.getCity_id(), student.getName(),
                            spUtil.getUser().getSession().getAccess_token(), coupon, new SLCallbackListener<Student>() {
                                @Override
                                public void onSuccess(Student data) {
                                    mAddCouponDialog.dismiss();
                                    Toast.makeText(MyCouponActivity.this, "优惠码添加成功!", Toast.LENGTH_SHORT).show();
                                    refreshStudent();
                                }

                                @Override
                                public void onFailure(String errorEvent, String message) {
                                    Toast.makeText(MyCouponActivity.this, "优惠码错误!", Toast.LENGTH_SHORT).show();
                                }
                            });
                    return true;
                }
            });
        }
        mAddCouponDialog.show();
    }

    private void receive() {
        if (mReceiveCouponDialog == null) {
            mReceiveCouponDialog = new BaseAlertDialog(MyCouponActivity.this, "领取优惠券", "恭喜您获得该优惠券!",
                    "请您尽快前往" + mCoupon.getChannel_name() + "领取该优惠券");
        }
        mReceiveCouponDialog.show();
    }

    /**
     * 重新加载学员信息
     */
    private void refreshStudent() {
        pd = ProgressDialog.show(MyCouponActivity.this, null, "数据加载中，请稍后……");
        this.msPresenter.getStudent(spUtil.getUser().getStudent().getId(), spUtil.getUser().getSession().getAccess_token(),
                new MSCallbackListener<Student>() {
                    @Override
                    public void onSuccess(Student student) {
                        User user = spUtil.getUser();
                        user.setStudent(student);
                        spUtil.setUser(user);
                        refreshUI();
                        if (pd != null) {
                            pd.dismiss();
                        }
                        mSrlMySetting.setRefreshing(false);
                        isRefresh = false;
                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        if (pd != null) {
                            pd.dismiss();
                        }
                        mSrlMySetting.setRefreshing(false);
                        isRefresh = false;
                    }
                });
    }

}
