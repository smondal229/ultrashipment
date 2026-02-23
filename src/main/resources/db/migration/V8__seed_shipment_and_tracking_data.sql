-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS pgcrypto;

BEGIN;

TRUNCATE TABLE shipment_tracking RESTART IDENTITY CASCADE;
TRUNCATE TABLE shipments RESTART IDENTITY CASCADE;

WITH constants AS (
    SELECT
        ARRAY['FEDEX','DHL','DELHIVERY','BLUEDART','UPS'] AS carriers,
        ARRAY[
            'Mumbai','Delhi','Bangalore','Kolkata','Chennai',
            'Hyderabad','Pune','Ahmedabad','Lucknow','Jaipur',
            'Surat','Kanpur','Nagpur','Indore','Bhopal'
        ] AS cities,
        ARRAY[
            '400001','110001','560001','700001','600001',
            '500001','411001','380001','226001','302001',
            '395003','208001','440001','452001','462001'
        ] AS postal_codes,
        ARRAY['RAZORPAY','STRIPE','PAYU','CASHFREE'] AS providers,
        ARRAY['INR','USD'] AS currencies,
        ARRAY['CARD','UPI','NETBANKING','WALLET'] AS payment_methods,
        ARRAY['00','01','02','05'] AS gateway_codes
),

inserted_shipments AS (

    INSERT INTO shipments (
        shipper_name,
        carrier_name,
        pickup_city,
        pickup_postal_code,
        pickup_state,
        pickup_country,
        pickup_street,
        pickup_contact_number,
        delivery_city,
        delivery_postal_code,
        delivery_state,
        delivery_country,
        delivery_street,
        delivery_contact_number,
        tracking_number,
        status,
        rate,
        shipment_delivery_type,
        item_weight,
        item_length,
        item_width,
        item_height,
        weight_unit,
        dim_unit,
        item_value,
        created_at,
        updated_at,
        current_location,
        payment_meta
    )

    SELECT
        'Suvo Exporters ' || gs,
        constants.carriers[1 + floor(random()*array_length(constants.carriers,1))],

        addr.pickup_city,
        addr.pickup_postal,
        'State ' || addr.pickup_city,
        'India',
        'Street ' || gs,
        '90000' || lpad(gs::text, 5, '0'),

        addr.delivery_city,
        addr.delivery_postal,
        'State ' || addr.delivery_city,
        'India',
        'Street ' || (gs+100),
        '80000' || lpad(gs::text, 5, '0'),

        'TRK' || lpad(gs::text, 6, '0'),

        'CREATED',

        ROUND((random()*50000 + 500)::numeric, 2),
        CASE WHEN random() > 0.5 THEN 'EXPRESS' ELSE 'STANDARD' END,
        ROUND((random()*5000)::numeric, 2),
        ROUND((random()*100)::numeric, 2),
        ROUND((random()*100)::numeric, 2),
        ROUND((random()*100)::numeric, 2),
        'GM',
        'CM',
        ROUND((random()*20000 + 1000)::numeric, 2),

        NOW() - (random() * interval '30 days'),
        NOW(),
        addr.pickup_city,

        jsonb_build_object(
            'transactionId', gen_random_uuid(),
            'provider', constants.providers[1 + floor(random()*array_length(constants.providers,1))],
            'currency', constants.currencies[1 + floor(random()*array_length(constants.currencies,1))],
            'paymentMethod', constants.payment_methods[1 + floor(random()*array_length(constants.payment_methods,1))],
            'gatewayResponseCode', constants.gateway_codes[1 + floor(random()*array_length(constants.gateway_codes,1))],
            'status', 'PENDING'
        )

    FROM generate_series(1, 1000) gs
    CROSS JOIN constants
    CROSS JOIN LATERAL (
        SELECT
            constants.cities[1 + floor(random()*array_length(constants.cities,1))] AS pickup_city,
            constants.cities[1 + floor(random()*array_length(constants.cities,1))] AS delivery_city,
            constants.postal_codes[1 + floor(random()*array_length(constants.postal_codes,1))] AS pickup_postal,
            constants.postal_codes[1 + floor(random()*array_length(constants.postal_codes,1))] AS delivery_postal
    ) addr

    RETURNING id, pickup_city, delivery_city, created_at
)

-- Insert tracking events (3 per shipment)
INSERT INTO shipment_tracking (
    shipment_id,
    status,
    location,
    description,
    event_time
)
SELECT
    s.id,
    status_seq.status,
    status_seq.location_city,
    status_seq.description,
    s.created_at + status_seq.time_offset
FROM inserted_shipments s
CROSS JOIN LATERAL (
    VALUES
        ('CREATED', s.pickup_city, 'PICKUP_ADDRESS', 'Shipment created', interval '0 hours'),
        ('IN_TRANSIT', 'Nagpur', 'HUB', 'Arrived at sorting hub', interval '12 hours'),
        ('DELIVERED', s.delivery_city, 'DELIVERY_ADDRESS', 'Shipment delivered', interval '48 hours')
) AS status_seq(status, location_city, location_type, description, time_offset);

COMMIT;