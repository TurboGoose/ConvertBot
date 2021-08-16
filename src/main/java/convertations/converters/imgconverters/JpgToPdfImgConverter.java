package convertations.converters.imgconverters;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import tools.files.FileNameTools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class JpgToPdfImgConverter implements ImgConverter {
    @Override
    public File convert(List<File> files) {
        checkExtensions(files);
        try {
            return convertImagesIntoPdf(files);
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    private void checkExtensions(List<File> files) {
        for (File file : files) {
            String extension = FileNameTools.extractExtension(file);
            if (!"jpg".equals(extension) && !"jpeg".equals(extension)) {
                throw new IllegalArgumentException(
                        String.format("Wrong file extension: got \".%s\" when \".jpg\" expected.", extension));
            }
        }
    }

    public File convertImagesIntoPdf(List<File> files) throws IOException, DocumentException {
        File outputFile = createTempPdfFile();
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(outputFile));
        document.open();
        for (File file : files) {
            document.newPage();
            Image image = Image.getInstance(file.getAbsolutePath());
            image.setAbsolutePosition(0, 0);
            image.setBorderWidth(0);
            image.scaleAbsolute(image.getWidth(), image.getHeight());
            document.add(image);
        }
        document.close();
        return outputFile;
    }

    private File createTempPdfFile() throws IOException {
        File result = File.createTempFile("images", ".pdf");
        result.deleteOnExit();
        return result;
    }
}
