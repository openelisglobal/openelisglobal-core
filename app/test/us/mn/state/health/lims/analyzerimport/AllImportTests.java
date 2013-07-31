package us.mn.state.health.lims.analyzerimport;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CobasReaderTest.class, SysmexReaderTest.class })
public class AllImportTests {

}
