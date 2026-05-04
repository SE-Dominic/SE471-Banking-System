package com.se370group1.banking_system.service.facade;

import org.springframework.stereotype.Service;

import com.se370group1.banking_system.service.BankAccountService;
import com.se370group1.banking_system.service.TransactionService;
import com.se370group1.banking_system.service.command.CommandInvoker;
import com.se370group1.banking_system.service.command.DepositCommand;
import com.se370group1.banking_system.service.command.TransferCommand;
import com.se370group1.banking_system.service.command.WithdrawCommand;
import com.se370group1.banking_system.service.strategy.StandardTransactionValidationStrategy;
import com.se370group1.banking_system.service.strategy.TransactionValidationStrategy;
import com.se370group1.banking_system.dto.TransactionDTO;

/**
 * Facade Pattern - BankingFacade
 *
 * Provides a single, simplified entry point for all complex banking operations.
 * The controller only needs to call BankingFacade — it doesn't need to know about
 * BankAccountService, TransactionService, the Command pattern, or Strategy pattern.
 *
 * Internally it:
 *   1. Uses the Strategy pattern to validate the amount before acting.
 *   2. Uses the Command pattern to execute the actual banking operation.
 *   3. Delegates transaction record-keeping to TransactionService.
 */
@Service
public class BankingFacade {

    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;
    private final CommandInvoker commandInvoker;
    private final TransactionValidationStrategy validationStrategy;

    public BankingFacade(BankAccountService bankAccountService,
                         TransactionService transactionService,
                         CommandInvoker commandInvoker) {
        this.bankAccountService = bankAccountService;
        this.transactionService = transactionService;
        this.commandInvoker = commandInvoker;
        // Default strategy — swap with PremiumValidationStrategy etc. if needed
        this.validationStrategy = new StandardTransactionValidationStrategy();
    }

    /**
     * Deposit funds into a bank account.
     * Validates the amount, then executes a DepositCommand.
     *
     * @param accountId  target account ID
     * @param amount     amount to deposit
     * @param t_dto      optional TransactionDTO to record the transaction (may be null)
     * @return true on success, false if validation fails or account not found
     */
    public boolean deposit(String accountId, double amount, TransactionDTO t_dto) {
        if (!validationStrategy.isValid(amount)) {
            System.out.println("Deposit rejected: invalid amount.");
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

    /**
     * Withdraw funds from a bank account.
     * Validates the amount, then executes a WithdrawCommand.
     *
     * @param accountId  source account ID
     * @param amount     amount to withdraw
     * @param t_dto      optional TransactionDTO to record the transaction (may be null)
     * @return true on success, false if validation fails, insufficient funds, or account not found
     */
    public boolean withdraw(String accountId, double amount, TransactionDTO t_dto) {
        if (!validationStrategy.isValid(amount)) {
            System.out.println("Withdrawal rejected: invalid amount.");
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

    /**
     * Transfer funds between two bank accounts.
     * Validates the amount, then executes a TransferCommand.
     *
     * @param sourceAccountID  account to debit
     * @param targetAccountID  account to credit
     * @param amount           amount to transfer
     * @return true on success, false if validation fails, insufficient funds, or account not found
     */
    public boolean transferFunds(String sourceAccountID, String targetAccountID, double amount) {
        if (!validationStrategy.isValid(amount)) {
            System.out.println("Transfer rejected: invalid amount.");
            return false;
        }

        return commandInvoker.executeCommand(
                new TransferCommand(bankAccountService, sourceAccountID, targetAccountID, amount)
        );
    }
}
