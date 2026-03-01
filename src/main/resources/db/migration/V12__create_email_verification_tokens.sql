ALTER TABLE users ADD COLUMN verified BOOLEAN DEFAULT FALSE;

CREATE TABLE email_verification_tokens (
    id BIGSERIAL PRIMARY KEY,

    token VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL,

    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_email_verification_username
ON email_verification_tokens(username);