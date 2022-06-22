package com.nttdata.apirestaccounts.service;

import com.nttdata.apirestaccounts.dto.CustomerDTO;
import com.nttdata.apirestaccounts.model.Account;
import com.nttdata.apirestaccounts.model.AccountType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<Account> create(Account account);

    Mono<Account> update(Account account);

    Flux<Account> listAll();

    Mono<Account> getById(String id);

    Mono<Account> getByAccountNumber(String accountNumber);

    Mono<Account> getByAccountCustomer(String customerId);

    Mono<Account> getByAccountCustomerAccountType(String customerId, String accountType);

    Mono<Account> getByCustomer_v2(String customerId, AccountType accountType);

    Mono<CustomerDTO> getByCustomerData(String customerId);
}
