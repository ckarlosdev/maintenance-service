package com.hmbrandt.maintenance_service.dto.Notification;

public record NotificationEmailRequest(
        String to,
        String subject,
        String htmlContent
) {
}
