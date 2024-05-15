package com.ps.example.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 用户(对象需要实现序列化接口，为后续网络传输序列化提供支持)
 * @Author: pangshuai
 * @Date: 2024/5/12 11:13
 **/
@Data
public class User implements Serializable {

    private String name;
}