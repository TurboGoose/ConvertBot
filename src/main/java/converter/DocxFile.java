package converter;

public class DocxFile implements ToTxtConvertible, ToPdfConvertible {
    @Override
    public PdfFile toPdf() {
        return null;
    }

    @Override
    public TxtFile toTxt() {
        return null;
    }
}
