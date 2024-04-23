package study.account;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.account.repository.AccountRepository;
import study.account.service.AccountService;

@SpringBootTest
@Transactional
public class TotalTest {

    @Autowired private AccountService accountService;
    @Autowired private AccountRepository accountRepository;

    @BeforeEach
    void before() {
    }

    @AfterEach
    void after() {
        accountRepository.deleteAll();
    }

    @Test
    void createAccount() throws Exception {
        // given


        // when

        // then

    }



}
