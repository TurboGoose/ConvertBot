package convertations.converters.docconverters.factory;

import convertations.conversions.Conversion;
import convertations.conversions.FileType;
import convertations.converters.docconverters.DocConverter;
import convertations.converters.docconverters.PdfToTxtDocConverter;
import convertations.converters.docconverters.TxtToPdfDocConverter;

public class DocConverterFactory implements AbstractDocConverterFactory {
    @Override
    public DocConverter getConverter(Conversion conversion) {
        if (conversion.getFrom().equals(FileType.PDF) && conversion.getTo().equals(FileType.TXT)) {
            return new PdfToTxtDocConverter();
        }
        if (conversion.getFrom().equals(FileType.TXT) && conversion.getTo().equals(FileType.PDF)) {
            return new TxtToPdfDocConverter();
        }
        return null;
    }
}
