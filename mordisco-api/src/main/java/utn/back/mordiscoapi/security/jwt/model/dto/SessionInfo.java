package utn.back.mordiscoapi.security.jwt.model.dto;

import java.time.LocalDateTime;

public record SessionInfo(Long id,
                          String deviceInfo,
                          String ipAddress,
                          LocalDateTime createdAt,
                          LocalDateTime expiryDate,
                          boolean isCurrentSession) { }
