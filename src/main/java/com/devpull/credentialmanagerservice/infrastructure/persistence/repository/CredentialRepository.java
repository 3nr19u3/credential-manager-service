package com.devpull.credentialmanagerservice.infrastructure.persistence.repository;

import com.devpull.credentialmanagerservice.domain.credential.CredentialStatus;
import com.devpull.credentialmanagerservice.infrastructure.persistence.entity.CredentialEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Repository
public class CredentialRepository {

    private final DatabaseClient db;

    private static final String Id = "id";

    private static final String UserId = "userId";

    private static final String Status = "status";

    public CredentialRepository(DatabaseClient db) { this.db = db; }

    public Mono<CredentialEntity> insert(CredentialEntity e) {
        String sql = """
            insert into credentials (user_id, type, issuer, license_number, expiry_date, status)
            values (:userId, :type, :issuer, :licenseNumber, :expiryDate, :status::credential_status)
            returning id, user_id, type, issuer, license_number, expiry_date, status, deleted_at, created_at
        """;

        return db.sql(sql)
                .bind("userId", e.getUserId())
                .bind("type", e.getType())
                .bind("issuer", e.getIssuer())
                .bind("licenseNumber", e.getLicenseNumber())
                .bind("expiryDate", e.getExpiryDate())
                .bind("status", e.getStatus().name())
                .map((row, meta) -> {
                    CredentialEntity out = new CredentialEntity();
                    out.setId(row.get("id", Long.class));
                    out.setUserId(row.get("user_id", Long.class));
                    out.setType(row.get("type", String.class));
                    out.setIssuer(row.get("issuer", String.class));
                    out.setLicenseNumber(row.get("license_number", String.class));
                    out.setExpiryDate(row.get("expiry_date", java.time.LocalDate.class));
                    out.setStatus(CredentialStatus.valueOf(row.get("status", String.class)));
                    out.setDeletedAt(row.get("deleted_at", OffsetDateTime.class));
                    out.setCreatedAt(row.get("created_at", OffsetDateTime.class));
                    return out;
                })
                .one();
    }

    public Mono<CredentialEntity> findByIdAndUser(long id, long userId) {
        String sql = """
      select * from credentials
      where id = :id and user_id = :userId and deleted_at is null
    """;
        return db.sql(sql).bind(Id, id).bind(UserId, userId)
                .map((row, meta) -> {
                    CredentialEntity out = new CredentialEntity();
                    out.setId(row.get("id", Long.class));
                    out.setUserId(row.get("user_id", Long.class));
                    out.setType(row.get("type", String.class));
                    out.setIssuer(row.get("issuer", String.class));
                    out.setLicenseNumber(row.get("license_number", String.class));
                    out.setExpiryDate(row.get("expiry_date", java.time.LocalDate.class));
                    out.setStatus(CredentialStatus.valueOf(row.get("status", String.class)));
                    out.setDeletedAt(row.get("deleted_at", OffsetDateTime.class));
                    out.setCreatedAt(row.get("created_at", OffsetDateTime.class));
                    return out;
                })
                .one();
    }

    public Mono<Long> softDelete(long id, long userId) {
        return db.sql("""
      update credentials set deleted_at = now()
      where id = :id and user_id = :userId and deleted_at is null
    """)
                .bind(Id, id)
                .bind(UserId, userId)
                .fetch()
                .rowsUpdated();
    }

    public Mono<Long> updateStatus(long id,  CredentialStatus status) {
        return db.sql("update credentials set status = :status::credential_status where id = :id")
                .bind(Id, id)
                .bind(Status, status.name())
                .fetch()
                .rowsUpdated();
    }

    public Flux<CredentialEntity> list(long userId,
                                       String type,
                                       String status,
                                       int limit,
                                       OffsetDateTime cursorCreatedAt,
                                       Long cursorId) {

        StringBuilder sql = new StringBuilder("""
      select * from credentials
      where user_id = :userId and deleted_at is null
    """);

        if (type != null) sql.append(" and type = :type");
        if (status != null) sql.append(" and status = :status::credential_status");

        if (cursorCreatedAt != null && cursorId != null) {
            sql.append(" and (created_at, id) < (:cursorCreatedAt, :cursorId)");
        }

        sql.append(" order by created_at desc, id desc limit :limit");

        DatabaseClient.GenericExecuteSpec spec = db.sql(sql.toString())
                .bind("userId", userId)
                .bind("limit", limit);

        if (type != null) spec = spec.bind("type", type);
        if (status != null) spec = spec.bind("status", status);
        if (cursorCreatedAt != null && cursorId != null) {
            spec = spec.bind("cursorCreatedAt", cursorCreatedAt).bind("cursorId", cursorId);
        }

        return spec.map((row, meta) -> {
            CredentialEntity out = new CredentialEntity();
            out.setId(row.get("id", Long.class));
            out.setUserId(row.get("user_id", Long.class));
            out.setType(row.get("type", String.class));
            out.setIssuer(row.get("issuer", String.class));
            out.setLicenseNumber(row.get("license_number", String.class));
            out.setExpiryDate(row.get("expiry_date", java.time.LocalDate.class));
            out.setStatus(CredentialStatus.valueOf(row.get("status", String.class)));
            out.setDeletedAt(row.get("deleted_at", OffsetDateTime.class));
            out.setCreatedAt(row.get("created_at", OffsetDateTime.class));
            return out;
        }).all();
    }
}
