package tools.files;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ExtensionToolsTest {
    @Test
    public void extractExtensionFromFileWithoutDots() {
        File file = new File("file");
        assertThat(ExtensionTools.extractExtension(file), is(""));
    }

    @Test
    public void extractExtensionFromFileWithOneDot() {
        File file = new File("file.txt");
        assertThat(ExtensionTools.extractExtension(file), is("txt"));
    }

    @Test
    public void extractExtensionFromFileWithTwoDots() {
        File file = new File("file.pdf.txt");
        assertThat(ExtensionTools.extractExtension(file), is("txt"));
    }

    @Test
    public void extractExtensionFromFileWithDotInTheEnd() {
        File file = new File("file.");
        assertThat(ExtensionTools.extractExtension(file), is(""));
    }

    @Test
    public void extractFilenameWithoutExtensionFromFileWithoutDots() {
        File file = new File("file");
        assertThat(ExtensionTools.extractFilenameWithoutExtension(file), is("file"));
    }

    @Test
    public void extractFilenameWithoutExtensionFromFileWithOneDot() {
        File file = new File("file.txt");
        assertThat(ExtensionTools.extractFilenameWithoutExtension(file), is("file"));
    }

    @Test
    public void extractFilenameWithoutExtensionFromFileWithTwoDots() {
        File file = new File("file.pdf.txt");
        assertThat(ExtensionTools.extractFilenameWithoutExtension(file), is("file.pdf"));
    }

    @Test
    public void extractFilenameWithoutExtensionFromFileWithDotInTheEnd() {
        File file = new File("file.");
        assertThat(ExtensionTools.extractFilenameWithoutExtension(file), is("file"));
    }
}
