package com.example.invoice.service.impl;

import com.example.invoice.model.Invoice;
import com.example.invoice.model.Product;
import com.example.invoice.repository.InvoiceRepository;
import com.example.invoice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class InvoiceManagementServiceImplTest {

    @Autowired
    private InvoiceManagementServiceImpl invoiceManagementService;

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId("12345678");
        invoice.setCustomerId("1234");
        ArrayList<Product> products = new ArrayList<>();
        Product product = new Product();
        Product product1 = new Product();
        product.setProductId("12345");
        product.setName("Internet Package");
        product.setPrice((long) 100);
        product.setDiscount((double) 10);

        product1.setProductId("12341");
        product1.setName("SMS Package");
        product1.setPrice((long) 101);
        product1.setDiscount((double) 10);

        products.add(product);
        invoice.setProducts(products);

        //GIVEN
        productRepository.save(product);
        productRepository.save(product1);
        invoiceRepository.save(invoice);
    }

    @Test
    void getInvoiceTest() {
        //WHEN
        Optional<Invoice> invoiceOptional = invoiceManagementService.getInvoice("12345678");

        //THEN
        assertEquals("12345678", invoiceOptional.get().getInvoiceId());
    }

    @Test
    void deleteInvoiceTest() {

        //WHEN
        invoiceManagementService.deleteInvoice("12345678");

        //THEN
        assertFalse(invoiceRepository.findById("12345678").isPresent());
    }

    @Test
    void updateInvoiceTest() {
        //GIVEN
        Invoice testDataForUpdateInvoice = buildTestDataForUpdateInvoice();

        //WHEN
        Invoice updateInvoice = invoiceManagementService.updateInvoice(testDataForUpdateInvoice);

        //THEN
        assertEquals(2, invoiceRepository.findById(testDataForUpdateInvoice.getInvoiceId()).get().getProducts().size());
        assertEquals(99L, productRepository.findById("12345").get().getPrice());
    }

    public Invoice buildTestDataForUpdateInvoice() {
        Invoice invoiceUpdateTestData = new Invoice();
        invoiceUpdateTestData.setInvoiceId("12345678");
        invoiceUpdateTestData.setCustomerId("1234");
        ArrayList<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setName("SMS Package");
        product.setProductId("12341");
        product.setPrice((long) 100);
        product.setDiscount((double) 10);
        products.add(product);
        Product product1 = new Product();
        product1.setProductId("12345");
        product1.setName("Internet Package");
        product1.setPrice((long) 99);
        product1.setDiscount((double) 10);
        products.add(product1);

        invoiceUpdateTestData.setProducts(products);
        return invoiceUpdateTestData;
    }
}