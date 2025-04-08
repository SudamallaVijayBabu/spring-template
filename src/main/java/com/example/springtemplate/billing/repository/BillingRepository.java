package com.example.springtemplate.billing.repository;

import com.example.springtemplate.billing.entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillingRepository extends JpaRepository<Billing, Long> {

    Optional<Billing> findByInvoiceNumber(String invoiceNumber);

    // Checks whether a Billing record with the given invoice number exists in the database.
    // Returns true if it exists, false otherwise. Useful for validation without fetching the full record.
    boolean existsByInvoiceNumber(String invoiceNumber);
}