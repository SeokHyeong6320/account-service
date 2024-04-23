package study.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import study.account.domain.UseBalance;
import study.account.service.TransactionService;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/use")
    public UseBalance.Response useBalance(@RequestBody UseBalance.Request request) {

        transactionService.createNewTransaction(
                request.getUserId(),
                request.getAccountNumber(),
                request.getAmount()
        );

        return null;
    }
}
