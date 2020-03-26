package test.xyz.srclab.common.model;

import java.util.Map;

public class ComplexModel {

    private String string;
    private Map<String, String> map;
    private Map<? super Integer, ? extends Number> mapGeneric;
    private Map<String, Map<? super Integer, ? extends Number>> mapNest;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public Map<String, Map<? super Integer, ? extends Number>> getMapNest() {
        return mapNest;
    }

    public void setMapNest(Map<String, Map<? super Integer, ? extends Number>> mapNest) {
        this.mapNest = mapNest;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public Map<? super Integer, ? extends Number> getMapGeneric() {
        return mapGeneric;
    }

    public void setMapGeneric(Map<? super Integer, ? extends Number> mapGeneric) {
        this.mapGeneric = mapGeneric;
    }
}
