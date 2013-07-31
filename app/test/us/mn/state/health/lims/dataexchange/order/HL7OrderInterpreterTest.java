/**
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/ 
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
 *
 */
package us.mn.state.health.lims.dataexchange.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import us.mn.state.health.lims.common.services.ITestIdentityService;
import us.mn.state.health.lims.dataexchange.order.action.HL7OrderInterpreter;
import us.mn.state.health.lims.dataexchange.order.action.IOrderInterpreter;
import us.mn.state.health.lims.dataexchange.order.action.IOrderInterpreter.InterpreterResults;
import us.mn.state.health.lims.dataexchange.order.action.IOrderInterpreter.OrderType;
import us.mn.state.health.lims.dataexchange.order.action.MessagePatient;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v251.message.OML_O21;
import ca.uhn.hl7v2.model.v251.segment.OBR;
import ca.uhn.hl7v2.model.v251.segment.OBX;
import ca.uhn.hl7v2.model.v251.segment.ORC;
import ca.uhn.hl7v2.model.v251.segment.PID;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;

public class HL7OrderInterpreterTest{
	
	private static final String incomingMSH = "MSH|^~\\&|ISANTE|JACMEL|OPENELIS|JACMEL|20130415173021||OML^O21^OML_O21|Q123456789T123456789X123456|P|2.5.1";
	private static final String incomingPID = "PID|1||1234^^^^ST~1234-12^^^^GU~1234-123^^^^NA~1234-124^^^^OB~1234-125^^^^PC||BROS^MARIO||19850102000000|F|||123 FAKE STREET^MARIO \\T\\ LUIGI BROS PLACE^TOADSTOOL KINGDOM^NES^A1B2C3^JP^HOME^^1234||(555)555-0123^HOME^JP:1234567|||S|";
	private static final String incomingRequestORC = "ORC|NW|12345-4321876|||||||20130415173021|||^Smith^John";
	private static final String incomingCancelORC = "ORC|CA|12345-4321876|||||||20130415173021|||^Smith^John";
	private static final String incomingOBR = "OBR|1|12345-4321876||P-Biochimie Reflotron^Biochimie Reflotron^11LAB";
	private static final String incomingOBX = "OBX|1|ST|SPEC_TYPE^Specimen Type^11LAB||Plasma||||||F";
	private OML_O21 message;
	
