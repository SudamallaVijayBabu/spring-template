package com.example.springtemplate.billing.util;


import com.example.springtemplate.billing.dto.BillingDTO;
import com.example.springtemplate.billing.exception.InvalidInvoiceException;
import com.example.springtemplate.billing.repository.BillingRepository;

import java.util.regex.Pattern;

public class BillingValidationUtil {

    private static final Pattern INVOICE_PATTERN = Pattern.compile("^INV-\\d{4}-\\d{4}$");

    //static means this method belongs to the class itself, not to any specific object.
    public static void validateInvoiceNumberFormat(String invoiceNumber) {
        if (!INVOICE_PATTERN.matcher(invoiceNumber).matches()) {
            throw new InvalidInvoiceException("Invalid invoice number format. Expected format: INV-YYYY-SEQ");
        }
        else if (invoiceNumber ==null || invoiceNumber.trim().isEmpty()){
            throw new InvalidInvoiceException("Invoice number cannot be empty.");
        }
    }
    //Valadition For Empty Fiels.
    public static void validateInvoiceNumberExist(String invoiceNumber, BillingRepository billingRepository) {
        if (billingRepository.existsByInvoiceNumber(invoiceNumber)) {
            throw new InvalidInvoiceException("Please check the invoice number. Invoice number already exists.");
        }
    }

    //Customer Valadition .
    public static void validateCustomerName(String CustomerName){

        if (CustomerName ==null || CustomerName.trim().isEmpty()){
            throw new InvalidInvoiceException("Customer Name cannot be empty.");
        }

    }
    //Amount Validation
    public static void validateAmount(Double Amount){
        if (Amount == null || Amount.doubleValue() <=0){
            throw  new InvalidInvoiceException("Amount must be greater than 0.");
        }
    }

    //Check Wether Invoice Number is There Or Not.
    public static void InvoiceNumberNotFound(String invoiceNumber) {
        if (invoiceNumber == null || invoiceNumber.trim().isEmpty()) {
            throw new InvalidInvoiceException("Sorry, there is no invoice created with this number: " + invoiceNumber);
        }
    }


}