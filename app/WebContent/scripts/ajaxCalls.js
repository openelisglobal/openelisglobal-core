function getLabOrder( orderNumber, success, failure){
	if( !failure ){	failure = defaultFailure;}
	
	new Ajax.Request('ajaxQueryXML',
			{
				method : 'get', 
				parameters : "provider=LabOrderSearchProvider&orderNumber=" + orderNumber ,
			    //indicator: 'throbbing',
				onSuccess : success,
				onFailure : failure
			});

}

function getDistrictsForRegion( regionId, selectedValue, success, failure){
	if( !failure ){	failure = defaultFailure;}
	
	new Ajax.Request('ajaxQueryXML',
			{
				method : 'get', 
				parameters : "provider=HealthDistrictForRegionProvider&regionId=" + regionId +"&selectedValue=" + selectedValue,
			    //indicator: 'throbbing',
				onSuccess : success,
				onFailure : failure
			});

}

function getCodeForOrganization( organizationId, success, failure){
	if( !failure ){	failure = defaultFailure;}
	
	new Ajax.Request('ajaxQueryXML',
			{
				method : 'get', 
				parameters : "provider=CodeForOrganizationProvider&organizationId=" + organizationId,
			    //indicator: 'throbbing',
				onSuccess : success,
				onFailure : failure
			});

}


function getTestsForSampleType(sampleTypeId, labOrderTypeId, success, failure) {
	var request = "&sampleType=" + sampleTypeId + "&labOrderType=" + labOrderTypeId;
	if( !failure ){	failure = defaultFailure;}
	
	new Ajax.Request('ajaxQueryXML', // url
	{// options
		method : 'get', // http method
		parameters : "provider=SampleEntryTestsForTypeProvider" + request,
		// indicator: 'throbbing'
		onSuccess : success,
		onFailure : failure
	});
}

function testConnectionOnServer(connectionId, url, success, failure) {
	var request = "&connectionId=" + connectionId + "&url=" + url;
	
	if( !failure ){	failure = defaultFailure;}
	
	new Ajax.Request('ajaxQueryXML', // url
	{// options
		method : 'get', // http method
		parameters : "provider=ConnectionTestProvider" + request,
		// indicator: 'throbbing'
		onSuccess : success,
		onFailure : failure
	});
}

function validateAccessionNumberOnServer(checkformatAndUsed, fieldId, accessionNumber, success, failure) {
	if( !failure ){	failure = defaultFailure;}
	new Ajax.Request(
			'ajaxXML', // url
			{// options
				method : 'get', // http method
				parameters : 'provider=SampleEntryAccessionNumberValidationProvider&checkFormatAndUsed=' + checkformatAndUsed + '&field='	+ fieldId + '&accessionNumber=' + accessionNumber,
				indicator : 'throbbing',
				onSuccess : success,
				onFailure : failure
			});
}

function validateNonConformityRecordNumberOnServer( field, success, failure){
	if( !failure){failure = defaultFailure;	}

	new Ajax.Request('ajaxXML',
			{
				method : 'get', 
				parameters : "provider=NonConformityRecordNumberValidationProvider&fieldId=" + field.id +"&value=" + field.value,
			    //indicator: 'throbbing',
				onSuccess : success,
				onFailure : failure
			});

}

function validatePhoneNumberOnServer( field, success, failure){
    if( !failure){failure = defaultFailure;	}

    new Ajax.Request('ajaxXML',
        {
            method : 'get',
            parameters : "provider=PhoneNumberValidationProvider&fieldId=" + field.id +"&value=" + field.value,
            //indicator: 'throbbing',
            onSuccess : success,
            onFailure : failure
        });

}

function validateSubjectNumberOnServer( subjectNumber, type, elementId, success, failure){
    if( !failure ){	failure = defaultFailure;}

    new Ajax.Request('ajaxXML',
        {
            method : 'get',
            parameters : "provider=SubjectNumberValidationProvider&subjectNumber=" + subjectNumber + "&numberType=" + type + "&fieldId=" + elementId,
            //indicator: 'throbbing',
            onSuccess : success,
            onFailure : failure
        });

}
function patientSearch(lastName, firstName, STNumber, subjectNumber, nationalId, labNumber, guid, suppressExternalSearch, success, failure){
	if( !failure){failure = defaultFailure;	}
	
	new Ajax.Request (
            'ajaxQueryXML',  //url
             {//options
               method: 'get', //http method
               parameters: "provider=PatientSearchProvider&lastName=" + lastName +
               			  "&firstName=" + firstName +
               			  "&STNumber=" + STNumber +
               			  "&subjectNumber=" + subjectNumber +
               			  "&nationalID=" + nationalId +
               			  "&labNumber=" + labNumber +
               			  "&guid=" + guid +
               			  "&suppressExternalSearch=" + suppressExternalSearch,
               onSuccess:  success,
               onFailure:  failure
              }
           );	
}

function getReflexUserChoice( resultId, analysisId, testId, accessionNumber, index, success, failure){
	if( !failure){failure = defaultFailure;	}
	
	new Ajax.Request (
            'ajaxQueryXML',  //url
            {//options
            method: 'get', //http method
            parameters: 'provider=TestReflexUserChoiceProvider&resultIds=' + resultId + 
            			'&analysisIds=' +  analysisId + 
            			'&testIds=' + testId + 
            			'&rowIndex=' + index +
            			'&accessionNumber=' + accessionNumber,
            indicator: 'throbbing',
            onSuccess:  success,
            onFailure:  failure
                 }
                );
	
}

function defaultFailure(xhr){
	//alert(xhr.responseText);
}
