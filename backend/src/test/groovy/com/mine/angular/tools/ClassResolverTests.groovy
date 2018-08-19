package com.mine.angular.tools

import org.junit.Assert
import org.junit.Test

class ClassResolverTests {
    @Test
    void testGetClassPackage() {
        String clsBasePath = System.getProperty("user.dir") + "/src/test/resources/classfiles/";
        String classPath = ClassResolver.getClassPath(new FileInputStream(clsBasePath + "BackendApplication.class"));
        Assert.assertEquals("com/mine/angular/BackendApplication.class", classPath);
    }
}
