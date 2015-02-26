INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Glucose(Urine)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 100, 70, 125, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Urobilinogen Phenlpyruvic Acid(Urine)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 100, 0, 8, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Renal function tests(Urine)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 0, 0, 0, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Creatinine(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 62, 115, 26, 120, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Urea(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 3, 7, 2, 8, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Sodium(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 135, 145, 135, 155, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Potassium(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 4, 6, 3, 6, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Chloride(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 98, 106, 78, 106, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Direct bilirubin(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 6, 0, 0, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Total bilirubin(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 24, 0, 0, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'SGPT/ALAT(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 35, 0, 65, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'SGOT/ASAT(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 35, 0, 50, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Serum Protein(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 60, 78, 60, 80, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Albumin(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 35, 55, 35, 50, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Alkaline Phosphate(Urine)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 36, 92, 100, 500, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Gamma GT(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 30, 0, 50, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Amylase(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 100, 0, 100, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Total cholestrol(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 2, 5, 2, 6, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Trigycerides(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 2, 2, 5, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'HDL(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 1, 100, 1, 1, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'LDE(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 100, 0, 3, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'PSA- Total(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 4, 0, 0, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'PSA-Free(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 25, 100, NULL, NULL, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'CSF Proteins(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 15, 60, 0, 4, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'CSF Glucose(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 40, 80, 2, 5, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Proteins(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 0, 60, 78, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Glucose-Fasting(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 4, 6, 2, 7, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Glucose- Random(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 4, 8, NULL, NULL, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Glucose-2 HR PC(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 4, 7, NULL, NULL, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Acid phosphatase(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 1, 6, 0, 4, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Triiodothyronine(T3)(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 70, 180, 1, 3, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Thyroid-stimulating Hormone(TSH)(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 1, 5, 0, 5, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Full blood count(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 0, 0, 0, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Manual WBCcounts(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 100, 4800, 10800, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Peripheral blood films(Swab)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 100, 0, 29, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Erythrocyte Sedimentation rate(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 20, 0, 100, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Hb electrophoresis(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 100, 0, 100, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'G6PD screening(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 5, 15, 5, NULL, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Bleeding time(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 2, 9, NULL, NULL, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Clotting time(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 100, 2, 6, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Prothrombin test(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 11, 14, NULL, NULL, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Partial prothrombin time(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 25, 35, NULL, NULL, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Reticulocyte counts %(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 1, 3, NULL, NULL, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Haemoglobin- Male(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 13, 18, NULL, NULL, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Haemoglobin- Female(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 12, 17, NULL, NULL, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'CD4:CD8(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 4, NULL, NULL, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'CD4%(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 25, 65, 30, 40, now() );
INSERT INTO clinlims.result_limits(  id, test_id, test_result_type_id, min_age, max_age, gender, low_normal, high_normal, low_valid, high_valid, lastupdated) 
	 VALUES ( nextval( 'clinlims.result_limits_seq' ) , ( select id from clinlims.test where description = 'Viral Load(Blood)' ), 
			 (select id from clinlims.type_of_test_result where test_result_type = 'N' ), 0, 'Infinity' , NULL, 0, 2000, 40, 10000, now() );
