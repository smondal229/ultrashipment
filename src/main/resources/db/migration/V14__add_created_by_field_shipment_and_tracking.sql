ALTER TABLE shipments
ADD COLUMN created_by_id BIGINT;

ALTER TABLE shipments
ADD CONSTRAINT fk_shipments_created_by
FOREIGN KEY (created_by_id)
REFERENCES users(id)
ON DELETE RESTRICT;

ALTER TABLE shipment_tracking
ADD COLUMN created_by_id BIGINT;

ALTER TABLE shipment_tracking
ADD CONSTRAINT fk_shipments_created_by
FOREIGN KEY (created_by_id)
REFERENCES users(id)
ON DELETE RESTRICT;