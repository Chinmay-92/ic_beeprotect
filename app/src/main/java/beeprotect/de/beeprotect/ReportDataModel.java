package beeprotect.de.beeprotect;


import java.io.Serializable;
import java.util.Objects;

import androidx.annotation.Nullable;

public class ReportDataModel implements Serializable, Comparable<ReportDataModel> {

    String id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportDataModel that = (ReportDataModel) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(tempDiff, that.tempDiff) &&
                Objects.equals(tensorflow, that.tensorflow) &&
                Objects.equals(pain, that.pain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tempDiff, tensorflow, pain);
    }

    @Override
    public int compareTo(ReportDataModel o) {
        return o.getName().compareTo(getName());
    }
}
