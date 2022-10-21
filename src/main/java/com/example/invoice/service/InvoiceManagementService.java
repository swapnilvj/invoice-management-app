package com.example.invoice.service;

import com.example.invoice.model.Invoice;

import java.util.Optional;

public interface InvoiceManagementService {

    Optional<Invoice> getInvoice(String invoiceId);
    Invoice updateInvoice(Invoice invoiceUpdate);
}
