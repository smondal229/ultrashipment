CREATE TABLE shipments (
    id BIGSERIAL PRIMARY KEY,
    shipper_name VARCHAR(255),
    carrier_name VARCHAR(255),
    pickup_location VARCHAR(255),
    delivery_location VARCHAR(255),
    tracking_number VARCHAR(255),
    status VARCHAR(50),
    rate NUMERIC,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
