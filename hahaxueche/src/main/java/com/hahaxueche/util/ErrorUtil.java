package com.hahaxueche.util;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by wangshirui on 16/9/10.
 */
public class ErrorUtil {
    public static final String INVALID_SESSION = "INVALID SESSION";

    public static boolean isHttp401(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 401;
    }

    public static boolean isHttp422(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 422;
    }

    public static boolean isHttp400(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 400;
    }

    public static boolean isInvalidSession(Throwable error) {
        return error.getMessage().equals(INVALID_SESSION);
    }
}
