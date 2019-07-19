package com.laymat.core.db.service.impl;

import com.laymat.core.db.entity.UserGood;
import com.laymat.core.db.dao.UserGoodDao;
import com.laymat.core.db.service.UserGoodService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * (TmUsergood)表服务实现类
 *
 * @author laymat
 * @since 2019-07-19 16:11:19
 */
@Service("userGoodService")
public class UserGoodServiceImpl implements UserGoodService {
    @Autowired
    private UserGoodDao userGoodDao;

}