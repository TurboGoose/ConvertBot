package converter;

public class TxtFile implements ToPdfConvertible, ToDocxConvertible {
    @Override
    public DocxFile toDocx() {
        return null;
    }

    @Override
    public PdfFile toPdf() {
        return null;
    }
}
