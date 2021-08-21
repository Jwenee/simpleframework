package org.simpleframework.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;


public class ClassUtilTest {

    @DisplayName("extractPackageClassTest")
    @Test
    public void extractPackageClassTest() {
        Set<Class<?>> classSet = ClassUtil.extractPackageClass("com.example.entity");
        System.out.println(classSet);
        Assertions.assertEquals(4, classSet.size());
    }
}
