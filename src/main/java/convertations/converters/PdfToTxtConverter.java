package convertations.converters;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import tools.files.FileNameTools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class PdfToTxtConverter implements Converter {
    @Override
    public File convert(File file) {
        checkExtension(file);
        try {
            String text = extractRawTextFromPdf(file);
            File txtFile = createTempTxtFile(file);
            saveTextInFile(text, txtFile);
            return txtFile;
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    private void checkExtension(File file) {
        String extension = FileNameTools.extractExtension(file);
        if (!"pdf".equals(extension)) {
            throw new IllegalArgumentException(
                    String.format("Wrong file extension: got \".%s\" when \".pdf\" expected.", extension));
        }
    }

    private String extractRawTextFromPdf(File file) throws IOException {
        PdfReader reader = new PdfReader(file.getAbsolutePath());
        StringBuilder parsedText = new StringBuilder();
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            parsedText.append(PdfTextExtractor.getTextFromPage(reader, i));
        }
        reader.close();
        return parsedText.toString();
    }

    private File createTempTxtFile(File file) throws IOException {
        File tempTxtFile = File.createTempFile(FileNameTools.extractFilenameWithoutExtension(file), ".txt");
        tempTxtFile.deleteOnExit();
        return tempTxtFile;
    }

    private void saveTextInFile(String text, File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.print(text);
        }
    }
}
