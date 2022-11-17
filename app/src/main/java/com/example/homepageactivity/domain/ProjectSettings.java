package com.example.homepageactivity.domain;

import java.util.HashMap;

public class ProjectSettings {

    private static HashMap<Color, String> colorDict=new HashMap<Color, String>(){{

    }};


    public String getColor(Color color){
        return colorDict.get(color);
    }

}
