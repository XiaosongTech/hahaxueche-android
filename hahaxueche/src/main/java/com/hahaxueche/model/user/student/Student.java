package com.hahaxueche.model.user.student;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.hahaxueche.model.payment.BankCard;
import com.hahaxueche.model.payment.Coupon;
import com.hahaxueche.model.payment.PaymentStage;
import com.hahaxueche.model.payment.PurchasedService;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.model.user.identity.IdCard;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/9/10.
 */
public class Student implements Parcelable {
    public String id;
    public String cell_phone;
    public String name;
    public int city_id = -1;
    public String user_id;
    public String avatar;
    public String current_coach_id;
    public ArrayList<PurchasedService> purchased_services;
    public int phase;
    public int current_course;
    public int bonus_balance;
    public BankCard bank_card;
    public ArrayList<Coupon> coupons;
    public ArrayList<Voucher> vouchers;
    public String agreement_url;
    public IdCard identity_card;
    public String user_identity_id;
    public boolean is_sales_agent;
    public InsuranceOrder insurance_order;
    public int prepaid_amount;

    public Student() {

    }

    protected Student(Parcel in) {
        id = in.readString();
        cell_phone = in.readString();
        name = in.readString();
        city_id = in.readInt();
        user_id = in.readString();
        avatar = in.readString();
        current_coach_id = in.readString();
        phase = in.readInt();
        current_course = in.readInt();
        bonus_balance = in.readInt();
        bank_card = in.readParcelable(BankCard.class.getClassLoader());
        coupons = in.createTypedArrayList(Coupon.CREATOR);
        vouchers = in.createTypedArrayList(Voucher.CREATOR);
        agreement_url = in.readString();
        identity_card = in.readParcelable(IdCard.class.getClassLoader());
        user_identity_id = in.readString();
        is_sales_agent = in.readByte() != 0;
        insurance_order = in.readParcelable(InsuranceOrder.class.getClassLoader());
        prepaid_amount = in.readInt();
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    public String getStudentPhaseLabel() {
        if (isPurchasedService()) {
            return "目前阶段：" + getPaymentStageLabel();
        } else {
            return "目前阶段：未付款";
        }
    }

    public boolean isPurchasedService() {
        return !TextUtils.isEmpty(current_coach_id) && purchased_services != null && purchased_services.size() > 0;
    }

    private PurchasedService getCurrentPS() {
        return isPurchasedService() ? purchased_services.get(0) : null;
    }

    public boolean isCompleted() {
        return (city_id >= 0 && !TextUtils.isEmpty(name));
    }

    public int getAccountBalance() {
        if (isPurchasedService()) {
            return getCurrentPS().unpaid_amount;
        } else {
            return bonus_balance;
        }
    }

    public String getPaymentStageLabel() {
        if (isPurchasedService()) {
            ArrayList<PaymentStage> paymentStageList = getCurrentPS().payment_stages;
            String tempPaymentStageStr = "";
            for (PaymentStage paymentStage : paymentStageList) {
                if (paymentStage.stage_number == getCurrentPS().current_payment_stage) {
                    tempPaymentStageStr = paymentStage.stage_name;
                    break;
                }
            }
            if (TextUtils.isEmpty(tempPaymentStageStr)) {
                if (paymentStageList.size() + 1 == getCurrentPS().current_payment_stage) {
                    tempPaymentStageStr = "已拿证";
                }
            }
            return tempPaymentStageStr;
        } else {
            return "未选择教练";
        }
    }

    public boolean isSigned() {
        return !TextUtils.isEmpty(agreement_url);
    }

    public boolean isUploadedIdInfo() {
        return identity_card != null && !TextUtils.isEmpty(identity_card.num);
    }

    /**
     * 是否已购买保险
     *
     * @return
     */
    public boolean isPurchasedInsurance() {
        return insurance_order != null && !TextUtils.isEmpty(insurance_order.paid_at);
    }

    /**
     * 是否已投保成功
     *
     * @return
     */
    public boolean isUploadedInsurance() {
        return insurance_order != null && !TextUtils.isEmpty(insurance_order.policy_no);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(cell_phone);
        dest.writeString(name);
        dest.writeInt(city_id);
        dest.writeString(user_id);
        dest.writeString(avatar);
        dest.writeString(current_coach_id);
        dest.writeInt(phase);
        dest.writeInt(current_course);
        dest.writeInt(bonus_balance);
        dest.writeParcelable(bank_card, flags);
        dest.writeTypedList(coupons);
        dest.writeTypedList(vouchers);
        dest.writeString(agreement_url);
        dest.writeParcelable(identity_card, flags);
        dest.writeString(user_identity_id);
        dest.writeByte((byte) (is_sales_agent ? 1 : 0));
        dest.writeParcelable(insurance_order, flags);
        dest.writeInt(prepaid_amount);
    }
}
