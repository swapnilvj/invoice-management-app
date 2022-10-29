package com.example.invoice.service.impl;

import com.example.invoice.model.Customer;
import com.example.invoice.model.Invoice;
import com.example.invoice.model.Product;
import com.example.invoice.repository.CustomerRepository;
import com.example.invoice.repository.InvoiceRepository;
import com.example.invoice.repository.ProductRepository;
import com.example.invoice.service.InvoiceManagementService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceManagementServiceImpl implements InvoiceManagementService {

    private final static Logger logger = LoggerFactory.getLogger(InvoiceManagementServiceImpl.class);

    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public InvoiceManagementServiceImpl(InvoiceRepository invoiceRepository, ProductRepository productRepository, CustomerRepository customerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }


    @Override
    public Optional<Invoice> getInvoice(String invoiceId) {
        logger.info(String.format("View Invoice request for Invoice Id: %s", invoiceId));
        return invoiceRepository.findById(invoiceId);
    }

    @Override
    public Invoice updateInvoice(Invoice invoiceUpdate) {
        logger.info(String.format("Update Invoice request for Invoice Id: %s", invoiceUpdate.getInvoiceId()));
        final Optional<Invoice> invoiceOptional = invoiceRepository.findById(invoiceUpdate.getInvoiceId());

        Invoice invoice = invoiceOptional.get();
        List<Product> invoiceProducts = invoice.getProducts();
        List<Product> finalProductsForInvoice = new ArrayList<>();
        List<Product> updateProducts = invoiceUpdate.getProducts();

        finalProductsForInvoice.addAll(getNewProducts(invoiceProducts, updateProducts));

        List<Product> updatedExistingProducts = getUpdatedExistingProducts(invoiceProducts, updateProducts);

        finalProductsForInvoice.addAll(updatedExistingProducts);

        invoice.setProducts(finalProductsForInvoice);
        logger.info(String.format("Updated Invoice: %s", invoice.toString()));

        productRepository.saveAll(updatedExistingProducts);
        return invoiceRepository.save(invoice);
    }

    @Override
    public void deleteInvoice(String invoiceId) {
        logger.info(String.format("Delete Invoice request for Invoice Id: %s", invoiceId));

        invoiceRepository.deleteById(invoiceId);
    }

    @Override
    public String importInvoice(String invoiceId) throws DocumentException, IOException {
        logger.info(String.format("Import Invoice request for Invoice Id: %s", invoiceId));
        Document document = new Document();

        File importInvoiceFile = new File("test.pdf");  //TODO - configurable file-location and file-name
        FileOutputStream fileOutputStream = new FileOutputStream(importInvoiceFile);
        try {
            Optional<Invoice> invoiceOptional = invoiceRepository.findById(invoiceId);
            if (!invoiceOptional.isPresent()) {
                throw new RuntimeException("Invoice Not Found.");
            }
            Invoice invoice = invoiceOptional.get();
            if (invoice.getImported()) {
                throw new RuntimeException("Invoice Already Imported.");
            }

            String customerId = invoice.getCustomerId();

            if (!customerRepository.findById(customerId).isPresent()) {
                throw new RuntimeException("Invalid Invoice Data.");
            }
            Customer customer = customerRepository.findById(customerId).get();

            List<Product> products = invoice.getProducts();

            PdfWriter pdfWriter = PdfWriter.getInstance(document, fileOutputStream);
            document.open();
            Paragraph invoiceParagraph = new Paragraph();
            invoiceParagraph.add(String.format("Dear %s,\n", customer.getName()));  //TODO: use template
            invoiceParagraph.add("Below are your invoice details\n");
            for (Product product : products) {

                invoiceParagraph.add(String.format("%s: %d €\n", product.getName(), product.getPrice()));
            }

            document.add(invoiceParagraph);
            byte[] pdfBytes = pdfWriter.getDirectContent().toPdf(pdfWriter);
            document.close();

            return importInvoiceFile.getAbsolutePath();
        } catch (DocumentException e) {
            logger.error(e.getMessage());
            throw e;
        }

    }

    private List<Product> getUpdatedExistingProducts(List<Product> invoiceProducts, List<Product> updateProducts) {
        List<Product> existingProducts = updateProducts.stream().filter(updateProduct -> invoiceProducts.contains(updateProduct))
                .collect(Collectors.toList());
        logger.info("Replace Existing Products count:" + existingProducts.size());
        return existingProducts;
    }

    private List<Product> getNewProducts(List<Product> invoiceProducts, List<Product> updateProducts) {
        List<Product> newProducts = updateProducts.stream().filter(updateProduct -> !invoiceProducts.contains(updateProduct))
                .collect(Collectors.toList());
        logger.info("New Products count:" + newProducts.size());
        return newProducts;
    }

}
