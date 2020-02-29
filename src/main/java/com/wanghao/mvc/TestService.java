package com.wanghao.mvc;

import com.wanghao.mvc.annotation.Autowired;
import com.wanghao.mvc.annotation.Service;

/**
 * @author wanghao7@corp.netease.com
 * @description
 * @date 2/29/20 8:31 PM
 */
@Service("testService")
public class TestService {

    @Autowired
    private NameService nameService;

    public void test() {
        System.out.println("test(): " + nameService.getName("wanghao"));
    }
}
