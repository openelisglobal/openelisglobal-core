
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, lastupdated)
VALUES ( nextval( 'test_result_seq' ) , (select id from clinlims.test where description = 'Score d''adherence(DBS)' ) ,
         'R' ,  now() ) ;

INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, lastupdated)
VALUES ( nextval( 'test_result_seq' ) , (select id from clinlims.test where description = 'Date de mise sous ARV(DBS)' ) ,
         'R' ,  now() ) ;
