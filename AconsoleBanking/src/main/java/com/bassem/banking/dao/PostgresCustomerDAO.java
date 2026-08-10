package com.bassem.banking.dao;

import com.bassem.banking.Customer;
import com.bassem.banking.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresCustomerDAO implements CustomerDAO {


    @Override
    public Customer save(Customer customer) {

        String sql = """
                INSERT INTO customers (id, name, email, password_hash)
                VALUES(?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnection.getConnection();

             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, customer.getId());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPasswordHash());
            ps.executeUpdate();

            return customer;

        } catch (SQLException e) {
            throw new RuntimeException("Error saving customer", e);

        }

    }


    @Override
    public Customer findById(Long id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();

             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

           try( ResultSet rs = ps.executeQuery()){

               if (rs.next()) {

                   Customer customer = new Customer();

                   customer.setId(rs.getLong("id"));
                   customer.setName(rs.getString("name"));
                   customer.setEmail(rs.getString("email"));
                   customer.setPasswordHash(rs.getString("password_hash"));

                   return customer;
           }

               return null;

           }


        } catch (SQLException e) {
            throw new RuntimeException("Error handling find customer.", e);
        }

    }

    // ------Find by email-----
    @Override
    public Customer findByEmail(String email) {
        String sql = """
            SELECT id, name, email, password_hash
            FROM customers
            WHERE email = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                return new Customer(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("password_hash")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error finding customer by email", e
            );
        }


        return null;
    }

    @Override
    public List<Customer> findAll() {
        String sql = "SELECT * FROM customers";
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getLong("id"));
                customer.setName(rs.getString("name"));
                customer.setEmail(rs.getString("email"));
                customer.setPasswordHash(rs.getString("password_hash"));

                customers.add(customer);

            }

            return customers;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding customers", e);

        }
    }

    @Override
    public void update(Customer customer) {

        String sql = """
            UPDATE customers
            SET name = ?, email = ?, password_hash = ?
            WHERE id = ?
            """;
        try(Connection connection =DatabaseConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPasswordHash());
            ps.setLong(4, customer.getId());
            ps.executeUpdate();

        } catch (RuntimeException | SQLException e) {
            throw new RuntimeException("Error updating customer", e);
        }


    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM customers WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting customer", e);
        }


    }
}
