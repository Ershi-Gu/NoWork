package com.ershi.common.resp;

import com.ershi.common.exception.ErrorEnum;
import lombok.Data;

/**
 * 通用返回体
 *
 * @author Ershi
 * @date 2024/12/04
 */
@Data
public class ApiResult<T> {

    /** 成功标识 true or false */
    private Boolean success;
    /** 错误码 */
    private Integer errCode;
    /** 错误信息 */
    private String errMsg;
    /** 返回数据 */
    private T data;

    /**
     * 无携带数据的成功返回
     *
     * @return {@link ApiResult}<{@link T}>
     */
    public static <T> ApiResult<T> success() {
        ApiResult<T> result = new ApiResult<T>();
        result.setData(null);
        result.setSuccess(Boolean.TRUE);
        return result;
    }

    /**
     * 携带数据的成功返回
     *
     * @param data
     * @return {@link ApiResult}<{@link T}>
     */
    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<T>();
        result.setData(data);
        result.setSuccess(Boolean.TRUE);
        return result;
    }

    /**
     * 失败返回
     *
     * @param code
     * @param msg
     * @return {@link ApiResult}<{@link T}>
     */
    public static <T> ApiResult<T> fail(Integer code, String msg) {
        ApiResult<T> result = new ApiResult<T>();
        result.setSuccess(Boolean.FALSE);
        result.setErrCode(code);
        result.setErrMsg(msg);
        return result;
    }

    /**
     * 失败返回
     *
     * @param errorEnum 异常枚举类
     * @return {@link ApiResult}<{@link T}>
     */
    public static <T> ApiResult<T> fail(ErrorEnum errorEnum) {
        ApiResult<T> result = new ApiResult<T>();
        result.setSuccess(Boolean.FALSE);
        result.setErrCode(errorEnum.getErrorCode());
        result.setErrMsg(errorEnum.getErrorMsg());
        return result;
    }

    /**
     * 判断是否成功
     *
     * @return boolean
     */
    public boolean isSuccess() {
        return this.success;
    }
}
