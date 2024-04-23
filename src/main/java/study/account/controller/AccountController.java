package study.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import study.account.domain.CloseAccount;
import study.account.domain.CreateAccount;
import study.account.service.AccountService;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/account")
    public CreateAccount.Response createAccount
            (@RequestBody CreateAccount.Request request) {

        return CreateAccount.Response.fromDto(accountService
                .createAccount(request.getUserId(), request.getInitialBalance()));
    }

    @DeleteMapping("/account")
    public CloseAccount.Response closeAccount
            (@RequestBody CloseAccount.Request request) {

        return CloseAccount.Response.fromDto(accountService
                .closeAccount(request.getUserId(), request.getAccountNumber()));
    }

}
