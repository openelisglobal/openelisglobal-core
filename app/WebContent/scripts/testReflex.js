// HashObj is in utilities.js
var unSelectedReflexs = new HashObj();


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

//	showHideReflexInstructions( index, false );
//	$("userChoicePendingId_" + index).value = false;
/*	if( $("userChoicePendingId_" + sibIndex) ){
		$("userChoicePendingId_" + sibIndex).value = false;
	}
	clearReflexChoice( index );

	if( sibIndex ){
//		showHideReflexInstructions( sibIndex, false );
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
    var response = JSON.parse( formField.firstChild.textContent);

	if (message.firstChild.nodeValue == "valid"){
		var rowIndex = response["rowIndex"];
        var selections = response["selections"];
        var i;

        $jq(".modal-body #testRow").val(rowIndex);
        $jq(".modal-body #targetIds").val(response["triggerIds"]);
        $jq(".selection_element").remove();
        $jq("#modal_ok").attr('disabled','disabled');
        for( i = 0; i < selections.length; i++){
           $jq(".modal-body").append(getSelectionRow(selections[i]["name"], selections[i]["value"], i));
        }
        $jq(".modal-body #selectAll").prop('checked', false);
        $jq(".selection_element").change( function(){ checkForCheckedReflexes(); });
        $jq("#headerLabel").text(response["triggers"]);

        showReflexSelection();
//		$("saveButtonId").disabled = true;
	}

}

function getSelectionRow(name, value, index){
    return "<p class='selection_element'><input style='vertical-align:text-bottom' id='selection_" + index + "' class='selectionCheckbox' value='" + value +  "' type='checkbox' >&nbsp;&nbsp;&nbsp;" + name + "</p>";
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

function addReflexToTests(){
    var index = $jq(".modal-body #testRow").val();
    var tests = '';
    var parentRow = $jq('#reflexInstruction_' + index);



    $jq(".selectionCheckbox").each(function(index, value){
        if(value.checked){
            tests += $jq.trim($jq(value).parent().text()) + ", ";
        }
    });
    tests = tests.substr(0, tests.length - 2 );

    parentRow.after(getSelectedTestDisplay(parentRow.attr("class"), index, $jq(".modal-body #targetIds" ).val(), $jq("#headerLabel").text().split(":")[1], tests));
//    $jq("#reflexedTests_" + index).text(tests);
 //   $jq("#reflexTestsParent_" + index).text($jq("#headerLabel").text().split(":")[1])
  //  $jq("#reflexSelection_" + index + ",#reflexInstruction_" + index).show();
}


function /*void*/ showHideReflexInstructions( rowIndex, show ){
	if( show ){
		$("reflexInstruction_" + rowIndex ).show();
		$("reflexSelection_" + rowIndex ).show();
	}else{
		$("reflexInstruction_" + rowIndex ).hide();
		$("reflexSelection_" + rowIndex ).hide();
	}
}

function /*void*/ clearReflexChoice( index ){

}

function reflexChoosen(index, rowColor, sibReflexKey ){
	$("reflexInstruction_" + index ).className=rowColor;
	$("reflexSelection_" + index ).className=rowColor;

	$("userChoicePendingId_" + index).value = false;
	
	unSelectedReflexs.removeItem( index );
	if( sibReflexKey ){
		var siblingElement = $(sibReflexKey);
		if( siblingElement ){
			var sibIndex = siblingElement.value;
			$("selectionOne_" + sibIndex).checked = $("selectionOne_" + index).checked;
			$("selectionTwo_" + sibIndex).checked = $("selectionTwo_" + index).checked;
		}
	}

	$("saveButtonId").disabled = !unSelectedReflexs.isEmpty();
}

function getSelectedTestDisplay( classValue, index, targetIds, parent, tests){
    return "<tr id='reflexSelection_'" + index + "_" + targetIds + " class='" + classValue + " reflexSelection_" + index + "'  >" +
        "<td colspan='4' style='text-align:right'>" + parent + "</td>" +
        "<td colspan='5'><textarea  readonly='true' id='reflexedTests' rows='2' style='width:80%' onclick='alert('gotcha');'>" + tests + "</textarea></td>" +
    "</tr>";
}
function showReflexSelection( element ){
    $jq('#reflexSelect').modal('show');
}