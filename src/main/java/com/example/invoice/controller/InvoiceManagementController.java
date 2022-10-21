package com.example.invoice.controller;

import com.example.invoice.model.Invoice;
import com.example.invoice.service.impl.InvoiceManagementServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Optional;

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
            return new ResponseEntity<>(importInvoice.get(), HttpStatus.OK);
        } catch (NoSuchElementException noSuchElementException) {
            logger.error(noSuchElementException.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @PostMapping("/invoice")
    public ResponseEntity<Invoice> updateInvoice(@RequestBody Invoice invoiceUpdate) {
        try {
            if(invoiceUpdate.getProducts().isEmpty()) {
                throw new RuntimeException("Operation Not Allowed.");
            }
            Invoice importInvoice = service.updateInvoice(invoiceUpdate);
            return new ResponseEntity<>(importInvoice, HttpStatus.OK);
        } catch (NoSuchElementException noSuchElementException) {
            logger.error(noSuchElementException.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
