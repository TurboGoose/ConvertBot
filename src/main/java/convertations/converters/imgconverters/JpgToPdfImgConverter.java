package convertations.converters.imgconverters;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import tools.files.FileNameTools;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JpgToPdfImgConverter implements ImgConverter {
    @Override
    public File convert(List<File> files) {
        checkExtensions(files);
        try {
            return combineImagesIntoPDF(files);
        } catch (IOException exc) {
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

    private File combineImagesIntoPDF(List<File> files) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            for (File img : files) {
                addImageAsNewPage(doc, img.getAbsolutePath());
            }
            File outputFile = createTempPdfFile();
            doc.save(outputFile);
            return outputFile;
        }
    }

    private void addImageAsNewPage(PDDocument doc, String imagePath) throws IOException {
        PDImageXObject image = PDImageXObject.createFromFile(imagePath, doc);
        int width = image.getWidth();
        int height = image.getHeight();
        PDRectangle pageSize = new PDRectangle(width, height);
        PDPage page = new PDPage(pageSize);
        doc.addPage(page);
        try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
            contents.drawImage(image, 0, 0, width, height);
        }
    }

    private File createTempPdfFile() throws IOException {
        File result = File.createTempFile("images", ".pdf");
        result.deleteOnExit();
        return result;
    }
}
