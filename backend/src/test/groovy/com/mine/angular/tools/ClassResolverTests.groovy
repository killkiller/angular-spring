package com.mine.angular.tools

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner)
class ClassResolverTests {

    ClassResolver classResolver = new ClassResolver();

    @Test
    void testGetClassPackage() {
        String clsBasePath = System.getProperty("user.dir") + "/src/test/resources/classfiles/";
        String classPath = classResolver.getClassPath(new FileInputStream(clsBasePath + "BackendApplication.class"));
        Assert.assertEquals("com/mine/angular/BackendApplication.class", classPath);
    }
}
