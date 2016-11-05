package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.FieldMapView;

import rx.Subscription;

/**
 * Created by wangshirui on 2016/10/26.
 */

public class FieldMapPresenter implements Presenter<FieldMapView> {
    private FieldMapView mFieldMapView;
    private Subscription subscription;
    private Field mField;
    HHBaseApplication application;

    @Override
    public void attachView(FieldMapView view) {
        this.mFieldMapView = view;
        application = HHBaseApplication.get(mFieldMapView.getContext());
    }

    @Override
    public void detachView() {
        this.mFieldMapView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mField = null;
    }

    public Field getField() {
        return mField;
    }

    public void setField(Field field) {
        this.mField = field;
    }
}
