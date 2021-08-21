package com.telegram.convertations.converters;

import java.io.File;
import java.util.List;

public interface Converter {
    File convert(List<File> files);
}
