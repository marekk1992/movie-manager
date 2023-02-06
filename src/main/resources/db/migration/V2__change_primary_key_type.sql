alter table movie add temp_id uuid default gen_random_uuid();

alter table movie drop column id;

alter table movie add primary key (temp_id);

alter table movie rename column temp_id to id;