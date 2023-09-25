package test.xyz.srclab.annotations;

import xyz.srclab.annotations.JavaBean;
import xyz.srclab.annotations.NonNull;

import java.util.List;

/**
 * @author sunqian
 */
@JavaBean
public class Bean1 {

    private String p1;

    @NonNull
    private String p2 = "p2";

    private List<String> p3;

    public String getP1() {
        return p1;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    @NonNull
    public String getP2() {
        return p2;
    }

    public void setP2(@NonNull String p2) {
        this.p2 = p2;
    }

    public List<String> getP3() {
        return p3;
    }

    public void setP3(List<String> p3) {
        this.p3 = p3;
    }
}
