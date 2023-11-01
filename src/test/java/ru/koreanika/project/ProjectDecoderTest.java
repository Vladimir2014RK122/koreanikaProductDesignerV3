package ru.koreanika.project;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ProjectDecoderTest {

    @Ignore
    @Test
    public void doDecode() throws IOException {
        File src = new File("/home/transcend/code/KOREANIKA/tmp/p9-restoration/p9-restoration.kproj");
        File dest = new File("/home/transcend/code/KOREANIKA/tmp/p9-restoration/p9-restoration-decoded.kproj");
        ProjectFileDecoder.decodeProjectFile(src, dest);
    }

}
