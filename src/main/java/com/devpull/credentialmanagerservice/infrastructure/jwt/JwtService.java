package com.devpull.credentialmanagerservice.infrastructure.jwt;

import com.devpull.credentialmanagerservice.domain.user.User;
import com.devpull.credentialmanagerservice.infrastructure.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {

    private final JwtEncoder encoder;

    @Value("${app.jwt.issuer}") String issuer;

    @Value("${app.jwt.accessTokenMinutes}") long minutes;

    public JwtService(JwtEncoder encoder) { this.encoder = encoder; }

    public String createToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plus(minutes, ChronoUnit.MINUTES);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(exp)
                .subject(Long.toString(user.id().value()))  // sub = userId
                .claim("email", user.email().value())
                .build();

        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
    }
}
