package com.ps.example.provider;

import com.ps.example.common.model.User;
import com.ps.example.common.service.UserService;
/**
 * @Description: 用户服务实现类
 * @Author: pangshuai
 * @Date: 2024/5/12 11:21
 **/
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
