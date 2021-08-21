package org.simpleframework.mvc.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Map;

// 待执行的controller及其实例和参数的映射
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControllerMethod {

    private Class<?> controllerClass;

    private Method invokeMethod;

    private Map<String, Class<?>> methodParameters;
}
