package com.se370group1.banking_system.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se370group1.banking_system.dto.BankAccountDTO;
import com.se370group1.banking_system.model.BankAccount;
import com.se370group1.banking_system.repository.BankAccountRepository;

@Service
public class BankAccountService {
    @Autowired
    private final BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public List<BankAccountDTO> getConnectedBankAccounts(String targedConnectedUserID) {
        //request bankAccountList of NORMAL DOMAIN OBJECTS from bankAccountRepository.java
        //pass the same targetConnectedUserID down to use for database searching
        List<BankAccount> bankAccountList = bankAccountRepository.findByConnectedUserID(targedConnectedUserID);
        List<BankAccountDTO> bankAccountDTOList = new ArrayList<>(); //this is the DTO list that will get returned to bankAccountController.java

        //if no bank accounts are connected to the user
        if(bankAccountList.isEmpty()) {
            System.out.println("No bank accounts found for user ID: " + targedConnectedUserID);
        }
        else
        {
            //convert the list of normal domain objects to the list of data transfer objects
            //simply copies the data using the DTO constructor
            for (BankAccount newBankAccount : bankAccountList) {
                BankAccountDTO dto = new BankAccountDTO(newBankAccount);
                bankAccountDTOList.add(dto);
            }
        }

        //return list of DTOs back to bankAccountController.java
        return bankAccountDTOList;
    }

    public Boolean withdrawFunds(String sourceAccountID, double amount) {
        BankAccount sourceAccount = bankAccountRepository.findById(sourceAccountID)
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        if (sourceAccount.getCurrentBalance() >= amount) {
            sourceAccount.setCurrentBalance(sourceAccount.getCurrentBalance() - amount);
            bankAccountRepository.save(sourceAccount);
            return true;
        }
        return false;
    }

    public Boolean depositFunds(String targetAccountID, double amount) {
        BankAccount targetAccount = bankAccountRepository.findById(targetAccountID)
                .orElseThrow(() -> new RuntimeException("Target account not found"));

        targetAccount.setCurrentBalance(targetAccount.getCurrentBalance() + amount);
        bankAccountRepository.save(targetAccount);
        return true;
    }

    public Boolean transferFunds(String sourceAccountID, String targetAccountID, double amount) {
        try {
            if (withdrawFunds(sourceAccountID, amount)) {
                return depositFunds(targetAccountID, amount);
            }
            return false;
        } catch (RuntimeException e) {
            return false;
        }
    }

    // New — used by BankingFacade to fetch the current balance
    // before WithdrawValidationStrategy checks if the amount is valid
    public double getBalance(String accountId) {
        BankAccount account = bankAccountRepository.findById(accountId).orElse(null);
        if (account == null) {
            System.out.println("Account not found: " + accountId);
            return 0.0;
        }
        return account.getCurrentBalance();
    }
}