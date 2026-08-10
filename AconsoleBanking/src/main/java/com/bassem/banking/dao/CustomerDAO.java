package com.bassem.banking.dao;

import com.bassem.banking.Customer;
import java.util.List;

public interface CustomerDAO {

    Customer save(Customer customer);

    Customer findById(Long id);

    Customer findByEmail(String email);

    List<Customer> findAll();

    void update(Customer customer);

    void delete(Long id);

}
