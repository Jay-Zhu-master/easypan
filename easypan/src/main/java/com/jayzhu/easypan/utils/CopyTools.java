package com.jayzhu.easypan.utils;

import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jayzhu
 * @version 1.0.0
 * @createDate 2023年10月09日 14:35:02
 * @packageName com.jayzhu.easypan.utils
 * @className CopyTools
 * @describe TODO
 */
public class CopyTools {
    public static <T, S> List<T> copyList(List<S> sList, Class<T> tClass) {
        List<T> list = new ArrayList<>();
        for (S s : sList) {
            T t = null;
            try {
                t = tClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            BeanUtils.copyProperties(s, t);
            list.add(t);
        }
        return list;
    }

    public static <T, S> T copy(S s, Class<T> tClass) {
        T t = null;
        try {
            t = tClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BeanUtils.copyProperties(s, t);
        return t;
    }
}
