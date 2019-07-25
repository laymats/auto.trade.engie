package com.laymat.core.db.dto.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import springfox.documentation.annotations.ApiIgnore;

import java.io.Serializable;

/**
 * 统一会话信息记录dto
 * @author dell
 */
@Data
@ApiIgnore
public class BaseUserSession implements Serializable {
    @JsonIgnore
    private Long userId;
}
