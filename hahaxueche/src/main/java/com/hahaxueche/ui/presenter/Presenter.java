package com.hahaxueche.ui.presenter;


/**
 * Created by wangshirui on 16/9/9.
 */
public interface  Presenter<V> {
    abstract void attachView(V view);

    abstract void detachView();

}
