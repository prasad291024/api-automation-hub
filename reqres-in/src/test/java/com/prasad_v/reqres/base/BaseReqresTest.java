package com.prasad_v.reqres.base;

import com.prasad_v.config.EnvironmentManager;
import org.testng.annotations.BeforeSuite;

public class BaseReqresTest {

    @BeforeSuite(alwaysRun = true)
    public void initEnvironment() {
        EnvironmentManager.getInstance().initializeEnvironment();
    }
}
