ALTER TABLE rooms
  ALTER COLUMN number_of_beds TYPE integer
  USING number_of_beds::integer;