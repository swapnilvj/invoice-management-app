package com.example.invoice.service.impl;

import com.example.invoice.model.Customer;
import com.example.invoice.model.Invoice;
import com.example.invoice.model.Product;
import com.example.invoice.repository.CustomerRepository;
import com.example.invoice.repository.InvoiceRepository;
import com.example.invoice.repository.ProductRepository;
import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class InvoiceManagementServiceImplTest {

    @Autowired
    private InvoiceManagementServiceImpl invoiceManagementService;

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;

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

    @Test
    void importInvoiceTest() throws IOException, DocumentException {
        //GIVEN
        Invoice testDataForUpdateInvoice = buildTestDataForImportInvoice();

        //WHEN
        String importInvoice = invoiceManagementService.importInvoice("12345679");

        System.out.println(importInvoice);
        //THEN
        Resource resource = new UrlResource(Paths.get(importInvoice).toUri());
        assertTrue(resource.getFile().exists());
        assertTrue(resource.getFile().length() > 0);
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

    public Invoice buildTestDataForImportInvoice() {
        Invoice invoiceImportTestData = new Invoice();
        invoiceImportTestData.setInvoiceId("12345679");
        invoiceImportTestData.setCustomerId("1235");
        invoiceImportTestData.setImported(false);
        ArrayList<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setName("SMS Package");
        product.setProductId("12349");
        product.setPrice((long) 91);
        product.setDiscount((double) 10);
        products.add(product);
        Product product1 = new Product();
        product1.setProductId("12348");
        product1.setName("Internet Package");
        product1.setPrice((long) 99);
        product1.setDiscount((double) 10);
        products.add(product1);

        invoiceImportTestData.setProducts(products);
        productRepository.save(product);
        productRepository.save(product1);
        invoiceRepository.save(invoiceImportTestData);

        Customer customer = new Customer();
        customer.setName("Test User1");
        customer.setEmail("test.user1@gmail.com");
        customer.setCustomerId("1235");
        customerRepository.save(customer);
        return invoiceImportTestData;
    }
}