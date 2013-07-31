package us.mn.state.health.lims.sample.bean;

public class SampleEditItem {
	private String accessionNumber;
	private String analysisId;
	private String sampleType;
	private String testName;
	private String sampleItemId;
	private String testId;
	private boolean canCancel = false;
	private boolean canceled;
	private boolean add;
	private String status;
	private String sortOrder;
	private boolean canRemoveSample = false;
	private boolean removeSample;

	public String getAccessionNumber() {
		return accessionNumber;
	}
	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}
	public String getSampleType() {
		return sampleType;
	}
	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public String getSampleItemId() {
		return sampleItemId;
	}
	public void setSampleItemId(String sampleItemId) {
		this.sampleItemId = sampleItemId;
	}
	public String getTestId() {
		return testId;
	}
	public void setTestId(String testId) {
		this.testId = testId;
	}
	public boolean isCanCancel() {
		return canCancel;
	}
	public void setCanCancel(boolean hasResults) {
		this.canCancel = hasResults;
	}
	public boolean isCanceled() {
		return canceled;
	}
	public void setCanceled(boolean remove) {
		this.canceled = remove;
	}

	public boolean isAdd() {
		return add;
	}
	public void setAdd(boolean add) {
		this.add = add;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setAnalysisId(String analysisId) {
		this.analysisId = analysisId;
	}
	public String getAnalysisId() {
		return analysisId;
	}
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	public boolean isCanRemoveSample() {
		return canRemoveSample;
	}
	public void setCanRemoveSample(boolean canRemoveSample) {
		this.canRemoveSample = canRemoveSample;
	}
	public boolean isRemoveSample() {
		return removeSample;
	}
	public void setRemoveSample(boolean removeSample) {
		this.removeSample = removeSample;
	}

}
