package com.laymat.core.db.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Date;
import java.util.HashSet;

public class SimpleSNBuilder {
    private static HashSet<String> _snHashSet = new HashSet<String>();

    private static final Object lockHelper = new Object();

    public static String createrSn(String prefix) {
        //{订单类型4位}{yyMMdd}{123456}
        if (StrUtil.isEmpty(prefix)) {
            prefix = "";
        }

        var sn = "";
        synchronized (lockHelper) {
            sn = String.format("%s%s%s", prefix, DateUtil.format(new Date(), "yyMMddHHmm"), RandomUtil.randomString("0123456789", 6));
            while (!_snHashSet.contains(sn)) {
                sn = String.format("%s%s%s", prefix, DateUtil.format(new Date(), "yyMMddHHmm"), RandomUtil.randomString("0123456789", 6));
                _snHashSet.add(sn);
            }
        }
        return sn;
    }
}
