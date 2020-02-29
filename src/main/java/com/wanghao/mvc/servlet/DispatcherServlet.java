package com.wanghao.mvc.servlet;

import com.wanghao.mvc.BeanUtils;
import com.wanghao.mvc.annotation.Autowired;
import com.wanghao.mvc.annotation.Controller;
import com.wanghao.mvc.annotation.RequestMapping;
import com.wanghao.mvc.annotation.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author wanghao
 * @description
 * @date 2/25/20 3:47 PM
 */
public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = -7911195212736080515L;

    private static Set<String> classNameSet = new HashSet<>();

    private static Map<String, Object> beanMap = new HashMap<>();

    private static Map<String, Object> controllerMap = new HashMap<>();

    private static Map<String, Method> methodMap = new HashMap<>();

    @Override
    public void init() {
        // 包扫描
        scanPackage("com.wanghao.mvc");

        // 类的实例化
        instantiation();

        // 依赖注入
        injectDependency();

        // 处理访问路径与Controller中方法的映射
        handleMapping();
    }

    private void handleMapping() {
        if (beanMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(Controller.class)) {
                String path;
                if (clazz.isAnnotationPresent(RequestMapping.class)) {
                    path = clazz.getAnnotation(RequestMapping.class).value();
                } else {
                    path = "";
                }

                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    RequestMapping requestMappingAnno = method.getAnnotation(RequestMapping.class);
                    if (requestMappingAnno != null) {
                        String mappingPath = path + requestMappingAnno.value();
                        methodMap.put(mappingPath, method);
                        controllerMap.put(mappingPath, instance);
                    }
                }
            }
        }
    }

    private void injectDependency() {
        if (beanMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Object instance = entry.getValue();
            Field[] fields = instance.getClass().getDeclaredFields();
            for (Field field : fields) {
                Autowired autowiredAnno = field.getAnnotation(Autowired.class);
                if (autowiredAnno != null) {
                    String beanName = BeanUtils.getAutowiredBeanName(autowiredAnno, field);
                    Object bean = beanMap.get(beanName);
                    if (bean != null) {
                        field.setAccessible(true);
                        try {
                            field.set(instance, bean);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void instantiation() {
        if (classNameSet.isEmpty()) {
            return;
        }

        try {
            for (String className : classNameSet) {
                String realClassName = className.replaceAll("\\.class", "");
                Class<?> clazz = Class.forName(realClassName);
                Controller controllerAnno = clazz.getAnnotation(Controller.class);
                if (controllerAnno != null) {
                    Object bean = clazz.newInstance();
                    String beanName = BeanUtils.getControllerBeanName(clazz);
                    beanMap.put(beanName, bean);

                    if (clazz.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping requestMappingAnno = clazz.getAnnotation(RequestMapping.class);
                        String path = requestMappingAnno.value();
                        if (!"".equals(path)) {
                            controllerMap.put(path, bean);
                        }
                    }

                    continue;
                }

                Service serviceAnno = clazz.getAnnotation(Service.class);
                if (serviceAnno != null) {
                    Object bean = clazz.newInstance();
                    String beanName = BeanUtils.getServiceBeanName(serviceAnno, clazz);
                    beanMap.put(beanName, bean);
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void scanPackage(String basePackage) {
        String basePath = basePackage.replaceAll("\\.", "/");
        URL url = getClass().getClassLoader().getResource(basePath);
        if (url == null) {
            return;
        }

        String filePath = url.getFile();
        File[] files = new File(filePath).listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                scanPackage(basePackage + "." + file.getName());
            } else {
                classNameSet.add(basePackage + "." + file.getName());
                System.out.println(basePackage + "." + file.getName());
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.replaceAll(contextPath, "");
        Method method = methodMap.get(path);
        if (method == null) {
            resp.getWriter().append(path).append(" not found");
            return;
        }

        Object controller = controllerMap.get("/" + path.split("/")[1]);
        try {
            Object ret = method.invoke(controller);
            resp.getWriter().append(ret.toString());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
