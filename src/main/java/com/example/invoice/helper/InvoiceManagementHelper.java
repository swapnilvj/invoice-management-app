package com.example.invoice.helper;

import com.example.invoice.model.Product;

public class InvoiceManagementHelper {

    public static final String DELETE_INVOICE_SUCCESSFUL_FOR_INVOICE_ID = "Delete Invoice Successful for Invoice ID: %s";
    public static final String INVOICE_TEMPLATE_TITLE = "Dear %s,\n" +
            "Below are your invoice details\n";
    public static final String INVOICE_TEMPLATE_BODY = "%-20s: %.2f €\n";
    public static final String INVOICE_TEMPLATE_FOOTER = "<Total>: %.2f €";
    public static final String PDF_FILENAME_FORMAT = "%s.pdf";

    public static float calculateDiscountedPrice(Product product) {
        float discountVal = product.getPrice() * product.getDiscount() / 100;
        return product.getPrice() - discountVal;
    }
}
