package converter;

public class PdfFile implements ToTxtConvertible, ToDocxConvertible {
    @Override
    public DocxFile toDocx() {
        return null;
    }

    @Override
    public TxtFile toTxt() {
        return null;
    }
}
