package com.aurum.ledger;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class LedgerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LedgerApplication.class, args);
    }

    @RestController
    @RequestMapping("/api")
    @CrossOrigin(origins = "*")
    static class LedgerController {

        private final Map<String, LeadRecord> leads = new ConcurrentHashMap<>();

        @GetMapping("/healthz")
        public Map<String, String> healthz() {
            return Map.of("status", "operational");
        }

        @PostMapping("/leads")
        public ResponseEntity<LeadRecord> createLead(@Valid @RequestBody LeadRequest request) {
            String id = UUID.randomUUID().toString();

            LeadRecord record = new LeadRecord(
                    id,
                    request.name(),
                    request.email(),
                    request.company(),
                    request.product(),
                    request.intent(),
                    Instant.now().toString()
            );

            leads.put(id, record);

            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        }

        @GetMapping("/leads/{id}")
        public LeadRecord readLead(@PathVariable String id) {
            LeadRecord record = leads.get(id);

            if (record == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found");
            }

            return record;
        }
    }

    record LeadRequest(
            @NotBlank @Size(max = 140) String name,
            @NotBlank @Email @Size(max = 320) String email,
            @Size(max = 140) String company,
            String product,
            @Size(max = 2000) String intent
    ) {
    }

    record LeadRecord(
            String id,
            String name,
            String email,
            String company,
            String product,
            String intent,
            String receivedAt
    ) {
    }
}
