INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Glucose(Urine)' ) , 'N' , null , now() , 10);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ketones(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 20);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ketones(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 30);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ketones(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 40);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Blood(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 50);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Blood(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 60);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Blood(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 70);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bilirubin(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 80);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bilirubin(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 90);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bilirubin(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 100);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Urobilinogen Phenlpyruvic Acid(Urine)' ) , 'N' , null , now() , 110);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HGC(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 120);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HGC(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 130);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HGC(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 140);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pus Cells (>5/hpf)(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 150);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pus Cells (>5/hpf)(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 160);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pus Cells (>5/hpf)(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 170);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'S. haematobium(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 180);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'S. haematobium(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 190);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'S. haematobium(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 200);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'T. vaginalis(Swab/Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 210);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'T. vaginalis(Swab/Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 220);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'T. vaginalis(Swab/Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 230);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Yeast Cells(Swab/Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 240);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Yeast Cells(Swab/Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 250);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Yeast Cells(Swab/Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 260);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Red blood cells(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 270);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Red blood cells(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 280);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Red blood cells(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 290);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bacteria(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 300);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bacteria(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 310);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bacteria(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 320);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Spermatozoa(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 330);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Spermatozoa(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 340);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Spermatozoa(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 350);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Renal function tests(Urine)' ) , 'N' , null , now() , 360);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Creatinine(Blood)' ) , 'N' , null , now() , 370);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Urea(Blood)' ) , 'N' , null , now() , 380);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Sodium(Blood)' ) , 'N' , null , now() , 390);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Potassium(Blood)' ) , 'N' , null , now() , 400);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Chloride(Blood)' ) , 'N' , null , now() , 410);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Direct bilirubin(Blood)' ) , 'N' , null , now() , 420);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Total bilirubin(Blood)' ) , 'N' , null , now() , 430);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'SGPT/ALAT(Blood)' ) , 'N' , null , now() , 440);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'SGOT/ASAT(Blood)' ) , 'N' , null , now() , 450);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Serum Protein(Blood)' ) , 'N' , null , now() , 460);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Albumin(Blood)' ) , 'N' , null , now() , 470);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Alkaline Phosphate(Urine)' ) , 'N' , null , now() , 480);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Gamma GT(Blood)' ) , 'N' , null , now() , 490);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Amylase(Blood)' ) , 'N' , null , now() , 500);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Total cholestrol(Blood)' ) , 'N' , null , now() , 510);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Trigycerides(Blood)' ) , 'N' , null , now() , 520);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HDL(Blood)' ) , 'N' , null , now() , 530);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'LDE(Blood)' ) , 'N' , null , now() , 540);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'PSA- Total(Blood)' ) , 'N' , null , now() , 550);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'PSA-Free(Blood)' ) , 'N' , null , now() , 560);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'CSF Proteins(Blood)' ) , 'N' , null , now() , 570);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'CSF Glucose(Blood)' ) , 'N' , null , now() , 580);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Proteins(Blood)' ) , 'N' , null , now() , 590);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Glucose-Fasting(Blood)' ) , 'N' , null , now() , 600);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Glucose- Random(Blood)' ) , 'N' , null , now() , 610);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Glucose-2 HR PC(Blood)' ) , 'N' , null , now() , 620);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Acid phosphatase(Blood)' ) , 'N' , null , now() , 630);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bence jones protein(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 640);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bence jones protein(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 650);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bence jones protein(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 660);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Triiodothyronine(T3)(Blood)' ) , 'N' , null , now() , 670);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Thyroid-stimulating Hormone(TSH)(Blood)' ) , 'N' , null , now() , 680);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Falciparum(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 690);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Falciparum(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 700);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Falciparum(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 710);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ovale(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 720);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ovale(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 730);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ovale(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 740);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Malariae(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 750);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Malariae(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 760);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Malariae(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 770);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Vivax(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 780);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Vivax(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 790);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Vivax(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 800);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Borrelia(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 810);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Borrelia(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 820);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Borrelia(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 830);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Trypanosomes(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 840);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Trypanosomes(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 850);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Trypanosomes(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 860);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'T. vaginalis(Genital Smears)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 870);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'T. vaginalis(Genital Smears)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 880);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'T. vaginalis(Genital Smears)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 890);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'S. haematobium(Genital Smears)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 900);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'S. haematobium(Genital Smears)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 910);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'S. haematobium(Genital Smears)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 920);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Yeast cells(Genital Smears)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 930);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Yeast cells(Genital Smears)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 940);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Yeast cells(Genital Smears)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 950);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'L. donovani(Bone Marrow)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 960);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'L. donovani(Bone Marrow)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 970);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'L. donovani(Bone Marrow)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 980);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Taenia spp.(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 990);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Taenia spp.(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1000);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Taenia spp.(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1010);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'H. nana(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1020);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'H. nana(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1030);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'H. nana(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1040);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'H. diminuta(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1050);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'H. diminuta(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1060);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'H. diminuta(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1070);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hookworm(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1080);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hookworm(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1090);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hookworm(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1100);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Roundworms(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1110);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Roundworms(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1120);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Roundworms(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1130);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'S. mansoni(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1140);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'S. mansoni(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1150);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'S. mansoni(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1160);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Trichuris trichiura(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1170);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Trichuris trichiura(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1180);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Trichuris trichiura(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1190);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Strongyloides stercoralis(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1200);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Strongyloides stercoralis(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1210);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Strongyloides stercoralis(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1220);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Isospora belli(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1230);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Isospora belli(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1240);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Isospora belli(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1250);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'E hystolytica(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1260);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'E hystolytica(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1270);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'E hystolytica(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1280);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Giardia lamblia(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1290);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Giardia lamblia(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1300);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Giardia lamblia(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1310);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cryptosporidium(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1320);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cryptosporidium(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1330);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cryptosporidium(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1340);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cyclospora(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1350);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cyclospora(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1360);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cyclospora(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1370);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Onchocerca volvulus(Skin)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1380);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Onchocerca volvulus(Skin)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1390);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Onchocerca volvulus(Skin)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1400);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Leishmania(Skin)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1410);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Leishmania(Skin)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1420);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Leishmania(Skin)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1430);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Naisseria(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1440);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Naisseria(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1450);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Naisseria(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1460);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Klebsiella(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1470);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Klebsiella(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1480);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Klebsiella(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1490);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Staphyloccoci(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1500);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Staphyloccoci(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1510);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Staphyloccoci(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1520);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Streprococci(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1530);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Streprococci(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1540);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Streprococci(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1550);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Proteus(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1560);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Proteus(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1570);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Proteus(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1580);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Shigella(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1590);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Shigella(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1600);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Shigella(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1610);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Salmonella(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1620);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Salmonella(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1630);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Salmonella(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1640);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'V. cholera(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1650);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'V. cholera(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1660);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'V. cholera(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1670);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'E. coli(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1680);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'E. coli(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1690);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'E. coli(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1700);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'C. neoformans(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1710);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'C. neoformans(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1720);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'C. neoformans(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1730);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cardinella vaginalis(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1740);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cardinella vaginalis(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1750);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cardinella vaginalis(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1760);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Haemophilus(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1770);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Haemophilus(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1780);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Haemophilus(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1790);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bordotella pertusis(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1800);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bordotella pertusis(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1810);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bordotella pertusis(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1820);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Psuedomonas(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1830);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Psuedomonas(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1840);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Psuedomonas(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1850);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Coliforms(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1860);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Coliforms(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1870);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Coliforms(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1880);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Faecal coliforms(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1890);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Faecal coliforms(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1900);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Faecal coliforms(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1910);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Enterococcus faecalis(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1920);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Enterococcus faecalis(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1930);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Enterococcus faecalis(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1940);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Total viable counts-22C(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1950);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Total viable counts-22C(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1960);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Total viable counts-22C(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 1970);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Total viable counts-37C(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 1980);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Total viable counts-37C(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 1990);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Total viable counts-37C(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2000);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Clostridium(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2010);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Clostridium(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2020);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Clostridium(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2030);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Full blood count(Blood)' ) , 'N' , null , now() , 2040);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Manual WBCcounts(Blood)' ) , 'N' , null , now() , 2050);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Peripheral blood films(Swab)' ) , 'N' , null , now() , 2060);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Erythrocyte Sedimentation rate(Blood)' ) , 'N' , null , now() , 2070);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Sickling test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2080);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Sickling test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2090);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Sickling test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2100);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hb electrophoresis(Blood)' ) , 'N' , null , now() , 2110);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'G6PD screening(Blood)' ) , 'N' , null , now() , 2120);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bleeding time(Blood)' ) , 'N' , null , now() , 2130);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Clotting time(Blood)' ) , 'N' , null , now() , 2140);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Prothrombin test(Blood)' ) , 'N' , null , now() , 2150);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Partial prothrombin time(Blood)' ) , 'N' , null , now() , 2160);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bone Marrow Aspirates(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2170);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bone Marrow Aspirates(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2180);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bone Marrow Aspirates(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2190);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Reticulocyte counts %(Blood)' ) , 'N' , null , now() , 2200);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Haemoglobin- Male(Blood)' ) , 'N' , null , now() , 2210);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Haemoglobin- Female(Blood)' ) , 'N' , null , now() , 2220);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'CD4:CD8(Blood)' ) , 'N' , null , now() , 2230);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'CD4%(Blood)' ) , 'N' , null , now() , 2240);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Peripheral Blood Films(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2250);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Peripheral Blood Films(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2260);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Peripheral Blood Films(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2270);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Tissue Impression(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2280);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Tissue Impression(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2290);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Tissue Impression(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2300);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pap Smear(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2310);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pap Smear(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2320);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pap Smear(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2330);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ascitic fluid(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2340);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ascitic fluid(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2350);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ascitic fluid(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2360);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'CSF(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2370);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'CSF(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2380);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'CSF(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2390);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pleural fluid(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2400);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pleural fluid(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2410);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pleural fluid(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2420);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cervix(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2430);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cervix(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2440);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cervix(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2450);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Prostrate(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2460);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Prostrate(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2470);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Prostrate(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2480);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Breast(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2490);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Breast(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2500);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Breast(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2510);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ovarian cyst(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2520);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ovarian cyst(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2530);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ovarian cyst(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2540);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Fibroids(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2550);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Fibroids(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2560);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Fibroids(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2570);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Lymph nodes(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2580);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Lymph nodes(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2590);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Lymph nodes(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2600);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rapid Plasma Reagin(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2610);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rapid Plasma Reagin(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2620);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rapid Plasma Reagin(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2630);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'TPHA(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2640);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'TPHA(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2650);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'TPHA(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2660);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'ASO Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2670);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'ASO Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2680);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'ASO Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2690);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2700);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2710);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2720);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Widal test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2730);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Widal test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2740);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Widal test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2750);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Brucella test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2760);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Brucella test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2770);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Brucella test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2780);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rheumatoid Factor Tests(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2790);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rheumatoid Factor Tests(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2800);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rheumatoid Factor Tests(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2810);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cryptococcal Antigen(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2820);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cryptococcal Antigen(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2830);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cryptococcal Antigen(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2840);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Helicobacter pylori test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2850);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Helicobacter pylori test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2860);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Helicobacter pylori test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2870);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis A Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2880);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis A Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2890);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis A Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2900);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis B Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2910);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis B Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2920);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis B Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2930);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis C Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2940);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis C Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2950);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis C Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 2960);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Viral Load(Blood)' ) , 'N' , null , now() , 2970);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Formal Gel Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive' )  , now() , 2980);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Formal Gel Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Negative' )  , now() , 2990);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Formal Gel Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Unspecified' )  , now() , 3000);
