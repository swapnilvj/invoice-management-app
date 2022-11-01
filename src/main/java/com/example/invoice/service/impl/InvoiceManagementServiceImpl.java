package com.example.invoice.service.impl;

import com.example.invoice.helper.PdfFileGenerator;
import com.example.invoice.model.Customer;
import com.example.invoice.model.Invoice;
import com.example.invoice.model.Product;
import com.example.invoice.repository.CustomerRepository;
import com.example.invoice.repository.InvoiceRepository;
import com.example.invoice.repository.ProductRepository;
import com.example.invoice.service.InvoiceManagementService;
import com.itextpdf.text.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.invoice.helper.InvoiceManagementHelper.*;

@Service
@Configuration
public class InvoiceManagementServiceImpl implements InvoiceManagementService {

    private final static Logger logger = LoggerFactory.getLogger(InvoiceManagementServiceImpl.class);

    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    @Value("${invoice.pdf.location}")
    private String fileLocation;

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

            String pdfInvoice = generatePdfInvoice(invoiceId, customer, products);
            updateInvoiceImportedFlag(invoice);
            return pdfInvoice;
        } catch (DocumentException e) {
            logger.error(e.getMessage());
            throw e;
        }

    }

    @Override
    public void mockLoadInvoices() {
        Invoice invoiceImportTestData = new Invoice();
        invoiceImportTestData.setInvoiceId("12345679");
        invoiceImportTestData.setCustomerId("1235");
        invoiceImportTestData.setImported(false);
        ArrayList<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setName("SMS Package");
        product.setProductId("12349");
        product.setPrice(91);
        product.setDiscount(10);
        products.add(product);
        Product product1 = new Product();
        product1.setProductId("12348");
        product1.setName("Internet Package");
        product1.setPrice(99);
        product1.setDiscount(10);
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
    }

    private void updateInvoiceImportedFlag(Invoice invoice) {
        invoice.setImported(true);
        invoiceRepository.save(invoice);
    }

    private String generatePdfInvoice(String invoiceId, Customer customer, List<Product> products) throws DocumentException, IOException {
        String invoiceData = buildInvoiceData(products, customer);

        PdfFileGenerator pdfFileGenerator = new PdfFileGenerator();
        return pdfFileGenerator.generateFile(invoiceId, this.fileLocation, invoiceData);
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

    private String buildInvoiceData(List<Product> products, Customer customer) {
        float totalPrice = 0;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(INVOICE_TEMPLATE_TITLE, customer.getName()));
        for (Product product : products) {

            float discountedPrice = calculateDiscountedPrice(product);

            stringBuilder.append(String.format(INVOICE_TEMPLATE_BODY, product.getName(), discountedPrice));
            totalPrice += discountedPrice;
        }
        stringBuilder.append(String.format(INVOICE_TEMPLATE_FOOTER, totalPrice));
        return stringBuilder.toString();
    }
}
