package com.example.homepageactivity.domain;

public class PageIconInfo {
    String iconName;
    Class page;
    int drawableImageRef;

    public PageIconInfo(String iconName, Class page, int drawableImageRef){
        this.iconName=iconName;
        this.page=page;
        this.drawableImageRef=drawableImageRef;
    }

    public String getIconName() {
        return iconName;
    }
    public Class getPageClass() {
        return page;
    }
    public int getDrawableImageRef() {
        return drawableImageRef;
    }
}
