package com.nttdata.apirestaccounts.handler;

import com.nttdata.apirestaccounts.dto.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@FeignClient(name = "api-rest-customers", url = "localhost:8081")
public interface CustomerClientRest {

    @GetMapping
    public Mono<ResponseEntity<Flux<CustomerDTO>>> list();

    @GetMapping("/{id}")
    public Mono<ResponseEntity<CustomerDTO>> getForId(@PathVariable("id") String id);
}
