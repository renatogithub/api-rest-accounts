package com.nttdata.apirestaccounts.dto;

import lombok.Data;

@Data
public class CustomerDTO {
    private String id;
    private String name;
    private String address;
    private String email;
    private String phone;
    private String numberDocument;
    private String customerType;
}
