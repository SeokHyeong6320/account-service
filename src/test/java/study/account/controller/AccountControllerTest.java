package study.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import study.account.domain.CloseAccount;
import study.account.domain.CreateAccount;
import study.account.dto.AccountDto;
import study.account.service.AccountService;

import static java.time.LocalDateTime.now;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AccountController.class)
@MockBean(JpaMetamodelMappingContext.class)
class AccountControllerTest {

    @MockBean private AccountService accountService;

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Test
    void successCreateAccount() throws Exception {
        // given
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("12345")
                        .balance(1000L)
                        .registeredAt(now())
                        .build());

        // when
        // then
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateAccount.Request(1L, 1111L)
                        )))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.accountNumber")
                        .value("12345"))
                .andDo(print());
    }

    @Test
    void successCloseAccount() throws Exception {
        // given

        given(accountService.closeAccount(anyLong(), anyString()))
                .willReturn(AccountDto.builder()
                        .userId(2L)
                        .accountNumber("1000000012")
                        .unregisteredAt(now())
                        .build());

        // when

        // then
        mockMvc.perform(delete("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CloseAccount.Request(2L, "1000000012")
                        )))
                .andExpect(jsonPath("$.userId").value(2L))
                .andExpect(jsonPath("$.accountNumber")
                        .value("1000000012"))
                .andDo(print());
    }







}