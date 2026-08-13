package com.bassem.banking.service;

import com.bassem.banking.AccountStatus;
import com.bassem.banking.AccountType;
import com.bassem.banking.BankAccount;
import com.bassem.banking.Customer;
import com.bassem.banking.DatabaseConnection;
import com.bassem.banking.Transaction;
import com.bassem.banking.TransactionType;
import com.bassem.banking.dao.BankAccountDAO;
import com.bassem.banking.dao.TransactionDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private TransactionDAO transactionDAO;
    private BankAccountDAO bankAccountDAO;
    private TransactionService transactionService;

    private Customer customer;
    private BankAccount account;

    @BeforeEach
    void setUp() {

        transactionDAO = mock(TransactionDAO.class);
        bankAccountDAO = mock(BankAccountDAO.class);

        transactionService =
                new TransactionService(
                        transactionDAO,
                        bankAccountDAO
                );

        customer =
                new Customer(
                        1L,
                        "Bassem",
                        "bassem@test.com",
                        "hash"
                );

        account =
                new BankAccount(
                        10L,
                        "ACC-10",
                        AccountType.CHECKING,
                        new BigDecimal("500.00"),
                        AccountStatus.ACTIVE,
                        customer
                );
    }

    private Connection mockPostgresConnection() {

        Connection connection =
                mock(Connection.class);

        return connection;
    }

    @Test
    void deposit_shouldIncreaseBalanceAndSaveTransaction() throws Exception {

        Connection connection =
                mockPostgresConnection();

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        when(transactionDAO.save(
                eq(connection),
                any(Transaction.class)
        )).thenAnswer(
                invocation -> invocation.getArgument(1)
        );

        try (MockedStatic<DatabaseConnection> database =
                     mockStatic(DatabaseConnection.class)) {

            database.when(DatabaseConnection::getConnection)
                    .thenReturn(connection);

            Transaction result =
                    transactionService.deposit(
                            customer,
                            10L,
                            new BigDecimal("100.00")
                    );

            assertEquals(
                    new BigDecimal("600.00"),
                    result.getResultingBalance()
            );

            assertEquals(
                    new BigDecimal("600.00"),
                    account.getBalance()
            );

            assertEquals(
                    TransactionType.DEPOSIT,
                    result.getType()
            );

            assertNotNull(result.getId());

            verify(bankAccountDAO)
                    .update(connection, account);

            verify(transactionDAO)
                    .save(eq(connection), any(Transaction.class));

            verify(connection)
                    .commit();
        }
    }

    @Test
    void deposit_shouldRejectZeroAmount() {

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.deposit(
                        customer,
                        10L,
                        BigDecimal.ZERO
                )
        );

        verifyNoInteractions(transactionDAO);
    }

    @Test
    void deposit_shouldRejectNegativeAmount() {

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.deposit(
                        customer,
                        10L,
                        new BigDecimal("-10.00")
                )
        );
    }

    @Test
    void deposit_shouldRejectUnauthorizedCustomer() {

        Customer otherCustomer =
                new Customer(
                        99L,
                        "Other",
                        "other@test.com",
                        "hash"
                );

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.deposit(
                        otherCustomer,
                        10L,
                        new BigDecimal("100.00")
                )
        );
    }

    @Test
    void deposit_shouldRollbackWhenTransactionSaveFails() throws Exception {

        Connection connection =
                mockPostgresConnection();

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        when(transactionDAO.save(
                eq(connection),
                any(Transaction.class)
        )).thenThrow(
                new RuntimeException("Database failure")
        );

        try (MockedStatic<DatabaseConnection> database =
                     mockStatic(DatabaseConnection.class)) {

            database.when(DatabaseConnection::getConnection)
                    .thenReturn(connection);

            assertThrows(
                    RuntimeException.class,
                    () -> transactionService.deposit(
                            customer,
                            10L,
                            new BigDecimal("100.00")
                    )
            );

            verify(connection).rollback();
            verify(connection, never()).commit();
        }
    }

    @Test
    void withdraw_shouldDecreaseBalance() throws Exception {

        Connection connection =
                mockPostgresConnection();

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        when(transactionDAO.save(
                eq(connection),
                any(Transaction.class)
        )).thenAnswer(
                invocation -> invocation.getArgument(1)
        );

        try (MockedStatic<DatabaseConnection> database =
                     mockStatic(DatabaseConnection.class)) {

            database.when(DatabaseConnection::getConnection)
                    .thenReturn(connection);

            Transaction result =
                    transactionService.withdraw(
                            customer,
                            10L,
                            new BigDecimal("100.00")
                    );

            assertEquals(
                    new BigDecimal("400.00"),
                    result.getResultingBalance()
            );

            assertEquals(
                    new BigDecimal("400.00"),
                    account.getBalance()
            );

            assertEquals(
                    TransactionType.WITHDRAW,
                    result.getType()
            );

            assertNotNull(result.getId());

            verify(connection).commit();
        }
    }

    @Test
    void withdraw_shouldRejectInsufficientBalance() {

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.withdraw(
                        customer,
                        10L,
                        new BigDecimal("600.00")
                )
        );
    }

    @Test
    void withdraw_shouldRejectClosedAccount() {

        account.setStatus(AccountStatus.CLOSED);

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.withdraw(
                        customer,
                        10L,
                        new BigDecimal("50.00")
                )
        );
    }

    @Test
    void transfer_shouldUpdateBothAccountsAndCommit() throws Exception {

        Connection connection =
                mockPostgresConnection();

        BankAccount destination =
                new BankAccount(
                        20L,
                        "ACC-20",
                        AccountType.SAVING,
                        new BigDecimal("300.00"),
                        AccountStatus.ACTIVE,
                        new Customer(
                                2L,
                                "Destination",
                                "destination@test.com",
                                "hash"
                        )
                );

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        when(bankAccountDAO.findById(20L))
                .thenReturn(destination);

        when(transactionDAO.save(
                eq(connection),
                any(Transaction.class)
        )).thenAnswer(
                invocation -> invocation.getArgument(1)
        );

        try (MockedStatic<DatabaseConnection> database =
                     mockStatic(DatabaseConnection.class)) {

            database.when(DatabaseConnection::getConnection)
                    .thenReturn(connection);

            transactionService.transfer(
                    customer,
                    10L,
                    20L,
                    new BigDecimal("100.00")
            );

            assertEquals(
                    new BigDecimal("400.00"),
                    account.getBalance()
            );

            assertEquals(
                    new BigDecimal("400.00"),
                    destination.getBalance()
            );

            verify(bankAccountDAO)
                    .update(connection, account);

            verify(bankAccountDAO)
                    .update(connection, destination);

            verify(transactionDAO, times(2))
                    .save(
                            eq(connection),
                            any(Transaction.class)
                    );

            verify(connection)
                    .commit();
        }
    }

    @Test
    void transfer_shouldRejectSameAccount() {

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.transfer(
                        customer,
                        10L,
                        10L,
                        new BigDecimal("100.00")
                )
        );
    }

    @Test
    void transfer_shouldRejectInsufficientBalance() {

        BankAccount destination =
                new BankAccount(
                        20L,
                        "ACC-20",
                        AccountType.SAVING,
                        new BigDecimal("300.00"),
                        AccountStatus.ACTIVE,
                        new Customer(
                                2L,
                                "Destination",
                                "destination@test.com",
                                "hash"
                        )
                );

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        when(bankAccountDAO.findById(20L))
                .thenReturn(destination);

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.transfer(
                        customer,
                        10L,
                        20L,
                        new BigDecimal("600.00")
                )
        );
    }

    @Test
    void transfer_shouldRollbackWhenTransactionSaveFails()
            throws Exception {

        Connection connection =
                mockPostgresConnection();

        BankAccount destination =
                new BankAccount(
                        20L,
                        "ACC-20",
                        AccountType.SAVING,
                        new BigDecimal("300.00"),
                        AccountStatus.ACTIVE,
                        new Customer(
                                2L,
                                "Destination",
                                "destination@test.com",
                                "hash"
                        )
                );

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        when(bankAccountDAO.findById(20L))
                .thenReturn(destination);

        when(transactionDAO.save(
                eq(connection),
                any(Transaction.class)
        )).thenThrow(
                new RuntimeException("Transaction insert failed")
        );

        try (MockedStatic<DatabaseConnection> database =
                     mockStatic(DatabaseConnection.class)) {

            database.when(DatabaseConnection::getConnection)
                    .thenReturn(connection);

            assertThrows(
                    RuntimeException.class,
                    () -> transactionService.transfer(
                            customer,
                            10L,
                            20L,
                            new BigDecimal("100.00")
                    )
            );

            verify(connection)
                    .rollback();

            verify(connection, never())
                    .commit();
        }
    }

    @Test
    void getTransactionHistory_shouldReturnHistoryForOwner() {

        Transaction transaction =
                new Transaction();

        transaction.setId(100L);
        transaction.setAmount(
                new BigDecimal("50.00")
        );
        transaction.setType(
                TransactionType.DEPOSIT
        );
        transaction.setResultingBalance(
                new BigDecimal("550.00")
        );
        transaction.setAccount(account);

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        when(transactionDAO.findByAccountId(10L))
                .thenReturn(
                        java.util.List.of(transaction)
                );

        var result =
                transactionService.getTransactionHistory(
                        customer,
                        10L
                );

        assertEquals(1, result.size());
        assertEquals(transaction, result.get(0));
    }

    @Test
    void getTransactionHistory_shouldRejectUnauthorizedCustomer() {

        Customer otherCustomer =
                new Customer(
                        99L,
                        "Other",
                        "other@test.com",
                        "hash"
                );

        when(bankAccountDAO.findById(10L))
                .thenReturn(account);

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService
                        .getTransactionHistory(
                                otherCustomer,
                                10L
                        )
        );

        verify(transactionDAO, never())
                .findByAccountId(anyLong());
    }

    @Test
    void findTransactionById_shouldReturnTransaction() {

        Transaction transaction =
                new Transaction();

        transaction.setId(100L);
        transaction.setAccount(account);

        when(transactionDAO.findById(100L))
                .thenReturn(transaction);

        Transaction result =
                transactionService.findTransactionById(
                        customer,
                        100L
                );

        assertEquals(transaction, result);
    }

    @Test
    void findTransactionById_shouldReturnNullWhenMissing() {

        when(transactionDAO.findById(100L))
                .thenReturn(null);

        assertNull(
                transactionService.findTransactionById(
                        customer,
                        100L
                )
        );
    }
}