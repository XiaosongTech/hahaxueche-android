package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.FieldMapView;

import rx.Subscription;

/**
 * Created by wangshirui on 2016/10/26.
 */

public class FieldMapPresenter extends HHBasePresenter implements Presenter<FieldMapView> {
    private FieldMapView mView;
    private Subscription subscription;
    private Field mField;
    HHBaseApplication application;

    @Override
    public void attachView(FieldMapView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
    }

    @Override
    public void detachView() {
        this.mView = null;
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
