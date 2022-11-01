package com.example.invoice.helper;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.example.invoice.helper.InvoiceManagementHelper.PDF_FILENAME_FORMAT;

public class PdfFileGenerator implements FileGenerator {

    @Override
    public String generateFile(String fileName, String fileLocation, String fileContent) throws IOException, DocumentException {
        Path path = Paths.get(fileLocation);
        Files.createDirectories(path);
        File importInvoiceFile = new File(path.toFile(), String.format(PDF_FILENAME_FORMAT, fileName));
        FileOutputStream fileOutputStream = new FileOutputStream(importInvoiceFile);
        Document document = new Document();
        PdfWriter.getInstance(document, fileOutputStream);

        document.open();
        Paragraph invoiceParagraph = new Paragraph();
        invoiceParagraph.add(fileContent);
        document.add(invoiceParagraph);
        document.close();

        return importInvoiceFile.getAbsolutePath();
    }
}
