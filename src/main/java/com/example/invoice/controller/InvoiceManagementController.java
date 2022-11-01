package com.example.invoice.controller;

import com.example.invoice.model.Invoice;
import com.example.invoice.service.impl.InvoiceManagementServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.example.invoice.helper.InvoiceManagementHelper.DELETE_INVOICE_SUCCESSFUL_FOR_INVOICE_ID;

@RestController
public class InvoiceManagementController {

    private final static Logger logger = LoggerFactory.getLogger(InvoiceManagementController.class);

    private InvoiceManagementServiceImpl service;

    @Autowired
    public InvoiceManagementController(InvoiceManagementServiceImpl service) {
        this.service = service;
    }

    @ResponseBody
    @GetMapping("/invoice")
    public ResponseEntity<Invoice> getInvoice(@RequestParam String invoiceId) {
        try {
            Optional<Invoice> importInvoice = service.getInvoice(invoiceId);

            return importInvoice.map(invoice -> new ResponseEntity<>(invoice, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (NoSuchElementException noSuchElementException) {
            logger.error("Invoice Not Found.");
            logger.error(noSuchElementException.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            logger.error("Get Invoice Request Failed.");
            logger.error(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @PutMapping("/invoice")
    public ResponseEntity<Invoice> updateInvoice(@RequestBody Invoice invoiceUpdate) {
        try {
            if (invoiceUpdate.getProducts().isEmpty()) {
                throw new RuntimeException("Operation Not Allowed.");
            }
            Invoice importInvoice = service.updateInvoice(invoiceUpdate);
            return new ResponseEntity<>(importInvoice, HttpStatus.OK);
        } catch (NoSuchElementException noSuchElementException) {
            logger.error(noSuchElementException.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            logger.error("Update Invoice Request Failed.");
            logger.error(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @DeleteMapping("/invoice")
    public ResponseEntity<String> deleteInvoice(@RequestParam String invoiceId) {
        try {
            service.deleteInvoice(invoiceId);
            return new ResponseEntity<>(String.format(DELETE_INVOICE_SUCCESSFUL_FOR_INVOICE_ID, invoiceId),
                    HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Delete Invoice Request Failed.");
            logger.error(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @GetMapping(value = "/invoice/importpdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> importInvoice(@RequestParam String invoiceId, HttpServletRequest request) {
        try {
            String invoicePdf = service.importInvoice(invoiceId);

            Resource resource = new UrlResource(Paths.get(invoicePdf).toUri());
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);


        } catch (Exception ex) {

            logger.error("Import Invoice PDF Request Failed.");
            logger.error(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
