package com.bassem.banking.service;

import com.bassem.banking.Customer;
import com.bassem.banking.dao.CustomerDAO;

import com.bassem.banking.PasswordUtil;
import java.util.List;

public class CustomerService
{

    private final CustomerDAO customerDAO;

    public CustomerService(CustomerDAO customerDAO){
           this.customerDAO = customerDAO;
    }

    // 1- ......create/register a new customer.....

    public void registerCustomer(Customer customer) {

        //  Check if email already exists
        Customer existingCustomer =
                customerDAO.findByEmail(customer.getEmail());

        if (existingCustomer != null) {
            throw new IllegalArgumentException(
                    "Email already exists."
            );
        }

        //   Hash the password
        String hashedPassword =
                PasswordUtil.hashPassword(customer.getPasswordHash());

        //  Store the hashed password in the Customer
        customer.setPasswordHash(hashedPassword);

        //  Save the customer
        customerDAO.save(customer);
    }


      //2 ---------- Login Customer ----------

    public Customer login(String email, String password) {

        // Find customer by email
        Customer customer = customerDAO.findByEmail(email);

        if (customer == null) {
            return null;
        }

        // Password check will be added here
          boolean passPassword =
                  PasswordUtil.checkPassword(
                          password,customer.getPasswordHash()
                  );
                  return passPassword ?  customer: null;
    }

    // 3 --------- Update Profile ------

    public void updateProfile(Customer customer){

              customerDAO.update(customer);
    }

    //4 --------- Find Customer By ID ----------

    public Customer findCustomerById(Long id) {
        return customerDAO.findById(id);
    }

    //5 --------- Find All Customer ----------

     public List<Customer> findAllCustomers(){
        return  customerDAO.findAll();
     }


}
