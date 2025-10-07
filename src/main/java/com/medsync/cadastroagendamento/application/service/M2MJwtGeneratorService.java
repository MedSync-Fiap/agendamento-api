package com.medsync.cadastroagendamento.application.service;

import com.medsync.cadastroagendamento.application.dto.M2MJwt;
import com.medsync.cadastroagendamento.domain.exception.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class M2MJwtGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(M2MJwtGeneratorService.class);
    
    private final JwtEncoder jwtEncoder;

    @Value("${app.security.jwt.issuer}")
    private String issuer;

    @Value("${app.security.jwt.audience}")
    private String audience;

    private final AtomicReference<M2MJwt> cachedJwtRef = new AtomicReference<>();
    private static final long EXPIRATION_IN_MINUTES = 30;
    private static final long REFRESH_BUFFER_IN_SECONDS = 60;

    public M2MJwtGeneratorService(@Qualifier("m2mJwtEncoder") JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }
    public M2MJwt getTokenHistorico() {
        try {
            M2MJwt currentJwt = cachedJwtRef.get();

            if (currentJwt != null && currentJwt.expiresAt().isAfter(Instant.now().plusSeconds(REFRESH_BUFFER_IN_SECONDS))) {
                return currentJwt;
            }

            return generateToken();
        } catch (Exception e) {
            log.error("Erro ao obter token JWT para histórico", e);
            throw new JwtException("Falha ao gerar token JWT para comunicação com histórico", e);
        }
    }

    private synchronized M2MJwt generateToken() {
        try {
            M2MJwt currentJwt = cachedJwtRef.get();
            if (currentJwt != null && currentJwt.expiresAt().isAfter(Instant.now().plusSeconds(REFRESH_BUFFER_IN_SECONDS))) {
                return currentJwt;
            }

            Instant now = Instant.now();
            Instant expiry = now.plus(EXPIRATION_IN_MINUTES, ChronoUnit.MINUTES);

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer(issuer)
                    .audience(Collections.singletonList(audience))
                    .issuedAt(now)
                    .expiresAt(expiry)
                    .subject("m2m-agendamento-service")
                    .build();

            String newToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            M2MJwt newJwt = new M2MJwt(newToken, expiry);
            this.cachedJwtRef.set(newJwt);

            return newJwt;
        } catch (Exception e) {
            log.error("Erro ao gerar token JWT", e);
            throw new JwtException("Falha ao gerar token JWT", e);
        }
    }

}
