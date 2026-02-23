CREATE TABLE shipments (
    id BIGSERIAL PRIMARY KEY,
    shipper_name VARCHAR(255),
    carrier_name VARCHAR(255),

    -- Pickup Address
    pickup_city VARCHAR(100) NOT NULL,
    pickup_postal_code VARCHAR(20) NOT NULL,
    pickup_state VARCHAR(100),
    pickup_country VARCHAR(100),
    pickup_street VARCHAR(255),
    pickup_contact_number VARCHAR(20),

    -- Delivery Address
    delivery_city VARCHAR(100) NOT NULL,
    delivery_postal_code VARCHAR(20) NOT NULL,
    delivery_state VARCHAR(100),
    delivery_country VARCHAR(100),
    delivery_street VARCHAR(255),
    delivery_contact_number VARCHAR(20),

    tracking_number VARCHAR(255),
    status VARCHAR(50),
    rate NUMERIC,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    is_flagged BOOLEAN NOT NULL DEFAULT FALSE
);
