package com.hahaxueche.presenter;


/**
 * Created by wangshirui on 16/9/9.
 */
public interface  Presenter<V> {
    void attachView(V view);

    void detachView();

}
