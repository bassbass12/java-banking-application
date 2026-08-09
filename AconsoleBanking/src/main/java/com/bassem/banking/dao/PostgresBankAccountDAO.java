package com.bassem.banking.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bassem.banking.AccountStatus;
import com.bassem.banking.AccountType;
import com.bassem.banking.BankAccount;
import com.bassem.banking.Customer;
import com.bassem.banking.DatabaseConnection;


public class PostgresBankAccountDAO implements BankAccountDAO{


    @Override
    public BankAccount save(BankAccount account) {
        String sql = """
            INSERT INTO bank_accounts
            (id, account_number,account_type,balance, status, customer_id)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement ps =connection.prepareStatement(sql)){

            ps.setLong(1, account.getId());
            ps.setString(2, account.getAccountNumber());
            ps.setString(3, account.getAccountType().name());
            ps.setBigDecimal(4, account.getBalance());
            ps.setString(5, account.getStatus().name());
            ps.setLong(6, account.getOwner().getId());
                ps.executeUpdate();
                return account;

        } catch(SQLException e) {
            throw new RuntimeException("Error saving bank account", e);
        }

    }


    @Override
    public  BankAccount findById(Long id){
        String sql =
       """  
       SELECT
        b.id, b.account_number, b.account_type, b.balance, b.status,
        c.id AS customer_id,
        c.name, c.email, c.password_hash
         FROM bank_accounts b
         JOIN customers c
         ON b.customer_id = c.id
           WHERE b.id = ?
       """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            try(ResultSet rs =ps.executeQuery()){
                if(rs.next()) {

                    Customer customer = new Customer();
                    customer.setId(rs.getLong("customer_id"));
                    customer.setName(rs.getString("name"));
                    customer.setEmail(rs.getString("email"));
                    customer.setPasswordHash(
                            rs.getString("password_hash")
                    );

                    BankAccount account =new BankAccount();

                    account.setId(rs.getLong("id"));
                    account.setAccountNumber(
                            rs.getString("account_number")
                    );
                    account.setAccountType(
                          AccountType.valueOf(
                                  rs.getString("account_type")
                           )
                    );
                    account.setBalance(
                            rs.getBigDecimal("balance")
                    );
                    account.setStatus(
                            AccountStatus.valueOf(
                                    rs.getString("status")
                            )
                    );

                   account.setOwner(customer);

                   return account;
                }

                return  null;
            }

        }catch (SQLException e) {
                throw new RuntimeException(
                        "Error finding bank account", e
                );
            }

    }

    @Override
    public List<BankAccount> findAll() {
        String sql =
                """  
                SELECT
                 b.id, b.account_number, b.account_type, b.balance, b.status,
                 c.id AS customer_id,
                 c.name, c.email, c.password_hash
                  FROM bank_accounts b
                  JOIN customers c
                  ON b.customer_id = c.id
                """;
        List<BankAccount> accounts = new ArrayList<>();

        try(Connection connection = DatabaseConnection.getConnection();
                      PreparedStatement ps = connection.prepareStatement(sql);
                      ResultSet  rs =ps.executeQuery()){

                          while ((rs.next())) {
                              Customer customer = new Customer();
                              customer.setId(rs.getLong("customer_id"));
                              customer.setName(rs.getString("name"));
                              customer.setEmail(rs.getString("email"));
                              customer.setPasswordHash(
                                      rs.getString("password_hash")
                              );

                              BankAccount account = new BankAccount();
                              account.setId(rs.getLong("id"));
                              account.setAccountNumber(
                                      rs.getString("account_number")
                              );
                              account.setAccountType(
                                      AccountType.valueOf(
                                              rs.getString("account_type")
                                      )
                              );

                              account.setBalance(
                                              rs.getBigDecimal("balance")
                              );

                              account.setStatus(
                                      AccountStatus.valueOf(
                                              rs.getString("status")
                                      )
                              );

                              account.setOwner(customer);
                              accounts.add(account);

                          }
                            return accounts;

                      }catch (SQLException e){
                          throw new RuntimeException("Error finding bank accounts.", e);
                      }


    }

    @Override
    public void update(BankAccount account) {

        String sql = """  
            UPDATE bank_accounts
            SET account_number = ?,
                account_type = ?,
                balance = ?,
                status = ?,
                customer_id = ?
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, account.getAccountNumber());
            ps.setString(2, account.getAccountType().name());
            ps.setBigDecimal(3, account.getBalance());
            ps.setString(4, account.getStatus().name());
            ps.setLong(5, account.getOwner().getId());
            ps.setLong(6, account.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error updating bank account", e
            );
        }


    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM bank_accounts WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error deleting bank account", e
            );
        }


    }
}
