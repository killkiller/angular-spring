package com.mine.angular.tools

import com.strobel.decompiler.DecompilerSettings
import com.strobel.decompiler.PlainTextOutput
import org.springframework.stereotype.Component
import com.strobel.decompiler.Decompiler

/**
 * Created by h00421015 on 2018/8/23.
 */
@Component
class ClassDecompiler {

    /**
     * class反编译，反编译后java文件置于javaPath
     * @param classPath
     * @param storePath
     */
    def decompile(String classPath, String storePath) {
        File toFile = new File(storePath);
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(storePath));
        Decompiler.decompile(classPath, new PlainTextOutput(writer));
        writer.close();

        return toFile.getName();
    }
}
