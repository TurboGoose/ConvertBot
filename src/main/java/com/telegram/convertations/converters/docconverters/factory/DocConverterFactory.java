package com.telegram.convertations.converters.docconverters.factory;

import com.telegram.convertations.conversions.Conversion;
import com.telegram.convertations.conversions.FileType;
import com.telegram.convertations.converters.docconverters.DocConverter;
import com.telegram.convertations.converters.docconverters.PdfToTxtDocConverter;
import com.telegram.convertations.converters.docconverters.TxtToPdfDocConverter;

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
