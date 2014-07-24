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
*/
package us.mn.state.health.lims.test.valueholder;

import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.valueholder.EnumValueItemImpl;
import us.mn.state.health.lims.common.valueholder.ValueHolder;
import us.mn.state.health.lims.common.valueholder.ValueHolderInterface;
import us.mn.state.health.lims.label.valueholder.Label;
import us.mn.state.health.lims.localization.valueholder.Localization;
import us.mn.state.health.lims.method.valueholder.Method;
import us.mn.state.health.lims.scriptlet.valueholder.Scriptlet;
import us.mn.state.health.lims.testtrailer.valueholder.TestTrailer;
import us.mn.state.health.lims.unitofmeasure.valueholder.UnitOfMeasure;

import java.sql.Date;

/**
 * @author benzd1
 */
public class Test extends EnumValueItemImpl {

	private static final long serialVersionUID = 1L;

	private String id;

	private String methodName;

	private ValueHolderInterface method;

	private String labelName;

	private ValueHolderInterface label;

	private String testTrailerName;

	private ValueHolderInterface testTrailer;

	private String testSectionName;

	private ValueHolderInterface testSection;

	private String scriptletName;

	private ValueHolderInterface scriptlet;

	private String unitOfMeasureId;

	private String testName;

	private String description;

	private String loinc;

	private String reportingDescription;

	private String stickerRequiredFlag;

	private String alternateTestDisplayValue;

	private Date activeBeginDate = null;

	private String activeBeginDateForDisplay;

	private Date activeEndDate = null;

	private String activeEndDateForDisplay;

	private String isReportable;

	private String timeHolding;

	private String timeWait;

	private String timeAverage;

	private String timeWarning;

	private String timeMax;

	private String labelQuantity;

	private UnitOfMeasure unitOfMeasure;

	private String sortOrder;

	private String localCode;
	
	private Boolean orderable;

    private ValueHolder localizedTestName;

    private ValueHolder localizedReportingName;
	
	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Test() {
		super();
		this.method = new ValueHolder();
		this.label = new ValueHolder();
		this.testTrailer = new ValueHolder();
		this.testSection = new ValueHolder();
		this.scriptlet = new ValueHolder();
        localizedTestName = new ValueHolder( );
        localizedReportingName = new ValueHolder( );
	}

	public void setId(String id) {
		this.id = id;
		this.key = id;
	}

	public String getId() {
		return id;
	}

	public Date getActiveBeginDate() {
		return activeBeginDate;
	}

	public void setActiveBeginDate(Date activeBeginDate) {
		this.activeBeginDate = activeBeginDate;
		// also update String date
		String locale = SystemConfiguration.getInstance().getDefaultLocale()
				.toString();
		this.activeBeginDateForDisplay = DateUtil.convertSqlDateToStringDate(
				activeBeginDate, locale);
	}

	public String getActiveBeginDateForDisplay() {
		return activeBeginDateForDisplay;
	}

	public void setActiveBeginDateForDisplay(String activeBeginDateForDisplay) {
		this.activeBeginDateForDisplay = activeBeginDateForDisplay;
		// also update the java.sql.Date
		String locale = SystemConfiguration.getInstance().getDefaultLocale()
				.toString();
		this.activeBeginDate = DateUtil.convertStringDateToSqlDate(
				this.activeBeginDateForDisplay, locale);
	}

	public Date getActiveEndDate() {
		return activeEndDate;
	}

	public void setActiveEndDate(Date activeEndDate) {
		this.activeEndDate = activeEndDate;
		// also update String date
		String locale = SystemConfiguration.getInstance().getDefaultLocale()
				.toString();
		this.activeEndDateForDisplay = DateUtil.convertSqlDateToStringDate(
				activeEndDate, locale);
	}

	public String getActiveEndDateForDisplay() {
		return activeEndDateForDisplay;
	}

