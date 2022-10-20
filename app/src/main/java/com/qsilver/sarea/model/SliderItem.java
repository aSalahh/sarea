package com.qsilver.sarea.model;

public class SliderItem {
    Integer backGround;
    String desc;

    public SliderItem(Integer backGround, String desc) {
        this.backGround = backGround;
        this.desc = desc;
    }

    public Integer getBackGround() {
        return backGround;
    }

    public void setBackGround(Integer backGround) {
        this.backGround = backGround;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}