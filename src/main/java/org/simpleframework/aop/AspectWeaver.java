package org.simpleframework.aop;

import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.aop.annotation.Order;
import org.simpleframework.aop.aspect.AspectInfo;
import org.simpleframework.aop.aspect.DefaultAspect;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.util.ValidationUtil;

import java.lang.annotation.Annotation;
import java.util.*;

public class AspectWeaver {

    private BeanContainer beanContainer;

    public AspectWeaver() {
        beanContainer = BeanContainer.getInstance();
    }

    /*public void doAop() {
        // 1.获取所有切面类
        Set<Class<?>> aspectSet = beanContainer.getClassesByAnnotation(Aspect.class);
        if (ValidationUtil.isEmpty(aspectSet)) return;

        // 2.将切面类按照不同的织入目标进行切分
        Map<Class<? extends Annotation>, List<AspectInfo>> categorizedMap = new HashMap<>();
        for (Class<?> aspectClass : aspectSet) {
            if (verifyAspect(aspectClass)) {
                categorizeAspect(categorizedMap, aspectClass);
            }
            else {
                throw new RuntimeException("Aspect class must be marked with @Aspect and @Order, and extend from" +
                        "DefaultAspect, its value not equal self.");
            }
        }
        // 3.按照不同的织入目标分别去按序织入Aspect的逻辑
        if (ValidationUtil.isEmpty(categorizedMap)) return;
        for (Class<? extends Annotation> category : categorizedMap.keySet()) {
            weaveByCategory(category, categorizedMap.get(category));
        }
    }*/

    public void doAopByAspectJ() {
        // 1.获取所有切面类
        Set<Class<?>> aspectSet = beanContainer.getClassesByAnnotation(Aspect.class);
        if (ValidationUtil.isEmpty(aspectSet)) return;
        // 2.拼装AspectInfo
        List<AspectInfo> aspectInfoList = packAspectInfoList(aspectSet);
        // 3.遍历容器里的类
        Set<Class<?>> classSet = beanContainer.getClasses();
        if (ValidationUtil.isEmpty(classSet)) return;
        for (Class<?> targetClass : classSet) {
            // 排除AspectClass自身
            if (targetClass.isAnnotationPresent(Aspect.class)) {
                continue;
            }
        // 4.粗筛符合条件的Aspect
            List<AspectInfo> roughMatchedAspectList =
                    collectRoughMatchedAspectListForSpecificClass(aspectInfoList, targetClass);
        // 5.尝试进行Aspect的织入
            wrapIfNecessary(roughMatchedAspectList, targetClass);
        }
    }

    private void wrapIfNecessary(List<AspectInfo> roughMatchedAspectList, Class<?> targetClass) {
        if (ValidationUtil.isEmpty(roughMatchedAspectList)) return;
        AspectListExecutor executor = new AspectListExecutor(targetClass, roughMatchedAspectList);
        Object proxyBean = ProxyCreator.createProxy(targetClass, executor);
        beanContainer.addBean(targetClass, proxyBean);
    }

    private List<AspectInfo> collectRoughMatchedAspectListForSpecificClass(List<AspectInfo> aspectInfoList, Class<?> targetClass) {
        ArrayList<AspectInfo> roughMatchAspectList = new ArrayList<>();
        if (ValidationUtil.isEmpty(aspectInfoList)) return roughMatchAspectList;

        for (AspectInfo aspectInfo : aspectInfoList) {
            // 初筛
            boolean matches = aspectInfo.getPointcutLocator().roughMatches(targetClass);
            if (matches) {
                roughMatchAspectList.add(aspectInfo);
            }
        }
        return roughMatchAspectList;
    }

