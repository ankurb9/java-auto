package com.ui.utils;
import com.microsoft.playwright.Page;
import static com.base.InitPlaywright.getPage;
public class UIOperations {

    Page page;
    public UIOperations(){
        page = getPage();
    }

    public void navigate(String URL){
        page.navigate(URL);
    }
}
