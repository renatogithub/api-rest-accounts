/**
 * Controller that receives the requests
 *
 * @author Renato Ponce
 * @version 1.0
 * @since 2022-06-24
 */

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

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Account>> getById(@PathVariable("id") String id) {
        return service.getById(id)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                ); //Mono<ResponseEntity<Account>>
    }

    @GetMapping("/accountNumber/{accountNumber}")
    public Mono<ResponseEntity<Account>> getByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
        return service.getByAccountNumber(accountNumber)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                ); //Mono<ResponseEntity<Account>>
    }

    @PostMapping
    public Mono<ResponseEntity<Account>> register(@RequestBody Account account, final ServerHttpRequest req) {

        return service.create(account)
                .map(ac -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(ac.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ac)
                );
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
