package com.example.invoice.service.impl;

import com.example.invoice.model.Invoice;
import com.example.invoice.model.Product;
import com.example.invoice.repository.InvoiceRepository;
import com.example.invoice.repository.ProductRepository;
import com.example.invoice.service.InvoiceManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceManagementServiceImpl implements InvoiceManagementService {

    private final static Logger logger = LoggerFactory.getLogger(InvoiceManagementServiceImpl.class);

    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;

    @Autowired
    public InvoiceManagementServiceImpl(InvoiceRepository invoiceRepository, ProductRepository productRepository) {
        this.invoiceRepository = invoiceRepository;
        this.productRepository = productRepository;
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
