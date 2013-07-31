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
* Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
*
* Contributor(s): CIRG, University of Washington, Seattle WA.
*/
package us.mn.state.health.lims.analyzerimport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import us.mn.state.health.lims.analyzerimport.analyzerreaders.SysmexReader;
import us.mn.state.health.lims.analyzerresults.dao.AnalyzerResultsDAO;
import us.mn.state.health.lims.analyzerresults.valueholder.AnalyzerResults;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.DAOImplFactory;
import us.mn.state.health.lims.common.util.HibernateProxy;

public class SysmexReaderTest {

	private static AnalyzerResultsDAOTest ANALYZER_RESULT_IMP;
	private SysmexReader reader;
	private List<String> testList;
	private static final String sysUserId = "1";
	private static final String FILE_HEADER = " ID Instrument,Ana. Jour,Ana. Heure,N' Rack,Pos. Tube,N' Echantillon,Info Echantillon,Ana. Mode,ID Patient,Ana. Info.,Info échantillon,Positif(Diff),Positif(Morph),Positif(Compt),Erreur(Fonc),Erreur(Résult),Info demande,GB anormaux,GB suspicions,GR anormaux,GR suspicions,PLQ anormales,PLQ suspicions,Info Unités,Info PLQ,Valider,Message(s) d'action(s)(RET),Message(s) d'action(s)(Delta),Commentaire Echantillon,Nom Patient,Date de naiss.,Sexe,Commentaire Patient,Service,Médecin,Paramètres transmis,N' Séquence,GB(10/uL),GB/M,GR(10^4/uL),GR/M,HBG(g/L),HBG/M,HCT(10^(-1)%),HCT/M,VGM(10^(-1)fL),VGM/M,TCMH(10^(-1)pg),TCMH/M,CCMH(g/L),CCMH/M,PLQ(10^3/uL),PLQ/M,IDR-SD(10^(-1)fL),IDR-SD/M,IDR-CV(10^(-1)%),IDR-CV/M,IDP,IDP/M,VPM(10^(-1)fL),VPM/M,P-RGC(10^(-1)%),P-RGC/M,PCT(10^(-2)%),PCT/M,NEUT#(10/uL),NEUT#/M,LYMPH#(10/uL),LYMPH#/M,MONO#(10/uL),MONO#/M,EO#(10/uL),EO#/M,BASO#(10/uL),BASO#/M,NEUT%(10^(-1)%),NEUT%/M,LYMPH%(10^(-1)%),LYMPH%/M,MONO%(10^(-1)%),MONO%/M,EO%(10^(-1)%),EO%/M,BASO%(10^(-1)%),BASO%/M,RET#(10^2uL),RET#/M,RET%(10^(-2)%),RET%/M,LFR(10^(-1)%),LFR/M,MFR(10^(-1)%),MFR/M,HFR(10^(-1)%),HFR/M,IRF(10^(-1)%),IRF/M,IG#(10/uL),IG#/M,IG%(10^(-1)%),IG%/M,NEUT#&(10/uL),NEUT#&/M,NEUT%&(10^(-1)%),NEUT%&/M,NRBC+W(10/uL),NRBC+W/M,LYMPH#&(10/uL),LYMPH#&/M,LYMPH%&(10^(-1)%),LYMPH%&/M,OTHER#(10/uL),OTHER#/M,OTHER%(10^(-1)%),OTHER%/M,GR-O(10^4/uL),GR-O/M,PLQ-O(10^3/uL),PLQ-O/M,IP Abn(GB)Scatterg. GB Anorm.,IP Abn(GB)Neutropénie,IP Abn(GB)Neutrophilie,IP Abn(GB)Lymphopénie,IP Abn(GB)Lymphocytose,IP Abn(GB)Monocytose,IP Abn(GB)Eosinophilie,IP Abn(GB)Basophilie,IP Abn(GB)Leucocytopénie,IP Abn(GB)Leucocytose,IP Abn(GR)Dist. GR An.,IP Abn(GR)D. pop. GR,IP Abn(GR)Anisocytose,IP Abn(GR)Microcytose,IP Abn(GR)Macrocytose,IP Abn(GR)Hypochromie,IP Abn(GR)Anémie,IP Abn(GR)Erythrocytose,IP Abn(GR)Scatterg. RET Anorm.,IP Abn(GR)Réticulocytose,IP Abn(PLQ)Dist. PLQ An.,IP Abn(PLQ)Thrombocytopénie,IP Abn(PLQ)Thrombocytose,IP Abn(PLQ)Scatterg. PLQ Anorm.,IP SUS(GB)Blasts?,IP SUS(GB)Gra. Immat.?,IP SUS(GB)Dev. Gauche?,IP SUS(GB)Lympho Aty.?,IP SUS(GB)Anor. Ly/Blasts?,IP SUS(GB)NRBC?,IP SUS(GB)Rés. GR Lyse?,IP SUS(GR)Agglut. GR?,IP SUS(GR)Turb/HGB Interf?,IP SUS(GR)Carence Fer?,IP SUS(GR)Déf. HGB?,IP SUS(GR)Fragments?,IP SUS(PLQ)Agg. PLQ?,IP SUS(PLQ)Agg. PLQ(S)?,Info par défaut,Qflag(Blasts?),Qflag(Gra. Immat.?),Qflag(Dev. Gauche?),Qflag(Lympho Aty?),Qflag(NRBC?),Qflag(Abn Ly/Bla?),Qflag(Rés. GR Lyse?),Qflag(Agglut. GR?),Qflag(Turb/HGB?),Qflag(Carence Fer?),Qflag(Déf. HGB?),Qflag(Fragments?),Qflag(Agg. PLQ?),Qflag(Agg. PLQ(S)?)";
	private static final String CONTROL = "XT-2000i^12520,19/06/2009,15:03:38,,0,QC-90440811,A,Manuel,,Normal,QC,,,,,,,0,0,0,0,0,0,0,0,1,0,0,,,,,,,,DH,18,737,,445,,124,,367,,825,,279,,338,,251,,451,,156,,82,,86,,97,,22,,358,,222,,79,,79,,506,,485,,301,,107,,107,,687,,992,,223,,745,,220,,35,,255,,11,,15,,347,,470,,737,,222,,301,,0,,0,,367,,218,,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,NUM+DIFF+RET,,,,,,,,,,,,,,";
	private static final String FULL_RESULT = "40	2009-06-23 13:04:26	ALTL		LART05136	11	SER	U/L	 	  19	  0.00248	R	X";
	private static final String NO_ACCESSION_RESULT = "40	2009-06-23 13:04:26	ALTL				11	SER	U/L	 	  19	  0.00248	R	X";
	private static final String NO_TEST_RESULT = "40	2009-06-23 13:04:26			LART05136	11	SER	U/L	 	  19	  0.00248	R	X";
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DAOImplFactory.getInstance().revertAll();
		HibernateProxy.useTestImpl(false);
	}

	@Before
	public void setUp() throws Exception {

		reader = new SysmexReader();
		testList = new ArrayList<String>();
//		reader.insertTestLines(testList);
		HibernateProxy.useTestImpl(true);
		HibernateProxy.TestQuery.setQueryListResponse( null);
	}

	@After
	public void tearDown() throws Exception {
		HibernateProxy.useTestImpl(false);
		DAOImplFactory.getInstance().revertAll();
		HibernateProxy.TestQuery.setQueryListResponse(null);
	}

	@Test
	public void testEmptyFile() {
		//expected not to throw an exception
//		reader.insertAnalyzerData(sysUserId);
	}

	@Test
	public void testHeaderOnly() {
		//expected not to throw an exception
		testList.add(FILE_HEADER);
//		reader.insertAnalyzerData(sysUserId);
	}

	@Test
	public void testControl() {
		//expected not to throw an exception
		testList.add(FILE_HEADER);
		testList.add(CONTROL);

		ANALYZER_RESULT_IMP.testIsControl( 0 );

//		reader.insertAnalyzerData(sysUserId);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleMatch() {
		testList.add(FILE_HEADER);
		testList.add(FULL_RESULT);

		Object[] response = new Object[6];
		response[0] = "774324";
		response[1] = "test";
		response[2] = new BigDecimal(2);
		response[3] = new BigDecimal(3);
		response[4] = new BigDecimal(4);
		response[5] = "5";

		List responseList = new ArrayList();
		responseList.add(response);
		HibernateProxy.TestQuery.setQueryListResponse(responseList);
//		ANALYZER_RESULT_IMP.testStatus( 0, AnalyzerResults.NOT_REVIEWED );
		ANALYZER_RESULT_IMP.testIsNotControl( 0 );

//		reader.insertAnalyzerData(sysUserId);
	}

	@Test
	public void testMissingAccessionNumber() {
		testList.add(FILE_HEADER);
		testList.add(NO_ACCESSION_RESULT);

//		ANALYZER_RESULT_IMP.testStatus( 0, AnalyzerResults.MATCHING_ACCESSION_NOT_FOUND );
		ANALYZER_RESULT_IMP.testIsNotControl( 0 );

//		reader.insertAnalyzerData(sysUserId);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMissingTest() {
		testList.add(FILE_HEADER);
		testList.add(NO_TEST_RESULT);

		List responseList = new ArrayList();
		responseList.add(new Object());
		HibernateProxy.TestQuery.setQueryListResponse(responseList);
//		ANALYZER_RESULT_IMP.testStatus( 0, AnalyzerResults.TEST_MAPPING_NOT_FOUND );
		ANALYZER_RESULT_IMP.testIsNotControl( 0 );

//		reader.insertAnalyzerData(sysUserId);
	}

	@Test
	public void testNoAccessionMatch() {
		testList.add(FILE_HEADER);
		testList.add(FULL_RESULT);

	//	ANALYZER_RESULT_IMP.testStatus( 0, AnalyzerResults.MATCHING_ACCESSION_NOT_FOUND );
		ANALYZER_RESULT_IMP.testIsNotControl( 0 );

//		reader.insertAnalyzerData(sysUserId);
	}


	class AnalyzerResultsDAOTest implements AnalyzerResultsDAO {

		private int controlIndex = -1;
		private int notControlIndex = -1;
		private int statusIndex = -1;
		private String status;

		public void getData(AnalyzerResults results) throws LIMSRuntimeException {
		}

		public void testIsNotControl(int i) {
			notControlIndex = i;
		}

		public void testStatus(int i, String status) {
			statusIndex = i;
			this.status = status;
		}

		public void testIsControl(int i) {
			controlIndex = i;
		}

		public List<AnalyzerResults> getResultsbyAnalyzer(String analyzer) throws LIMSRuntimeException {
			return null;
		}

		public void insertAnalyzerResults(List<AnalyzerResults> results, String sysUserId) throws LIMSRuntimeException {

			if( controlIndex != -1){
				Assert.assertTrue(results.get(controlIndex).getIsControl());
			}

			if( notControlIndex != -1){
				Assert.assertFalse(results.get(notControlIndex).getIsControl());
			}

			if( statusIndex != -1 ){
//				Assert.assertTrue(status.equals(results.get(statusIndex).getStatusId()));
			}
		}

		public AnalyzerResults readAnalyzerResults(String idString) {
			return null;
		}

		public void updateData(AnalyzerResults results) throws LIMSRuntimeException {
		}

		
		@SuppressWarnings("rawtypes")
		public List getNextRecord(String id, String table, Class clazz) throws LIMSRuntimeException {
			return null;
		}

		@SuppressWarnings("rawtypes")
		public List getPreviousRecord(String id, String table, Class clazz) throws LIMSRuntimeException {
			return null;
		}

		@SuppressWarnings("rawtypes")
		public Integer getTotalCount(String table, Class clazz) throws LIMSRuntimeException {
			return null;
		}

		public void delete(List<AnalyzerResults> deletableAnalyzerResults) throws LIMSRuntimeException {
			// TODO Auto-generated method stub

		}

	}
}
