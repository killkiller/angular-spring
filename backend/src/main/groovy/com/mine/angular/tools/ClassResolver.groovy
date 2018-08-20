package com.mine.angular.tools

import javassist.ClassPool
import javassist.CtClass
import org.springframework.stereotype.Component

@Component
class ClassResolver {
    private final static String SPRING_BOOT_JAR_BASE = "!/BOOT-INF/classes!/";

    def getClassPath(InputStream inputStream) {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(inputStream);
        return ctClass.getName().replaceAll("\\.", "/") + ".class";
    }

    /**
     * 返回class运行位置，可能在目录也可能在jar包中
     * @param clazz
     * @return dir
     */
    def getClassRunLocation(Class<?> clazz) {
        URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        String urlPath = URLDecoder.decode(url.getPath(), "utf-8");
        // spring boot将用户类置于/path/to/jar!/BOOT-INF/classes!/
        if (urlPath.indexOf(SPRING_BOOT_JAR_BASE) > -1) {
            return urlPath.substring(0, urlPath.length() - SPRING_BOOT_JAR_BASE.length())
        }
        return urlPath;
    }

    /**
     * 从jar包中拷贝文件
     * @param from
     * @param to
     */
    def copyFileFromJar(String from, String to) {
        URL url = this.getClass().getResource(from);
        URLConnection urlConnection = url.openConnection();
        InputStream is = urlConnection.getInputStream();
        if (null == is) {
            throw new FileNotFoundException("$from not found");
        }
        OutputStream os = new FileOutputStream(to);
        byte[] buffer = new byte[1024];
        int readBytes;
        try {
            while ((readBytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
        } finally {
            os.close();
            is.close();
        }
    }
}
