package ru.koreanika.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

public class ProjectFileDecoder {

    public static boolean decodeProjectFile(File srcFile, File dstFile) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(srcFile);

        byte[] buf = new byte[fileInputStream.available()];
        fileInputStream.read(buf);
        fileInputStream.close();

        for (int i = 0; i < buf.length; i++) {
            buf[i] -= 76;
        }

        FileOutputStream fileOutputStream = new FileOutputStream(dstFile);
        fileOutputStream.write(buf);

        fileOutputStream.close();

        if (!isFileZipped(dstFile)) {
            if (!dstFile.getPath().equals(srcFile.getPath())) {
                dstFile.delete();
            } else {
                //copy input file to output file
                FileOutputStream fOutputStream = new FileOutputStream(dstFile);
                FileInputStream fInputStream = new FileInputStream(srcFile);
                for (int i = 0; i < fInputStream.available(); i++) {
                    fOutputStream.write(fInputStream.read());
                }
                fInputStream.close();
                fOutputStream.close();
            }
            return false;
        }
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
