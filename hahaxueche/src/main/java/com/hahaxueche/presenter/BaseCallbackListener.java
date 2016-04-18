package com.hahaxueche.presenter;

/**
 * Created by gibxin on 2016/4/18.
 */
public interface BaseCallbackListener<T> {
    /**
     * 成功时调用，返回数据
     *
     * @param data
     */
    public void onSuccess(T data);

    /**
     * 失败时调用
     *
     * @param errorEvent 错误码
     * @param message    错误信息
     */
    public void onFailure(String errorEvent, String message);
}
