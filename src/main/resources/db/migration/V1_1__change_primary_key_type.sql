ALTER TABLE movie ADD temp_id uuid DEFAULT gen_random_uuid();

ALTER TABLE movie DROP COLUMN id;

ALTER TABLE movie ADD PRIMARY KEY (temp_id);

ALTER TABLE movie RENAME COLUMN temp_id TO id;