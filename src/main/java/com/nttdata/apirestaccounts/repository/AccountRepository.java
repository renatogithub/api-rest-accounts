/**
 * Repository that stores Account information
 *
 * @author Renato Ponce
 * @version 1.0
 * @since 2022-06-24
 */

package com.nttdata.apirestaccounts.repository;

import com.nttdata.apirestaccounts.model.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {

    Mono<Account> findByAccountNumber(String accountNumber);

    Flux<Account> findByCustomer_Id(String customerId);

    Mono<Account> findByCustomer_IdAndAccountType_Code(String customerId, String codeAccountType);
}
