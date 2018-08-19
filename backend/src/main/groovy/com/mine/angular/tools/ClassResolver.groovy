package com.mine.angular.tools

import javassist.ClassPool
import javassist.CtClass

class ClassResolver {
    def static getClassPath(InputStream inputStream) {
        ClassPool pool= ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(inputStream);
        return ctClass.getName().replaceAll("\\.", "/") + ".class";
    }
}
