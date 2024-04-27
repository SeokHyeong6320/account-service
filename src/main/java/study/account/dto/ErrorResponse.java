package study.account.dto;

import lombok.*;
import study.account.type.ErrorCode;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

    private ErrorCode errorCode;
    private String errorMessage;

}
