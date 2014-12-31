INSERT INTO clinlims.dictionary ( id, is_active, dict_entry, lastupdated, dictionary_category_id ) 
	VALUES ( nextval( 'clinlims.dictionary_seq' ) , 'Y' , 'n/a' , now(), ( select id from clinlims.dictionary_category where description = 'Kenya Lab' ));
INSERT INTO clinlims.dictionary ( id, is_active, dict_entry, lastupdated, dictionary_category_id ) 
	VALUES ( nextval( 'clinlims.dictionary_seq' ) , 'Y' , 'Positive/Negative' , now(), ( select id from clinlims.dictionary_category where description = 'Kenya Lab' ));
INSERT INTO clinlims.dictionary ( id, is_active, dict_entry, lastupdated, dictionary_category_id ) 
	VALUES ( nextval( 'clinlims.dictionary_seq' ) , 'Y' , '' , now(), ( select id from clinlims.dictionary_category where description = 'Kenya Lab' ));
