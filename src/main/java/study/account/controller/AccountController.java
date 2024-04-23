package study.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import study.account.domain.AccountInfo;
import study.account.domain.CloseAccount;
import study.account.domain.CreateAccount;
import study.account.service.AccountService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public CreateAccount.Response createAccount
            (@RequestBody CreateAccount.Request request) {

        return CreateAccount.Response.fromDto(accountService
                .createAccount(request.getUserId(), request.getInitialBalance()));
    }

    @DeleteMapping
    public CloseAccount.Response closeAccount
            (@RequestBody CloseAccount.Request request) {

        return CloseAccount.Response.fromDto(accountService
                .closeAccount(request.getUserId(), request.getAccountNumber()));
    }

    @GetMapping
    public List<AccountInfo> getAccountsByUserId(@RequestParam Long userId) {

        return accountService.getAccountList(userId).stream()
                .map(accountDto -> AccountInfo.builder()
                        .accountNumber(accountDto.getAccountNumber())
                        .balance(accountDto.getBalance()).build())
                .collect(Collectors.toList());
    }

}
