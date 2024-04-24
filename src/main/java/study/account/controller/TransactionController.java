package study.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import study.account.domain.CancelTransaction;
import study.account.domain.TransactionInfo;
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
            transactionService.saveFailNewTransaction
                    (request.getAccountNumber(), request.getAmount());
            throw e;
        }
    }

    @PostMapping("/cancel")
    public CancelTransaction.Response cancelTransaction
            (@RequestBody CancelTransaction.Request request) {

        try {
            return CancelTransaction.Response
                    .fromDto(transactionService.cancelTransaction
                            (request.getTransactionId(),
                                    request.getAccountNumber(),
                                    request.getAmount()));
        } catch (AccountException e) {
            transactionService
                    .saveFailCancelTransaction(
                            request.getAccountNumber(),
                            request.getAmount()
                    );
            throw e;
        }
    }

    @GetMapping
    public TransactionInfo getTransactionInfo() {
        return null;
    }
}
