package com.ultraship.tms.messaging.model;

import com.ultraship.tms.service.EmailService;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class MailEvent implements Serializable {

    private String to;
    private String token;
    private MailType type;

    public enum MailType {
        VERIFICATION {
            public void process(EmailService service, MailEvent event) {
                service.sendVerificationEmail(event.getTo(), event.getToken());
            }
        },
        RESET_PASSWORD {
            public void process(EmailService service, MailEvent event) {
                service.sendResetEmail(event.getTo(), event.getToken());
            }
        };

        public abstract void process(EmailService service, MailEvent event);
    }

    public MailEvent() {}

    public MailEvent(String to, String token, MailType type) {
        this.to = to;
        this.token = token;
        this.type = type;
    }

}
