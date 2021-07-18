package convertations.converters;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class JpgToPdfConverterTest {
    Converter converter = new JpgToPdfConverter();

    @Test
    public void convertThenCheckResultExisting() throws Exception {
        File sourceFile = new File(ClassLoader.getSystemResource("jpgToPdfTests/pics").toURI());
        File targetFile = converter.convert(sourceFile);
        assertThat(targetFile.exists(), is(true));
    }
}
