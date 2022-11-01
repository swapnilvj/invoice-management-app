package com.example.invoice.helper;

import java.io.IOException;

public interface FileGenerator {
    String generateFile(String fileName, String fileLocation, String fileContent) throws Exception;
}
