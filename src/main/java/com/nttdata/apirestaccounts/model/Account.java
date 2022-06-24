/**
 * Bean Stores Account Information
 *
 * @author Renato Ponce
 * @version 1.0
 * @since 2022-06-24
 */

package com.nttdata.apirestaccounts.model;

import com.nttdata.apirestaccounts.dto.CustomerDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "accounts")
public class Account {
    @Id
    private String id;
    private AccountType accountType;
    private String accountNumber;
    private String currency;
    private double amount;
    private CustomerDTO customer;
    private String state;
    private int maxLimitMovementPerMonth;
    private List<HeadLine> headlines;
    private List<AuthorizedSigner> authorizedSigners;
}
