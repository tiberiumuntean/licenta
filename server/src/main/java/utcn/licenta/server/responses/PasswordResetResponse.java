package utcn.licenta.server.responses;

import lombok.Data;

@Data
public class PasswordResetResponse {
    private int status;
    private String message;

    public PasswordResetResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
