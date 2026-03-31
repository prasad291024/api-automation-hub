package com.prasad_v.dummy.base;

import com.prasad_v.config.EnvironmentManager;
import org.testng.annotations.BeforeSuite;

public class BaseDummyTest {

    @BeforeSuite(alwaysRun = true)
    public void initEnvironment() {
        EnvironmentManager.getInstance().initializeEnvironment();
    }
}
