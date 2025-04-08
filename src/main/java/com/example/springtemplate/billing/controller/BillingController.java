package com.example.springtemplate.billing.controller;

import com.example.springtemplate.billing.dto.BillingDTO;
import com.example.springtemplate.billing.service.BillingService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<BillingDTO> invoices = billingService.findAll();

        if (invoices.isEmpty()) {
            return ResponseEntity
                    .ok("No Invoice generated till now. Please create an invoice.");
        }

        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{invoiceNumber}")
    public BillingDTO getBillingByInvoiceNumber(@PathVariable String invoiceNumber) {
        return billingService.getBillingByInvoiceNumber(invoiceNumber);
    }

    @PostMapping
    public BillingDTO create(@RequestBody BillingDTO dto) {
        return billingService.create(dto,"");
    }

    @PutMapping("/{invoiceNumber}")
    public BillingDTO updateByInvoiceNumber(@RequestBody BillingDTO dto , @PathVariable String invoiceNumber){
        return billingService.create(dto ,invoiceNumber) ;
    }

    @DeleteMapping("/{invoiceNumber}")
    public ResponseEntity<?> deleteByInvoiceNumber(@PathVariable String invoiceNumber) {
        BillingDTO deletedBilling = billingService.deleteByInvoiceNumber(invoiceNumber);
        return ResponseEntity.ok("Deleted Successfully");
    }

    @GetMapping("/paging")
    public Page<BillingDTO> paging(@RequestParam (defaultValue = "0") int page ,@RequestParam(defaultValue = "1")int size){

        return billingService.getPagingValues(page ,size);

    }


}