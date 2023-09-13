package com.huo.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果类
 * @param <T>
 */
@Data
public class GeneralResult<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据

    public static <T> GeneralResult<T> success(T object) {
        GeneralResult<T> r = new GeneralResult<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> GeneralResult<T> error(String msg) {
        GeneralResult r = new GeneralResult();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public GeneralResult<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