	private IOrderInterpreter interpreter = new HL7OrderInterpreter();
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
	}

	@Before
	public void setUp() throws Exception{
		ModelClassFactory mcf = new DefaultModelClassFactory();
		
		message = new OML_O21(mcf);
		message.parse(incomingMSH);
	
		ITestIdentityService mockTestIdentityService = mock( ITestIdentityService.class);
		when(mockTestIdentityService.doesPanelExist(anyString())).thenReturn( true );
		when(mockTestIdentityService.doesTestExist(anyString())).thenReturn( true );
		((HL7OrderInterpreter)interpreter).setTestIdentityService(mockTestIdentityService);
	}

	private void populateValidMessage(boolean request) throws HL7Exception{
		createValidPID();
		createValidORC(request);
		createValidOBR();
		createValidOBX();
	}

	private void createValidOBR() throws HL7Exception{
		OBR obr = message.getORDER().getOBSERVATION_REQUEST().getOBR();
		obr.parse(incomingOBR);
	}

	private void createValidOBX() throws HL7Exception{
		OBX obx = message.getORDER().getOBSERVATION_REQUEST().getOBSERVATION().getOBX();
		obx.parse(incomingOBX);
	}

	private void createValidORC(boolean request) throws HL7Exception{
		ORC orc = message.getORDER().getORC();
		if(request){
			orc.parse(incomingRequestORC);
		}else{
			orc.parse(incomingCancelORC);
		}
	}

	private void createValidPID() throws HL7Exception{
		PID pid = message.getPATIENT().getPID(); 
		pid.parse(incomingPID);
	}
	
	@Test
	public void testGettersWithoutInterpreting(){
		assertNull(interpreter.getReferringOrderNumber());
		assertNull(interpreter.getMessage());
		assertTrue(interpreter.getResultStatus().isEmpty());
		assertNull(interpreter.getMessagePatient() );
		assertNull(interpreter.getOrderType());
		assertTrue(interpreter.getUnsupportedPanels().isEmpty());
		assertTrue(interpreter.getUnsupportedTests().isEmpty());
	}

	@Test
	public void testGetReferringOrderNumber() throws HL7Exception{
		populateValidMessage(true);
		interpreter.interpret(message);
		assertEquals("12345-4321876", interpreter.getReferringOrderNumber());
	}

	@Test
	public void testGetMessage() throws HL7Exception{
		populateValidMessage(true);
		interpreter.interpret(message);
		assertTrue( interpreter.getMessage().startsWith(incomingMSH));
	}

	@Test
	public void testGetResultPatient() throws HL7Exception{
		populateValidMessage(true);
		interpreter.interpret(message);
		MessagePatient patient = interpreter.getMessagePatient();
		assertNotNull( patient);
		assertEquals("1234-12", patient.getGuid());
		assertEquals("1234",  patient.getStNumber());
		assertEquals("1234-124", patient.getObNumber());
		assertEquals("1234-123",  patient.getNationalId());
		assertEquals("1234-125", patient.getPcNumber());
		assertEquals("F",  patient.getGender());
		assertEquals( "02/01/1985", patient.getDisplayDOB());
		assertEquals("MARIO", patient.getFirstName());
		assertEquals("BROS", patient.getLastName());
		assertEquals(null, patient.getMothersFirstName());
		assertEquals("123 FAKE STREET", patient.getAddressStreet());
		assertEquals("TOADSTOOL KINGDOM", patient.getAddressVillage());
		assertEquals("NES", patient.getAddressDepartment());
	}
	
	@Test
	public void testAmbigiousDOB() throws HL7Exception{

		PID pid = message.getPATIENT().getPID(); 
		pid.parse("PID|1||1234^^^^ST~1234-12^^^^GU~1234-123^^^^NA~1234-124^^^^OB~1234-125^^^^PC||BROS^MARIO||1985|F|||123 FAKE STREET^MARIO \\T\\ LUIGI BROS PLACE^TOADSTOOL KINGDOM^NES^A1B2C3^JP^HOME^^1234||(555)555-0123^HOME^JP:1234567|||S|");
		createValidORC(true);
		createValidOBR();
		createValidOBX();
		interpreter.interpret(message);
		MessagePatient patient = interpreter.getMessagePatient();
		assertNotNull( patient);
		assertEquals( "XX/XX/1985", patient.getDisplayDOB());
		}
	@Test
	public void testRequestOrder() throws HL7Exception{
		populateValidMessage(true);
		interpreter.interpret(message);
		assertEquals(OrderType.REQUEST, interpreter.getOrderType());
	}
	
	@Test
	public void testCancelOrder() throws HL7Exception{
		populateValidMessage(false);
		interpreter.interpret(message);
		assertEquals(OrderType.CANCEL, interpreter.getOrderType());
	}
	
	@Test 
	public void testOKRequestOrder() throws HL7Exception{
		populateValidMessage(true);
		interpreter.interpret(message);
		assertEquals(InterpreterResults.OK, interpreter.getResultStatus().get(0));
	}
	
	@Test 
	public void testOKCancelOrder() throws HL7Exception{
		populateValidMessage(false);
		interpreter.interpret(message);
		assertEquals(InterpreterResults.OK, interpreter.getResultStatus().get(0));
	}

	@Test 
	public void testUnknownRequest() throws HL7Exception{
		createValidPID();
		ORC orc = message.getORDER().getORC();
		orc.parse("ORC|WW|12345-4321876|||||||20130415173021|||^Smith^John");
		interpreter.interpret(message);
		assertEquals(InterpreterResults.UNKNOWN_REQUEST_TYPE, interpreter.getResultStatus().get(0));
	}
	
	@Test 
	public void testMIssingOrderNumber() throws HL7Exception{
		createValidPID();
		ORC orc = message.getORDER().getORC();
		orc.parse("ORC|CA||");
		interpreter.interpret(message);
		assertEquals(InterpreterResults.MISSING_ORDER_NUMBER, interpreter.getResultStatus().get(0));
	}
	
	@Test 
	public void testMIssingPatientGuid() throws HL7Exception{
		PID pid = message.getPATIENT().getPID(); 
		pid.parse("PID|1||1234^^^^ST~1234-123^^^^NA~1234-124^^^^OB~1234-125^^^^PC||BROS^MARIO||19850101000000|F|||123 FAKE STREET^MARIO \\T\\ LUIGI BROS PLACE^TOADSTOOL KINGDOM^NES^A1B2C3^JP^HOME^^1234||(555)555-0123^HOME^JP:1234567|||S|");
		createValidORC(true);
		interpreter.interpret(message);
		assertEquals(InterpreterResults.MISSING_PATIENT_GUID, interpreter.getResultStatus().get(0));
	}

	@Test 
	public void testMissingPatientDOB() throws HL7Exception{
		PID pid = message.getPATIENT().getPID(); 
		pid.parse("PID|1||1234^^^^ST~1234-12^^^^GU~1234-123^^^^NA~1234-124^^^^OB~1234-125^^^^PC||BROS^MARIO|||F|||123 FAKE STREET^MARIO \\T\\ LUIGI BROS PLACE^TOADSTOOL KINGDOM^NES^A1B2C3^JP^HOME^^1234||(555)555-0123^HOME^JP:1234567|||S|");
		createValidORC(true);
		interpreter.interpret(message);
		assertEquals(InterpreterResults.MISSING_PATIENT_DOB, interpreter.getResultStatus().get(0));
	}
	
	@Test 
	public void testMissingPatientGender() throws HL7Exception{
		PID pid = message.getPATIENT().getPID(); 
		pid.parse("PID|1||1234^^^^ST~1234-12^^^^GU~1234-123^^^^NA~1234-124^^^^OB~1234-125^^^^PC||BROS^MARIO||19850101000000||||123 FAKE STREET^MARIO \\T\\ LUIGI BROS PLACE^TOADSTOOL KINGDOM^NES^A1B2C3^JP^HOME^^1234||(555)555-0123^HOME^JP:1234567|||S|");
		createValidORC(true);
		interpreter.interpret(message);
		assertEquals(InterpreterResults.MISSING_PATIENT_GENDER, interpreter.getResultStatus().get(0));
	}	
	
	@Test 
	public void testUnknownPatientGender() throws HL7Exception{
		PID pid = message.getPATIENT().getPID(); 
		pid.parse("PID|1||1234^^^^ST~1234-12^^^^GU~1234-123^^^^NA~1234-124^^^^OB~1234-125^^^^PC||BROS^MARIO||19850101000000|O|||123 FAKE STREET^MARIO \\T\\ LUIGI BROS PLACE^TOADSTOOL KINGDOM^NES^A1B2C3^JP^HOME^^1234||(555)555-0123^HOME^JP:1234567|||S|");
		createValidORC(true);
		interpreter.interpret(message);
		assertEquals(InterpreterResults.MISSING_PATIENT_GENDER, interpreter.getResultStatus().get(0));
	}	
	
	@Test 
	public void testMissingPatientIdentifier() throws HL7Exception{
		PID pid = message.getPATIENT().getPID(); 
		pid.parse("PID|1||1234-12^^^^GU||BROS^MARIO||19850101000000|F|||123 FAKE STREET^MARIO \\T\\ LUIGI BROS PLACE^TOADSTOOL KINGDOM^NES^A1B2C3^JP^HOME^^1234||(555)555-0123^HOME^JP:1234567|||S|");
		createValidORC(true);
		interpreter.interpret(message);
		assertEquals(InterpreterResults.MISSING_PATIENT_IDENTIFIER, interpreter.getResultStatus().get(0));
	}	

	@Test 
	public void testMissingTests() throws HL7Exception{
		createValidPID();
		createValidORC(true);
		interpreter.interpret(message);
		assertEquals(InterpreterResults.MISSING_TESTS, interpreter.getResultStatus().get(0));
	}	

	@Test 
	public void testUnsupportedTests() throws HL7Exception{
		createValidPID();
		createValidORC(true);
		OBR obr = message.getORDER().getOBSERVATION_REQUEST().getOBR();
		obr.parse("OBR|1|12345-4321876||T-Biochimie Reflotron^Biochimie Reflotron^11LAB");
		
		ITestIdentityService mockTestIdentityService = mock( ITestIdentityService.class);
		when(mockTestIdentityService.doesPanelExist(anyString())).thenReturn( true );
		when(mockTestIdentityService.doesTestExist(anyString())).thenReturn( false );
		((HL7OrderInterpreter)interpreter).setTestIdentityService(mockTestIdentityService);
		
		interpreter.interpret(message);
		assertEquals(InterpreterResults.UNSUPPORTED_TESTS, interpreter.getResultStatus().get(0));
	}	

	@Test 
	public void testUnsupportedPanels() throws HL7Exception{
		populateValidMessage(true);
		
		ITestIdentityService mockTestIdentityService = mock( ITestIdentityService.class);
		when(mockTestIdentityService.doesPanelExist(anyString())).thenReturn( false );
		when(mockTestIdentityService.doesTestExist(anyString())).thenReturn( true );
		((HL7OrderInterpreter)interpreter).setTestIdentityService(mockTestIdentityService);
		
		interpreter.interpret(message);
		assertEquals(InterpreterResults.UNSUPPORTED_PANELS, interpreter.getResultStatus().get(0));
	}	

	@Test 
	public void testUnknowRequestIdentifier() throws HL7Exception{
		createValidPID();
		createValidORC(true);
		OBR obr = message.getORDER().getOBSERVATION_REQUEST().getOBR();
		obr.parse("OBR|1|12345-4321876||W^Biochimie Reflotron^ITECH");
				
		interpreter.interpret(message);
		assertEquals(InterpreterResults.OTHER_THAN_PANEL_OR_TEST_REQUESTED, interpreter.getResultStatus().get(0));
	}	

	@Test 
	public void testUnSupportedPanelAndTest() throws HL7Exception{
		populateValidMessage(true);
		
		ORC orc = message.getORDER().getOBSERVATION_REQUEST().getPRIOR_RESULT().getORDER_PRIOR().getORC();
		orc.parse(incomingRequestORC);
		OBR obr = message.getORDER().getOBSERVATION_REQUEST().getPRIOR_RESULT().getORDER_PRIOR().getOBR();
		obr.parse("OBR|1|12345-4321876||T-Biochimie Reflotron Test^Biochimie Reflotron Test^ITECH");
		
		ITestIdentityService mockTestIdentityService = mock( ITestIdentityService.class);
		when(mockTestIdentityService.doesPanelExist(anyString())).thenReturn( false );
		when(mockTestIdentityService.doesTestExist(anyString())).thenReturn( false );
		((HL7OrderInterpreter)interpreter).setTestIdentityService(mockTestIdentityService);

		
		interpreter.interpret(message);
		assertEquals(2, interpreter.getResultStatus().size());
		
		assertEquals("Biochimie Reflotron Test", interpreter.getUnsupportedTests().get(0));
		assertEquals("Biochimie Reflotron", interpreter.getUnsupportedPanels().get(0));
	}	

	@Test 
	public void testTwoUnsupportedPanels() throws HL7Exception{
		//The purpose of this is to make sure that UNSUPPORTED_* is only set once even if there is more than one
		//unknown panel or test
		populateValidMessage(true);
		
		ORC orc = message.getORDER().getOBSERVATION_REQUEST().getPRIOR_RESULT().getORDER_PRIOR().getORC();
		orc.parse(incomingRequestORC);
		OBR obr = message.getORDER().getOBSERVATION_REQUEST().getPRIOR_RESULT().getORDER_PRIOR().getOBR();
		obr.parse("OBR|1|12345-4321876||P-Biochimie Reflotron Test^Biochimie Reflotron Test^ITECH");
		
		ITestIdentityService mockTestIdentityService = mock( ITestIdentityService.class);
		when(mockTestIdentityService.doesPanelExist(anyString())).thenReturn( false );
		when(mockTestIdentityService.doesTestExist(anyString())).thenReturn( false );
		((HL7OrderInterpreter)interpreter).setTestIdentityService(mockTestIdentityService);

		
		interpreter.interpret(message);
		assertEquals(1, interpreter.getResultStatus().size());
		assertEquals(InterpreterResults.UNSUPPORTED_PANELS, interpreter.getResultStatus().get(0));
		
		assertEquals("Biochimie Reflotron", interpreter.getUnsupportedPanels().get(0));
		assertEquals("Biochimie Reflotron Test", interpreter.getUnsupportedPanels().get(1));
	}	

}
