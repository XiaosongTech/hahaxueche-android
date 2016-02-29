package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.signupLogin.CostItem;
import com.hahaxueche.utils.Util;

import java.util.List;

/**
 * 费用明细dialog
 * Created by gibxin on 2016/2/26.
 */
public class FeeDetailDialog extends Dialog implements View.OnClickListener {
    private List<CostItem> mCostItemList;//费用明细列表
    private String mTotalFee;//合计金额
    private Context mContext;
    private String mShowType;
    private LinearLayout llyCostItemList;
    private TextView tvAlreadyKnow;
    private TextView tvTotalCount;
    private TextView tvFeeDialogTitle;
    private TextView tvSurePay;
    private TextView tvFeeCancel;
    private LinearLayout llyPayBottom;
    private OnBtnClickListener mListener;

    public FeeDetailDialog(Context context) {
        super(context);
        mContext = context;
    }

    public interface OnBtnClickListener {
        public void onPay();
    }
    public FeeDetailDialog(Context context, List<CostItem> costItemList, String totalFee, String showType,OnBtnClickListener listener) {
        super(context);
        mContext = context;
        mListener = listener;
        mCostItemList = costItemList;
        mTotalFee = totalFee;
        mShowType = showType;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fee_detail, null);
        setContentView(view);
        llyCostItemList = (LinearLayout) view.findViewById(R.id.lly_cost_item_list);
        tvAlreadyKnow = (TextView) view.findViewById(R.id.tv_already_know);
        tvTotalCount = (TextView) view.findViewById(R.id.tv_total_count);
        tvTotalCount.setText(Util.getMoneyYuan(mTotalFee));
        tvFeeDialogTitle = (TextView) view.findViewById(R.id.tv_fee_dialog_title);
        llyPayBottom = (LinearLayout) view.findViewById(R.id.lly_pay_bottom);
        tvSurePay = (TextView) view.findViewById(R.id.tv_sure_pay);
        tvFeeCancel = (TextView) view.findViewById(R.id.tv_fee_cancel);
        loadCostItemList();
        loadBottomBtn();
        initEvents();
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        lp.x = 100; // 新位置X坐标
        lp.y = 150; // 新位置Y坐标
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }

    /**
     * 加载收费项目
     */
    private void loadCostItemList() {
        if (mCostItemList != null && mCostItemList.size() > 0) {
            for (CostItem costItem : mCostItemList) {
                RelativeLayout rly = new RelativeLayout(mContext);
                LinearLayout.LayoutParams flParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                rly.setPadding(Util.instence(mContext).dip2px(15), Util.instence(mContext).dip2px(10), Util.instence(mContext).dip2px(15),
                        Util.instence(mContext).dip2px(10));
                rly.setLayoutParams(flParams);
                //收费项目名称
                TextView tvCostName = new TextView(mContext);
                RelativeLayout.LayoutParams tvNameParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                tvNameParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                tvCostName.setLayoutParams(tvNameParams);
                tvCostName.setText(costItem.getName());
                tvCostName.setTextColor(mContext.getResources().getColor(R.color.fCTxtGray));
                tvCostName.setTextSize(16);
                rly.addView(tvCostName);
                //收费金额
                RelativeLayout.LayoutParams tvCostParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                TextView tvCostFee = new TextView(mContext);
                tvCostParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                tvCostFee.setLayoutParams(tvCostParams);
                if (costItem.getCost().equals("0")) {
                    tvCostFee.setText("免费赠送");
                } else {
                    tvCostFee.setText(Util.getMoneyYuan(costItem.getCost()));
                }
                tvCostFee.setTextColor(mContext.getResources().getColor(R.color.app_theme_color));
                tvCostFee.setTextSize(16);
                rly.addView(tvCostFee);
                llyCostItemList.addView(rly);
            }
        }
    }

    /**
     * 加载底部显示内容
     */
    private void loadBottomBtn() {
        if (mShowType != null) {
            if (mShowType.equals("1")) {
                tvAlreadyKnow.setVisibility(View.VISIBLE);
                tvFeeDialogTitle.setText(mContext.getResources().getString(R.string.fCTakeCertPrice));
            } else if (mShowType.equals("2")) {
                llyPayBottom.setVisibility(View.VISIBLE);
                tvFeeDialogTitle.setText(mContext.getResources().getString(R.string.fCPayDetail));
            }
        }
    }

    /**
     * 事件初始化
     */
    private void initEvents() {
        tvAlreadyKnow.setOnClickListener(this);
        tvFeeCancel.setOnClickListener(this);
        tvSurePay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_already_know:
                dismiss();
                break;
            case R.id.tv_fee_cancel:
                dismiss();
                break;
            case R.id.tv_sure_pay:
                if (mListener != null) {
                    mListener.onPay();
                }
                break;
        }
    }
}
