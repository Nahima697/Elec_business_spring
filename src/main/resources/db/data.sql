-- Activer l'extension pgcrypto pour gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. Insert roles (avec ON CONFLICT)
INSERT INTO user_role(name) VALUES ('ROLE_USER')
ON CONFLICT (name) DO NOTHING;

-- 2. Insert users avec UUID générés
-- 2. Insert users (CORRIGÉ : Sans role_id ni phone_verif_code)
INSERT INTO app_user (
    id, username, email, password, phone_number,
    email_verified, email_verif_at,
    phone_verified, phone_verif_at,
    profile_picture_url,
    created_at
) VALUES
      (
          gen_random_uuid(), 'user1', 'user1@test.com', '{bcrypt}password123', '0600000001',
          true, NOW(),
          true, NOW(),
          NULL,
          NOW()
      ),
      (
          gen_random_uuid(), 'user2', 'user2@test.com', '{bcrypt}password456', '0600000002',
          true, NOW(),
          true, NOW(),
          NULL,
          NOW()
      ),
      (
          gen_random_uuid(), 'user3', 'user3@test.com', '{bcrypt}password789', '0600000003',
          true, NOW(),
          true, NOW(),
          NULL,
          NOW()
      )
ON CONFLICT (email) DO NOTHING;

-- 3. NOUVEAU : Liaison Many-to-Many dans la table de jointure
INSERT INTO user_user_role (user_id, role_id)
VALUES
    (
        (SELECT id FROM app_user WHERE username = 'user1'),
        (SELECT id FROM user_role WHERE name = 'ROLE_USER')
    ),
    (
        (SELECT id FROM app_user WHERE username = 'user2'),
        (SELECT id FROM user_role WHERE name = 'ROLE_USER')
    ),
    (
        (SELECT id FROM app_user WHERE username = 'user3'),
        (SELECT id FROM user_role WHERE name = 'ROLE_USER')
    )
ON CONFLICT DO NOTHING;

-- 3. Insert charging_location (avec UUID et FK user_id)
INSERT INTO charging_location (
    id, address_line, postal_code, city, country, name, user_id
) VALUES (
             gen_random_uuid(), '1 rue Lyon', '69007', 'Lyon', 'France', 'Lyon7',
             (SELECT id FROM app_user WHERE username = 'user1')
         )
ON CONFLICT  DO NOTHING;

-- 4. Insert charging_station
INSERT INTO charging_station (
    id, name, description, power_kw, price, created_at, lat, lng, location_id, image_url
) VALUES
      (
          gen_random_uuid(), 'Station A', 'Charge rapide 50kW', 50.00, 0.25, NOW(), 45.750000, 4.850000,
          (SELECT id FROM charging_location WHERE address_line = '1 rue Lyon' LIMIT 1),
          NULL
      ),
      (
          gen_random_uuid(), 'Station B', 'Borne classique 22kW', 22.00, 0.18, NOW(), 45.751000, 4.851000,
          (SELECT id FROM charging_location WHERE address_line = '1 rue Lyon' LIMIT 1),
          NULL
      )
ON CONFLICT DO NOTHING;


-- 5. Insert time_slot (exemple pour Station A)
INSERT INTO time_slot (id, station_id, start_time, end_time, is_available, availability) VALUES (
                                                                                                    gen_random_uuid(),
                                                                                                    (SELECT id FROM charging_station WHERE name = 'Station A' LIMIT 1),
                                                                                                    (NOW() + INTERVAL '1 hour')::timestamp,  -- Conversion en timestamp sans fuseau horaire
                                                                                                    (NOW() + INTERVAL '8 hour')::timestamp,  -- Conversion en timestamp sans fuseau horaire
                                                                                                    true,
                                                                                                    tsrange((NOW() + INTERVAL '1 hour')::timestamp, (NOW() + INTERVAL '8 hour')::timestamp, '[]')  -- Conversion ici aussi
                                                                                                )
ON CONFLICT DO NOTHING;
-- 6. Insert booking_status (en se basant sur enum)
INSERT INTO booking_status (id, name) VALUES
                                          (1, 'PENDING'),
                                          (2, 'ACCEPTED'),
                                          (3, 'REJECTED'),
                                          (4, 'CANCELLED')
ON CONFLICT (name) DO NOTHING;

-- 7. Insert booking
INSERT INTO booking (
    id, user_id, station_id, start_date, end_date, total_price, created_at, status_id
) VALUES (
             gen_random_uuid(),
             (SELECT id FROM app_user WHERE username = 'user1'),
             (SELECT id FROM charging_station WHERE name = 'Station A' LIMIT 1),
             NOW() + INTERVAL '2 hour',
             NOW() + INTERVAL '4 hour',
             0.50,
             NOW(),
             (SELECT id FROM booking_status WHERE name = 'PENDING')
         )
ON CONFLICT DO NOTHING;
