package com.wanghao.mvc;

import com.wanghao.mvc.annotation.Autowired;
import com.wanghao.mvc.annotation.Service;

/**
 * @author wanghao
 * @description
 * @date 2/25/20 3:35 PM
 */
@Service("nameService")
public class NameService {

    @Autowired
    private TestService testService;

    public String getName(String name) {
        System.out.println("testService is null: " + (testService == null));
        return name;
    }
}
