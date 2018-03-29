package us.mn.state.health.lims.dataexchange.orderresult;

import java.io.IOException;

import org.apache.commons.validator.GenericValidator;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.hoh.api.DecodeException;
import ca.uhn.hl7v2.hoh.api.EncodeException;
import ca.uhn.hl7v2.hoh.api.IReceivable;
import ca.uhn.hl7v2.hoh.api.ISendable;
import ca.uhn.hl7v2.hoh.hapi.api.MessageSendable;
import ca.uhn.hl7v2.hoh.hapi.client.HohClientSimple;
import ca.uhn.hl7v2.hoh.sockets.TlsSocketFactory;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.message.ACK;
import ca.uhn.hl7v2.model.v251.message.ORU_R01;
import ca.uhn.hl7v2.model.v251.segment.OBR;
import ca.uhn.hl7v2.model.v251.segment.ORC;
import ca.uhn.hl7v2.model.v251.segment.PID;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.dataexchange.order.valueholder.ElectronicOrder;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;

public class OrderResponseWorker {
	
	public static enum Event{
		ORDER_RECEIVED_NO_SPEC,
		SPEC_RECEIVED,
		PRELIMINARY_RESULT,
		FINAL_RESULT,
		CORRECTION,
		TESTING_NOT_DONE
	}
	
	// TO DO needs to be user configurable 
	private String host = "localhost";
	private int port = 443;
	private String uri = "/";
	
	private Event event;
	private ElectronicOrder order;
	
	public void triggerEvent(Event event, ElectronicOrder order) throws HL7Exception, IOException {
		this.event = event;
		this.order = order;
		try {		
			sendMessage(createEventMessageSendable());
		} catch (Exception e) {
			LogEvent.logError("OrderResponseWorker","triggerEvent()",e.toString());
			System.out.println("Failed to send message.");
		}
	}
	
	public ISendable<Message> createEventMessageSendable() throws HL7Exception, IOException {
		ORU_R01 hl7Message = new ORU_R01();
		hl7Message.initQuickstart("ORU", "R01", "P");
		hl7Message.getMSH().getSendingApplication().getUniversalID().setValue("OpenELIS");
		hl7Message.getMSH().getReceivingApplication().getUniversalID().setValue("iSantePlus");
		
		String orc1 = "RE"; 
		String obr25 = "";
		switch (event) {
		case ORDER_RECEIVED_NO_SPEC: 
			obr25 = "I";
			break;
		case SPEC_RECEIVED: 
			obr25 = "P";
			break;
		case PRELIMINARY_RESULT: 
			obr25 = "A";
			break;
		case FINAL_RESULT: 
			obr25 = "F";
			break;
		case CORRECTION: 
			obr25 = "C";
			break;
		case TESTING_NOT_DONE: 
			obr25 = "X";
		}
		OBR obrSegment = hl7Message.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR();
		obrSegment.getResultStatus().setValue(obr25);	
		ORC orcSegment = hl7Message.getPATIENT_RESULT().getORDER_OBSERVATION().getORC();
		orcSegment.getOrderControl().setValue(orc1);

		// set up order numbers 
		orcSegment.getFillerOrderNumber().getEi1_EntityIdentifier().setValue(order.getExternalId());
		orcSegment.getPlacerOrderNumber().getEi1_EntityIdentifier().setValue(order.getExternalId());
		
		// set up patient info (only id set so far)
		PID pidSegment = hl7Message.getPATIENT_RESULT().getPATIENT().getPID();
		Patient patient = getPatientFromLabNumber(order.getExternalId());
		pidSegment.getPatientID().getIDNumber().setValue(patient.getExternalId());
		/*Patient patient = getPatientFromLabNumber(order.getExternalId());
		pidSegment.getPatientID().getIDNumber().setValue("1");*/
	
		
		System.out.println(hl7Message.encode());
		return new MessageSendable(hl7Message);
	}
	
	public void sendMessage(ISendable<Message> message) throws EncodingNotSupportedException, HL7Exception, DecodeException, IOException, EncodeException {
		Parser parser = PipeParser.getInstanceWithNoValidation();
		HohClientSimple client = new HohClientSimple(host, port, uri, parser);
		
		// TO DO add message signing for authentication of messages
		
		// https
		client.setSocketFactory(new TlsSocketFactory());
		
		IReceivable<Message> receivable = client.sendAndReceiveMessage(message);
		Message response = receivable.getMessage();
		ACK ack = (ACK) response;
		if (hasError(ack)) {
			System.out.println("iSantePlus has returned an error");
		}
	}	
	
	public Patient getPatientFromLabNumber(String labNo) {
		SampleDAO sampleDAO = new SampleDAOImpl();
		Sample sample = sampleDAO.getSampleByAccessionNumber(order.getExternalId());
		if(sample != null && !GenericValidator.isBlankOrNull(sample.getId())){
			SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
			return sampleHumanDAO.getPatientForSample(sample);
		}
		return new Patient();
	}
	
	public boolean hasError(ACK ack) {
		try {
			if (ack.getERRAll().size() <= 0) {
				return true;
			}
		} catch (HL7Exception e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}

}
