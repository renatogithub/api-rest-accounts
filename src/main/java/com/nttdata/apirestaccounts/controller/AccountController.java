package com.nttdata.apirestaccounts.controller;

import com.nttdata.apirestaccounts.model.Account;
import com.nttdata.apirestaccounts.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService service;

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    @GetMapping
    public Mono<ResponseEntity<Flux<Account>>> list() {
        Flux<Account> fxAccounts = service.listAll();

        return Mono.just(ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fxAccounts));
    }

    @GetMapping("/{accountNumber}")
    public Mono<ResponseEntity<Account>> getByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
        return service.getByAccountNumber(accountNumber)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                ); //Mono<ResponseEntity<Account>>
    }

    @PostMapping
    public Mono<ResponseEntity<Account>> register(@RequestBody Account account, final ServerHttpRequest req) {

/*        Mono<Account> monoAccount = service.getByCustomer_v2(account.getCustomer().getId());

        monoAccount.doOnNext(x -> log.info("a:" + x.toString())).subscribe();*/

        log.info("trayendo1:" + account);
        log.info("trayendo2:" + account.getCustomer());
        log.info("trayendo3:" + account.getCustomer().toString());

        return service.create(account)
                .map(p->ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                );

        /*return service.getByCustomer_v2(account.getCustomer().getId(),account.getAccountType())
                .doOnNext(a -> log.info("probando1:" + a))
                .doOnNext(a->log.info("probando2:" + a.getAccountType().getCode()))
                //.doOnNext(a->log.info("probando3:" + a.getCustomer().toString()))
                //.filter(a -> a.getCustomer().getCustomerType().equals("P"))
                //.filter(a -> a.getAccountType().getCode().equals("CA"))
                //.doOnNext(a->System.out.println("aa1:" + a.toString()))
                //.switchIfEmpty(Mono.defer(()->service.create(account)))
                //.filter(a -> a.getAccountType().getCode().equals("CC"))
                //.doOnNext(a->System.out.println("aa2:" + a.toString()))
                //.switchIfEmpty(Mono.defer(()->service.create(account)))
                //.filter(a -> a.getAccountType().getCode().equals("PF"))
                //.doOnNext(a->System.out.println("aa3:" + a.toString()))
                .switchIfEmpty(service.create(account))
                //service.validateSaveAccount(account);
                .map(a -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(a.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(a));*/


//                .filter(a -> a.getAccountType().equals("CC"))
        //               .filter(a -> a.getAccountType().equals("PF"))

/*        return monoAccount.flatMap(a -> {
                    if (a.getCustomer().getCustomerType().equals("P") && a.getId() != null) {
                        return service.create(account);
                    }
                }).flatMap(a-)*/

/*        Mono<CustomerDTO> monoCustomerDTO = service.getByCustomerData(account.getCustomer().getId());

        monoCustomerDTO.doOnNext(cust -> {
            if (cust.getCustomerType().equals("CA") || cust.getCustomerType().equals("CC") || cust.getCustomerType().equals("PF")) {

            }
        });*/

        /*return service.getByCustomer_v2(account.getCustomer().getId())
                .switchIfEmpty(service.create(account))
                .map(a -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(a.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(a));*/

/*        return service.getByCustomer_v2(account.getCustomer().getId())
                .switchIfEmpty(service.create(account))
                .map(a -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(a.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(a));*/
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Account>> update(@PathVariable("id") String id, @RequestBody Account account) {

        Mono<Account> monoBody = Mono.just(account);
        Mono<Account> monoBD = service.getById(id);

        return monoBD
                .zipWith(monoBody, (bd, a) -> {
                    bd.setId(id);
                    bd.setAccountNumber(a.getAccountNumber());
                    bd.setAccountType(a.getAccountType());
                    bd.setAmount(a.getAmount());
                    bd.setAuthorizedSigners(a.getAuthorizedSigners());
                    bd.setCustomer(a.getCustomer());
                    bd.setCurrency(a.getCurrency());
                    bd.setHeadlines(a.getHeadlines());
                    bd.setMaxLimitMovementPerMonth(a.getMaxLimitMovementPerMonth());
                    bd.setState(a.getState());
                    return bd;
                })
                .flatMap(service::update) //bd->service.modificar(bd)
                .map(a -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(a))
                .defaultIfEmpty(new ResponseEntity<Account>(HttpStatus.NOT_FOUND));
    }

}
