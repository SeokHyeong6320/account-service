package study.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import study.account.domain.CancelTransaction;
import study.account.domain.UseBalance;
import study.account.exception.AccountException;
import study.account.service.TransactionService;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/use")
    public UseBalance.Response useBalance
            (@RequestBody UseBalance.Request request) {

        try {
            return UseBalance.Response
                    .fromDto(transactionService.createNewTransaction
                            (request.getUserId(),
                                    request.getAccountNumber(),
                                    request.getAmount()));

        } catch (AccountException e) {
            transactionService.saveFailTransaction
                    (request.getAccountNumber(), request.getAmount());
            throw e;
        }
    }

    @PostMapping("/cancel")
    public CancelTransaction.Response cancelTransaction
            (@RequestBody CancelTransaction.Request request) {

        return null;
    }
}