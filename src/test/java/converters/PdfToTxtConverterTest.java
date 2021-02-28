package converters;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class PdfToTxtConverterTest {
    Converter converter = new PdfToTxtConverter();

    @Test
    public void convertBlankPdfToTxtFile() throws Exception {
        File sourceFile = new File(ClassLoader.getSystemResource("pdfToTxtTests/Blank.pdf").toURI());
        File targetFile = converter.convert(sourceFile);
        String text = readTextFromFile(targetFile);
        assertThat(text.isBlank(), is(true));
    }

    @Test
    public void convertPdfWithTextToTxtFile() throws Exception {
        File sourceFile = new File(ClassLoader.getSystemResource("pdfToTxtTests/Text.pdf").toURI());
        File targetFile = converter.convert(sourceFile);
        String text = readTextFromFile(targetFile);
        assertThat(text.contains("Hello world!"), is(true));
    }

    @Test
    public void convertPdfWithTextAndPictureToTxtFile() throws Exception {
        File sourceFile = new File(ClassLoader.getSystemResource("pdfToTxtTests/TextAndPicture.pdf").toURI());
        File targetFile = converter.convert(sourceFile);
        String text = readTextFromFile(targetFile);
        assertThat(text.contains("Begin"), is(true));
        assertThat(text.contains("End"), is(true));
    }

    private String readTextFromFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines().collect(Collectors.joining());
        }
    }
}
