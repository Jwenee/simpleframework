package org.simpleframework.util;

import java.util.Collection;
import java.util.Map;

public class ValidationUtil {

    // 判断集合是否为null或size为0
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    // 判断String是否为null或为空字符串
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static boolean isEmpty(Object[] objArr) {
        return objArr == null || objArr.length == 0;
    }

    public static boolean isEmpty(Map<?,?> map) {
        return map == null || map.isEmpty();
    }
}
