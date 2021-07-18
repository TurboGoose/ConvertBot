package convertations.converters;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import tools.files.FileNameTools;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JpgToPdfConverter implements Converter {
    @Override
    public File convert(File file) {
        checkExtensions(file);
        try {
            File outputFile = createTempPdfFile();
            combineImagesIntoPDF(file, outputFile);
            return outputFile;
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    private void checkExtensions(File file) {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(String.format("%s is not a directory.", file.getAbsolutePath()));
        }
        for (File f : Objects.requireNonNull(file.listFiles())) {
            if (!f.isFile()) {
                throw new IllegalArgumentException(String.format("%s is not a file.", f.getAbsolutePath()));
            }
            String extension = FileNameTools.extractExtension(f);
            if (!"jpg".equals(extension) && !"jpeg".equals(extension)) {
                throw new IllegalArgumentException(
                        String.format("Wrong file extension: got \".%s\" when \".jpg\" expected.", extension));
            }
        }
    }

    private File createTempPdfFile() throws IOException {
        File result = File.createTempFile("images", ".pdf");
        result.deleteOnExit();
        return result;
    }

    private void combineImagesIntoPDF(File sourceDir, File targetDir) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            List<String> imagePaths = Arrays.stream(Objects.requireNonNull(sourceDir.listFiles()))
                    .map(File::getAbsolutePath)
                    .collect(Collectors.toList());
            for (String path : imagePaths) {
                addImageAsNewPage(doc, path);
            }
            doc.save(targetDir);
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
}
