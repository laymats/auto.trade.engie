package com.laymat.core.db.dto.base;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * 统一分页查询dto
 *
 * @author dell
 */
@Data
public class QueryBase extends BaseUserSession {
    private Integer size;
    private Integer page;

    public Page getPage() {
        if (size == null || size == 0) {
            this.size = 10;
        }
        if (page == null || page == 0) {
            this.page = 1;
        }
        return new Page(this.page, this.size);
    }

    public boolean checkInt(Integer c) {
        if (c != null && c != 0) {
            return true;
        }
        return false;
    }

    public boolean checkStr(String s) {
        if (!StrUtil.isBlank(s)) {
            return true;
        } else {
            return false;
        }
    }
}
