package converter;

import java.io.File;
import java.io.IOException;

public class TxtFile implements ToPdfConvertible, ToDocxConvertible {
    protected File file;

    public TxtFile(File file) {
        this.file = file;
    }

    @Override
    public DocxFile toDocx() throws IOException {
        return null;
    }

    @Override
    public PdfFile toPdf() throws IOException {
        return null;
    }

    public File getFile() {
        return file;
    }
}
