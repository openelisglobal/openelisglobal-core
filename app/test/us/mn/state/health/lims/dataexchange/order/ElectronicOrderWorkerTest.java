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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.ExternalOrderStatus;
import us.mn.state.health.lims.dataexchange.order.action.IOrderExistanceChecker;
import us.mn.state.health.lims.dataexchange.order.action.IOrderExistanceChecker.CheckResult;
import us.mn.state.health.lims.dataexchange.order.action.IOrderInterpreter;
import us.mn.state.health.lims.dataexchange.order.action.IOrderInterpreter.InterpreterResults;
import us.mn.state.health.lims.dataexchange.order.action.IOrderInterpreter.OrderType;
import us.mn.state.health.lims.dataexchange.order.action.IOrderPersister;
import us.mn.state.health.lims.dataexchange.order.action.MessagePatient;
import us.mn.state.health.lims.dataexchange.order.action.OrderWorker;
import us.mn.state.health.lims.dataexchange.order.action.OrderWorker.OrderResult;

public class ElectronicOrderWorkerTest{
	private static final String ORDER_NUMBER = "orderNumber";
	private OrderWorker orderWorker;
	private IOrderInterpreter mockOrderInterpreter;
	private IOrderExistanceChecker mockOrderExistanceChecker;
	private IOrderPersister mockOrderPersister;
	private StatusService mockStatusService;

	@Before
	public void setUp() throws Exception{
		mockOrderInterpreter = mock( IOrderInterpreter.class);
		mockOrderExistanceChecker = mock( IOrderExistanceChecker.class);
		mockOrderPersister = mock( IOrderPersister.class);
		mockStatusService = mock( StatusService.class);
		orderWorker = new OrderWorker(null);
		orderWorker.setInterpreter(mockOrderInterpreter);
		orderWorker.setExistanceChecker(mockOrderExistanceChecker);
		orderWorker.setPersister(mockOrderPersister);
		orderWorker.setStatusService(mockStatusService);
		
	}

	@Test
	public void testRequestOK(){
		setupForStatusTests(OrderType.REQUEST, CheckResult.NOT_FOUND);
		assertEquals(OrderResult.OK, orderWorker.handleOrderRequest());
	}
	
	@Test
	public void testRequestOKPreviousCanceled(){
		setupForStatusTests(OrderType.REQUEST, CheckResult.ORDER_FOUND_CANCELED);
		assertEquals(OrderResult.OK, orderWorker.handleOrderRequest());
	}

	@Test
	public void testDuplicateOrderQueued(){
		setupForStatusTests(OrderType.REQUEST, CheckResult.ORDER_FOUND_QUEUED);
		assertEquals(OrderResult.DUPLICATE_ORDER, orderWorker.handleOrderRequest());
	}
	
	@Test
	public void testDuplicateOrderInProgress(){
		setupForStatusTests(OrderType.REQUEST, CheckResult.ORDER_FOUND_INPROGRESS);
		assertEquals(OrderResult.DUPLICATE_ORDER, orderWorker.handleOrderRequest());
	}
	
	@Test
	public void testCancelOKQueued(){
		setupForStatusTests(OrderType.CANCEL, CheckResult.ORDER_FOUND_QUEUED);
		assertEquals(OrderResult.OK, orderWorker.handleOrderRequest());
	}
	
	@Test
	public void testNotCancelableBecauseNotFound(){
		setupForStatusTests(OrderType.CANCEL, CheckResult.NOT_FOUND);
		assertEquals(OrderResult.NON_CANCELABLE_ORDER, orderWorker.handleOrderRequest());
	}

	@Test
	public void testNotCancelableBecauseAlreadyCanceled(){
		setupForStatusTests(OrderType.CANCEL, CheckResult.ORDER_FOUND_CANCELED);
		assertEquals(OrderResult.NON_CANCELABLE_ORDER, orderWorker.handleOrderRequest());
	}
	
