// HashObj is in utilities.js
var unSelectedReflexs = new HashObj();


function showUserReflexChoices( index, sibIndex )
{
	var resultId = $("resultId_" + index).value;
	var analysisElement = $("analysisId_" + index );
	var analysisId =  analysisElement ? analysisElement.value : "";
	var accessionElement = $("accessionNumberId_" + index);
	var accessionNumber = accessionElement ? accessionElement.value : "";
	var testId = $("testId_" + index ).value;

	var sibResultId = sibIndex ? $("resultId_" + sibIndex).value : null;
	var sibAnalysisId = sibIndex ? $("analysisId_" + sibIndex ).value : null;
	var sibTestId = sibIndex ? $("testId_" + sibIndex ).value : null;

	showHideReflexInstructions( index, false );
	$("userChoicePendingId_" + index).value = false;
	if( $("userChoicePendingId_" + sibIndex) ){
		$("userChoicePendingId_" + sibIndex).value = false;
	}
	clearReflexChoice( index );

	if( sibIndex ){
		showHideReflexInstructions( sibIndex, false );
		clearReflexChoice( sibIndex );
		if(sibResultId == 0){
			return;
		}else{
			resultId += ',' + sibResultId;
			analysisId += ',' + sibAnalysisId;
			testId += ',' + sibTestId;
		}
	}

	getReflexUserChoice( resultId, analysisId, testId, accessionNumber, index, processTestReflexSuccess, processTestReflexFailure);
}

function /*void*/ processTestReflexSuccess(xhr)
{
	//alert( xhr.responseText );
	var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
	var message = xhr.responseXML.getElementsByTagName("message").item(0);


	if (message.firstChild.nodeValue == "valid"){
		var choiceNode = formField.getElementsByTagName("userchoice").item(0);
		var rowIndex = getReflexValueFromXmlElement( choiceNode, "rowIndex" );

		$("selectionOneLabel_" + rowIndex).innerHTML = getReflexValueFromXmlElement( choiceNode, "selectionOneText" );
		$("selectionOne_" + rowIndex).value = getReflexValueFromXmlElement( choiceNode, "selectionOneId" );
		$("selectionTwoLabel_" + rowIndex).innerHTML = getReflexValueFromXmlElement( choiceNode, "selectionTwoText" );
		$("selectionTwo_" + rowIndex).value = getReflexValueFromXmlElement( choiceNode, "selectionTwoId" );
		$("userChoicePendingId_" + rowIndex).value = true;
		
		unSelectedReflexs.setItem( rowIndex, null );
		$("reflexInstruction_" + rowIndex ).className = "alert alert-info";
		$("reflexSelection_" + rowIndex ).className = "alert alert-info";

		showHideReflexInstructions( rowIndex, true );
		$("saveButtonId").disabled = true;
	}

}

function getReflexValueFromXmlElement( parent, tag ){
	var element = parent.getElementsByTagName( tag ).item(0);

	return element ? element.firstChild.nodeValue : "";
}

function /*void*/ processTestReflexFailure(xhr){
	//alert("failed");
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
	$("selectionOne_" + index).checked = false;
	$("selectionTwo_" + index).checked = false;
	unSelectedReflexs.removeItem( index );
	$("saveButtonId").disabled = !unSelectedReflexs.isEmpty();
}

function /*void*/ reflexChoosen(index, rowColor, sibReflexKey ){
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