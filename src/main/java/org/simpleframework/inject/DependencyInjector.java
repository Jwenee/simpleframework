package org.simpleframework.inject;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.inject.annotation.Autowired;
import org.simpleframework.util.ClassUtil;
import org.simpleframework.util.ValidationUtil;

import java.lang.reflect.Field;
import java.util.Set;

@Slf4j
public class DependencyInjector {

    private BeanContainer beanContainer;

    public DependencyInjector() {
        beanContainer = BeanContainer.getInstance();
    }

    public void doIoc() {
        // 1.遍历Bean容器中所有的Class对象
        Set<Class<?>> classSet = beanContainer.getClasses();
        if (ValidationUtil.isEmpty(classSet)) {
            log.warn("Empty classSet in beanContainer.");
            return;
        }
        for (Class<?> clazz : classSet) {
            // 2.遍历Class对象的所有成员变量
            Field[] fields = clazz.getDeclaredFields();
            if (ValidationUtil.isEmpty(fields)) continue;

            for (Field field : fields) {
                // 3.找出被Autowired标记的成员变量
                if (field.isAnnotationPresent(Autowired.class)) {
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String autowiredValue = autowired.value();
                    // 4.获取这些成员变量的类型
                    Class<?> fieldClass = field.getType();
                    // 5.获取这些成员变量类型在容器对应的实例
                    Object fieldValue = getFieldInstance(fieldClass, autowiredValue);
                    if (fieldValue == null) {
                        throw new RuntimeException("Unable to inject relevant type, target fieldClass: " + fieldClass.getClass() + " autowiredValue: " + autowiredValue);
                    } else {
                        // 6.通过反射将对应的成员变量实例注入到成员变量所对应的实例
                        Object bean = beanContainer.getBean(clazz);
                        ClassUtil.setField(field, bean, fieldValue, true);
                    }
                }
            }
        }
    }

    // 根据Class从容器中获取其实例或者实现类
    private Object getFieldInstance(Class<?> fieldClass, String autowiredValue) {
        Object fieldValue = beanContainer.getBean(fieldClass);
        if (fieldValue != null) {
            // 实现类
            return fieldValue;
        }
        else {
            // 接口
            Class<?> implementedClass = getImplementClass(fieldClass, autowiredValue);
            if (implementedClass != null) {
                return beanContainer.getBean(implementedClass);
            } else {
                return null;
            }
        }
    }

    // 获取接口的实现类
    private Class<?> getImplementClass(Class<?> fieldClass, String autowiredValue) {
        Set<Class<?>> classSet = beanContainer.getClassesBySupper(fieldClass);
        if (!ValidationUtil.isEmpty(classSet)) {
            // 此时注解为""
            if (ValidationUtil.isEmpty(autowiredValue)) {
                // 获取到实例数量为1
                if (classSet.size() == 1) {
                    return classSet.iterator().next();
                } else {
                    throw new RuntimeException("Multiple implemented classes for " + fieldClass.getName() + ", please set value of @Autowired to pick one");
                }
            } else {
                // 此时注解有特定值
                for (Class<?> clazz : classSet) {
                    if (clazz.getSimpleName().equals(autowiredValue)) {
                        return clazz;
                    }
                }
            }
        }
        return null;
    }
}
