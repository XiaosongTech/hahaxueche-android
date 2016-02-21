package com.hahaxueche.presenter.findCoach;

/**
 * Created by gibxin on 2016/2/21.
 */
public interface FCCallbackListener <T>{
    /**
     * 成功时调用，返回数据
     * @param data
     */
    public void onSuccess(T data);

    /**
     * 失败时调用
     * @param errorEvent 错误码
     * @param message    错误信息
     */
    public void onFailure(String errorEvent, String message);
}