	@Test
	public void testNotCancelableBecauseInProgress(){
		setupForStatusTests(OrderType.CANCEL, CheckResult.ORDER_FOUND_INPROGRESS);
		assertEquals(OrderResult.NON_CANCELABLE_ORDER, orderWorker.handleOrderRequest());
	}
	
	@Test
	public void testUnknownRequest(){
		setupForInterpreterErrorTests(InterpreterResults.UNKNOWN_REQUEST_TYPE);
		assertEquals(OrderResult.MESSAGE_ERROR, orderWorker.handleOrderRequest());
	}
	
	@Test
	public void testMissingDOB(){
		setupForInterpreterErrorTests(InterpreterResults.MISSING_PATIENT_DOB);
		assertEquals(OrderResult.MESSAGE_ERROR, orderWorker.handleOrderRequest());
	}

	@Test
	public void testMissingGender(){
		setupForInterpreterErrorTests(InterpreterResults.MISSING_PATIENT_GENDER);
		assertEquals(OrderResult.MESSAGE_ERROR, orderWorker.handleOrderRequest());
	}	
	
	@Test
	public void testMissingGuid(){
		setupForInterpreterErrorTests(InterpreterResults.MISSING_PATIENT_GUID);
		assertEquals(OrderResult.MESSAGE_ERROR, orderWorker.handleOrderRequest());
	}
	
	@Test
	public void testMissingPatientID(){
		setupForInterpreterErrorTests(InterpreterResults.MISSING_PATIENT_IDENTIFIER);
		assertEquals(OrderResult.MESSAGE_ERROR, orderWorker.handleOrderRequest());
	}
	
	@Test
	public void testMissingTests(){
		setupForInterpreterErrorTests(InterpreterResults.MISSING_TESTS);
		assertEquals(OrderResult.MESSAGE_ERROR, orderWorker.handleOrderRequest());
	}
	
	@Test
	public void testOtherThanTestOrPanel(){
		setupForInterpreterErrorTests(InterpreterResults.OTHER_THAN_PANEL_OR_TEST_REQUESTED);
		assertEquals(OrderResult.MESSAGE_ERROR, orderWorker.handleOrderRequest());
	}
	
	@Test
	public void testUnsupportedPanels(){
		setupForInterpreterErrorTests(InterpreterResults.UNSUPPORTED_PANELS);
		assertEquals(OrderResult.MESSAGE_ERROR, orderWorker.handleOrderRequest());
	}
	
	@Test
	public void testUnsupportedTests(){
		setupForInterpreterErrorTests(InterpreterResults.UNSUPPORTED_TESTS);
		assertEquals(OrderResult.MESSAGE_ERROR, orderWorker.handleOrderRequest());
	}
	
	private void setupForStatusTests(OrderType orderType, CheckResult checkResult){
		List<InterpreterResults> interpretResults = new ArrayList<InterpreterResults>();
		interpretResults.add(InterpreterResults.OK);
		when( mockOrderInterpreter.interpret(null)).thenReturn(interpretResults);
		when( mockOrderInterpreter.getReferringOrderNumber()).thenReturn(ORDER_NUMBER);
		when( mockOrderInterpreter.getOrderType()).thenReturn(orderType);
		MessagePatient patient = new MessagePatient();
		when( mockOrderInterpreter.getMessagePatient()).thenReturn(patient);
		String msg = new String();
		when( mockOrderInterpreter.getMessage()).thenReturn(msg);
		when( mockOrderExistanceChecker.check(ORDER_NUMBER)).thenReturn(checkResult);
		when( mockStatusService.getStatusID(ExternalOrderStatus.Entered)).thenReturn("2");
	}
	
	private void setupForInterpreterErrorTests(InterpreterResults interpreterResults){
		List<InterpreterResults> interpretResults = new ArrayList<InterpreterResults>();
		interpretResults.add(interpreterResults);
		when( mockOrderInterpreter.interpret(null)).thenReturn(interpretResults);
	}
}
