/**
 * Implementation Interface Service Account
 *
 * @author Renato Ponce
 * @version 1.0
 * @since 2022-06-24
 */

package com.nttdata.apirestaccounts.service;

import com.nttdata.apirestaccounts.dto.CustomerDTO;
import com.nttdata.apirestaccounts.model.Account;
import com.nttdata.apirestaccounts.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Primary
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private WebClient webClient;

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Override
    public Mono<Account> create(Account account) {
        return validateSaveAccount(account);
    }

    @Override
    public Mono<Account> update(Account account) {
        return repository.save(account);
    }

    @Override
    public Flux<Account> listAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Account> getByAccountNumber(String accountNumber) {
        return repository.findByAccountNumber(accountNumber);
    }

    @Override
    public Mono<Account> getById(String id) {
        return repository.findById(id);
    }

    private Mono<CustomerDTO> findByCustomer(String customerId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", customerId);
        return webClient.get().uri("/{id}", params).accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> response.bodyToMono(CustomerDTO.class));
    }

    private Mono<Account> validateSaveAccount(Account account) {

        Mono<Account> monoAccountCustomer = repository.findByCustomer_IdAndAccountType_Code(account.getCustomer().getId(), account.getAccountType().getCode());

        Mono<CustomerDTO> monoCustomer = findByCustomer(account.getCustomer().getId());

        return monoCustomer
                .flatMap(c -> {
                    log.info("The customer type code is: " + c.getCustomerType());
                    if (c.getCustomerType().equalsIgnoreCase("P")) {
                        log.info("The customer is personal");
                        return monoAccountCustomer
                                .switchIfEmpty(repository.save(account));
                    } else if (c.getCustomerType().equalsIgnoreCase("E")) {
                        log.info("The customer is Business");

                        if (account.getAccountType().getCode().equalsIgnoreCase("CC")) {
                            log.info("The account type is: " + account.getAccountType().getCode());
                            log.info("This type of account is allowed for the type of customer Business");
                            return repository.save(account);
                        } else {
                            log.info("The account type is: " + account.getAccountType().getCode());
                            log.info("This type of account not is allowed for the type of customer Business");
                        }
                    }
                    return Mono.just(account);
                });
    }
}