    private List<AspectInfo> packAspectInfoList(Set<Class<?>> aspectSet) {
        ArrayList<AspectInfo> aspectInfoList = new ArrayList<>();
        for (Class<?> aspectClass : aspectSet) {
            if (verifyAspect(aspectClass)) {
                Order orderTag = aspectClass.getAnnotation(Order.class);
                Aspect aspectTag = aspectClass.getAnnotation(Aspect.class);
                DefaultAspect defaultAspect = (DefaultAspect) beanContainer.getBean(aspectClass);

                // 初始化表达式定位器
                PointcutLocator pointcutLocator = new PointcutLocator(aspectTag.pointcut());
                AspectInfo aspectInfo = new AspectInfo(orderTag.value(), defaultAspect, pointcutLocator);
                aspectInfoList.add(aspectInfo);
            }
            else {
                throw new RuntimeException("Aspect class must be marked @Order and @Aspect, extend DefaultAspect.");
            }
        }
        return aspectInfoList;
    }

    private boolean verifyAspect(Class<?> aspectClass) {
        /* //注解形式
        if (isDefaultAspect(aspectClass)){
            return aspectClass.isAnnotationPresent(Aspect.class)
                    && aspectClass.isAnnotationPresent(Order.class)
                    && DefaultAspect.class.isAssignableFrom(aspectClass)
                    && aspectClass.getAnnotation(Aspect.class).value() != Aspect.class;
        }
        else {
            //表达式形式
            return aspectClass.isAnnotationPresent(Aspect.class)
                    && aspectClass.isAnnotationPresent(Order.class)
                    && DefaultAspect.class.isAssignableFrom(aspectClass);
        }*/
        return aspectClass.isAnnotationPresent(Aspect.class)
                && aspectClass.isAnnotationPresent(Order.class)
                && DefaultAspect.class.isAssignableFrom(aspectClass);
    }

    /*private boolean isDefaultAspect(Class<?> aspectClass){
        if (!aspectClass.getAnnotation(Aspect.class).value().getName()
                .equals(DefaultAspect.class.getName())) {
            return false;
        }
        else {
            return true;
        }
    }*/

    private void weaveByCategory(Class<? extends Annotation> category, List<AspectInfo> aspectInfoList) {
        // 1.获取被代理类的集合
        Set<Class<?>> classSet = beanContainer.getClassesByAnnotation(category);
        if (ValidationUtil.isEmpty(classSet)) return;
        // 2.遍历被代理类，分别为每个代理类生成动态代理实例
        for (Class<?> targetClass : classSet) {
            AspectListExecutor aspectListExecutor = new AspectListExecutor(targetClass, aspectInfoList);
            Object proxyBean = ProxyCreator.createProxy(targetClass, aspectListExecutor);
        // 3.将动态代理对象实例添加到容器中，取代未被代理前的类实例
            beanContainer.addBean(targetClass, proxyBean);
        }
    }

/*
    private void categorizeAspect(Map<Class<? extends Annotation>, List<AspectInfo>> categorizedMap, Class<?> aspectClass) {
        Aspect aspectTag = aspectClass.getAnnotation(Aspect.class);
        Order orderTag = aspectClass.getAnnotation(Order.class);
        DefaultAspect aspect = (DefaultAspect) beanContainer.getBean(aspectClass);

        AspectInfo aspectInfo = new AspectInfo(orderTag.value(), aspect);
        if (!categorizedMap.containsKey(aspectTag.value())) {
            // 如果织入的joinPoint第一次出现，则以joinPoint为key，以新创建的List<AspectInfo>为value
            List<AspectInfo> aspectInfoList = new ArrayList<>();
            aspectInfoList.add(aspectInfo);
            categorizedMap.put(aspectTag.value(), aspectInfoList);
        }
        else {
            // 非第一次出现
            List<AspectInfo> aspectInfoList = categorizedMap.get(aspectTag.value());
            aspectInfoList.add(aspectInfo);
        }
    }
*/

/*    // 必须给Aspect类添加@Aspect和@Order，同时必须继承DefaultAspect.class，@Aspect不能是它自身
    private boolean verifyAspect(Class<?> aspectClass) {
        return aspectClass.isAnnotationPresent(Aspect.class) &&
                aspectClass.isAnnotationPresent(Order.class) &&
                DefaultAspect.class.isAssignableFrom(aspectClass) &&
                aspectClass.getAnnotation(Aspect.class).value() != Aspect.class;
    }*/
}
