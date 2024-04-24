package study.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import study.account.domain.CancelTransaction;
import study.account.domain.TransactionInfo;
import study.account.domain.UseBalance;
import study.account.dto.TransactionDto;
import study.account.exception.AccountException;
import study.account.exception.ServiceException;
import study.account.exception.TransactionException;
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

        } catch (ServiceException e) {
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
        } catch (ServiceException e) {
            transactionService
                    .saveFailCancelTransaction(
                            request.getAccountNumber(),
                            request.getAmount()
                    );
            throw e;
        }
    }

    @GetMapping
    public TransactionInfo getTransactionInfo
            (@RequestParam("transaction-id") String transactionId) {

        TransactionDto transactionDto =
                transactionService.findTransaction(transactionId);

        return TransactionInfo.builder()
                .accountNumber(transactionDto.getAccountNumber())
                .transactionType(transactionDto.getTransactionType())
                .resultType(transactionDto.getResultType())
                .transactionId(transactionDto.getTransactionId())
                .amount(transactionDto.getAmount())
                .transactedAt(transactionDto.getTransactedAt())
                .build();
    }
}




