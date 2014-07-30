update clinlims.test set GUID='847e0fdd-8c2f-4f94-a176-b0ce7c577500' where description = 'Chikungunya Test Rapide(Serum)';
update clinlims.test set GUID='03f55673-170b-444a-a5e2-b7b060c15763' where description = 'Recherche Virus Chikungunya(Serum)';

update clinlims.test set local_code='Chikungunya Test Rapide-Serum' where guid = '847e0fdd-8c2f-4f94-a176-b0ce7c577500';
update clinlims.test set local_code='Recherche Virus Chikungunya-Serum' where guid = '03f55673-170b-444a-a5e2-b7b060c15763';

INSERT INTO localization(  id, description, english, french, lastupdated)
VALUES ( nextval('localization_seq'), 'test name', (select name from clinlims.test where guid = '03f55673-170b-444a-a5e2-b7b060c15763' ), (select name from clinlims.test where guid = '03f55673-170b-444a-a5e2-b7b060c15763' ), now());
update clinlims.test set name_localization_id = currval( 'localization_seq' ) where guid ='03f55673-170b-444a-a5e2-b7b060c15763';
INSERT INTO localization(  id, description, english, french, lastupdated)
VALUES ( nextval('localization_seq'), 'test report name', (select reporting_description from clinlims.test where guid = '03f55673-170b-444a-a5e2-b7b060c15763' ), (select reporting_description from clinlims.test where guid = '03f55673-170b-444a-a5e2-b7b060c15763' ), now());
update clinlims.test set reporting_name_localization_id = currval( 'localization_seq' ) where guid ='03f55673-170b-444a-a5e2-b7b060c15763';
INSERT INTO localization(  id, description, english, french, lastupdated)
VALUES ( nextval('localization_seq'), 'test name', (select name from clinlims.test where guid = '847e0fdd-8c2f-4f94-a176-b0ce7c577500' ), (select name from clinlims.test where guid = '847e0fdd-8c2f-4f94-a176-b0ce7c577500' ), now());
update clinlims.test set name_localization_id = currval( 'localization_seq' ) where guid ='847e0fdd-8c2f-4f94-a176-b0ce7c577500';
INSERT INTO localization(  id, description, english, french, lastupdated)
VALUES ( nextval('localization_seq'), 'test report name', (select reporting_description from clinlims.test where guid = '847e0fdd-8c2f-4f94-a176-b0ce7c577500' ), (select reporting_description from clinlims.test where guid = '847e0fdd-8c2f-4f94-a176-b0ce7c577500' ), now());
update clinlims.test set reporting_name_localization_id = currval( 'localization_seq' ) where guid ='847e0fdd-8c2f-4f94-a176-b0ce7c577500';





