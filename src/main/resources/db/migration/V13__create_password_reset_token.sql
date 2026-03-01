CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,

    token VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL,

    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_password_reset_username
ON password_reset_tokens(username);