package com.se370group1.banking_system.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.se370group1.banking_system.dto.TransactionDTO;
import com.se370group1.banking_system.model.Transaction;
import com.se370group1.banking_system.repository.TransactionRepository;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionDTO> getTransactions(String connectedUserID) {
        List<Transaction> transactionList = transactionRepository.findByConnectedBankAccountID(connectedUserID);
        List<TransactionDTO> transactionDTOList = new ArrayList<>();

        if (transactionList.isEmpty()) {
            System.out.println("No transactions have been made on this account.");
        } else {
            for (Transaction temp_transaction : transactionList) {
                System.out.println("Extracting transaction id: " + temp_transaction.getTransactionID());
                TransactionDTO t_dto = new TransactionDTO(temp_transaction);
                transactionDTOList.add(t_dto);
            }
        }
        return transactionDTOList;
    }

    public TransactionDTO getMostRecentTransaction(List<TransactionDTO> dto) {
        if (dto.isEmpty()) {
            System.out.println("No transactions found.");
            return null;
        }
        return dto.get(dto.size() - 1);
    }

    public void processTransaction(TransactionDTO t_dto) {
        Transaction pending_transaction = new Transaction(t_dto);
        transactionRepository.save(pending_transaction);
    }
}