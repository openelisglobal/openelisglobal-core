INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Urine Chemistry') , now(), null,  (select id from clinlims.test where description = 'Glucose(Urine)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Urine Chemistry') , now(), null,  (select id from clinlims.test where description = 'Ketones(Urine)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Urine Chemistry') , now(), null,  (select id from clinlims.test where description = 'Blood(Urine)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Urine Chemistry') , now(), null,  (select id from clinlims.test where description = 'Bilirubin(Urine)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Urine Chemistry') , now(), null,  (select id from clinlims.test where description = 'Urobilinogen Phenlpyruvic Acid(Urine)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Urine Chemistry') , now(), null,  (select id from clinlims.test where description = 'HGC(Urine)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Urine Microscopy') , now(), null,  (select id from clinlims.test where description = 'Pus Cells (>5/hpf)(Urine)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Urine Microscopy') , now(), null,  (select id from clinlims.test where description = 'S. haematobium(Urine)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Urine Microscopy') , now(), null,  (select id from clinlims.test where description = 'T. vaginalis(Swab/Urine)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Urine Microscopy') , now(), null,  (select id from clinlims.test where description = 'Yeast Cells(Swab/Urine)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Urine Microscopy') , now(), null,  (select id from clinlims.test where description = 'Red blood cells(Urine)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Urine Microscopy') , now(), null,  (select id from clinlims.test where description = 'Bacteria(Urine)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Urine Microscopy') , now(), null,  (select id from clinlims.test where description = 'Spermatozoa(Urine)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Blood Chemistry') , now(), null,  (select id from clinlims.test where description = 'Renal function tests(Urine)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Blood Chemistry') , now(), null,  (select id from clinlims.test where description = 'Creatinine(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Blood Chemistry') , now(), null,  (select id from clinlims.test where description = 'Urea(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Blood Chemistry') , now(), null,  (select id from clinlims.test where description = 'Sodium(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Blood Chemistry') , now(), null,  (select id from clinlims.test where description = 'Potassium(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Blood Chemistry') , now(), null,  (select id from clinlims.test where description = 'Chloride(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Liver function tests') , now(), null,  (select id from clinlims.test where description = 'Direct bilirubin(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Liver function tests') , now(), null,  (select id from clinlims.test where description = 'Total bilirubin(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Liver function tests') , now(), null,  (select id from clinlims.test where description = 'SGPT/ALAT(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Liver function tests') , now(), null,  (select id from clinlims.test where description = 'SGOT/ASAT(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Liver function tests') , now(), null,  (select id from clinlims.test where description = 'Serum Protein(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Liver function tests') , now(), null,  (select id from clinlims.test where description = 'Albumin(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Liver function tests') , now(), null,  (select id from clinlims.test where description = 'Alkaline Phosphate(Urine)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Liver function tests') , now(), null,  (select id from clinlims.test where description = 'Gamma GT(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Lipid profile') , now(), null,  (select id from clinlims.test where description = 'Amylase(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Lipid profile') , now(), null,  (select id from clinlims.test where description = 'Total cholestrol(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Lipid profile') , now(), null,  (select id from clinlims.test where description = 'Trigycerides(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Lipid profile') , now(), null,  (select id from clinlims.test where description = 'HDL(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Lipid profile') , now(), null,  (select id from clinlims.test where description = 'LDE(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Lipid profile') , now(), null,  (select id from clinlims.test where description = 'PSA- Total(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'CSF chemistry') , now(), null,  (select id from clinlims.test where description = 'CSF Proteins(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'CSF chemistry') , now(), null,  (select id from clinlims.test where description = 'CSF Glucose(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Body fluids') , now(), null,  (select id from clinlims.test where description = 'Proteins(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Body fluids') , now(), null,  (select id from clinlims.test where description = 'Glucose-Fasting(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Body fluids') , now(), null,  (select id from clinlims.test where description = 'Acid phosphatase(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Body fluids') , now(), null,  (select id from clinlims.test where description = 'Bence jones protein(Urine)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Thyroid function tests') , now(), null,  (select id from clinlims.test where description = 'Triiodothyronine(T3)(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Thyroid function tests') , now(), null,  (select id from clinlims.test where description = 'Thyroid-stimulating Hormone(TSH)(Blood)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Blood Smears') , now(), null,  (select id from clinlims.test where description = 'Falciparum(Smear)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Blood Smears') , now(), null,  (select id from clinlims.test where description = 'Ovale(Smear)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Blood Smears') , now(), null,  (select id from clinlims.test where description = 'Malariae(Smear)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Blood Smears') , now(), null,  (select id from clinlims.test where description = 'Vivax(Smear)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Blood Smears') , now(), null,  (select id from clinlims.test where description = 'Borrelia(Smear)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Blood Smears') , now(), null,  (select id from clinlims.test where description = 'Trypanosomes(Smear)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Genital Smears') , now(), null,  (select id from clinlims.test where description = 'T. vaginalis(Genital Smears)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Genital Smears') , now(), null,  (select id from clinlims.test where description = 'S. haematobium(Genital Smears)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Genital Smears') , now(), null,  (select id from clinlims.test where description = 'Yeast cells(Genital Smears)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Spleen/bone marrow') , now(), null,  (select id from clinlims.test where description = 'L. donovani(Bone Marrow)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Stool') , now(), null,  (select id from clinlims.test where description = 'Taenia spp.(Stool)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Stool') , now(), null,  (select id from clinlims.test where description = 'H. nana(Stool)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Stool') , now(), null,  (select id from clinlims.test where description = 'H. diminuta(Stool)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Stool') , now(), null,  (select id from clinlims.test where description = 'Hookworm(Stool)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Stool') , now(), null,  (select id from clinlims.test where description = 'Roundworms(Stool)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Stool') , now(), null,  (select id from clinlims.test where description = 'S. mansoni(Stool)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Stool') , now(), null,  (select id from clinlims.test where description = 'Trichuris trichiura(Stool)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Stool') , now(), null,  (select id from clinlims.test where description = 'Strongyloides stercoralis(Stool)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Stool') , now(), null,  (select id from clinlims.test where description = 'Isospora belli(Stool)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Stool') , now(), null,  (select id from clinlims.test where description = 'E hystolytica(Stool)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Stool') , now(), null,  (select id from clinlims.test where description = 'Giardia lamblia(Stool)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Stool') , now(), null,  (select id from clinlims.test where description = 'Cryptosporidium(Stool)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Stool') , now(), null,  (select id from clinlims.test where description = 'Cyclospora(Stool)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Skin snips') , now(), null,  (select id from clinlims.test where description = 'Onchocerca volvulus(Skin)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Skin snips') , now(), null,  (select id from clinlims.test where description = 'Leishmania(Skin)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Smears') , now(), null,  (select id from clinlims.test where description = 'Tissue Impression(Smear)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Smears') , now(), null,  (select id from clinlims.test where description = 'Pap Smear(Smear)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Fluid Cytology') , now(), null,  (select id from clinlims.test where description = 'Ascitic fluid(Smear)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Fluid Cytology') , now(), null,  (select id from clinlims.test where description = 'CSF(Smear)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Fluid Cytology') , now(), null,  (select id from clinlims.test where description = 'Pleural fluid(Smear)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Tissue Histology') , now(), null,  (select id from clinlims.test where description = 'Cervix(Tissue)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Tissue Histology') , now(), null,  (select id from clinlims.test where description = 'Prostrate(Tissue)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Tissue Histology') , now(), null,  (select id from clinlims.test where description = 'Breast(Tissue)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Tissue Histology') , now(), null,  (select id from clinlims.test where description = 'Ovarian cyst(Tissue)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Tissue Histology') , now(), null,  (select id from clinlims.test where description = 'Fibroids(Tissue)' and is_active = 'Y' ) ); 
INSERT INTO clinlims.panel_item( id, panel_id, lastupdated , sort_order, test_id)
	VALUES ( nextval( 'clinlims.panel_item_seq' ) , (select id from clinlims.panel where name = 'Tissue Histology') , now(), null,  (select id from clinlims.test where description = 'Lymph nodes(Tissue)' and is_active = 'Y' ) ); 
