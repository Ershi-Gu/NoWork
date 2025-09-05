package com.ershi.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.ershi.common.exception.BusinessErrorEnum;
import com.ershi.common.exception.BusinessException;
import com.ershi.common.exception.ErrorEnum;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.*;

/**
 * 断言工具类
 *
 * @author Ershi
 * @date 2024/12/05
 */
public class AssertUtil {

    public static void isTrue(boolean expression, String msg) {
        if (!expression) {
            throwException(msg);
        }
    }

    public static void isTrue(boolean expression, ErrorEnum errorEnum, Object... args) {
        if (!expression) {
            throwException(errorEnum, args);
        }
    }

    public static void isFalse(boolean expression, String msg) {
        if (expression) {
            throwException(msg);
        }
    }

    public static void isFalse(boolean expression, ErrorEnum errorEnum, Object... args) {
        if (expression) {
            throwException(errorEnum, args);
        }
    }

    public static void nonNull(Object obj, String msg) {
        if (Objects.isNull(obj)) {
            throwException(msg);
        }
    }

    public static void nonNull(Object obj, ErrorEnum errorEnum) {
        if (Objects.isNull(obj)) {
            throwException(errorEnum);
        }
    }

    public static void isNotEmpty(Object obj, String msg) {
        if (isEmpty(obj)) {
            throwException(msg);
        }
    }

    public static void isNotEmpty(Object obj, ErrorEnum errorEnum, Object... args) {
        if (isEmpty(obj)) {
            throwException(errorEnum, args);
        }
    }

    public static void isEmpty(Object obj, String msg) {
        if (!isEmpty(obj)) {
            throwException(msg);
        }
    }

    public static void equal(Object o1, Object o2, String msg) {
        if (!ObjectUtil.equal(o1, o2)) {
            throwException(msg);
        }
    }

    public static void equal(Object o1, Object o2, ErrorEnum errorEnum, Object... args) {
        if (!ObjectUtil.equal(o1, o2)) {
            throwException(errorEnum, args);
        }
    }

    public static void notEqual(Object o1, Object o2, String msg) {
        if (ObjectUtil.equal(o1, o2)) {
            throwException(msg);
        }
    }

    public static void notEqual(Object o1, Object o2, ErrorEnum errorEnum, Object... args) {
        if (ObjectUtil.equal(o1, o2)) {
            throwException(errorEnum, args);
        }
    }

    public static void isBlank(String str, String msg) {
        if (StringUtils.isNotBlank(str)) {
                throwException(msg);
            }
        }

        public static void isBlank(String str, ErrorEnum errorEnum, Object... args) {
            if (StringUtils.isNotBlank(str)) {
                throwException(errorEnum, args);
        }
    }

    public static void isNotBlank(String str, String msg) {
        if (StringUtils.isBlank(str)) {
            throwException(msg);
        }
    }

    public static void isNotBlank(String str, ErrorEnum errorEnum, Object... args) {
        if (StringUtils.isBlank(str)) {
            throwException(errorEnum, args);
        }
    }

    private static boolean isEmpty(Object obj) {
        return ObjectUtil.isEmpty(obj);
    }

    private static void throwException(String msg) {
        throwException(null, msg);
    }

    private static void throwException(ErrorEnum errorEnum, Object... arg) {
        if (Objects.isNull(errorEnum)) {
            errorEnum = BusinessErrorEnum.COMMON_ERROR;
        }
        throw new BusinessException(errorEnum.getErrorCode(), MessageFormat.format(errorEnum.getErrorMsg(), arg));
    }

}
