package com.example.satunetra.model;

import java.util.List;
import java.util.Map;

public class Tag {
    private Map<String, String> child;
    private String value;

    public String getChildName(String key){
        return child.get(key);
    }


    public boolean childEquals(String tag){
        return child.containsKey(tag);
    }



    public Map<String, String> getChild() {
        return child;
    }

    public void setChild(Map<String, String> child) {
        this.child = child;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
