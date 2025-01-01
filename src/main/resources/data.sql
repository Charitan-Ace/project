DROP TABLE IF EXISTS project CASCADE;

CREATE TABLE project (
    id SERIAL PRIMARY KEY, -- Assuming an ID column for primary key
    title VARCHAR(255) NOT NULL,
    description TEXT,
    goal DECIMAL(15, 2) NOT NULL, -- Supports monetary values
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status_type ENUM('PENDING', 'APPROVED', 'HALTED', 'DELETED') NOT NULL, -- Updated enum values for StatusType
    category_type ENUM('FOOD', 'HEALTH', 'EDUCATION', 'ENVIRONMENT', 'RELIGION', 'HUMANITARIAN', 'HOUSING', 'OTHER') NOT NULL, -- Updated enum values for CategoryType
    country_iso_code CHAR(2) NOT NULL, -- ISO 3166-1 alpha-3 format
    charity_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- created_at
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- updated_at
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


INSERT INTO project (
    title,
    description,
    goal,
    start_time,
    end_time,
    status_type,
    charity_id,
    created_at,
    updated_at
  )
VALUES (
    'Middle East Crisis',
    'Support for victims of the Middle East crisis, organized by an Israeli non-profit organization.',
    5000000.00,
    '2026-01-01 00:00:00+00',
    '2026-12-31 21:59:59+00',
    'PENDING',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  ),
  (
    'Ukraine – Russia War',
    'Aid for Ukrainian refugees and citizens affected by the Russia-Ukraine war, organized by a Ukrainian non-profit organization.',
    3000000.00,
    '2026-02-01 00:00:00+00',
    '2026-12-31 21:59:59+00',
    'PENDING',
    2,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  ),
  (
    'Food Program in South Africa',
    'Providing food and aid to South African countries including Lesotho, Malawi, Namibia, Zambia, Zimbabwe, Mozambique, and Angola.',
    7000000.00,
    '2026-03-01 00:00:00+00',
    '2026-12-31 21:59:59+00',
    'PENDING',
    3,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  );
-- Insert Local Charity Projects
INSERT INTO project (
    title,
    description,
    goal,
    start_time,
    end_time,
    status_type,
    charity_id,
    created_at,
    updated_at
  )
VALUES (
    'Yagi Typhoon Support – Vietnam',
    'Aid for victims of the Yagi Typhoon in Vietnam, organized by an individual.',
    1000000.00,
    '2026-04-01 00:00:00+00',
    '2026-12-31 21:59:59+00',
    'PENDING',
    4,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  ),
  (
    'Milton Hurricane Support – USA',
    'Support for victims of the Milton Hurricane in the USA, organized by an individual.',
    1500000.00,
    '2026-05-01 00:00:00+00',
    '2026-12-31 21:59:59+00',
    'PENDING',
    5,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  ),
  (
    'Helping Ukrainian Refugee – Germany',
    'Support for Ukrainian refugees in Germany, organized by a company.',
    2500000.00,
    '2026-06-01 00:00:00+00',
    '2026-12-31 21:59:59+00',
    'PENDING',
    6,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  ),
  (
    'Supporting SOS Children’s Village – China',
    'Aid and support for SOS Children’s Village in China, organized by a company.',
    4000000.00,
    '2026-07-01 00:00:00+00',
    '2026-12-31 21:59:59+00',
    'PENDING',
    7,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  );