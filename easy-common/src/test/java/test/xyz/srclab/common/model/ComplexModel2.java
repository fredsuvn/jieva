package test.xyz.srclab.common.model;

import java.util.Map;

public class ComplexModel2 {

    private String string;
    private Map<Long, Long> map;
    private Map<? super String, ? extends String> mapGeneric;
    private Map<Long, Map<? super String, ? extends String>> mapNest;

    public Map<Long, Map<? super String, ? extends String>> getMapNest() {
        return mapNest;
    }

    public void setMapNest(Map<Long, Map<? super String, ? extends String>> mapNest) {
        this.mapNest = mapNest;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Map<Long, Long> getMap() {
        return map;
    }

    public void setMap(Map<Long, Long> map) {
        this.map = map;
    }

    public Map<? super String, ? extends String> getMapGeneric() {
        return mapGeneric;
    }

    public void setMapGeneric(Map<? super String, ? extends String> mapGeneric) {
        this.mapGeneric = mapGeneric;
    }
}
