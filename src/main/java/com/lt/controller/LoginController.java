package com.lt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author gaijf
 * @description
 * @date 2019/11/4
 */
@Controller
public class LoginController {

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String login(){
        return "login.html";
    }

    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String index(){
        return "index.html";
    }

    @RequestMapping(value = "/welcome",method = RequestMethod.GET)
    public String welcome(){
        return "pages/welcome.html";
    }
}
