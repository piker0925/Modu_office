package com.modu.office.dto.request.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerLoginRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
