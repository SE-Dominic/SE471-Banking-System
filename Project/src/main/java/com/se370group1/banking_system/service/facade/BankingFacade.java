package com.se370group1.banking_system.service.facade;

import org.springframework.stereotype.Service;

import com.se370group1.banking_system.service.BankAccountService;
import com.se370group1.banking_system.service.TransactionService;
import com.se370group1.banking_system.service.command.CommandInvoker;
import com.se370group1.banking_system.service.command.DepositCommand;
import com.se370group1.banking_system.service.command.TransferCommand;
import com.se370group1.banking_system.service.command.WithdrawCommand;
import com.se370group1.banking_system.service.strategy.DepositValidationStrategy;
import com.se370group1.banking_system.service.strategy.WithdrawValidationStrategy;
import com.se370group1.banking_system.dto.TransactionDTO;

@Service
public class BankingFacade {

    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;
    private final CommandInvoker commandInvoker;

    public BankingFacade(BankAccountService bankAccountService,
                         TransactionService transactionService,
                         CommandInvoker commandInvoker) {
        this.bankAccountService = bankAccountService;
        this.transactionService = transactionService;
        this.commandInvoker = commandInvoker;
    }

    public boolean deposit(String accountId, double amount, TransactionDTO t_dto) {
        DepositValidationStrategy validation = new DepositValidationStrategy();
        if (!validation.isValid(amount)) {
            System.out.println("Deposit rejected: amount must be greater than 0 and no more than $10,000.");
            return false;
        }

        boolean success = commandInvoker.executeCommand(
                new DepositCommand(bankAccountService, accountId, amount)
        );

        if (success && t_dto != null) {
            transactionService.processTransaction(t_dto);
        }
        return success;
    }

    public boolean withdraw(String accountId, double amount, TransactionDTO t_dto) {
        double currentBalance = bankAccountService.getBalance(accountId);
        WithdrawValidationStrategy validation = new WithdrawValidationStrategy(currentBalance);
        if (!validation.isValid(amount)) {
            System.out.println("Withdrawal rejected: amount must be greater than 0 and cannot exceed balance of $" + currentBalance);
            return false;
        }

        boolean success = commandInvoker.executeCommand(
                new WithdrawCommand(bankAccountService, accountId, amount)
        );

        if (success && t_dto != null) {
            transactionService.processTransaction(t_dto);
        }
        return success;
    }

    public boolean transferFunds(String sourceAccountID, String targetAccountID, double amount) {
        double currentBalance = bankAccountService.getBalance(sourceAccountID);
        WithdrawValidationStrategy validation = new WithdrawValidationStrategy(currentBalance);
        if (!validation.isValid(amount)) {
            System.out.println("Transfer rejected: amount must be greater than 0 and cannot exceed balance of $" + currentBalance);
            return false;
        }

        return commandInvoker.executeCommand(
                new TransferCommand(bankAccountService, sourceAccountID, targetAccountID, amount)
        );
    }
}