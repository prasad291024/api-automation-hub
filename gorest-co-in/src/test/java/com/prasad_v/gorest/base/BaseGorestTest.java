package com.prasad_v.gorest.base;

import com.prasad_v.config.EnvironmentManager;
import org.testng.annotations.BeforeSuite;

public class BaseGorestTest {

    @BeforeSuite(alwaysRun = true)
    public void initEnvironment() {
        EnvironmentManager.getInstance().initializeEnvironment();
    }
}
