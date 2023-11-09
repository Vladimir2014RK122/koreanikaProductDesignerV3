package ru.koreanika.service;

import org.junit.Test;
import ru.koreanika.common.material.ImageIndex;

import static org.junit.Assert.*;

public class ImageIndexTest {

    @Test
    public void testCanParseIndexFile() {
        ImageIndex index = new ImageIndexProvider().get();

        assertNotNull(index);
        assertFalse(index.isEmpty());
        assertTrue(index.size() > 1700);
    }


}
