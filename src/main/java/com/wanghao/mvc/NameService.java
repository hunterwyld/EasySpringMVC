package com.wanghao.mvc;

import com.wanghao.mvc.annotation.Service;

/**
 * @author wanghao
 * @description
 * @date 2/25/20 3:35 PM
 */
@Service("nameService")
public class NameService {

    public String getName() {
        return "wanghao";
    }
}
