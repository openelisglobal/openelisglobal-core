<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<script type="text/javascript" language="JavaScript1.2">
function showDiv( div ){
	hideAllDivs();
	document.getElementById(div).style.display = "inline";
}

function hideAllDivs(){
	document.getElementById('DBS_Id').style.display = "none";
	document.getElementById('Initial_VIH_Id').style.display = "none";
	document.getElementById('Suivi_VIH_Id').style.display = "none";
	document.getElementById('Enfant_VIH_Id').style.display = "none";
	document.getElementById('RTN_Id').style.display = "none";
	document.getElementById('Indeterminate_Id').style.display = "none";
}


</script>

</head>
<body>
<b>Forms</b><br><br>
<input type="radio" name="radio1" value="bob2" checked="checked" onclick="showDiv('RTN_Id');" >RTN<br/>
<input type="radio" name="radio1" value="bob"  onclick="showDiv('DBS_Id');">Enfants DBS<br/>
<input type="radio" name="radio1" value="bob1" onclick="showDiv('Enfant_VIH_Id');" >Enfants VIH (DPV)<br/>
<input type="radio" name="radio1" value="bob3" onclick="showDiv('Initial_VIH_Id');" >Initial VIH<br/>
<input type="radio" name="radio1" value="bob3" onclick="showDiv('Suivi_VIH_Id');" >Suivi VIH<br/>
<input type="radio" name="radio1" value="bob3" onclick="showDiv('Indeterminate_Id');" >Indetermines<br/>
<br/>
<hr>
<br/>


<div id="DBS_Id" style="display:none;">
<table>
<tr>
<td>Code du Site</td>
<td><input type="text" name="text2" size="3" maxlength="3"/></td>
</tr>
<tr>
<td>Nom du Site</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Numéro DBS de l'enfant</td>
<td>DBS-
	<input type="text" name="text2" size="3" maxlength="3"/>-
	<input type="text" name="text2" size="4" maxlength="4"/>
</td>
</tr>
<tr>
<td>Numéro d'identification Site de l'infant</td>
<td><input type="text" name="text2" size="12"/></td>
</tr><tr>
<td>Numéro de Laboratory</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Date de naissance (jj/mm/aaaa)</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Age</td>
<td>Mois <input type="text" name="text2" size="2"/> ou Semaines<input type="text" name="text2" size="2"/></td>
</tr>
<tr>
<td>Sexe</td>
<td><select name="combobox10">
	<option value="b" selected> </option>
	<option value="1">1 Masculin</option>
	<option value="2">2 Féminin</option>
</select>
</tr>
<tr>
<td>Quelle PCR (1 ou 2)</td>
<td><input type="text" name="text2" size="1" maxlength="1"/></td>
</tr>
<tr>
<td>Raison pour une seconde PCR</td>
<td><select name="combobox20">
	<option value="b" selected> </option>
	<option value="1">1 PCR en confirmation de la 1st PCR positive</option>
	<option value="1">2 PCR 6 semaines apès arrêt d'allaitement</option>
	<option value="1">3 PCR après resultat Indéterminé</option>
	<option value="2">7 Non Applicable pour une 1st PCR</option>
</select>
</tr>
<tr>
<td>Nom du demandeur</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Nom du preleveur</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
</table>
</div>
<div id="Initial_VIH_Id" style="display:none;">
<table>
<tr>
<td>Date de prélèvement (jj/mm/aaaa)</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Subjetno</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Numéro de Laboratory</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Sexe</td>
<td><select name="combobox10">
	<option value="b" selected> </option>
	<option value="1">1 Masculin</option>
	<option value="2">2 Féminin</option>
</select>
</tr>
<tr>
<td>Date de naissance (jj/mm/aaaa)</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
</table>
<br>
<b>BIOCHIMIE-HEMATOLOGIE-IMMUNOLOGIE</b>
<table>
<tr>
	<td>Tube sec prélevé</td>
	<td><input type="checkbox" name="checkbox1" value="1"/></td>
</tr>
<tr>
	<td>Sérologie VIH à faire</td>
	<td><input type="checkbox" name="checkbox1" value="1"/></td>
</tr>
<tr>
	<td>Glycémie</td>
	<td><input type="checkbox" name="checkbox1" value="1"/></td>
</tr>
<tr>
	<td>Créatininémie</td>
	<td><input type="checkbox" name="checkbox1" value="1"/></td>
</tr>
<tr>
	<td>Transaminases</td>
	<td><input type="checkbox" name="checkbox1" value="1"/></td>
</tr>
<tr>
	<td>Tube EDTA prélevé</td>
	<td><input type="checkbox" name="checkbox1" value="1"/></td>
</tr>
<tr>
	<td>NFS</td>
	<td><input type="checkbox" name="checkbox1" value="1"/></td>
</tr>
<tr>
	<td>CD4/CD8</td>
	<td><input type="checkbox" name="checkbox1" value="1"/></td>
</tr>
<tr>
	<td>Charge virale plasmatic</td>
	<td><select name="combobox40">
	<option value="b" selected> </option>
	<option value="1">1 Oui</option>
	<option value="2">2 Non</option>
	<option value="7">7 NA</option>
</select></td>
</tr>
</table>

</div>


