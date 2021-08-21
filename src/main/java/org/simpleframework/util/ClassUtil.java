package org.simpleframework.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ClassUtil {

    public static final String FILE_PROTOCOL = "file";

    /**
     * 获取包路径下的类集合
     * @param packageName 包名
     * @return set of Class
     */
    public static Set<Class<?>> extractPackageClass(String packageName) {
        // 1.获取类的加载器
        ClassLoader classLoader = getClassLoader();
        // 2.通过类加载器获取加载的资源
        URL resource = classLoader.getResource(packageName.replace(".", "/"));
        if (resource == null) {
            log.warn("Unable to retrieve anything from package: " + packageName);
            return null;
        }
        // 3.依据不同的资源类型，采用不同的方式获取资源的集合
        Set<Class<?>> classSet = null;
        if (resource.getProtocol().equalsIgnoreCase(FILE_PROTOCOL)) {
            classSet = new HashSet<>();
            File packageDirectory = new File(resource.getPath());
            extractClassFile(classSet, packageDirectory, packageName);
        }
        return classSet;
    }

    /**
     * 递归获取目标package里面的所有class文件（包括子package）
     * @param emptyClassSet 存储目标类的集合
     * @param fileSource 文件或目录
     * @param packageName 包名
     */
    private static void extractClassFile(Set<Class<?>> emptyClassSet, File fileSource, String packageName) {
        if (!fileSource.isDirectory()) return;

        // 如果是一个文件夹，则调用listFiles方法获取文件夹下的文件或文件夹
        File[] files = fileSource.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) return true;
                else {
                    String absolutePath = file.getAbsolutePath();
                    if (absolutePath.endsWith(".class")) {
                        addToClassSet(absolutePath);
                    }
                }
                return false;
            }

            // 根据class文件的绝对路径，获取并生成class对象，存储到classSet
            private void addToClassSet(String absolutePath) {
                absolutePath = absolutePath.replace(File.separator, ".");
                String className = absolutePath.substring(absolutePath.indexOf(packageName));
                className = className.substring(0, className.lastIndexOf("."));
                Class<?> aClass = loadClass(className);
                emptyClassSet.add(aClass);
            }
        });

        if (files != null) {
            for (File file : files) {
                extractClassFile(emptyClassSet, file, packageName);
            }
        }
    }

    /**
     * 获取Class对象
     * @param className package+classname
     * @return Class
     */
    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("Load class error: " + e);
            throw new RuntimeException(e);
        }
    }

    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    // 实例化class
    public static <T> T newInstance(Class<?> clazz, boolean accessible) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(accessible);
            return (T)constructor.newInstance();
        } catch (Exception e) {
            log.error("newInstance error: " + e);
            throw new RuntimeException(e);
        }
    }

    // 设置成员变量
    public static void setField(Field field, Object target, Object value, boolean accessible) {
        field.setAccessible(accessible);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            log.error("setField error: " + e);
            throw new RuntimeException(e);
        }
    }
}
