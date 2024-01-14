package com.base;
import com.ui.utils.UIOperations;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import static com.base.InitPlaywright.*;

public class UIBase{

    protected UIOperations method;


@BeforeClass(alwaysRun = true)
public void init(){
    initPlaywrite(false);
}

@AfterClass(alwaysRun = true)
public void cleanup(){
    cleanUp();
}

@BeforeMethod(alwaysRun = true)
    public void launchBrowser() {
    initPage();
    method = new UIOperations();
}

@AfterMethod(alwaysRun = true)
    public void closeBrowser(){
    closeBrowserContext();
}

}
