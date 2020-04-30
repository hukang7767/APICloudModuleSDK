package com.alpha.modulegnoga.measurement;

public class Param {

    private String name, value, units ,content;

    public Param() { }

    public Param(String name , String value , String units , String content)
    {
        this.name = name;
        this.value = value;
        this.units = units;
         this.content = content;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    public String getName()
    {
        return this.name;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
    public String getValue()
    {
        return this.value;
    }
    public void setUnits(String units)
    {
        this.units = units;

    }
    public String getUnits(){
        return this.units;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }



}
