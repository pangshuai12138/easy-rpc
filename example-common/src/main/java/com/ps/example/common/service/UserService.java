package com.ps.example.common.service;

import com.ps.example.common.model.User;

/**
 * @Description: UserService
 * @Author: pangshuai
 * @Date: 2024/5/12 11:15
 **/
public interface UserService {

    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    User getUser(User user);
}
