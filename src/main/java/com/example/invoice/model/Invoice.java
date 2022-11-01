package com.example.invoice.model;

import com.example.invoice.helper.InvoiceManagementHelper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Table(name = "Invoice")
@Entity
public class Invoice {

    @JsonProperty("InvoiceId")
    @Id
    @Size(min = 8, max = 8)
    private String invoiceId;

    @JsonProperty("CustomerId")
    @Size(min = 4, max = 4)
    private String customerId;
    @JsonProperty("Products")
    @OneToMany(targetEntity=Product.class, fetch=FetchType.EAGER)
    @Size(min = 1)
    private List<Product> products;
    private Boolean isImported;

    public Invoice() {
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @JsonIgnore
    public Boolean getImported() {
        return isImported;
    }

    public void setImported(Boolean imported) {
        isImported = imported;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceId='" + invoiceId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", products=" + products +
                ", isImported=" + isImported +
                '}';
    }
}
