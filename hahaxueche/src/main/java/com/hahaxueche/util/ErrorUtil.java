package com.hahaxueche.util;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by wangshirui on 16/9/10.
 */
public class ErrorUtil {
    public static boolean isHttp401(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 401;
    }
}
