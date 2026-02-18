-- Enable faster inserts
BEGIN;

-- Clear existing data (optional)
TRUNCATE TABLE shipment_tracking RESTART IDENTITY CASCADE;
TRUNCATE TABLE shipments RESTART IDENTITY CASCADE;

-- Carriers
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
        'DELIVERED',
        'CANCELLED'
    ];

    shipment_id BIGINT;
    carrier TEXT;
    origin TEXT;
    destination TEXT;
    tracking_no TEXT;
    created_time TIMESTAMP;
    event_time TIMESTAMP;
    num_events INT;
    i INT;
    j INT;

BEGIN

FOR i IN 1..100 LOOP

    carrier := carriers[1 + floor(random()*array_length(carriers,1))];
    origin := cities[1 + floor(random()*array_length(cities,1))];
    destination := cities[1 + floor(random()*array_length(cities,1))];

    tracking_no := 'TRK' || lpad(i::text, 6, '0');

    created_time := NOW() - (random() * interval '30 days');

    INSERT INTO shipments(
        shipper_name,
        carrier_name,
        pickup_location,
        delivery_location,
        tracking_number,
        status,
        rate,
        shipment_delivery_type,
        weight_gm,
        length_cm,
        width_cm,
        height_cm,
        created_at,
        updated_at,
        current_location
    )
    VALUES (
        'Suvo Exporters ' || i,
        carrier,
        origin,
        destination,
        tracking_no,
        'CREATED',
        (random()*50000 + 500)::numeric,
        CASE WHEN random() > 0.5 THEN 'EXPRESS' ELSE 'STANDARD' END,
        random()*5000,
        random()*100,
        random()*100,
        random()*100,
        created_time,
        created_time,
        origin
    )
    RETURNING id INTO shipment_id;

    -- tracking events count
    num_events := 3 + floor(random()*5);

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
            statuses[LEAST(j, array_length(statuses,1))],
            cities[1 + floor(random()*array_length(cities,1))],
            event_time,
            gen_random_uuid()
        );

    END LOOP;

    -- Update shipment with latest status
    UPDATE shipments
    SET
        status = statuses[LEAST(num_events, array_length(statuses,1))],
        current_location = cities[1 + floor(random()*array_length(cities,1))],
        updated_at = event_time
    WHERE id = shipment_id;

END LOOP;

END $$;

COMMIT;
