package bci.challenge.dto;

import lombok.Data;

@Data
public class PhoneDTO {

    private long number;
    private int cityCode;
    private String countryCode;

}
