package com.example.homepageactivity.domain;

public class PageIconInfo {
    String iconName;
    Class page;

    public PageIconInfo(String iconName, Class page){
        this.iconName=iconName;
        this.page=page;
    }

    public String getIconName() {
        return iconName;
    }
    public Class getPage() {
        return page;
    }
}
