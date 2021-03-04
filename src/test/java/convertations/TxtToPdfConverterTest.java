package convertations;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import convertations.converters.Converter;
import convertations.converters.TxtToPdfConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TxtToPdfConverterTest {
    @TempDir
    File tempDir;
    Converter converter = new TxtToPdfConverter();

    @Test
    public void convertBlankTxtToPdf() throws Exception {
        testScenario("");
    }

    @Test
    public void convertSimpleTxtToPdf() throws Exception {
        testScenario("Hello world!");
    }

    @Test
    public void convertComplexTxtToPdf() throws Exception {
        String text =
                "+------------+------------+\n" +
                "|     1      |     2      |\n" +
                "+------------+------------+\n" +
                "|     3      |     4      |\n" +
                "+------------+------------+";
        testScenario(text);
    }

    private void testScenario(String text) throws Exception {
        File sourceFile = createTxtFile(text);
        File targetFile = converter.convert(sourceFile);
        assertThat(readPdf(targetFile).contains(text), is(true));
    }

    private File createTxtFile(String text) throws FileNotFoundException {
        File txtFile = new File(tempDir, "TxtToPdfTest.txt");
        try (PrintWriter writer = new PrintWriter(txtFile)) {
            writer.print(text);
        }
        return txtFile;
    }

    private String readPdf(File file) throws IOException {
        PdfReader reader = new PdfReader(file.getAbsolutePath());
        StringBuilder parsedText = new StringBuilder();
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            parsedText.append(PdfTextExtractor.getTextFromPage(reader, i));
        }
        reader.close();
        return parsedText.toString();
    }
}
