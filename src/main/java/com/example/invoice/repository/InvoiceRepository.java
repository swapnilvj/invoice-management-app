package com.example.invoice.repository;

import com.example.invoice.model.Invoice;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InvoiceRepository extends CrudRepository<Invoice, String> {

    Optional<Invoice> findByInvoiceIdAndIsImported(String invoiceId, Boolean isImported);
}
