package com.example.wegoeco;

public class Trip {

    public int startTime;
    public int endTime;
    public int startSOC;
    public int endSOC;
    public int startODO;
    public int endODO;
    public double kmperkw;
    public double kiloWattHours = 17.6;


    public Trip(int startTime, int endTime, int startSOC, int endSOC, int startODO, int endODO,  double kmperkw){
        startTime = this.startTime;
        endTime = this.endTime;
        startSOC = this.startSOC;
        endSOC = this.endSOC;
        startODO = this.startODO;
        endODO = this.endODO;
    }

    public void calKmperkw() {

        double SOCDif = getStartSOC() - getEndSOC();
        double kmDif = getStartODO() - getEndODO();

        kmperkw = kmDif / (kiloWattHours * (SOCDif/1000));


    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getStartSOC() {
        return startSOC;
    }

    public void setStartSOC(int startSOC) {
        this.startSOC = startSOC;
    }

    public int getEndSOC() {
        return endSOC;
    }

    public void setEndSOC(int endSOC) {
        this.endSOC = endSOC;
    }

    public int getStartODO() {
        return startODO;
    }

    public void setStartODO(int startODO) {
        this.startODO = startODO;
    }

    public int getEndODO() {
        return endODO;
    }

    public void setEndODO(int endODO) {
        this.endODO = endODO;
    }

    public double getKmperkw() {
        return kmperkw;
    }

    public void setKmperkw(double kmperkw) {
        this.kmperkw = kmperkw;
    }
}
