INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Yeast Cells (>5/hpf)(Urine)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 10);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Red Blood Cells (>5/hpf)(Urine)' ) , 'N' , null , now() , 20);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Leukocytes(Urine)' ) , 'N' , null , now() , 30);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Other Microscopic Findings(Urine)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 40);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Glucose(Urine)' ) , 'R' , null , now() , 50);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ketones(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 60);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ketones(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 70);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ketones(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 80);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Blood(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 90);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Blood(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 100);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Blood(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 110);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bilirubin(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 120);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bilirubin(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 130);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bilirubin(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 140);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Specific Gravity(Urine)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 150);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'pH(Urine)' ) , 'N' , null , now() , 160);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Protein(Urine)' ) , 'N' , null , now() , 170);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Nitrite(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 180);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Nitrite(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 190);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Nitrite(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 200);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Urobilinogen Phenlpyruvic Acid(Urine)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 210);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pregnancy Test(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 220);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pregnancy Test(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 230);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pregnancy Test(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 240);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pus Cells (>5/hpf)(Urine)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 250);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'S. haematobium(Genital Swab)' ) , 'N' , null , now() , 260);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'T. vaginalis(Genital Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 270);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'T. vaginalis(Genital Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 280);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'T. vaginalis(Genital Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 290);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Yeast Cells(Genital Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 300);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Yeast Cells(Genital Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 310);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Yeast Cells(Genital Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 320);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Thick/Thin Smear for Malaria(Blood)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 330);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Malaria rapid test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 340);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Malaria rapid test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 350);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Malaria rapid test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 360);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Blood film for hemoparasites(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 370);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Blood film for hemoparasites(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 380);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Blood film for hemoparasites(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 390);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Wet mount microscopy(Genital Specimen)' ) , 'R' , null , now() , 400);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Stool exam(Stool)' ) , 'R' , null , now() , 410);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Parasites examination(Skin)' ) , 'R' , null , now() , 420);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Parasites examination(Bone Marrow)' ) , 'R' , null , now() , 430);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'India ink(CSF)' ) , 'R' , null , now() , 440);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'KOH(Genital Specimen)' ) , 'R' , null , now() , 450);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Occult blood(Stool)' ) , 'R' , null , now() , 460);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'KOH(Skin)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 470);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bence-Jones Protein(Urine)' ) , 'R' , null , now() , 480);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Rapid Test - KHB(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 490);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Rapid Test - KHB(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 500);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Rapid Test - KHB(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 510);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Rapid Test - First Response(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 520);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Rapid Test - First Response(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 530);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Rapid Test - First Response(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 540);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Rapid Test - Uni-Gold(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 550);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Rapid Test - Uni-Gold(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 560);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Rapid Test - Uni-Gold(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 570);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV EIA(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 580);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV EIA(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 590);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV EIA(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 600);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Antigen Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 610);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Antigen Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 620);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Antigen Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 630);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Measles(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 640);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Measles(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 650);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Measles(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 660);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rubella(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 670);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rubella(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 680);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rubella(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 690);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rapid Plasma Reagin(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 700);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rapid Plasma Reagin(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 710);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rapid Plasma Reagin(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 720);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'TPHA(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 730);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'TPHA(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 740);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'TPHA(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 750);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'ASO Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 760);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'ASO Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 770);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'ASO Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 780);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Salmonella antigen test(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 790);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Salmonella antigen test(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 800);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Salmonella antigen test(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 810);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Salmonella antigen test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 820);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Salmonella antigen test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 830);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Salmonella antigen test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 840);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Widal test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 850);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Widal test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 860);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Widal test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 870);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Brucella test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 880);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Brucella test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 890);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Brucella test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 900);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rheumatoid Factor Tests(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 910);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rheumatoid Factor Tests(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 920);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rheumatoid Factor Tests(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 930);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cryptococcal Antigen(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 940);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cryptococcal Antigen(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 950);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cryptococcal Antigen(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 960);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cryptococcal Antigen(CSF)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 970);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cryptococcal Antigen(CSF)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 980);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cryptococcal Antigen(CSF)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 990);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Helicobacter pylori test(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1000);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Helicobacter pylori test(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1010);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Helicobacter pylori test(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1020);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis A Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1030);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis A Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1040);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis A Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1050);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis B Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1060);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis B Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1070);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis B Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1080);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis C Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1090);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis C Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1100);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis C Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1110);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'WBC Count(Blood)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 1120);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Neutrophil, Absolute(Blood)' ) , 'N' , null , now() , 1130);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Lymphocyte, Absolute(Blood)' ) , 'N' , null , now() , 1140);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Monocyte, Absolute(Blood)' ) , 'N' , null , now() , 1150);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Eosinophil, Absolute(Blood)' ) , 'N' , null , now() , 1160);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Basophil, Absolute(Blood)' ) , 'N' , null , now() , 1170);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Neutrophil(Blood)' ) , 'N' , null , now() , 1180);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Lymphocyte(Blood)' ) , 'N' , null , now() , 1190);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Monocyte(Blood)' ) , 'N' , null , now() , 1200);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Eosinophil(Blood)' ) , 'N' , null , now() , 1210);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Basophil(Blood)' ) , 'N' , null , now() , 1220);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'RBC Count - Male(Blood)' ) , 'N' , null , now() , 1230);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'RBC Count - Female(Blood)' ) , 'N' , null , now() , 1240);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hemoglobin - Male(Blood)' ) , 'N' , null , now() , 1250);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hemoglobin - Female(Blood)' ) , 'N' , null , now() , 1260);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hematocrit - Male(Blood)' ) , 'N' , null , now() , 1270);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hematocrit - Female(Blood)' ) , 'N' , null , now() , 1280);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'MCV(Blood)' ) , 'N' , null , now() , 1290);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'MCH(Blood)' ) , 'N' , null , now() , 1300);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'MCHC(Blood)' ) , 'N' , null , now() , 1310);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'RDW(Blood)' ) , 'N' , null , now() , 1320);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'PLT(Blood)' ) , 'N' , null , now() , 1330);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'PCT(Blood)' ) , 'N' , null , now() , 1340);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'MPV(Blood)' ) , 'N' , null , now() , 1350);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'PDW(Blood)' ) , 'N' , null , now() , 1360);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hb electrophoresis(Blood)' ) , 'N' , null , now() , 1370);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'G6PD screening(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1380);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'G6PD screening(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1390);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'G6PD screening(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1400);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bleeding Time(Blood)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 1410);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Thrombin Clotting Time(Blood)' ) , 'N' , null , now() , 1420);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Prothrombin Time(Blood)' ) , 'N' , null , now() , 1430);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Partial prothrombin time(Blood)' ) , 'N' , null , now() , 1440);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bone Marrow Aspirates(Bone Marrow)' ) , 'N' , null , now() , 1450);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Erythrocyte Sedimentation rate (ESR)(Blood)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 1460);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Reticulocyte counts %(Blood)' ) , 'N' , null , now() , 1470);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Haemoglobin - HemoCue(Blood)' ) , 'N' , null , now() , 1480);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'CD4 %(Blood)' ) , 'N' , null , now() , 1490);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'CD4, Absolute(Blood)' ) , 'N' , null , now() , 1500);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'CD4:CD8 ratio(Blood)' ) , 'N' , null , now() , 1510);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Direct bilirubin(Blood)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 1520);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Total bilirubin(Blood)' ) , 'N' , null , now() , 1530);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'SGPT/ALT - Male(Blood)' ) , 'N' , null , now() , 1540);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'SGPT/ALT - Female(Blood)' ) , 'N' , null , now() , 1550);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'SGOT/AST(Blood)' ) , 'N' , null , now() , 1560);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Serum Protein(Blood)' ) , 'N' , null , now() , 1570);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Albumin(Blood)' ) , 'N' , null , now() , 1580);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Alkaline Phosphatase - Male(Blood)' ) , 'N' , null , now() , 1590);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Alkaline Phosphatase - Female(Blood)' ) , 'N' , null , now() , 1600);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Gamma GT(Blood)' ) , 'N' , null , now() , 1610);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Amylase(Blood)' ) , 'N' , null , now() , 1620);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Creatinine(Blood)' ) , 'N' , null , now() , 1630);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Urea(Blood)' ) , 'N' , null , now() , 1640);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Sodium(Blood)' ) , 'N' , null , now() , 1650);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Potassium(Blood)' ) , 'N' , null , now() , 1660);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Chloride(Blood)' ) , 'N' , null , now() , 1670);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bicarbonate(Blood)' ) , 'N' , null , now() , 1680);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Creatinine Clearance(Urine)' ) , 'N' , null , now() , 1690);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Total cholestrol(Blood)' ) , 'N' , null , now() , 1700);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Trigycerides(Blood)' ) , 'N' , null , now() , 1710);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HDL(Blood)' ) , 'N' , null , now() , 1720);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'LDL(Blood)' ) , 'N' , null , now() , 1730);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'PSA - Total(Blood)' ) , 'N' , null , now() , 1740);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'PSA - Free(Blood)' ) , 'N' , null , now() , 1750);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Proteins(CSF)' ) , 'N' , null , now() , 1760);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Glucose(CSF)' ) , 'N' , null , now() , 1770);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Glucose - Fasting (Finger-Stick Test)(Blood)' ) , 'N' , null , now() , 1780);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Glucose - Random (Finger-Stick Test)(Blood)' ) , 'N' , null , now() , 1790);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Glucose-Fasting (Serum/Plasma)(Blood)' ) , 'N' , null , now() , 1800);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Glucose- Random(Blood)' ) , 'N' , null , now() , 1810);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Glucose-2 HR PC(Blood)' ) , 'N' , null , now() , 1820);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Acid phosphatase(Blood)' ) , 'N' , null , now() , 1830);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Triiodothyronine (T3)(Blood)' ) , 'N' , null , now() , 1840);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Thyroid-stimulating Hormone (TSH)(Blood)' ) , 'N' , null , now() , 1850);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bacterial culture(Blood)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 1860);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bacterial culture(Urine)' ) , 'R' , null , now() , 1870);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bacterial culture(Pus swab)' ) , 'R' , null , now() , 1880);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bacterial culture(Vaginal swab)' ) , 'R' , null , now() , 1890);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bacterial culture(Respiratory specimen)' ) , 'R' , null , now() , 1900);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bacterial culture(Rectal swab)' ) , 'R' , null , now() , 1910);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bacterial culture(Urethral swab)' ) , 'R' , null , now() , 1920);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bacterial culture(CSF)' ) , 'R' , null , now() , 1930);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bacterial culture(Aspirates)' ) , 'R' , null , now() , 1940);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bacterial culture(Eye swab)' ) , 'R' , null , now() , 1950);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bacterial culture(Ear swab)' ) , 'R' , null , now() , 1960);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Drug Sensitivity(Blood)' ) , 'R' , null , now() , 1970);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Drug Sensitivity(Urine)' ) , 'R' , null , now() , 1980);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Drug Sensitivity(Pus swab)' ) , 'R' , null , now() , 1990);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Drug Sensitivity(Vaginal swab)' ) , 'R' , null , now() , 2000);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Drug Sensitivity(Respiratory specimen)' ) , 'R' , null , now() , 2010);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Drug Sensitivity(Rectal swab)' ) , 'R' , null , now() , 2020);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Drug Sensitivity(Urethral swab)' ) , 'R' , null , now() , 2030);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Drug Sensitivity(CSF)' ) , 'R' , null , now() , 2040);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Drug Sensitivity(Aspirates)' ) , 'R' , null , now() , 2050);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Drug Sensitivity(Eye swab)' ) , 'R' , null , now() , 2060);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Drug Sensitivity(Ear swab)' ) , 'R' , null , now() , 2070);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Gram stain(Blood)' ) , 'R' , null , now() , 2080);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Gram stain(Urine)' ) , 'R' , null , now() , 2090);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Gram stain(Pus swab)' ) , 'R' , null , now() , 2100);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Gram stain(Vaginal swab)' ) , 'R' , null , now() , 2110);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Gram stain(Respiratory specimen)' ) , 'R' , null , now() , 2120);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Gram stain(Rectal swab)' ) , 'R' , null , now() , 2130);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Gram stain(Urethral swab)' ) , 'R' , null , now() , 2140);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Gram stain(CSF)' ) , 'R' , null , now() , 2150);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Gram stain(Aspirates)' ) , 'R' , null , now() , 2160);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Gram stain(Eye swab)' ) , 'R' , null , now() , 2170);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Gram stain(Ear swab)' ) , 'R' , null , now() , 2180);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'AFB stain(Respiratory specimen)' ) , 'R' , null , now() , 2190);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'TB fluorescence microscopy(Respiratory specimen)' ) , 'R' , null , now() , 2200);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'TB culture(Respiratory specimen)' ) , 'R' , null , now() , 2210);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Early infant diagnosis HIV PCR(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2220);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Early infant diagnosis HIV PCR(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2230);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Early infant diagnosis HIV PCR(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2240);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Early infant diagnosis HIV PCR(Dried Blood Spots)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2250);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Early infant diagnosis HIV PCR(Dried Blood Spots)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2260);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Early infant diagnosis HIV PCR(Dried Blood Spots)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2270);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Viral Load(Blood)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 2280);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis C Qualitative PCR(Blood)' ) , 'N' , null , now() , 2290);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis C Viral Load(Blood)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 2300);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis B Viral Load(Blood)' ) , 'N' , null , now() , 2310);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Gene Xpert MTB/RIF(Respiratory specimen)' ) , 'N' , null , now() , 2320);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'ABO grouping(Blood)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 2330);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rh grouping(Blood)' ) , 'R' , null , now() , 2340);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cross matching(Blood)' ) , 'R' , null , now() , 2350);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Direct Coombs test(Blood)' ) , 'R' , null , now() , 2360);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Indirect Coombs test(Blood)' ) , 'R' , null , now() , 2370);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Du test(Blood)' ) , 'R' , null , now() , 2380);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Tissue Impression(Lymph nodes)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 2390);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Tissue Impression(Respiratory tract lavage)' ) , 'R' , null , now() , 2400);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Tissue Impression(Pleural fluid)' ) , 'R' , null , now() , 2410);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Tissue Impression(Ascitic fluid)' ) , 'R' , null , now() , 2420);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Tissue Impression(CSF)' ) , 'R' , null , now() , 2430);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Tissue Impression(Liver tissue)' ) , 'R' , null , now() , 2440);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Tissue Impression(Kidney tissue)' ) , 'R' , null , now() , 2450);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Tissue Impression(Vaginal swab)' ) , 'R' , null , now() , 2460);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'H & E staining(Cervix)' ) , 'R' , null , now() , 2470);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'H & E staining(Prostrate)' ) , 'R' , null , now() , 2480);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'H & E staining(Breast)' ) , 'R' , null , now() , 2490);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'H & E staining(Ovarian cyst)' ) , 'R' , null , now() , 2500);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'H & E staining(Fibroids)' ) , 'R' , null , now() , 2510);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'H & E staining(Lymph nodes)' ) , 'R' , null , now() , 2520);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Prussian Blue Staining(Bone Marrow Biopsy)' ) , 'R' , null , now() , 2530);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pap smear(Cervical smear)' ) , 'R' , null , now() , 2540);
