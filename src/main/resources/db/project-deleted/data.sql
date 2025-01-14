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
           'c5cb1459-9118-4697-a499-ec94c8648338',
           'Middle East Crisis',
           'Providing humanitarian aid to those affected by ongoing conflicts in the Middle East, including shelter, medical supplies, and food.',
           50000.00,
           '2025-01-01 00:00:00+00',
           '2025-01-10 23:59:59+00',
           'COMPLETED',
           'HUMANITARIAN',
           'IL',
           'e69accdd-4121-4172-a072-bf181a21cbfd'
       ),
       (
           'a5fc3b19-cb3d-461a-87db-c2ef8111bab1',
           'Ukraine-Russia War',
           'Supporting displaced families and individuals affected by the war between Ukraine and Russia, including shelter, food, and psychological support.',
           100000.00,
           '2024-01-01 00:00:00+00',
           '2024-12-10 23:59:59+00',
           'COMPLETED',
           'EDUCATION',
           'UA',
           '6f9619ff-8b86-d011-b42d-00cf4fc964ff'
       ),
       (
           'f3fd724b-bc67-480e-a6f1-58ae9f1f2998',
           'Food Program in South Africa',
           'Addressing hunger and malnutrition in South Africa by providing meals and nutritional support to communities in need.',
           75000.00,
           '2024-01-01 00:00:00+00',
           '2024-12-31 23:59:59+00',
           'COMPLETED',
           'FOOD',
           'ZA',
           '72d4e2c7-85a7-4d90-8135-ef7418c39b1d'
       );
