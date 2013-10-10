if(window.Prototype) {
    delete Object.prototype.toJSON;
    delete Array.prototype.toJSON;
    delete Hash.prototype.toJSON;
    delete String.prototype.toJSON;
}

function showUserReflexChoices( index, resultId, sibIndex )
{
	var analysisElement = $("analysisId_" + index );
	var analysisId =  analysisElement ? analysisElement.value : "";
	var accessionElement = $("accessionNumberId_" + index);
	var accessionNumber = accessionElement ? accessionElement.value : "";
	var testId = $("testId_" + index ).value;

	var sibResultId = sibIndex ? $("resultId_" + sibIndex).value : null;
	var sibAnalysisId = sibIndex ? $("analysisId_" + sibIndex ).value : null;
	var sibTestId = sibIndex ? $("testId_" + sibIndex ).value : null;


//	$("userChoicePendingId_" + index).value = false;
/*	if( $("userChoicePendingId_" + sibIndex) ){
		$("userChoicePendingId_" + sibIndex).value = false;
	}
	clearReflexChoice( index );

	if( sibIndex ){
		clearReflexChoice( sibIndex );
		if(sibResultId == 0){
			return;
		}else{
			resultId += ',' + sibResultId;
			analysisId += ',' + sibAnalysisId;
			testId += ',' + sibTestId;
		}
	}
 */
	getReflexUserChoice( resultId, analysisId, testId, accessionNumber, index, processTestReflexSuccess);
}

function /*void*/ processTestReflexSuccess(xhr)
{
	//alert( xhr.responseText );
	var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
	var message = xhr.responseXML.getElementsByTagName("message").item(0);

	if (message.firstChild.nodeValue == "valid"){
        buildPopUp(formField.firstChild.textContent);
	}
}

function buildPopUp(rawResponse){
    var response = JSON.parse( rawResponse);
    var rowIndex = response["rowIndex"];
    var selections = response["selections"];
    var i, selected;

    $jq(".modal-body #testRow").val(rowIndex);
    $jq(".modal-body #targetIds").val(response["triggerIds"]);
    $jq(".modal-body #serverResponse").val(rawResponse.replace(/\"/g, "'"));
    $jq(".selection_element").remove();
    $jq("#modal_ok").attr('disabled','disabled');
    for( i = 0; i < selections.length; i++){
        selected = jQuery.inArray(selections[i]["value"], response["selected"]) != -1;
        $jq(".modal-body").append(getSelectionRow(selections[i]["name"], selections[i]["value"], i, selected));
    }
    $jq(".modal-body #selectAll").prop('checked', false);
    $jq(".selection_element").change( function(){ checkForCheckedReflexes(); });
    $jq("#headerLabel").text(response["triggers"]);

    showReflexSelection();
}
function getSelectionRow(name, value, index, selected ){
    var check = selected ? "checked='checked' " : "";
    return "<p class='selection_element'><input style='vertical-align:text-bottom' id='selection_" +
        index + "' class='selectionCheckbox' value='" +
        value +  "' type='checkbox' " +
        check + ">&nbsp;&nbsp;&nbsp;" +
        name + "</p>";
}

function modalSelectAll(selectBox){
    if( $jq(selectBox).prop('checked')){
        $jq(selectBox).click(function(){ $jq('.selectionCheckbox').prop('checked', true); });
        $jq("#modal_ok").removeAttr('disabled');
    } else{
        $jq(selectBox).click(function(){ $jq('.selectionCheckbox').prop('checked', false); });
        $jq("#modal_ok").attr('disabled','disabled');
    }
}

function checkForCheckedReflexes(){
    var disable = true;
    $jq(".selectionCheckbox").each(function(index, value){
           if( value.checked){
               disable = false;
           }
    });

    if( disable){
        $jq("#modal_ok").attr('disabled','disabled');
    }else{
        $jq("#modal_ok").removeAttr('disabled');
    }
}

function addReflexToTests( editLabel ){
    var index = $jq(".modal-body #testRow").val();
    var tests = '';
    var parentRow = $jq('#noteRow_' + index);
    var rawResponse = $jq(".modal-body #serverResponse").val().replace(/'/g, "\"");

    var response = JSON.parse(rawResponse);

    var selectedReflexes = [];//

    $jq(".selectionCheckbox").each(function(index, value){
        if(value.checked){
            tests += $jq.trim($jq(value).parent().text()) + ", ";
            selectedReflexes.push(value.value);
        }
    });

    response["selected"] = selectedReflexes;

    rawResponse = JSON.stringify(response).replace(/\"/g, "'");
    tests = tests.substr(0, tests.length - 2 );

    parentRow.after(getSelectedTestDisplay(parentRow.attr("class"), index, $jq(".modal-body #targetIds" ).val(), $jq("#headerLabel").text().split(":")[1], tests, editLabel, rawResponse));
//    $jq("#reflexServerResultId_" + index).val($jq(".modal-body #serverResponse").val());
//    $jq("#reflexedTests_" + index).text(tests);
 //   $jq("#reflexTestsParent_" + index).text($jq("#headerLabel").text().split(":")[1])
}


function getSelectedTestDisplay( classValue, index, targetIds, parent, tests, editLabel, response){
     return "<tr id='reflexSelection_" + index + "_" + targetIds + "' class='" + classValue + " reflexSelection_" + index + "'  >" +
        "<td colspan='5' style='text-align:right'>" + parent + "<input type='hidden' class='rawResponse' value=\"" + response + "\" /></td>" +
        "<td colspan='3'><textarea  readonly='true' id='reflexedTests' rows='2' style='width:98%' >" + tests + "</textarea></td>" +
        "<td colspan='1' style='text-align: left'><input type='button' value='" + editLabel + "' onclick=\"editReflexes('reflexSelection_" + index + "_" + targetIds  + "');\"></td>"
    "</tr>";
}

function removeReflexesFor( triggers, row){
    $jq("#reflexSelection_" + row + "_" + triggers).remove();
}

function editReflexes(rowId){
    var rawResponse = $jq("#" + rowId + " .rawResponse").val();
    buildPopUp(rawResponse.replace(/'/g, "\"").replace(/\'/g, "\""));
}

function showReflexSelection( element ){
    $jq('#reflexSelect').modal('show');
}