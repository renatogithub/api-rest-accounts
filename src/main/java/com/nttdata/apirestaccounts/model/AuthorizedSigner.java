package com.nttdata.apirestaccounts.model;

import lombok.Data;

@Data
public class AuthorizedSigner {
    private String name;
    private String lastname;
    private String numberDocument;
    private String email;
}
