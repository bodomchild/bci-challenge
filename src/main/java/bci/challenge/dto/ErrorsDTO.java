package bci.challenge.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ErrorsDTO {

    private List<ErrorDTO> error;

    @Getter
    @Builder
    public static class ErrorDTO {
        @JsonFormat(pattern = "MMM dd, yyyy hh:mm:ss a")
        private LocalDateTime timestamp;
        private int code;
        private String detail;
    }

}
