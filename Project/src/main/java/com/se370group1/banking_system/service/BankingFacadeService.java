package com.se370group1.banking_system.service;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se370group1.banking_system.dto.BankAccountDTO;
import com.se370group1.banking_system.dto.TransactionDTO;
import com.se370group1.banking_system.dto.UserDTO;
import com.se370group1.banking_system.model.BankAccount;
import com.se370group1.banking_system.repository.BankAccountRepository;
import com.se370group1.banking_system.service.facade.BankingFacade;

import jakarta.servlet.http.HttpSession;

@Service
public class BankingFacadeService {

    private final UserService userService;
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final BankingFacade bankingFacade;  // ADD THIS

    public BankingFacadeService(
            UserService userService,
            BankAccountService bankAccountService,
            TransactionService transactionService,
            BudgetService budgetService,
            BankingFacade bankingFacade) {  // ADD THIS

        this.userService = userService;
        this.bankAccountService = bankAccountService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
        this.bankingFacade = bankingFacade;  // ADD THIS
    }

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

    public List<TransactionDTO> getTransactions(String connectedUserID) {
        return transactionService.getTransactions(connectedUserID);
    }

    // NOW routes through BankingFacade (uses Strategy + Command internally)
    public String handleTransaction(TransactionDTO transactionDTO) {
        double amount = transactionDTO.getAmountDollars();
        String accountId = transactionDTO.getConnectedBankAccount();

        boolean success;
        if (amount >= 0) {
            success = bankingFacade.deposit(accountId, amount, transactionDTO);
        } else {
            success = bankingFacade.withdraw(accountId, Math.abs(amount), transactionDTO);
        }

        return success ? "Transaction successful." : "Transaction failed. Check the amount or account balance.";
    }

    // NOW routes through BankingFacade (uses Strategy + Command internally)
    public Boolean transferFundsAndRecordTransaction(
            String sourceAccountID, String targetAccountID, double amount) {
        return bankingFacade.transferFunds(sourceAccountID, targetAccountID, amount);
    }

    public List<BankAccountDTO> getAccounts(String connectedUserID) {
        return bankAccountService.getConnectedBankAccounts(connectedUserID);
    }
}