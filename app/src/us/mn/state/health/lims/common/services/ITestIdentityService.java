package us.mn.state.health.lims.common.services;

public interface ITestIdentityService{

	public abstract boolean doesTestExist(String name);

	public abstract boolean doesPanelExist(String name);

}