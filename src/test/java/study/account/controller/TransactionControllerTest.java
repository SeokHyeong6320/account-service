package study.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import study.account.domain.UseBalance;
import study.account.dto.TransactionDto;
import study.account.service.TransactionService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static study.account.type.TransactionResultType.S;

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

}