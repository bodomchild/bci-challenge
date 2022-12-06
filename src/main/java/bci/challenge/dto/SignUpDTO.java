package bci.challenge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class SignUpDTO {

    private String name;

    @NotNull
    @Pattern(regexp = "^[a-z]+@[a-z]+\\.[a-z]{2,}$", message = "Invalid email format")
    private String email;

    @NotNull
    @Pattern(regexp = "^(?=(?:\\D*\\d){2}\\D*$)(?=[^A-Z]*[A-Z][^A-Z]*$)[a-zA-Z\\d]{8,12}$", message = "Invalid password format")
    private String password;

    private List<PhoneDTO> phones;

}
