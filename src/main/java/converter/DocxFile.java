package converter;

import java.io.File;
import java.io.IOException;

public class DocxFile implements ToTxtConvertible, ToPdfConvertible {
    protected File file;

    public DocxFile(File file) {
        this.file = file;
    }

    @Override
    public PdfFile toPdf() throws IOException {
        return null;
    }

    @Override
    public TxtFile toTxt() throws IOException {
        return null;
    }

    public File getFile() {
        return file;
    }
}
