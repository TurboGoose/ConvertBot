package convertations.converters;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import tools.files.FileNameTools;

import java.io.*;

public class TxtToPdfConverter implements Converter {
    @Override
    public File convert(File file) {
        checkExtension(file);
        try {
            File pdfFile = createTempPdfFile(file);
            writeTxtToPdf(file, pdfFile);
            return pdfFile;
        } catch (IOException | DocumentException exc) {
            throw new IllegalStateException(exc);
        }
    }

    private void checkExtension(File file) {
        String extension = FileNameTools.extractExtension(file);
        if (!"txt".equals(extension)) {
            throw new IllegalArgumentException(
                    String.format("Wrong file extension: got \".%s\" when \".txt\" expected", extension));
        }
    }

    private File createTempPdfFile(File file) throws IOException {
        File tempPdfFile = File.createTempFile(FileNameTools.extractFilenameWithoutExtension(file), ".pdf");
        tempPdfFile.deleteOnExit();
        return tempPdfFile;
    }

    private void writeTxtToPdf(File txt, File pdf) throws IOException, DocumentException {
        Document pdfDoc = new Document(PageSize.A4);
        PdfWriter.getInstance(pdfDoc, new FileOutputStream(pdf)).setPdfVersion(PdfWriter.PDF_VERSION_1_7);
        pdfDoc.open();
        Font font = new Font();
        font.setStyle(Font.NORMAL);
        font.setSize(11);
        pdfDoc.add(new Paragraph(System.lineSeparator()));
        try (BufferedReader reader = new BufferedReader(new FileReader(txt))) {
            String strLine;
            while ((strLine = reader.readLine()) != null) {
                Paragraph par = new Paragraph(strLine + System.lineSeparator(), font);
                par.setAlignment(Element.ALIGN_JUSTIFIED);
                pdfDoc.add(par);
            }
        }
        pdfDoc.close();
    }
}