<div id="Suivi_VIH_Id" style="display:none;">
<table>
<tr>
<td>Date de prélèvement (jj/mm/aaaa)</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Subjetno</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Numéro de Laboratory</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Sexe</td>
<td><select name="combobox10">
	<option value="b" selected> </option>
	<option value="1">1 Masculin</option>
	<option value="2">2 Féminin</option>
</select>
</tr>
<tr>
<td>Date de naissance (jj/mm/aaaa)</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
</table>
<br>
<b>BIOCHIMIE-HEMATOLOGIE-IMMUNOLOGIE</b>
<table>
<tr>
	<td>Tube sec prélevé</td>
	<td><input type="checkbox" name="checkbox1" value="1"/></td>
</tr>
<tr>
	<td>Sérologie VIH à faire</td>
	<td><input type="checkbox" name="checkbox1" value="1"/></td>
</tr>
<tr>
	<td>Glycémie</td>
	<td><input type="checkbox" name="checkbox1" value="1"/></td>
</tr>
<tr>
	<td>Créatininémie</td>
	<td><input type="checkbox" name="checkbox1" value="1"/></td>
</tr>
<tr>
	<td>Transaminases</td>
	<td><input type="checkbox" name="checkbox1" value="1"/></td>
</tr>
<tr>
	<td>Tube EDTA prélevé</td>
	<td><input type="checkbox" name="checkbox1" value="1"/></td>
</tr>
<tr>
	<td>NFS</td>
	<td><input type="checkbox" name="checkbox1" value="1"/></td>
</tr>
<tr>
	<td>CD4/CD8</td>
	<td><input type="checkbox" name="checkbox1" value="1"/></td>
</tr>
<tr>
	<td>Charge virale plasmatic</td>
	<td><select name="combobox40">
	<option value="b" selected> </option>
	<option value="1">1 Oui</option>
	<option value="2">2 Non</option>
	<option value="7">3 NA</option>
</select></td>
</tr>
</table>

</div>



<div id="RTN_Id" style="display:inline;">
<table>
<tr>
<td>Date de prélèvement (jj/mm/aaaa)</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Date de naissance (jj/mm/aaaa)</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Age</td>
<td>Ans<input type="text" name="text2" size="2"/> ou Mois<input type="text" name="text2" size="2"/></td>
</tr>
<tr>
<td>Sexe</td>
<td><select name="combobox10">
	<option value="b" selected> </option>
	<option value="1">1 Masculin</option>
	<option value="2">2 Féminin</option>
</select>
</tr>
<tr>
<td>Sérologie VIH</td>
<td><input type="checkbox" name="checkbox1" value="CD4"/></td>
</tr>
<tr>
<td>Numéro de Laboratory</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
</table>
</div>

<div id="Enfant_VIH_Id" style="display:none;">
<table>
<tr>
<td>Date de prélèvement (jj/mm/aaaa)</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Code du Site</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Nom du Site</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Numéro de l'enfant</td>
<td><input type="text" name="text2" size="4" maxlength="4"/>-
	<input type="text" name="text2" size="1" maxlength="1"/>-
	<input type="text" name="text2" size="4" maxlength="4"/>-
	<input type="text" name="text2" size="2" maxlength="2" /></td>
</tr>
<tr>
<td>Numéro de Laboratory</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Date de naissance (jj/mm/aaaa)</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Age</td>
<td>Mois<input type="text" name="text2" size="2"/> ou Semaines<input type="text" name="text2" size="2"/></td>
</tr>
<tr>
<td>Sexe</td>
<td><select name="combobox2">
	<option value="b" selected> </option>
	<option value="1">1 Masculin</option>
	<option value="2">2 Féminin</option>
</select>
</td>

</tr>
<tr>
<td>Type d'allaitement</td>
<td><select name="combobox1">
	<option value="b" selected> </option>
	<option value="1">1 Allaité(e)</option>
	<option value="2">2 Jamais allaité(e)</option>
	<option value="3">3 Arrét allaitement depuis 2 mois</option>
</select>
</td>
</tr>
<tr>
<td>Catégorie</td>
<td><select name="combobox3">
	<option value="b" selected> </option>
	<option value="1">1 Initial</option>
	<option value="2">2 Confirmation</option>
</select>
</td>
</tr>
<tr>
<td>Résult précédente</td>
<td><select name="combobox4">
	<option value="b" selected> </option>
	<option value="1">1 Negatif</option>
	<option value="2">2 Positif</option>
	<option value="3">3 Indéterminé</option>
	<option value="4">7 NA</option>
</select>
</td>
</tr>
<tr>
</table>
</div>


<div id="Indeterminate_Id" style="display:none;">
<table>
<tr>
<td>Site</td>
<td><input type="text" name="text2" size="3" maxlength="3"/></td>
</tr>
<tr>
<td>BP</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Telephone</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Fax</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>E-mail</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
<tr>
<td>Sexe</td>
<td><select name="combobox10">
	<option value="b" selected> </option>
	<option value="1">Masculin</option>
	<option value="2">Feminin</option>
</select>
</tr>
<tr>
<td>Date de naissance (jj/mm/aaaa)</td>
<td><input type="text" name="text2" size="12"/></td>
</tr>
</table>
</div>

</body>
</html>