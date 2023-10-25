package ru.koreanika.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

public class ProjectFileEncoder {

    public static boolean encodeProjectFile(File srcFile, File dstFile) throws IOException {
        if (!isFileZipped(srcFile)) {
            return false;
        }

        /* ADD CUSTOM ENCRYPT */
        FileInputStream fileInputStream = new FileInputStream(srcFile);

        byte[] buf = new byte[fileInputStream.available()];
        fileInputStream.read(buf);
        fileInputStream.close();

        for (int i = 0; i < buf.length; i++) {
            buf[i] += 76;
        }

        FileOutputStream fileOutputStream = new FileOutputStream(dstFile);
        fileOutputStream.write(buf);

        fileOutputStream.close();

        return true;
    }

    public static boolean isFileZipped(File file) {
        try {
            return new ZipInputStream(new FileInputStream(file)).getNextEntry() != null;
        } catch (IOException e) {
            return false;
        }
    }
}
