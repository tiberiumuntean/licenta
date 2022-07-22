package com.licenta.user.requests;

public class PasswordResetRequest {
    private Integer id;
    private String oldPassword;
    private String newPassword;

    public PasswordResetRequest(Integer id, String oldPassword, String newPassword) {
        this.id = id;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
