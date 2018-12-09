package com.bluefiddleguy.sparser.render;

import java.io.File;
import java.io.InputStream;

public interface TokenGeneratorFactory  {
    public TemplateTokenGenerator fromTemplateFile(InputStream templateSource);
}
