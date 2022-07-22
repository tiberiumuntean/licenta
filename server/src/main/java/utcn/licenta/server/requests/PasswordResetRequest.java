package utcn.licenta.server.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordResetRequest {
    @NotBlank
    private Integer id;

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}
