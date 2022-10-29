package com.example.invoice.service;

import com.example.invoice.model.Invoice;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public interface InvoiceManagementService {

    Optional<Invoice> getInvoice(String invoiceId);
    Invoice updateInvoice(Invoice invoiceUpdate);
    void deleteInvoice(String invoiceId);
    String importInvoice(String invoiceId) throws DocumentException, IOException;
}
