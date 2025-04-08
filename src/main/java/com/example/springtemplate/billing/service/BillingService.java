package com.example.springtemplate.billing.service;


import com.example.springtemplate.billing.dto.BillingDTO;
import com.example.springtemplate.billing.entity.Billing;
import com.example.springtemplate.billing.exception.InvalidInvoiceException;
import com.example.springtemplate.billing.repository.BillingRepository;
import com.example.springtemplate.billing.util.BillingValidationUtil;
import com.example.springtemplate.common.exception.ResourceNotFoundException;
import com.example.springtemplate.common.util.DateTimeUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BillingService {

    private final BillingRepository billingRepository;


    public BillingService(BillingRepository billingRepository) {
        this.billingRepository = billingRepository;
    }

    public BillingDTO create(BillingDTO dto, String invoiceNumber) {
        System.out.println(invoiceNumber);


        Billing savedBilling;

        if (invoiceNumber == null || invoiceNumber.isEmpty()) {
            // Validate fields
            BillingValidationUtil.validateCustomerName(dto.getCustomerName());
            BillingValidationUtil.validateInvoiceNumberFormat(dto.getInvoiceNumber());
            BillingValidationUtil.validateAmount(dto.getAmount());

            //  Check for duplicate invoice number for new entries
            BillingValidationUtil.validateInvoiceNumberExist(dto.getInvoiceNumber(), billingRepository);

            //  Save new billing
            Billing billing = Billing.builder()
                    .invoiceNumber(dto.getInvoiceNumber())
                    .customerName(dto.getCustomerName())
                    .amount(dto.getAmount())
                    .createdAt(LocalDateTime.now())
                    .build();

            savedBilling = billingRepository.save(billing);
        } else {
            //  Update existing billing
            BillingValidationUtil.validateInvoiceNumberFormat(invoiceNumber);
            System.out.println(invoiceNumber);

            Billing billing = billingRepository.findByInvoiceNumber(invoiceNumber)
                    .orElseThrow(() -> new InvalidInvoiceException("Sorry!,There is no invoice available with this number: " + invoiceNumber));

            // Validate presence of invoice number value itself

            if(dto.getCustomerName() != null){
                BillingValidationUtil.validateCustomerName(dto.getCustomerName());
                billing.setCustomerName(dto.getCustomerName());
            }

            if(dto.getAmount() !=null ){
                BillingValidationUtil.validateAmount(dto.getAmount());
                billing.setAmount(dto.getAmount());
            }

            savedBilling = billingRepository.save(billing);

            dto.setInvoiceNumber(billing.getInvoiceNumber());
            dto.setCustomerName(billing.getCustomerName());
            dto.setAmount(billing.getAmount());

        }

        // Set duration and return
        dto.setDurationSinceCreated(DateTimeUtil.calculateDuration(savedBilling.getCreatedAt()));
        return dto;
    }



    public List<BillingDTO> findAll() {
        return billingRepository.findAll(Sort.by("createdAt").ascending())
                .stream()
                .map(b -> BillingDTO.builder()
                        .invoiceNumber(b.getInvoiceNumber())
                        .customerName(b.getCustomerName())
                        .amount(b.getAmount())
                        .durationSinceCreated(DateTimeUtil.calculateDuration(b.getCreatedAt()))
                        .build())
                .collect(Collectors.toList());
    }

    public BillingDTO getBillingByInvoiceNumber(String invoiceNumber) {
        BillingValidationUtil.validateInvoiceNumberFormat(invoiceNumber);

        Billing billing = billingRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice number not found: " + invoiceNumber));


        BillingDTO dto = new BillingDTO();
        dto.setInvoiceNumber(billing.getInvoiceNumber());
        dto.setCustomerName(billing.getCustomerName());
        dto.setAmount(billing.getAmount());
        dto.setDurationSinceCreated(DateTimeUtil.calculateDuration(billing.getCreatedAt()));
        return dto;

    }

    //Delete Function
    public BillingDTO deleteByInvoiceNumber(String invoiceNumber) {
        // Validate format
        BillingValidationUtil.validateInvoiceNumberFormat(invoiceNumber);
        // Validate presence of invoice number value itself
        BillingValidationUtil.InvoiceNumberNotFound(invoiceNumber);
        // Fetch billing data
        Optional<Billing> optionalBilling = billingRepository.findByInvoiceNumber(invoiceNumber);

        if (optionalBilling.isPresent()) {
            Billing billing = optionalBilling.get();

            // Delete the billing record
            billingRepository.delete(billing);

            // Prepare and return DTO with deleted billing info
            BillingDTO deletedDto = new BillingDTO();
            deletedDto.setInvoiceNumber(billing.getInvoiceNumber());
            deletedDto.setCustomerName(billing.getCustomerName());
            deletedDto.setAmount(billing.getAmount());
            deletedDto.setDurationSinceCreated(DateTimeUtil.calculateDuration(billing.getCreatedAt()));

            return deletedDto;
        } else {
            throw new EntityNotFoundException("Billing with invoice number " + invoiceNumber + " not found.");
        }
    }

    //Paging
    public Page<BillingDTO> getPagingValues(int page, int size) {

        Pageable pageable = PageRequest.of(page , size);
        Page<Billing> billing = billingRepository.findAll(pageable);
        Page<BillingDTO> billingDTO;
        return billingDTO = billing.map(b -> BillingDTO.builder()
                .invoiceNumber(b.getInvoiceNumber())
                .customerName(b.getCustomerName())
                .amount(b.getAmount())
                .durationSinceCreated(DateTimeUtil.calculateDuration(b.getCreatedAt()))
                .build());

    }

}