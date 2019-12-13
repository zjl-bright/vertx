/*
 * Copyright (c) 2019. zjl. All rights reserved.
 */

package me.zjl.boot.model;

/**
 * 数据状态类型
 *
 * @Auther: zjl
 * @Date: 2019-09-30
 * @Version: 1.0
 */
public enum Status {

    DELETED("已删除", -1),

    DISABLED("已禁用，未激活，无效，无, 不是, 无效, 不共享", 0),

    ENABLED("已启用, 已激活, 有效, 有, 是, 有效, 共享", 1);

    private String describe;

    private Integer value;

    Status(String describe, Integer value) {
        this.describe = describe;
        this.value = value;
    }

    public Integer value() {
        return value;
    }

}
