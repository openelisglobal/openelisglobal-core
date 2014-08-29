INSERT INTO localization(  id, description, english, french, lastupdated)
VALUES ( nextval('localization_seq'), 'test name', 'Chikungunya Rapid Test', (select name from clinlims.test where guid = '847e0fdd-8c2f-4f94-a176-b0ce7c577500' ), now());
update clinlims.test set name_localization_id = currval( 'localization_seq' ) where guid ='847e0fdd-8c2f-4f94-a176-b0ce7c577500';
INSERT INTO localization(  id, description, english, french, lastupdated)
VALUES ( nextval('localization_seq'), 'test name', 'Chikungunya Virus Test', (select name from clinlims.test where guid = '03f55673-170b-444a-a5e2-b7b060c15763' ), now());
update clinlims.test set name_localization_id = currval( 'localization_seq' ) where guid ='03f55673-170b-444a-a5e2-b7b060c15763';