package com.example.invoice.repository;

import com.example.invoice.model.Customer;
import com.example.invoice.model.Invoice;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, String> {

}