	public void setActiveEndDateForDisplay(String activeEndDateForDisplay) {
		this.activeEndDateForDisplay = activeEndDateForDisplay;
		// also update the java.sql.Date
		String locale = SystemConfiguration.getInstance().getDefaultLocale()
				.toString();
		this.activeEndDate = DateUtil.convertStringDateToSqlDate(
				this.activeEndDateForDisplay, locale);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getIsReportable() {
		return isReportable;
	}

	public void setIsReportable(String isReportable) {
		this.isReportable = isReportable;
	}

	public String getLabelQuantity() {
		return labelQuantity;
	}

	public void setLabelQuantity(String labelQuantity) {
		this.labelQuantity = labelQuantity;
	}

	public String getLoinc() {
		return loinc;
	}

	public void setLoinc(String loinc) {
		this.loinc = loinc;
	}

	public String getReportingDescription() {
		return reportingDescription;
	}

	public void setReportingDescription(String reportingDescription) {
		this.reportingDescription = reportingDescription;
	}

	public String getStickerRequiredFlag() {
		return stickerRequiredFlag;
	}

	public void setStickerRequiredFlag(String stickerRequiredFlag) {
		this.stickerRequiredFlag = stickerRequiredFlag;
	}


	public String getTimeAverage() {
		return timeAverage;
	}

	public void setTimeAverage(String timeAverage) {
		this.timeAverage = timeAverage;
	}

	public String getTimeHolding() {
		return timeHolding;
	}

	public void setTimeHolding(String timeHolding) {
		this.timeHolding = timeHolding;
	}

	public String getTimeMax() {
		return timeMax;
	}

	public void setTimeMax(String timeMax) {
		this.timeMax = timeMax;
	}

	public String getTimeWait() {
		return timeWait;
	}

	public void setTimeWait(String timeWait) {
		this.timeWait = timeWait;
	}

	public String getTimeWarning() {
		return timeWarning;
	}

	public void setTimeWarning(String timeWarning) {
		this.timeWarning = timeWarning;
	}

	public String getUnitOfMeasureId() {
		return unitOfMeasureId;
	}

	public void setUnitOfMeasureId(String unitOfMeasureId) {
		this.unitOfMeasureId = unitOfMeasureId;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
		this.name = testName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public String getTestTrailerName() {
		return testTrailerName;
	}

	public void setTestTrailerName(String testTrailerName) {
		this.testTrailerName = testTrailerName;
	}

	public String getTestSectionName() {
		return testSectionName;
	}

	public void setTestSectionName(String testSectionName) {
		this.testSectionName = testSectionName;
	}

	public String getScriptletName() {
		return scriptletName;
	}

	public void setScriptletName(String scriptletName) {
		this.scriptletName = scriptletName;
	}

	public void setMethod(Method method) {
		this.method.setValue(method);
	}

	protected void setMethodHolder(ValueHolderInterface method) {
		this.method = method;
	}

	public Method getMethod() {
		return (Method) this.method.getValue();
	}

	protected ValueHolderInterface getMethodHolder() {
		return this.method;
	}

	public void setLabel(Label label) {
		this.label.setValue(label);
	}

	protected void setLabelHolder(ValueHolderInterface label) {
		this.label = label;
	}

	public Label getLabel() {
		return (Label) this.label.getValue();
	}

	protected ValueHolderInterface getLabelHolder() {
		return this.label;
	}

	public void setTestTrailer(TestTrailer testTrailer) {
		this.testTrailer.setValue(testTrailer);
	}

	protected void setTestTrailerHolder(ValueHolderInterface testTrailer) {
		this.testTrailer = testTrailer;
	}

	public TestTrailer getTestTrailer() {
		return (TestTrailer) this.testTrailer.getValue();
	}

	protected ValueHolderInterface getTestTrailerHolder() {
		return this.testTrailer;
	}

	public void setTestSection(TestSection testSection) {
		this.testSection.setValue(testSection);
	}

	protected void setTestSectionHolder(ValueHolderInterface testSection) {
		this.testSection = testSection;
	}

	public TestSection getTestSection() {
		return (TestSection) this.testSection.getValue();
	}

	protected ValueHolderInterface getTestSectionHolder() {
		return this.testSection;
	}

	public void setScriptlet(Scriptlet scriptlet) {
		this.scriptlet.setValue(scriptlet);
	}

	protected void setScriptletHolder(ValueHolderInterface scriptlet) {
		this.scriptlet = scriptlet;
	}

	public Scriptlet getScriptlet() {
		return (Scriptlet) this.scriptlet.getValue();
	}

	protected ValueHolderInterface getScriptletHolder() {
		return this.scriptlet;
	}

	public UnitOfMeasure getUnitOfMeasure() {
		return this.unitOfMeasure;
	}

	public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public String getTestDisplayValue() {
		if (!StringUtil.isNullorNill(this.testName)) {
			return testName + "-" + description;
		} else {
			return description;
		}
	}

	public String getAlternateTestDisplayValue() {
		if (!StringUtil.isNullorNill(this.description)) {
			alternateTestDisplayValue = description + "-" + testName;
		} else {
			alternateTestDisplayValue = testName;
		}
		return alternateTestDisplayValue;
	}

	public void setAlternateTestDisplayValue(String alternateTestDisplayValue) {
		this.alternateTestDisplayValue = alternateTestDisplayValue;
	}

	@Override
	protected String getDefaultLocalizedName() {
		return testName;
	}

	public void setLocalCode( String localCode ) {
		this.localCode = localCode;
	}

	public String getLocalCode() {
		return localCode;
	}

	public Boolean getOrderable() {
		return orderable;
	}

	public void setOrderable(Boolean orderable) {
		this.orderable = orderable;
	}

    public Localization getLocalizedTestName(){
        return (Localization)localizedTestName.getValue();
    }

    public void setLocalizedTestName( Localization localizedName ){
        this.localizedTestName.setValue( localizedName );
    }

    public Localization getLocalizedReportingName(){
        return (Localization)localizedReportingName.getValue();
    }

    public void setLocalizedReportingName( Localization localizedReportingName ){
        this.localizedReportingName.setValue( localizedReportingName );
    }
}