package com.laymat.core.db.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLogin implements Serializable {
    private String uname;
    private String upass;
}
