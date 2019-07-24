package com.laymat.core.db.dto;

import com.laymat.core.db.entity.User;
import com.laymat.core.db.entity.UserGood;
import lombok.Data;

@Data
public class GetUserAccount {
    private User user;
    private UserGood userGood;
}
