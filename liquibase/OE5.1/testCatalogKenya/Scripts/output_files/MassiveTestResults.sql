INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Glucose(Urine)' ) , 'N' , null , now() , 10);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ketones(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 20);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Blood(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 30);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bilirubin(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 40);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Urobilinogen Phenlpyruvic Acid(Urine)' ) , 'N' , null , now() , 50);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HGC(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 60);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pus Cells (>5/hpf)(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 70);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'S. haematobium(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 80);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'T. vaginalis(Swab/Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 90);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Yeast Cells(Swab/Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 100);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Red blood cells(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 110);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bacteria(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 120);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Spermatozoa(Urine)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 130);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Fasting blood sugar(Urine/Blood)' ) , 'N' , null , now() , 140);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Random blood sugar(Urine/Blood)' ) , 'N' , null , now() , 150);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'OGTT(Blood)' ) , 'N' , null , now() , 160);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Renal function tests(Urine)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 170);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Creatinine(Blood)' ) , 'N' , null , now() , 180);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Urea(Blood)' ) , 'N' , null , now() , 190);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Sodium(Blood)' ) , 'N' , null , now() , 200);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Potassium(Blood)' ) , 'N' , null , now() , 210);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Chloride(Blood)' ) , 'N' , null , now() , 220);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Direct bilirubin(Blood)' ) , 'N' , null , now() , 230);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Total bilirubin(Blood)' ) , 'N' , null , now() , 240);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'SGPT/ALAT(Blood)' ) , 'N' , null , now() , 250);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'SGOT/ASAT(Blood)' ) , 'N' , null , now() , 260);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Serum Protein(Blood)' ) , 'N' , null , now() , 270);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Albumin(Blood)' ) , 'N' , null , now() , 280);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Alkaline Phodphate(Urine)' ) , 'N' , null , now() , 290);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Gamma GT(Blood)' ) , 'N' , null , now() , 300);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Amylase(Blood)' ) , 'N' , null , now() , 310);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Total cholestrol(Blood)' ) , 'N' , null , now() , 320);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Trigycerides(Blood)' ) , 'N' , null , now() , 330);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HDL(Blood)' ) , 'N' , null , now() , 340);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'LDE(Blood)' ) , 'N' , null , now() , 350);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'PSA(Blood)' ) , 'N' , null , now() , 360);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'CSF Proteins(Blood)' ) , 'N' , null , now() , 370);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'CSF Glucose(Blood)' ) , 'N' , null , now() , 380);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Proteins(Blood)' ) , 'N' , null , now() , 390);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Glucose(Blood)' ) , 'N' , null , now() , 400);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Acid phosphatase(Blood)' ) , 'N' , null , now() , 410);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bence jones protein(Urine)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 420);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Triiodothyronine(T3)(Blood)' ) , 'N' , null , now() , 430);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Thyroid-stimulating Hormone(TSH)(Blood)' ) , 'N' , null , now() , 440);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Falciparum(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 450);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ovale(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 460);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Malariae(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 470);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Vivax(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 480);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Borrelia(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 490);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Trypanosomes(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 500);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'T. vaginalis(Genital Smears)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 510);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'S. haematobium(Genital Smears)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 520);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Yeast cells(Genital Smears)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 530);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'L. donovani(Bone Marrow)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 540);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Taenia spp.(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 550);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'H. nana(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 560);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'H. diminuta(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 570);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hookworm(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 580);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Roundworms(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 590);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'S. mansoni(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 600);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Trichuris trichiura(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 610);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Strongyloides stercoralis(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 620);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Isospora belli(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 630);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'E hystolytica(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 640);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Giardia lamblia(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 650);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cryptosporidium(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 660);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cyclospora(Stool)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 670);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Onchocerca volvulus(Skin)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 680);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Leishmania(Skin)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 690);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Naisseria(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 700);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Klebsiella(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 710);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Staphyloccoci(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 720);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Streprococci(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 730);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Proteus(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 740);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Shigella(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 750);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Salmonella(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 760);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'V. cholera(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 770);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'E. coli(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 780);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'C. neoformans(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 790);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cardinella vaginalis(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 800);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Haemophilus(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 810);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bordotella pertusis(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 820);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Psuedomonas(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 830);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Coliforms(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 840);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Faecal coliforms(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 850);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Enterococcus faecalis(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 860);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Total viable counts-22C(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 870);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Total viable counts-37C(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 880);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Clostridium(Swab)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 890);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Full blood count(Blood)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 900);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Manual WBCcounts(Blood)' ) , 'N' , null , now() , 910);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Peripheral blood films(Swab)' ) , 'N' , null , now() , 920);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Erythrocyte Sedimentation rate(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='n/a' )  , now() , 930);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Sickling test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 940);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hb electrophoresis(Blood)' ) , 'N' , null , now() , 950);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'G6PD screening(Blood)' ) , 'N' , null , now() , 960);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bleeding time(Blood)' ) , 'N' , null , now() , 970);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Clotting time(Blood)' ) , 'N' , null , now() , 980);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Prothrombin test(Blood)' ) , 'N' , null , now() , 990);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Partial prothrombin time(Blood)' ) , 'N' , null , now() , 1000);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Bone Marrow Aspirates(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1010);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Reticulocyte counts(Blood)' ) , 'N' , null , now() , 1020);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Haemoglobin(Blood)' ) , 'N' , null , now() , 1030);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'CD4/CD8(Blood)' ) , 'N' , null , now() , 1040);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'CD4%(Blood)' ) , 'N' , null , now() , 1050);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Peripheral Blood Films(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1060);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Tissue Impression(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1070);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pap Smear(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1080);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ascitic fluid(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1090);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'CSF(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1100);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Pleural fluid(Smear)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1110);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cervix(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1120);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Prostrate(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1130);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Breast(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1140);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Ovarian cyst(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1150);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Fibroids(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1160);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Lymph nodes(Tissue)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1170);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rapid Plasma Region(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1180);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'TPHA(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1190);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'ASO Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1200);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'HIV Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1210);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Widal test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1220);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Brucella test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1230);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Rheumatoid Factor Tests(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1240);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Cryptococcal Antigen(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1250);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Helicobacter pylori test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1260);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis A Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1270);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis B Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1280);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Hepatitis C Test(Blood)' ) , 'D' ,  ( select max(id) from clinlims.dictionary where dict_entry ='Positive/Negative' )  , now() , 1290);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Viral Load(Blood)' ) , 'N' , null , now() , 1300);
INSERT INTO clinlims.test_result( id, test_id, tst_rslt_type, value , lastupdated, sort_order)
	 VALUES ( nextval( 'clinlims.test_result_seq' ) , ( select id from clinlims.test where description = 'Formal Gel Test(Blood)' ) , 'Q' ,  ( select max(id) from clinlims.dictionary where dict_entry ='' )  , now() , 1310);
