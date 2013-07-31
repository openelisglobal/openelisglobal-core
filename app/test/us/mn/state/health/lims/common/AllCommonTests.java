package us.mn.state.health.lims.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import us.mn.state.health.lims.common.provider.validation.AccessionNumberProgramValidatorTest;
import us.mn.state.health.lims.common.provider.validation.AccessionNumberSiteYearValidatorTest;
import us.mn.state.health.lims.common.provider.validation.AccessionNumberYearValidatorTest;
import us.mn.state.health.lims.common.provider.validation.PasswordValidationTest;
import us.mn.state.health.lims.common.util.DateUtilTest;

@RunWith(Suite.class)
@SuiteClasses({DateUtilTest.class,
	AccessionNumberProgramValidatorTest.class,
	AccessionNumberSiteYearValidatorTest.class,
	AccessionNumberYearValidatorTest.class,
	PasswordValidationTest.class})
public class AllCommonTests {

}
