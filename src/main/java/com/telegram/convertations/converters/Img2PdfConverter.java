package com.telegram.convertations.converters;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.telegram.convertations.conversions.SupportedFileExtensions;
import com.telegram.utils.FileNameTools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Img2PdfConverter implements Converter {
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
            String extension = FileNameTools.extractExtension(file).toLowerCase();
            if (!SupportedFileExtensions.get().contains(extension)) {
                throw new IllegalArgumentException(String.format("Wrong file extension: %s.", extension));
            }
        }
    }

    public File convertImagesIntoPdf(List<File> files) throws IOException, DocumentException {
        File outputFile = createTempPdfFile();
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(outputFile));
        document.open();
        for (File file : files) {
            Image image = Image.getInstance(file.getAbsolutePath());
            float width = image.getWidth();
            float height = image.getHeight();
            image.setAbsolutePosition(0, 0);
            image.setBorderWidth(0);
            image.scaleAbsolute(width, height);
            document.setPageSize(new Rectangle(width, height));
            document.newPage();
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
