package converter;

import java.io.File;

public class DocxFile implements ToTxtConvertible, ToPdfConvertible {
    protected File file;

    public DocxFile(File file) {
        this.file = file;
    }

    @Override
    public PdfFile toPdf() {
        return null;
    }

    @Override
    public TxtFile toTxt() {
        return null;
    }

    public File getFile() {
        return file;
    }
}
