package com.nttdata.apirestaccounts.service;

import com.nttdata.apirestaccounts.dto.CustomerDTO;
import com.nttdata.apirestaccounts.model.Account;
import com.nttdata.apirestaccounts.model.AccountType;
import com.nttdata.apirestaccounts.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private WebClient webClient;

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Override
    public Mono<Account> create(Account account) {
//        return repository.save(account);
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

    @Override
    public Mono<Account> getByAccountCustomer(String customerId) {
        log.info("z:" + customerId);
        return repository.findByCustomer_Id(customerId);
    }

    @Override
    public Mono<Account> getByAccountCustomerAccountType(String customerId, String accountType) {
        return repository.findByCustomer_IdAndAccountType_Code(customerId, accountType);
    }

    private Mono<CustomerDTO> findByCustomer(String customerId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", customerId);
        return webClient.get().uri("/{id}", params).accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> response.bodyToMono(CustomerDTO.class));
    }

    private Mono<Account> validateSaveAccount(Account account) {

        log.info("x:" + account.getCustomer().getId());

        // Mono<Account> monoAccount=getByAccountCustomer(account.getCustomer().getId());

/*        return monoAccount
                .flatMap(data->findByCustomer(account.getCustomer().getId())
                        .flatMap(cust->{
                            data.setCustomer(cust);
                        })

                )
        }).defaultIfEmpty(new Account()).doOnNext(x->x.toString());*/


        return getByAccountCustomerAccountType(account.getCustomer().getId(),account.getAccountType().getCode())
//                .filter(acc -> acc.getAccountType().getCode().equals(account.getAccountType().getCode()))
                .doOnNext(acc -> log.info("acc" + acc.toString()))
                .flatMap(acc -> findByCustomer(account.getCustomer().getId())
                        .doOnNext(x -> log.info("data:" + x.toString()))
                        .flatMap(cust -> {
                            log.info("cust:" + cust.toString());
                            acc.setCustomer(cust);
                            log.info("datax:" + acc.toString());
                            if (acc.getCustomer().getCustomerType().equals("P")) {
                                log.info("data.getCustomer().getCustomerType():" + acc.getCustomer().getCustomerType());
                                log.info("data.getAccountType().getCode():" + acc.getAccountType().getCode());
                                if (acc.getAccountType().getCode().equals("CA") || acc.getAccountType().getCode().equals("CC") || acc.getAccountType().getCode().equals("PF")) {
                                    log.info("si entro a la condicional");
                                    return Mono.empty();
                                }
                            }else if(acc.getCustomer().getCustomerType().equals("E")){
                                log.info("data.getCustomer().getCustomerType():" + acc.getCustomer().getCustomerType());
                                if (acc.getAccountType().getCode().equals("CA") || acc.getAccountType().getCode().equals("PF")) {
                                    log.info("si entro a la condicional DE NEGOCIO");
                                    return Mono.empty();
                                }
                            }
                            return Mono.just(acc);
                        }).switchIfEmpty(repository.save(acc))
                )
                .switchIfEmpty(repository.save(account));
/*        return getByCustomer_v2(account.getCustomer().getId(),account.accountType.getCode())
                .flatMap(data->{
                    if(data.getCustomer()==null){

                    }
                });*/
    }

   /* private Mono<Account> validateSaveWithCriteria(Account account){
        return

        return isUserBusinessVip(bankAccount.getDniUser())
                .flatMap(condition-> userBusinessVipHaveCredit(bankAccount.getDniUser())
                        .flatMap(haveCredit->{
                            if(bankAccount.getBalance()>0 && typeAccount.test(bankAccount.getTypeAccount())){
                                if(condition.getStatus().equals(true) && haveCredit.getStatus().equals(true)){
                                    bankAccount.setBenefitStatus(true);
                                }
                                return bankAccountRepository.save(bankAccount);
                            }
                            return Mono.empty();
                        }));
    }*/


    @Override
    public Mono<Account> getByCustomer_v2(String customerId, AccountType accountType) {
        Map<String, Object> params = new HashMap<String, Object>();
        log.info("id:" + customerId);
        params.put("id", customerId);


        return repository.findByCustomer_IdAndAccountType_Code(customerId, accountType.getCode())
                .zipWhen(account -> {
                    Mono<CustomerDTO> monoCustomer = webClient.get().uri("/{id}", params).accept(MediaType.APPLICATION_JSON)
                            .exchangeToMono(response -> response.bodyToMono(CustomerDTO.class));
                    return monoCustomer;
                })
                .map(tuple2 -> {
                    Account ac = tuple2.getT1();
                    CustomerDTO cust = tuple2.getT2();
                    ac.setAccountType(accountType);
                    ac.setCustomer(cust);
                    return ac;
                });


        /*
        CustomerDTO customerDTO=null;
        monoCustomer.doOnNext(a->customerDTO=a);

        return repository.findByCustomer_Id(customerId)
                .flatMap(a->a.setCustomer())*/

/*        return monoCustomer
                .merge(repository.findByCustomer_Id(customerId),(a,b)-> {
                    b.setCustomer(a);
                    return b;
                });*/

/*        return repository.findByCustomer_Id(customerId)
                .zipWith(monoCustomer,(a,b)->{
                    a.setCustomer(b);
                    return a;
                });*/

  /*      Mono<Account> monoAccount = repository.findByCustomer_Id(customerId);

        monoAccount.doOnNext(x -> log.info("x:" + x.toString())).subscribe();

        return monoAccount
                .doOnNext(x -> log.info(x.toString()))
                .zipWith(monoCustomer, (acc, cust) -> {
                    System.out.println("cust:" + cust.toString());
                    acc.setCustomer(cust);
                    return acc;
                }).defaultIfEmpty(new Account());*/

/*        Mono.zip(monoCustomer, monoAccount)
                .map(a -> {
                    return a.getT2().setCustomer(a.getT1().getClass());
                });*/

/*        return monoAccount.flatMap(a->{

        });*/

     /*   CustomerDTO customerDTO=null;

        monoCustomer.flatMap(c->new CustomerDTO())


        return repository.findByCustomer_Id(customerId)
                .mergeWith(monoCustomer.

//        monoCustomer.doOnNext(a->log.info("nombre:" + a.getName())).subscribe();

        monoCustomer.flatMap(a->{})


        monoCustomer
                .flatMap(cust.ge)
                .map(a->repository.findByCustomer_Id(customerId))
                .flatMap(a.g)


        Mono<Account> monoAccount = repository.findByCustomer_Id(customerId);

        return monoAccount
                .flatMap()

        return monoCustomer
                .map(c-> c.getCustomerType())

        return monoAccount
                .zipWith(monoCustomer, (acc, cust) -> {
                    System.out.println("cust:" + cust.toString());
                    acc.setCustomer(cust);
                    return acc;
                }).defaultIfEmpty(new Account());*/

        //return repository.findByCustomer_Id(customerId);
    }

    @Override
    public Mono<CustomerDTO> getByCustomerData(String customerId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", customerId);
        return webClient.get().uri("/{id}", params).accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> response.bodyToMono(CustomerDTO.class));
    }
}
