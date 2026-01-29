package com.devpull.credentialmanagerservice.domain.credential;

import com.devpull.credentialmanagerservice.domain.credential.exception.CredentialCannotBeDeletedException;
import com.devpull.credentialmanagerservice.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CredentialTest {

    @Test
    void canSoftDelete_onlyWhenPendingOrRejected() {
        Credential base = new Credential(
                new CredentialId(1),
                new UserId(10),
                CredentialType.HVAC_LICENSE,
                "Issuer",
                "ABC",
                LocalDate.parse("2027-12-31"),
                CredentialStatus.PENDING,
                null,
                OffsetDateTime.now()
        );

        assertTrue(base.canSoftDelete());

        Credential approved = new Credential(
                base.id(), base.userId(), base.type(), base.issuer(), base.licenseNumber(),
                base.expiryDate(), CredentialStatus.APPROVED, null, base.createdAt()
        );

        assertFalse(approved.canSoftDelete());
        assertThrows(CredentialCannotBeDeletedException.class,
                () -> approved.softDeleted(OffsetDateTime.now()));
    }
}
