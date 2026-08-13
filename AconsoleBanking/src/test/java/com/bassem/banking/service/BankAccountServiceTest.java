package com.bassem.banking.service;

import com.bassem.banking.AccountStatus;
import com.bassem.banking.AccountType;
import com.bassem.banking.BankAccount;
import com.bassem.banking.Customer;
import com.bassem.banking.dao.BankAccountDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankAccountServiceTest {

    private BankAccountDAO bankAccountDAO;
    private BankAccountService bankAccountService;

    private Customer customer;

    @BeforeEach
    void setUp() {

        bankAccountDAO = mock(BankAccountDAO.class);

        bankAccountService =
                new BankAccountService(bankAccountDAO);

        customer = new Customer(
                1L,
                "Bassem",
                "bassem@test.com",
                "hash"
        );
    }

    @Test
    void openAccount_shouldSaveActiveAccount() {

        BankAccount account =
                new BankAccount(
                        10L,
                        "ACC-10",
                        AccountType.CHECKING,
                        new BigDecimal("500.00"),
                        AccountStatus.ACTIVE,
                        customer
                );

        when(bankAccountDAO.save(account))
                .thenReturn(account);

        BankAccount result =
                bankAccountService.openAccount(account);

        assertEquals(account, result);
        assertEquals(AccountStatus.ACTIVE, result.getStatus());

        verify(bankAccountDAO).save(account);
    }

    @Test
    void openAccount_shouldRejectNullAccount() {

        assertThrows(
                IllegalArgumentException.class,
                () -> bankAccountService.openAccount(null)
        );

        verifyNoInteractions(bankAccountDAO);
    }

    @Test
    void openAccount_shouldRejectBlankAccountNumber() {

        BankAccount account =
                new BankAccount(
                        10L,
                        "",
                        AccountType.CHECKING,
                        new BigDecimal("500.00"),
                        AccountStatus.ACTIVE,
                        customer
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> bankAccountService.openAccount(account)
        );

        verify(bankAccountDAO, never()).save(any());
    }

    @Test
    void openAccount_shouldRejectNegativeBalance() {

        BankAccount account =
                new BankAccount(
                        10L,
                        "ACC-10",
                        AccountType.CHECKING,
                        new BigDecimal("-1.00"),
                        AccountStatus.ACTIVE,
                        customer
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> bankAccountService.openAccount(account)
        );

        verify(bankAccountDAO, never()).save(any());
    }

    @Test
    void openAccount_shouldRejectMissingOwner() {

        BankAccount account =
                new BankAccount(
                        10L,
                        "ACC-10",
                        AccountType.CHECKING,
                        new BigDecimal("500.00"),
                        AccountStatus.ACTIVE,
                        null
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> bankAccountService.openAccount(account)
        );
    }

    @Test
    void closeAccount_shouldCloseZeroBalanceAccount() {

        BankAccount account =
                new BankAccount(
                        10L,
                        "ACC-10",
                        AccountType.CHECKING,
                        BigDecimal.ZERO,
                        AccountStatus.ACTIVE,
                        customer
                );

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        bankAccountService.closeAccount(10L);

        assertEquals(
                AccountStatus.CLOSED,
                account.getStatus()
        );

        verify(bankAccountDAO).update(account);
    }

    @Test
    void closeAccount_shouldRejectMissingAccount() {

        when(bankAccountDAO.findById(10L))
                .thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> bankAccountService.closeAccount(10L)
        );

        verify(bankAccountDAO, never()).update(any());
    }

    @Test
    void closeAccount_shouldRejectNonZeroBalance() {

        BankAccount account =
                new BankAccount(
                        10L,
                        "ACC-10",
                        AccountType.CHECKING,
                        new BigDecimal("100.00"),
                        AccountStatus.ACTIVE,
                        customer
                );

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        assertThrows(
                IllegalArgumentException.class,
                () -> bankAccountService.closeAccount(10L)
        );

        assertEquals(
                AccountStatus.ACTIVE,
                account.getStatus()
        );

        verify(bankAccountDAO, never()).update(any());
    }

    @Test
    void closeAccount_shouldRejectAlreadyClosedAccount() {

        BankAccount account =
                new BankAccount(
                        10L,
                        "ACC-10",
                        AccountType.CHECKING,
                        BigDecimal.ZERO,
                        AccountStatus.CLOSED,
                        customer
                );

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        assertThrows(
                IllegalArgumentException.class,
                () -> bankAccountService.closeAccount(10L)
        );

        verify(bankAccountDAO, never()).update(any());
    }

    @Test
    void findAccountById_shouldReturnAccount() {

        BankAccount account =
                new BankAccount(
                        10L,
                        "ACC-10",
                        AccountType.CHECKING,
                        BigDecimal.ZERO,
                        AccountStatus.ACTIVE,
                        customer
                );

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        assertEquals(
                account,
                bankAccountService.findAccountById(10L)
        );
    }

    @Test
    void findAllAccounts_shouldReturnAccounts() {

        when(bankAccountDAO.findAll())
                .thenReturn(java.util.List.of());

        assertNotNull(
                bankAccountService.findAllAccounts()
        );

        verify(bankAccountDAO).findAll();
    }

    @Test
    void updateAccount_shouldRejectNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> bankAccountService.updateAccount(null)
        );

        verify(bankAccountDAO, never()).update(any());
    }
}