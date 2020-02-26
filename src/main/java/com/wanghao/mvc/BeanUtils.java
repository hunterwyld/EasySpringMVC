package com.wanghao.mvc;

import com.wanghao.mvc.annotation.Autowired;
import com.wanghao.mvc.annotation.Service;

import java.lang.reflect.Field;

/**
 * @author wanghao
 * @description
 * @date 2/25/20 8:39 PM
 */
public class BeanUtils {

    /**
     * 将传入类的类名的首字母转换为小写，把转换后的类名作为beanName
     */
    public static String getControllerBeanName(Class clazz) {
        String className = clazz.getSimpleName();
        char firstChar = className.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            return String.valueOf(firstChar).toLowerCase() + className.substring(1);
        }
        return className;
    }


    /**
     * 如果@Service注解的value不为空，则将value作为beanName
     * 如果为空，则将传入Class的类名的首字母转换为小写，把转换后的类名作为beanName
     */
    public static String getServiceBeanName(Service serviceAnno, Class clazz) {
        if (serviceAnno == null) {
            throw new IllegalArgumentException("service annotation is null");
        }

        String value = serviceAnno.value();
        if (!"".equals(value)) {
            return value;
        }

        String className = clazz.getSimpleName();
        char firstChar = className.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            return String.valueOf(firstChar).toLowerCase() + className.substring(1);
        }
        return className;
    }

    /**
     * 如果@Autowired注解的value不为空，则将value作为beanName
     * 如果为空，则将传入Field的名称作为beanName
     */
    public static String getAutowiredBeanName(Autowired autowiredAnno, Field field) {
        if (autowiredAnno == null) {
            throw new IllegalArgumentException("autowired annotation is null");
        }

        String value = autowiredAnno.value();
        if (!"".equals(value)) {
            return value;
        }

        return field.getName();
    }
}
