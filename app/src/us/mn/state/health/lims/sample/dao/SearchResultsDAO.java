package us.mn.state.health.lims.sample.dao;

import us.mn.state.health.lims.common.dao.BaseDAO;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.provider.query.PatientSearchResults;

import java.util.List;

public interface SearchResultsDAO extends BaseDAO {

	public List<PatientSearchResults> getSearchResults( String lastName, String firstName, String STNumber, String subjectNumber, String nationalID, String externalID, String patientID, String guid) throws LIMSRuntimeException;
}
