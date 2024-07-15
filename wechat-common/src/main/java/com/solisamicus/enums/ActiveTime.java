package com.solisamicus.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @Desc: 用户简历活跃度 枚举
 */
public enum ActiveTime {
    just("just",3 * 60 * 60),                        // 3 小时前
    today("today", 24 * 60 * 60),                    // 1 天前
    threeDays("threeDays", 3 * 24 * 60 * 60),        // 3 天前
    thisWeek("thisWeek", 7 * 24 * 60 * 60),          // 7 天前
    thisMonth("thisMonth", 30 * 24 * 60 * 60);       // 30 天前

    public final String active;
    public final Integer times;

    ActiveTime(String active, Integer times) {
        this.active = active;
        this.times = times;
    }

    public static Integer getActiveTimes(String active) {
        if (StringUtils.isBlank(active)) {
            return 0;
        }

        ActiveTime[] times = ActiveTime.values();
        for (ActiveTime at :times) {
            if (at.active.equalsIgnoreCase(active)) {
                return at.times;
            }
        }
        // 如果没有匹配的，则使用默认的thisMonth
        return times[times.length-1].times;
    }
}
