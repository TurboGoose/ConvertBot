package convertations.converters.imgconverters;

import java.io.File;
import java.util.List;

public interface ImgConverter {
    File convert(List<File> files);
}
