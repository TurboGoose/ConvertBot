package converter;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.*;

public class PdfFile implements ToTxtConvertible, ToDocxConvertible {
    protected final File file;

    public PdfFile(File file) {
        this.file = file;
    }

    @Override
    public DocxFile toDocx() {
        try (XWPFDocument doc = new XWPFDocument()) {
            PdfReader reader = new PdfReader(file.getAbsolutePath());
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);

            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                TextExtractionStrategy strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
                String text = strategy.getResultantText();
                XWPFParagraph par = doc.createParagraph();
                XWPFRun run = par.createRun();
                run.setText(text);
                run.addBreak(BreakType.PAGE);
            }
            File tempDocx = File.createTempFile(file.getName(), ".docx");
            tempDocx.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(tempDocx)) {
                doc.write(out);
            }
            reader.close();
            return new DocxFile(tempDocx);
        } catch (IOException exc) {
            throw new IllegalStateException(exc);
        }
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
