ALTER TABLE NOTE ADD COLUMN NEW_COLUMN BLOB(104857600);
UPDATE NOTE SET NEW_COLUMN=FILEDATA;
ALTER TABLE NOTE DROP COLUMN FILEDATA;
RENAME COLUMN NOTE.NEW_COLUMN TO FILEDATA;