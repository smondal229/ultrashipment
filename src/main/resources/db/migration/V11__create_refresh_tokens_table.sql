CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,

    token VARCHAR(500) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL,

    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_refresh_username ON refresh_tokens(username);