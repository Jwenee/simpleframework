package org.simpleframework.aop;

import com.example.controller.superadmin.HeadLineOperationController;
import org.junit.jupiter.api.Test;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.inject.DependencyInjector;

public class AspectWeaverTest {

    @Test
    public void doAopTest() {
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.example");

        new AspectWeaver().doAopByAspectJ();
        new DependencyInjector().doIoc();

        HeadLineOperationController headLineOperationController =
                (HeadLineOperationController) beanContainer.getBean(HeadLineOperationController.class);

        headLineOperationController.addHeadLine(null, null);
    }
}
