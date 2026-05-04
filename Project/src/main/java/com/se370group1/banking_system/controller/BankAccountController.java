package com.se370group1.banking_system.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.se370group1.banking_system.dto.BankAccountDTO;
import com.se370group1.banking_system.service.BankingFacadeService;

@RestController
@RequestMapping("/api/bankAccount")
public class BankAccountController {

    private final BankingFacadeService bankingFacadeService;

    public BankAccountController(BankingFacadeService bankingFacadeService) {
        this.bankingFacadeService = bankingFacadeService;
    }

    @RequestMapping(value = "/getConnectedBankAccounts", method = {RequestMethod.GET})
    public List<BankAccountDTO> getConnectedBankAccounts(@RequestParam String targetConnectedUserID) {
        System.out.println("getConnectedBankAccounts controller called with userID: " + targetConnectedUserID);
        return bankingFacadeService.getAccounts(targetConnectedUserID);
    }

    @RequestMapping(value = "/depositFunds", method = {RequestMethod.GET, RequestMethod.POST})
    public Boolean depositFunds(
            @RequestParam String targetAccountID,
            @RequestParam double amount) {
        return bankingFacadeService.handleDeposit(targetAccountID, amount);
    }

    @RequestMapping(value = "/withdrawFunds", method = {RequestMethod.GET, RequestMethod.POST})
    public Boolean withdrawFunds(
            @RequestParam String sourceAccountID,
            @RequestParam double amount) {
        return bankingFacadeService.handleWithdraw(sourceAccountID, amount);
    }

    @RequestMapping(value = "/transferFunds", method = {RequestMethod.GET, RequestMethod.POST})
    public Boolean transferFunds(
            @RequestParam String sourceAccountID,
            @RequestParam String targetAccountID,
            @RequestParam double amount) {
        return bankingFacadeService.transferFundsAndRecordTransaction(sourceAccountID, targetAccountID, amount);
    }
}