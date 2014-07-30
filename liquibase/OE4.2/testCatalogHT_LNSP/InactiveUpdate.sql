update clinlims.test set GUID='PSEUDO_GUID_56' where description = 'VIH-1 PCR Qualitatif(DBS)';
update clinlims.test set GUID='PSEUDO_GUID_57' where description = 'VIH-1 PCR Qualitatif(Sang Total)';

update clinlims.test set local_code='VIH-1 PCR Qualitatif-DBS' where guid = 'PSEUDO_GUID_56';
update clinlims.test set local_code='VIH-1 PCR Qualitatif-Blood' where guid = 'PSEUDO_GUID_57';

INSERT INTO clinlims.localization(  id, description, english, french, lastupdated)
VALUES ( nextval('localization_seq'), 'test name', 'HIV DNA PCR', (select name from clinlims.test where guid = 'PSEUDO_GUID_56' ), now());
update clinlims.test set name_localization_id = currval( 'localization_seq' ) where guid ='PSEUDO_GUID_56';
INSERT INTO clinlims.localization(  id, description, english, french, lastupdated)
VALUES ( nextval('localization_seq'), 'test report name','HIV DNA PCR' , (select reporting_description from clinlims.test where guid = 'PSEUDO_GUID_56' ), now());
update clinlims.test set reporting_name_localization_id = currval( 'localization_seq' ) where guid ='PSEUDO_GUID_56';
INSERT INTO clinlims.localization(  id, description, english, french, lastupdated)
VALUES ( nextval('localization_seq'), 'test name', 'HIV DNA PCR', (select name from clinlims.test where guid = 'PSEUDO_GUID_57' ), now());
update clinlims.test set name_localization_id = currval( 'localization_seq' ) where guid ='PSEUDO_GUID_57';
INSERT INTO clinlims.localization(  id, description, english, french, lastupdated)
VALUES ( nextval('localization_seq'), 'test report name', 'HIV DNA PCR', (select reporting_description from clinlims.test where guid = 'PSEUDO_GUID_57' ), now());
update clinlims.test set reporting_name_localization_id = currval( 'localization_seq' ) where guid ='PSEUDO_GUID_57';
