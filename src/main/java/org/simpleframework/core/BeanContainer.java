package org.simpleframework.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.core.annotation.Component;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.core.annotation.Repository;
import org.simpleframework.core.annotation.Service;
import org.simpleframework.util.ClassUtil;
import org.simpleframework.util.ValidationUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanContainer {
    // 存放所有被配置标记的目标对象的map
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    // 加载Bean的注解列表
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION
            = Arrays.asList(Component.class, Controller.class, Repository.class, Service.class, Aspect.class);

    // 获取Bean容器实例
    public static BeanContainer getInstance() {
        return ContainerHolder.HOLDER.instance;
    }

    // 容器是否加载过
    private boolean loaded = false;

    public boolean isLoaded() {
        return loaded;
    }

    public int size() {
        return beanMap.size();
    }

    private enum ContainerHolder {
        HOLDER;
        private BeanContainer instance;
        ContainerHolder() {
            instance = new BeanContainer();
        }
    }

    // 扫描加载所有的bean
    public synchronized void loadBeans(String packageName) {
        if (isLoaded()) {
            log.warn("BeanContainer has been loaded.");
            return;
        }

        Set<Class<?>> classSet = ClassUtil.extractPackageClass(packageName);
        if (ValidationUtil.isEmpty(classSet)) {
            log.warn("Extract nothing from packageName: " + packageName);
            return;
        }

        for (Class<?> clazz : classSet) {
            for (Class<? extends Annotation> annotation : BEAN_ANNOTATION) {
                // 如果类上面标记了定义的注解，将目标类本身作为键，目标类的实例作为值，存放到beanMap
                if (clazz.isAnnotationPresent(annotation)) {
                    beanMap.put(clazz, ClassUtil.newInstance(clazz, true));
                }
            }
        }
        loaded = true;
    }

    // 添加Class对象及其bean实例
    public Object addBean(Class<?> clazz, Object bean) {
        return beanMap.put(clazz, bean);
    }

    // 移除一个容器管理的对象
    public Object removeBean(Class<?> clazz) {
        return beanMap.remove(clazz);
    }

    // 根据Class对象获取bean实例
    public Object getBean(Class<?> clazz) {
        return beanMap.get(clazz);
    }

    // 获取容器管理的所有Class对象
    public Set<Class<?>> getClasses() {
        return beanMap.keySet();
    }

    // 获取所有bean集合
    public Set<Object> getBeans() {
        return new HashSet<>(beanMap.values());
    }

    // 根据注解筛选出bean的Class集合
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        Set<Class<?>> keySet = getClasses();

        if (ValidationUtil.isEmpty(keySet)) {
            log.warn("Nothing in beanMap.");
            return null;
        }

        HashSet<Class<?>> classSet = new HashSet<>();
        for (Class<?> clazz : keySet) {
            if (clazz.isAnnotationPresent(annotation)) {
                classSet.add(clazz);
            }
        }
        return classSet.size() > 0 ? classSet : null;
    }

    // 根据接口或者父类获取实现类或者子类的Class集合，不包含其本身
    public Set<Class<?>> getClassesBySupper(Class<?> interfaceOrClass) {
        Set<Class<?>> keySet = getClasses();

        if (ValidationUtil.isEmpty(keySet)) {
            log.warn("Nothing in beanMap.");
            return null;
        }

        HashSet<Class<?>> classSet = new HashSet<>();
        for (Class<?> clazz : keySet) {
            if (interfaceOrClass.isAssignableFrom(clazz) && !clazz.equals(interfaceOrClass)) {
                classSet.add(clazz);
            }
        }
        return classSet.size() > 0 ? classSet : null;
    }
}
