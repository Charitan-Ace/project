CREATE EXTENSION IF NOT EXISTS pgcrypto;


DROP TABLE IF EXISTS project CASCADE;
CREATE TABLE project (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    -- Assuming an ID column for primary key
    title VARCHAR(255) NOT NULL,
    description TEXT,
    goal DOUBLE PRECISION NOT NULL,
    -- Changed to DOUBLE PRECISION,
    -- Supports monetary values
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status_type VARCHAR(50) NOT NULL,
    category_type VARCHAR(50) NOT NULL,
    -- Updated enum values for CategoryType
    country_iso_code VARCHAR(50) NOT NULL,
    -- ISO 3166-1 alpha-3 format
    charity_id VARCHAR(50)
    -- created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- -- created_at
    -- updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- updated_at
);
INSERT INTO project (
        id,
        title,
        description,
        goal,
        start_time,
        end_time,
        status_type,
        category_type,
        country_iso_code,
        charity_id
    )
VALUES -- Global Crisis Charity Projects
    (
        '233a5ccf-3ed4-4c59-b0e6-3f07072f1a4a',
        'Middle East Crisis',
        'Providing humanitarian aid to those affected by ongoing conflicts in the Middle East, including shelter, medical supplies, and food.',
        50000.00,
        '2025-01-01 00:00:00+00',
        '2025-12-31 23:59:59+00',
        'PENDING',
        'HUMANITARIAN',
        'IL',
        'd4e7aef8-629f-4419-91a7-8f3eb5bb5e2b'
    ),
    (
        '0c112e0e-09a0-4c16-8d87-51e2847055cc',
        'Ukraine-Russia War',
        'Supporting displaced families and individuals affected by the war between Ukraine and Russia, including shelter, food, and psychological support.',
        100000.00,
        '2025-01-01 00:00:00+00',
        '2025-12-31 23:59:59+00',
        'APPROVED',
        'HUMANITARIAN',
        'UA',
        '72d4e2c7-85a7-4d90-8135-ef7418c39b1d'
    ),
    (
        'cffedd29-972f-41c4-bbe6-54829087caf1',
        'Food Program in South Africa',
        'Addressing hunger and malnutrition in South Africa by providing meals and nutritional support to communities in need.',
        75000.00,
        '2025-01-01 00:00:00+00',
        '2025-12-31 23:59:59+00',
        'APPROVED',
        'FOOD',
        'ZA',
        '14eeb072-6635-45c3-aad5-7e76fda0b26e'
    ),
    -- Local Charity Projects
    (
        'dda59ffb-0ef5-4a8e-8457-04149f414825',
        'Yagi Typhoon Support',
        'Helping communities in Vietnam recover from the devastating effects of the Yagi Typhoon by providing rebuilding assistance and emergency supplies.',
        20000.00,
        '2025-01-01 00:00:00+00',
        '2025-12-31 23:59:59+00',
        'PENDING',
        'HUMANITARIAN',
        'VN',
        'e69accdd-4121-4172-a072-bf181a21cbfd'
    ),
    (
        'fb51bc08-5ffa-4444-b70a-3410b1bc91ca',
        'Milton Hurricane Support',
        'Assisting residents in the USA affected by the Milton Hurricane with emergency relief, temporary housing, and recovery efforts.',
        30000.00,
        '2025-01-01 00:00:00+00',
        '2025-12-31 23:59:59+00',
        'PENDING',
        'HUMANITARIAN',
        'US',
        '9b75c19d-f70d-4c6d-b10b-3d517bbac1c8'
    ),
    (
        '144a5377-2ccc-4efe-81e8-18a6da36450b',
        'Helping Ukrainian Refugee',
        'Providing essential services such as housing, education, and job assistance to Ukrainian refugees in Germany.',
        40000.00,
        '2025-01-01 00:00:00+00',
        '2025-12-31 23:59:59+00',
        'APPROVED',
        'HUMANITARIAN',
        'DE',
        'e69accdd-4121-4172-a072-bf181a21cbfd'
    ),
    (
        '9284aa44-9bea-4647-80bd-55c413590404',
        'Supporting SOS Children’s Village',
        'Helping orphaned and abandoned children in China through educational programs and community support.',
        35000.00,
        '2025-01-01 00:00:00+00',
        '2025-12-31 23:59:59+00',
        'APPROVED',
        'HUMANITARIAN',
        'CN',
        '6f9619ff-8b86-d011-b42d-00cf4fc964ff'
    );
