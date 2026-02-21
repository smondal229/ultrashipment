-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS pgcrypto;

BEGIN;

-- Clear existing data
TRUNCATE TABLE shipment_tracking RESTART IDENTITY CASCADE;
TRUNCATE TABLE shipments RESTART IDENTITY CASCADE;

DO $$

DECLARE
    carriers TEXT[] := ARRAY['FEDEX', 'DHL', 'DELHIVERY', 'BLUEDART', 'UPS'];

    cities TEXT[] := ARRAY[
        'Mumbai','Delhi','Bangalore','Kolkata','Chennai',
        'Hyderabad','Pune','Ahmedabad','Lucknow','Jaipur',
        'Surat','Kanpur','Nagpur','Indore','Bhopal'
    ];

    statuses TEXT[] := ARRAY[
        'CREATED',
        'PICKED_UP',
        'IN_TRANSIT',
        'OUT_FOR_DELIVERY',
        'DELIVERED'
    ];

    providers TEXT[] := ARRAY['RAZORPAY','STRIPE','PAYU','CASHFREE'];
    currencies TEXT[] := ARRAY['INR','USD'];
    payment_methods TEXT[] := ARRAY['CARD','UPI','NETBANKING','WALLET'];
    gateway_codes TEXT[] := ARRAY['00','01','02','05'];

    shipment_id BIGINT;
    carrier TEXT;
    origin TEXT;
    destination TEXT;
    tracking_no TEXT;
    created_time TIMESTAMP;
    event_time TIMESTAMP;
    num_events INT;
    final_status TEXT;

    payment_json JSONB;

    i INT;
    j INT;

BEGIN

FOR i IN 1..100 LOOP

    carrier := carriers[1 + floor(random()*array_length(carriers,1))];
    origin := cities[1 + floor(random()*array_length(cities,1))];
    destination := cities[1 + floor(random()*array_length(cities,1))];

    tracking_no := 'TRK' || lpad(i::text, 6, '0');
    created_time := NOW() - (random() * interval '30 days');

    num_events := 3 + floor(random()*3); -- 3 to 5 events
    final_status := statuses[num_events];

    -- Build payment JSON matching PaymentMeta record
    payment_json := jsonb_build_object(
        'transactionId', gen_random_uuid(),
        'provider', providers[1 + floor(random()*array_length(providers,1))],
        'currency', currencies[1 + floor(random()*array_length(currencies,1))],
        'paymentMethod', payment_methods[1 + floor(random()*array_length(payment_methods,1))],
        'gatewayResponseCode', gateway_codes[1 + floor(random()*array_length(gateway_codes,1))],
        'status',
            CASE
                WHEN final_status = 'DELIVERED' THEN 'SUCCESS'
                ELSE 'PENDING'
            END
    );

    INSERT INTO shipments(
        shipper_name,
        carrier_name,
        pickup_location,
        delivery_location,
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
    VALUES (
        'Suvo Exporters ' || i,
        carrier,
        origin,
        destination,
        tracking_no,
        final_status,
        ROUND((random()*50000 + 500)::numeric, 2),
        CASE WHEN random() > 0.5 THEN 'EXPRESS' ELSE 'STANDARD' END,
        ROUND((random()*5000)::numeric, 2),
        ROUND((random()*100)::numeric, 2),
        ROUND((random()*100)::numeric, 2),
        ROUND((random()*100)::numeric, 2),
        'GM',
        'CM',
        ROUND((random()*20000 + 1000)::numeric, 2),
        created_time,
        created_time,
        origin,
        payment_json
    )
    RETURNING id INTO shipment_id;

    event_time := created_time;

    FOR j IN 1..num_events LOOP

        event_time := event_time + interval '4 hours' + random()*interval '6 hours';

        INSERT INTO shipment_tracking(
            shipment_id,
            status,
            location,
            event_time,
            event_id
        )
        VALUES(
            shipment_id,
            statuses[j],
            cities[1 + floor(random()*array_length(cities,1))],
            event_time,
            gen_random_uuid()
        );

    END LOOP;

    -- Update final shipment state
    UPDATE shipments
    SET
        current_location = destination,
        updated_at = event_time
    WHERE id = shipment_id;

END LOOP;

END $$;

COMMIT;