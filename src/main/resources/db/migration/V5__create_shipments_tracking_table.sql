CREATE TABLE shipment_tracking (
    id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    location VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_shipment_tracking_shipment
        FOREIGN KEY (shipment_id)
        REFERENCES shipments(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_tracking_shipment_id
ON shipment_tracking(shipment_id);
