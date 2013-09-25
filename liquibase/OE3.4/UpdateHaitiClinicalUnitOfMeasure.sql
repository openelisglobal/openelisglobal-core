update clinlims.test set uom_id = (select id from clinlims.unit_of_measure where name = '10^3/µl') where name = 'Plaquettes' and uom_id is null;
