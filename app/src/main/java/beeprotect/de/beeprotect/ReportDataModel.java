package beeprotect.de.beeprotect;


import java.io.Serializable;

public class ReportDataModel implements Serializable {

    String name;
    Double tempDiff;
    String tensorflow;
    String pain;

    public ReportDataModel(String name, Double tempDiff, String tensorflow, String pain) {
        this.name = name;
        this.tempDiff = tempDiff;
        this.tensorflow = tensorflow;
        this.pain = pain;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTempDiff() {
        return tempDiff;
    }

    public void setTempDiff(Double tempDiff) {
        this.tempDiff = tempDiff;
    }

    public String getTensorflow() {
        return tensorflow;
    }

    public void setTensorflow(String tensorflow) {
        this.tensorflow = tensorflow;
    }

    public String getPain() {
        return pain;
    }

    public void setPain(String pain) {
        this.pain = pain;
    }

    public String getName() {
        return name;
    }

}