-- DROP TABLE IF EXISTS project CASCADE;
-- CREATE TABLE project (
--     id BIGSERIAL PRIMARY KEY,  -- assuming this is inherited from AbstractEntity, which likely has an id field
--     title VARCHAR(255) NOT NULL,
--     description TEXT,
--     goal DOUBLE PRECISION,
--     start_time TIMESTAMP WITH TIME ZONE,
--     end_time TIMESTAMP WITH TIME ZONE,
--     status_type VARCHAR(50) NOT NULL,  -- assuming StatusType is an enum and its values are stored as strings
--     charity_id BIGINT NOT NULL,  -- assuming charityId is a foreign key referencing another table (charity table)
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- created_at
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- updated_at
-- );
-- -- Create function to update the updated_at column in project_entity
-- CREATE OR REPLACE FUNCTION update_project_timestamp()
-- RETURNS TRIGGER AS $$
-- BEGIN
--     NEW.updated_at = NOW();
--     RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;
-- -- Create trigger to invoke the function on update for project_entity
-- CREATE TRIGGER set_project_timestamp
-- BEFORE UPDATE ON project
-- FOR EACH ROW
-- EXECUTE PROCEDURE update_project_timestamp();
-- Insert Global Charity Projects
-- INSERT INTO project (
--     title,
--     description,
--     goal,
--     start_time,
--     end_time,
--     status_type,
--     charity_id,
--     created_at,
--     updated_at
--   )
-- VALUES (
--     'Middle East Crisis',
--     'Support for victims of the Middle East crisis, organized by an Israeli non-profit organization.',
--     5000000.00,
--     '2026-01-01 00:00:00+00',
--     '2026-12-31 21:59:59+00',
--     'PENDING',
--     1,
--     CURRENT_TIMESTAMP,
--     CURRENT_TIMESTAMP
--   ),
--   (
--     'Ukraine – Russia War',
--     'Aid for Ukrainian refugees and citizens affected by the Russia-Ukraine war, organized by a Ukrainian non-profit organization.',
--     3000000.00,
--     '2026-02-01 00:00:00+00',
--     '2026-12-31 21:59:59+00',
--     'PENDING',
--     2,
--     CURRENT_TIMESTAMP,
--     CURRENT_TIMESTAMP
--   ),
--   (
--     'Food Program in South Africa',
--     'Providing food and aid to South African countries including Lesotho, Malawi, Namibia, Zambia, Zimbabwe, Mozambique, and Angola.',
--     7000000.00,
--     '2026-03-01 00:00:00+00',
--     '2026-12-31 21:59:59+00',
--     'PENDING',
--     3,
--     CURRENT_TIMESTAMP,
--     CURRENT_TIMESTAMP
--   );
-- -- Insert Local Charity Projects
-- INSERT INTO project (
--     title,
--     description,
--     goal,
--     start_time,
--     end_time,
--     status_type,
--     charity_id,
--     created_at,
--     updated_at
--   )
-- VALUES (
--     'Yagi Typhoon Support – Vietnam',
--     'Aid for victims of the Yagi Typhoon in Vietnam, organized by an individual.',
--     1000000.00,
--     '2026-04-01 00:00:00+00',
--     '2026-12-31 21:59:59+00',
--     'PENDING',
--     4,
--     CURRENT_TIMESTAMP,
--     CURRENT_TIMESTAMP
--   ),
--   (
--     'Milton Hurricane Support – USA',
--     'Support for victims of the Milton Hurricane in the USA, organized by an individual.',
--     1500000.00,
--     '2026-05-01 00:00:00+00',
--     '2026-12-31 21:59:59+00',
--     'PENDING',
--     5,
--     CURRENT_TIMESTAMP,
--     CURRENT_TIMESTAMP
--   ),
--   (
--     'Helping Ukrainian Refugee – Germany',
--     'Support for Ukrainian refugees in Germany, organized by a company.',
--     2500000.00,
--     '2026-06-01 00:00:00+00',
--     '2026-12-31 21:59:59+00',
--     'PENDING',
--     6,
--     CURRENT_TIMESTAMP,
--     CURRENT_TIMESTAMP
--   ),
--   (
--     'Supporting SOS Children’s Village – China',
--     'Aid and support for SOS Children’s Village in China, organized by a company.',
--     4000000.00,
--     '2026-07-01 00:00:00+00',
--     '2026-12-31 21:59:59+00',
--     'PENDING',
--     7,
--     CURRENT_TIMESTAMP,
--     CURRENT_TIMESTAMP
--   );