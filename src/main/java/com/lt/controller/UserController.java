package com.lt.controller;

import com.lt.common.exception.ResultEntity;
import com.lt.entity.User;
import com.lt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gaijf
 * @description
 * @date 2019/9/17
 */
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("/user/{id}")
    public ResultEntity<User> getUserById(@PathVariable int id){
        User user = userService.getUserById(id);
        return ResultEntity.success(user);
    }
}
