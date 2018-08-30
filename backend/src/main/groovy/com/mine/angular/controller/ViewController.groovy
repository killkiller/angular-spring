package com.mine.angular.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Created by h00421015 on 2018/8/30.
 * 将所有Angular Router定义的url在此注册，将其重定向SPA
 *
 */
@Controller
class ViewController {

    @RequestMapping(path = ['/create', '/env/{id}'])
    def index() {
        return "forward:/index.html";
    }

}
