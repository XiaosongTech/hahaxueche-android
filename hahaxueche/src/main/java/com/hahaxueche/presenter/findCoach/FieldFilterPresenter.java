package com.hahaxueche.presenter.findCoach;

import android.text.Html;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.FieldFilterView;
import com.hahaxueche.util.HHLog;

import java.util.ArrayList;

import rx.Subscription;

/**
 * Created by wangshirui on 2016/10/17.
 */

public class FieldFilterPresenter implements Presenter<FieldFilterView> {
    private FieldFilterView mFieldFilterView;
    private Subscription subscription;
    private ArrayList<Field> mSelectFields;
    HHBaseApplication application;

    @Override
    public void attachView(FieldFilterView view) {
        this.mFieldFilterView = view;
        application = HHBaseApplication.get(mFieldFilterView.getContext());
        mFieldFilterView.setHints(Html.fromHtml(mFieldFilterView.getContext().getResources().getString(R.string.field_filter_hints)));
    }

    public void initMap() {
        User user = application.getSharedPrefUtil().getUser();
        int cityId = 0;
        if (user != null && user.student != null) {
            cityId = user.student.city_id;
        }
        mFieldFilterView.initMap(application.getConstants().getFields(cityId));
    }

    @Override
    public void detachView() {
        this.mFieldFilterView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mSelectFields = null;
    }

    public ArrayList<Field> getSelectFields() {
        return mSelectFields;
    }

    public void setSelectFields(ArrayList<Field> fields) {
        this.mSelectFields = fields;
        mFieldFilterView.setSelectFieldText(getSelectFieldText());
    }

    public boolean containsSelectField(Field field) {
        if (mSelectFields == null || mSelectFields.size() < 1) return false;
        for (Field selectField : mSelectFields) {
            if (selectField.id.equals(field.id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param field
     * @return true表示添加，false表示去除
     */
    public boolean selectField(Field field) {
        if (mSelectFields == null) {
            mSelectFields = new ArrayList<>();
            mSelectFields.add(field);
            mFieldFilterView.setSelectFieldText(getSelectFieldText());
            return true;
        }
        if (containsSelectField(field)) {
            removeSelectField(field);
            mFieldFilterView.setSelectFieldText(getSelectFieldText());
            return false;
        } else {
            mSelectFields.add(field);
            mFieldFilterView.setSelectFieldText(getSelectFieldText());
            return true;
        }
    }

    private boolean removeSelectField(Field field) {
        if (mSelectFields == null || mSelectFields.size() < 1) return false;
        for (Field selectField : mSelectFields) {
            if (selectField.id.equals(field.id)) {
                return mSelectFields.remove(selectField);
            }
        }
        return false;
    }

    private String getSelectFieldText() {
        if (mSelectFields != null && mSelectFields.size() > 0) {
            return "查看训练场（已选" + mSelectFields.size() + "）个";
        } else {
            return "查看训练场教练";
        }
    }
}
