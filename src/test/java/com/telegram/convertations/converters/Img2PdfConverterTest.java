package com.telegram.convertations.converters;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class Img2PdfConverterTest {
    Converter converter = new Img2PdfConverter();

    @Test
    public void convertThenCheckResultExisting() throws Exception {
        List<File> sourceFiles = Arrays.asList(Objects.requireNonNull(
                new File(ClassLoader.getSystemResource("jpgToPdfTests/pics").toURI()).listFiles()));
        File targetFile = converter.convert(sourceFiles);
        assertThat(targetFile.exists(), is(true));
        String fineName = targetFile.getName();
        assertThat(fineName.substring(fineName.lastIndexOf(".")), is(".pdf"));
    }
}
