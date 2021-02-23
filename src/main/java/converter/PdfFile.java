package converter;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class PdfFile implements ToTxtConvertible, ToDocxConvertible {
    protected final File file;

    public PdfFile(File file) {
        this.file = file;
    }

    @Override
    public DocxFile toDocx() {
        return null;
    }

    @Override
    public TxtFile toTxt() {
        try {
            File txtFile = File.createTempFile(file.getName(), ".txt");
            txtFile.deleteOnExit();
            String extractedText = extractRawTextFromPdf();
            saveTextInFile(extractedText, txtFile);
            return new TxtFile(txtFile);
        } catch (IOException exc) {
            throw new IllegalStateException(exc);
        }
    }

    protected String extractRawTextFromPdf() throws IOException {
        PdfReader reader = new PdfReader(file.getAbsolutePath());
        StringBuilder parsedText = new StringBuilder();
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            parsedText.append(PdfTextExtractor.getTextFromPage(reader, i));
        }
        reader.close();
        return parsedText.toString();
    }

    protected void saveTextInFile(String text, File file) throws FileNotFoundException {
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.print(text);
        }
    }

    public File getFile() {
        return file;
    }
}
