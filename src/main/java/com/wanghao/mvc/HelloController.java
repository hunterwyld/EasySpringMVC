package com.wanghao.mvc;

import com.wanghao.mvc.annotation.Autowired;
import com.wanghao.mvc.annotation.Controller;
import com.wanghao.mvc.annotation.RequestMapping;

/**
 * @author wanghao
 * @description
 * @date 2/25/20 3:26 PM
 */
@Controller
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    private NameService nameService;

    @RequestMapping("/to")
    public String sayHelloTo() {
        String ret = "say hello to " + nameService.getName();
        System.out.println(ret);
        return ret;
    }

    @RequestMapping("/from")
    public String sayHelloFrom() {
        String ret = "say hello from " + nameService.getName();
        System.out.println(ret);
        return ret;
    }
}
