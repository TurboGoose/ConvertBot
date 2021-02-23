package converter;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;

public class TxtFile implements ToPdfConvertible, ToDocxConvertible {
    protected File file;

    public TxtFile(File file) {
        this.file = file;
    }

    @Override
    public DocxFile toDocx() {
        return null;
    }

    @Override
    public PdfFile toPdf() {
        try {
            File pdfFile = File.createTempFile(file.getName(), ".pdf");
            pdfFile.deleteOnExit();
            Document pdfDoc = new Document(PageSize.A4);
            PdfWriter.getInstance(pdfDoc, new FileOutputStream(pdfFile)).setPdfVersion(PdfWriter.PDF_VERSION_1_7);
            pdfDoc.open();
            Font font = new Font();
            font.setStyle(Font.NORMAL);
            font.setSize(11);
            pdfDoc.add(new Paragraph(System.lineSeparator()));
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String strLine;
                while ((strLine = reader.readLine()) != null) {
                    Paragraph par = new Paragraph(strLine + System.lineSeparator(), font);
                    par.setAlignment(Element.ALIGN_JUSTIFIED);
                    pdfDoc.add(par);
                }
            }
            pdfDoc.close();
            return new PdfFile(pdfFile);
        } catch (IOException | DocumentException exc) {
            throw new IllegalStateException(exc);
        }
    }

    public File getFile() {
        return file;
    }
}
