package study.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import study.account.domain.CancelTransaction;
import study.account.domain.UseBalance;
import study.account.dto.TransactionDto;
import study.account.service.TransactionService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static study.account.type.TransactionResultType.S;
import static study.account.type.TransactionType.CANCEL;
import static study.account.type.TransactionType.USE;

@WebMvcTest(TransactionController.class)
@MockBean(JpaMetamodelMappingContext.class)
class TransactionControllerTest {

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void successUseBalance() throws Exception {
        // given
        given(transactionService
                .createNewTransaction(anyLong(), anyString(), anyLong()))
                .willReturn(TransactionDto.builder()
                        .accountNumber("1000000012")
                        .resultType(S)
                        .transactionId("transactionId")
                        .amount(1000L)
                        .transactedAt(LocalDateTime.parse("2024-04-23T21:19:33"))
                        .build());

        // when
        // then
        mockMvc.perform(post("/transaction/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UseBalance
                                        .Request(1L,
                                        "1000000012",
                                        1000L)
                        )))
                .andExpect(jsonPath("$.accountNumber")
                        .value("1000000012"))
                .andExpect(jsonPath("$.resultType")
                        .value("S"))
                .andExpect(jsonPath("$.transactionId")
                        .value("transactionId"))
                .andExpect(jsonPath("$.transactedAt")
                        .value("2024-04-23T21:19:33"))
                .andDo(print());
    }

    @Test
    void successCancelTransaction() throws Exception {
        // given
        given(transactionService
                .cancelTransaction(anyString(), anyString(), anyLong()))
                .willReturn(TransactionDto.builder()
                        .accountNumber("1000000012")
                        .resultType(S)
                        .transactionType(CANCEL)
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.parse("2024-04-23T21:19:33"))
                        .amount(1000L)
                        .build());

        // when
        // then
        mockMvc.perform(post("/transaction/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CancelTransaction
                                        .Request("transactionId",
                                        "1000000012",
                                        1000L)
                        )))
                .andExpect(jsonPath("$.accountNumber")
                        .value("1000000012"))
                .andExpect(jsonPath("$.resultType")
                        .value("S"))
                .andExpect(jsonPath("$.transactionId")
                        .value("transactionId"))
                .andExpect(jsonPath("$.amount").value(1000L))
                .andExpect(jsonPath("$.transactedAt")
                        .value("2024-04-23T21:19:33"))
                .andDo(print());
    }

    @Test
    void successFindTransaction() throws Exception {
        // given
        TransactionDto transactionDto = TransactionDto.builder()
                .transactionId("transactionId")
                .accountNumber("100000000")
                .amount(1000L)
                .transactionType(USE)
                .resultType(S)
                .transactedAt(LocalDateTime.parse("2024-04-23T21:19:33"))
                .build();

        given(transactionService.findTransaction(anyString()))
                .willReturn(transactionDto);

        // when
        // then
        mockMvc.perform(get("/transaction?transaction-id=transactionId"))
                .andDo(print())
                .andExpect(jsonPath("$.accountNumber")
                        .value("100000000"))
                .andExpect(jsonPath("$.transactionType")
                        .value("USE"))
                .andExpect(jsonPath("$.resultType")
                        .value("S"))
                .andExpect(jsonPath("$.transactionId")
                        .value("transactionId"))
                .andExpect(jsonPath("$.amount")
                        .value(1000))
                .andExpect(jsonPath("$.transactedAt")
                        .value("2024-04-23T21:19:33")
                );

    }

}