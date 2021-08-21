package org.simpleframework.mvc.processor.impl;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.annotation.RequestMapping;
import org.simpleframework.mvc.annotation.RequestParam;
import org.simpleframework.mvc.annotation.ResponseBody;
import org.simpleframework.mvc.processor.RequestProcessor;
import org.simpleframework.mvc.render.ResultRender;
import org.simpleframework.mvc.render.impl.JsonResultRender;
import org.simpleframework.mvc.render.impl.ResourceNotFoundResultRender;
import org.simpleframework.mvc.render.impl.ViewResultRender;
import org.simpleframework.mvc.type.ControllerMethod;
import org.simpleframework.mvc.type.RequestMethod;
import org.simpleframework.mvc.type.RequestPathInfo;
import org.simpleframework.util.ConverterUtil;
import org.simpleframework.util.ValidationUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ControllerRequestProcessor implements RequestProcessor {

    private BeanContainer beanContainer;

    private Map<RequestPathInfo, ControllerMethod> pathInfoControllerMethodMap = new ConcurrentHashMap<>();

    public ControllerRequestProcessor() {
        this.beanContainer = BeanContainer.getInstance();
        Set<Class<?>> requestMappingSet = beanContainer.getClassesByAnnotation(RequestMapping.class);
        initPathControllerMethodMap(requestMappingSet);
    }

    private void initPathControllerMethodMap(Set<Class<?>> requestMappingSet) {
        if (ValidationUtil.isEmpty(requestMappingSet)) {
            return;
        }
        // 1.遍历所有被@RequestMapping标记的类，获取类注解的属性值作为一级路径
        for (Class<?> requestMappingClass : requestMappingSet) {
            RequestMapping requestMapping = requestMappingClass.getAnnotation(RequestMapping.class);
            String basePath = requestMapping.value();
            if (!basePath.startsWith("/")) {
                basePath = "/" + basePath;
            }

        // 2.遍历类里所有被@RequestMapping标记的方法，获取属性值作为二级路径
            Method[] methods = requestMappingClass.getDeclaredMethods();
            if (ValidationUtil.isEmpty(methods)) {
                continue;
            }
            for (Method method : methods) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping methodRequest = method.getAnnotation(RequestMapping.class);
                    String methodPath = methodRequest.value();
                    if (!methodPath.startsWith("/")) {
                        methodPath = "/" + methodPath;
                    }
                    String url = basePath + methodPath;
        // 3.解析方法里被@RequesttParam标记的参数
        //   获取注解的属性值作为参数名
        //   获取被标记的参数数据类型，建立参数名与参数类型的映射
                    Map<String, Class<?>> methodParams = new HashMap<>();
                    Parameter[] parameters = method.getParameters();
                    if (!ValidationUtil.isEmpty(parameters)) {
                        for (Parameter parameter : parameters) {
                            RequestParam param = parameter.getAnnotation(RequestParam.class);
                            // controller方法所有参数都需要@RequestParam
                            if (param == null) {
                                throw new RuntimeException("All parameters must have @RequestParam");
                            }
                            methodParams.put(param.value(), parameter.getType());
                        }
                    }
            // 4.将获取到的信息封装成RequestPathInfo，ControllerMethod实例放到map
                    String httpMethod = String.valueOf(methodRequest.method());

                    RequestPathInfo requestPathInfo = new RequestPathInfo(httpMethod, url);
                    if (this.pathInfoControllerMethodMap.containsKey(requestPathInfo)) {
                        log.warn("duplicate url: {} registration, current class {} method {} will override the former one",
                                requestPathInfo.getHttpPath(), requestMappingClass.getName(), method.getName());
                    }

                    ControllerMethod controllerMethod = new ControllerMethod(requestMappingClass, method, methodParams);
                    this.pathInfoControllerMethodMap.put(requestPathInfo, controllerMethod);
                }
            }
        }

    }

    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        // 1.解析请求方法，路径，获取对应的controllerMethod实例
        String requestMethod = requestProcessorChain.getRequestMethod();
        String requestPath = requestProcessorChain.getRequestPath();
        ControllerMethod controllerMethod = this.pathInfoControllerMethodMap.get(new RequestPathInfo(requestMethod, requestPath));
        if (controllerMethod == null) {
            requestProcessorChain.setResultRender(new ResourceNotFoundResultRender(requestMethod, requestPath));
            return false;
        }

        // 2.解析请求参数，传递给controllerMethod实例执行
        Object result = invokeControllerMethod(controllerMethod, requestProcessorChain.getRequest());
        // 3.根据处理结果，选择对应的render进行渲染
        setResultRender(result, controllerMethod, requestProcessorChain);
        return true;
    }

    private void setResultRender(Object result, ControllerMethod controllerMethod, RequestProcessorChain requestProcessorChain) {
        if (result == null) {
            return;
        }
        ResultRender resultRender;
        boolean isJson = controllerMethod.getInvokeMethod().isAnnotationPresent(ResponseBody.class);
        if (isJson) {
            resultRender = new JsonResultRender(result);
        } else {
            resultRender = new ViewResultRender(result);
        }
        requestProcessorChain.setResultRender(resultRender);
    }

    private Object invokeControllerMethod(ControllerMethod controllerMethod, HttpServletRequest request) {
        // 1.从请求中获取参数名及其对应值
        Map<String, String> requestParamMap = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> parameter : parameterMap.entrySet()) {
            if (!ValidationUtil.isEmpty(parameter.getValue())) {
                // 一个参数对应一个值
                requestParamMap.put(parameter.getKey(), parameter.getValue()[0]);
            }
        }

        // 2.实例化方法
        List<Object> methodParams = new ArrayList<>();
        Map<String, Class<?>> methodParamMap = controllerMethod.getMethodParameters();
        for (String paramName : methodParamMap.keySet()) {
            Class<?> type = methodParamMap.get(paramName);
            String requestValue = requestParamMap.get(paramName);

            Object value;
            // 处理请求参数，支持String，基础类型及其包装类
            if (requestValue == null) {
                value = ConverterUtil.primitiveNull(type);
            }
            else {
                value = ConverterUtil.convert(type, requestValue);
            }

            methodParams.add(value);
        }

        // 3.执行方法
        Object controller = beanContainer.getBean(controllerMethod.getControllerClass());
        Method invokeMethod = controllerMethod.getInvokeMethod();
        invokeMethod.setAccessible(true);

        Object result;
        try {
            if (methodParams.size() == 0) {
                result = invokeMethod.invoke(controller);
            }
            else {
                result = invokeMethod.invoke(controller, methodParams.toArray());
            }
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
