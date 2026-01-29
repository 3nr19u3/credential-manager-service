package com.devpull.credentialmanagerservice.api.v1.admin;

import com.devpull.credentialmanagerservice.api.v1.admin.dto.UpdateStatusRequest;
import com.devpull.credentialmanagerservice.application.credentials.CredentialService;
import com.devpull.credentialmanagerservice.domain.credential.CredentialStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/admin/credentials")
public class AdminController {

    private final CredentialService service;

    public AdminController(CredentialService service) { this.service = service; }

    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateStatus(@PathVariable long id,
                                   @RequestBody UpdateStatusRequest req) {
        CredentialStatus status = req.status();
        return service.adminUpdateStatus(id, status);
    }

}
