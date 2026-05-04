package com.se370group1.banking_system.service;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.se370group1.banking_system.dto.BankAccountDTO;
import com.se370group1.banking_system.dto.TransactionDTO;
import com.se370group1.banking_system.service.command.CommandInvoker;
import com.se370group1.banking_system.service.command.DepositCommand;
import com.se370group1.banking_system.service.command.TransferCommand;
import com.se370group1.banking_system.service.command.WithdrawCommand;
import com.se370group1.banking_system.service.strategy.DepositValidationStrategy;
import com.se370group1.banking_system.service.strategy.WithdrawValidationStrategy;

import jakarta.servlet.http.HttpSession;

@Service
public class BankingFacadeService {

    private final UserService userService;
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final CommandInvoker commandInvoker;

    public BankingFacadeService(
            UserService userService,
            BankAccountService bankAccountService,
            TransactionService transactionService,
            BudgetService budgetService,
            CommandInvoker commandInvoker) {

        this.userService = userService;
        this.bankAccountService = bankAccountService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
        this.commandInvoker = commandInvoker;
    }

    // ── User / Session ────────────────────────────────────────────────────────

    public Boolean logInUser(String username, String password, HttpSession session) {
        try {
            Boolean correctLogin = userService.LogInUser(username, password);
            if (correctLogin) {
                session.setAttribute("username", username);
                session.setAttribute("isLoggedIn", true);
                return true;
            }
            return false;
        } catch (IllegalAccessError error) {
            System.out.println(error.getMessage());
            return false;
        }
    }

    public void createNewUser(
            String username, String password, String firstName, String lastName,
            String email, int phoneNum, int socialSecurityNum,
            String street, String city, String state, String zip) {

        String fullAddress = String.format("%s, %s, %s %s", street, city, state, zip);
        String userID = java.util.UUID.randomUUID().toString();
        String fullName = firstName + " " + lastName;

        userService.createNewUser(userID, username, password, fullName, email,
                phoneNum, socialSecurityNum, fullAddress);
    }

    public void logOutUser(HttpSession session) {
        session.invalidate();
    }

    public Map<String, Object> sessionStatus(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");
        if (isLoggedIn != null && isLoggedIn) {
            response.put("isLoggedIn", true);
            response.put("username", session.getAttribute("username"));
        } else {
            response.put("isLoggedIn", false);
        }
        return response;
    }

    public String dashboard(HttpSession session) {
        Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");
        if (isLoggedIn != null && isLoggedIn) {
            return "Welcome to the dashboard, " + session.getAttribute("username") + "!";
        } else {
            throw new IllegalStateException("Unauthorized access. Please log in.");
        }
    }

    // ── Accounts & Transactions ───────────────────────────────────────────────

    public List<BankAccountDTO> getAccounts(String connectedUserID) {
        return bankAccountService.getConnectedBankAccounts(connectedUserID);
    }

    public List<TransactionDTO> getTransactions(String connectedUserID) {
        return transactionService.getTransactions(connectedUserID);
    }

    // ── Strategy + Command: Deposit ───────────────────────────────────────────

    public String handleTransaction(TransactionDTO transactionDTO) {
        double amount = transactionDTO.getAmountDollars();
        String accountId = transactionDTO.getConnectedBankAccount();

        boolean success;
        if (amount >= 0) {
            // DepositValidationStrategy: must be > 0 and <= $10,000
            DepositValidationStrategy validation = new DepositValidationStrategy();
            if (!validation.isValid(amount)) {
                return "Deposit rejected: amount must be greater than 0 and no more than $10,000.";
            }
            success = commandInvoker.executeCommand(
                    new DepositCommand(bankAccountService, accountId, amount)
            );
        } else {
            // WithdrawValidationStrategy: must be > 0 and <= current balance
            double currentBalance = bankAccountService.getBalance(accountId);
            WithdrawValidationStrategy validation = new WithdrawValidationStrategy(currentBalance);
            if (!validation.isValid(Math.abs(amount))) {
                return "Withdrawal rejected: amount must be greater than 0 and cannot exceed balance of $" + currentBalance;
            }
            success = commandInvoker.executeCommand(
                    new WithdrawCommand(bankAccountService, accountId, Math.abs(amount))
            );
        }

        if (success) {
            transactionService.processTransaction(transactionDTO);
        }

        return success ? "Transaction successful." : "Transaction failed. Check the amount or account balance.";
    }

    // ── Strategy + Command: Transfer ──────────────────────────────────────────

    public Boolean transferFundsAndRecordTransaction(
            String sourceAccountID, String targetAccountID, double amount) {

        // WithdrawValidationStrategy: source account must have sufficient funds
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
    // ── Strategy + Command: Direct Deposit ───────────────────────────────────

    public Boolean handleDeposit(String accountId, double amount) {
        DepositValidationStrategy validation = new DepositValidationStrategy();
        if (!validation.isValid(amount)) {
            System.out.println("Deposit rejected: must be greater than $0 and no more than $10,000.");
            return false;
        }
        return commandInvoker.executeCommand(new DepositCommand(bankAccountService, accountId, amount));
    }

    // ── Strategy + Command: Direct Withdraw ──────────────────────────────────

    public Boolean handleWithdraw(String accountId, double amount) {
        double currentBalance = bankAccountService.getBalance(accountId);
        WithdrawValidationStrategy validation = new WithdrawValidationStrategy(currentBalance);
        if (!validation.isValid(amount)) {
            System.out.println("Withdrawal rejected: exceeds balance of $" + currentBalance);
            return false;
        }
        return commandInvoker.executeCommand(new WithdrawCommand(bankAccountService, accountId, amount));
    }

}