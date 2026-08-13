package com.bassem.banking.service;

import com.bassem.banking.Customer;
import com.bassem.banking.dao.CustomerDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    private CustomerDAO customerDAO;
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerDAO = mock(CustomerDAO.class);
        customerService = new CustomerService(customerDAO);
    }

    @Test
    void registerCustomer_shouldSaveCustomer() {

        Customer customer = new Customer(
                1L,
                "Bassem",
                "bassem@test.com",
                "password123"
        );

        when(customerDAO.findByEmail("bassem@test.com"))
                .thenReturn(null);

        customerService.registerCustomer(customer);

        verify(customerDAO).save(customer);

        assertNotEquals(
                "password123",
                customer.getPasswordHash()
        );
    }

    @Test
    void registerCustomer_shouldRejectNullCustomer() {

        assertThrows(
                IllegalArgumentException.class,
                () -> customerService.registerCustomer(null)
        );

        verifyNoInteractions(customerDAO);
    }

    @Test
    void registerCustomer_shouldRejectBlankName() {

        Customer customer = new Customer(
                1L,
                " ",
                "bassem@test.com",
                "password123"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> customerService.registerCustomer(customer)
        );

        verify(customerDAO, never()).save(any());
    }

    @Test
    void registerCustomer_shouldRejectBlankEmail() {

        Customer customer = new Customer(
                1L,
                "Bassem",
                "",
                "password123"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> customerService.registerCustomer(customer)
        );
    }

    @Test
    void registerCustomer_shouldRejectBlankPassword() {

        Customer customer = new Customer(
                1L,
                "Bassem",
                "bassem@test.com",
                ""
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> customerService.registerCustomer(customer)
        );
    }

    @Test
    void registerCustomer_shouldRejectDuplicateEmail() {

        Customer existing = new Customer(
                2L,
                "Existing",
                "bassem@test.com",
                "hashed"
        );

        Customer customer = new Customer(
                1L,
                "Bassem",
                "bassem@test.com",
                "password123"
        );

        when(customerDAO.findByEmail("bassem@test.com"))
                .thenReturn(existing);

        assertThrows(
                IllegalArgumentException.class,
                () -> customerService.registerCustomer(customer)
        );

        verify(customerDAO, never()).save(any());
    }

    @Test
    void login_shouldReturnCustomerForCorrectPassword() {

        Customer customer = new Customer(
                1L,
                "Bassem",
                "bassem@test.com",
                "password123"
        );

        // Register first so passwordHash becomes a real BCrypt hash.
        when(customerDAO.findByEmail("bassem@test.com"))
                .thenReturn(null);

        customerService.registerCustomer(customer);

        reset(customerDAO);

        when(customerDAO.findByEmail("bassem@test.com"))
                .thenReturn(customer);

        Customer result =
                customerService.login(
                        "bassem@test.com",
                        "password123"
                );

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void login_shouldReturnNullForWrongPassword() {

        Customer customer = new Customer(
                1L,
                "Bassem",
                "bassem@test.com",
                "password123"
        );

        when(customerDAO.findByEmail("bassem@test.com"))
                .thenReturn(null);

        customerService.registerCustomer(customer);

        reset(customerDAO);

        when(customerDAO.findByEmail("bassem@test.com"))
                .thenReturn(customer);

        Customer result =
                customerService.login(
                        "bassem@test.com",
                        "wrongPassword"
                );

        assertNull(result);
    }

    @Test
    void login_shouldReturnNullForUnknownEmail() {

        when(customerDAO.findByEmail("missing@test.com"))
                .thenReturn(null);

        Customer result =
                customerService.login(
                        "missing@test.com",
                        "password"
                );

        assertNull(result);
    }

    @Test
    void updateProfile_shouldCallDao() {

        Customer customer = new Customer(
                1L,
                "Updated",
                "updated@test.com",
                "password"
        );

        customerService.updateProfile(customer);

        verify(customerDAO).update(customer);
    }

    @Test
    void findCustomerById_shouldReturnCustomer() {

        Customer customer = new Customer(
                1L,
                "Bassem",
                "bassem@test.com",
                "hash"
        );

        when(customerDAO.findById(1L))
                .thenReturn(customer);

        Customer result =
                customerService.findCustomerById(1L);

        assertEquals(customer, result);
    }

    @Test
    void findAllCustomers_shouldReturnCustomers() {

        Customer customer = new Customer(
                1L,
                "Bassem",
                "bassem@test.com",
                "hash"
        );

        when(customerDAO.findAll())
                .thenReturn(java.util.List.of(customer));

        var result =
                customerService.findAllCustomers();

        assertEquals(1, result.size());
        assertEquals(customer, result.get(0));
    }
}