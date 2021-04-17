package com.example.wegoeco;

public class Trip {

    private int startTime;
    private int endTime;
    private int startSOC;
    private int endSOC;
    private int startODO;
    private int endODO;
    private double kmperkw;
    private int kmDif;
    private int SOCDif;
    private double kwhForTrip;
    private double score;
    private double kiloWattHours = 17.6;

    public Trip(){

    }


    public Trip(int startTime, int endTime, int startSOC, int endSOC, int startODO, int endODO,  double kmperkw, int kmDif, int SOCDif, double kwForTrip){
        this.startTime = startTime;
        this.endTime = endTime;
        this.startSOC = startSOC;
        this.endSOC = endSOC;
        this.startODO = startODO;
        this.endODO = endODO;
        this.kmperkw = kmperkw;
        this.kmDif = kmDif;
        this.SOCDif = SOCDif;
        this.kwhForTrip = kwForTrip;
    }

    public Trip(int startTime, int endTime, int startSOC, int endSOC, int startODO, int endODO){
        this.startTime = startTime;
        this.endTime = endTime;
        this.startSOC = startSOC;
        this.endSOC = endSOC;
        this.startODO = startODO;
        this.endODO = endODO;
        calSOCDif();
        calkmDif();
        calkwForTrip();
        calKmperkw();
    }

    public Trip(int startTime, int endTime, double kmperkw, int kmDif, double kwForTrip){
        this.startTime = startTime;
        this.endTime = endTime;
        this.kmperkw = kmperkw;
        this.kmDif = kmDif;
        this.kwhForTrip = kwForTrip;
    }

    public void calKmperkw() {
        calSOCDif();
        calkmDif();
        calkwForTrip();
        setKmperkw(kmDif / kwhForTrip);
    }

    public void calkmDif(){
        setKmDif(getEndODO()- getStartODO());
    }

    public void calSOCDif(){
        setSOCDif(getStartSOC() - getEndSOC());
    }

    public void calkwForTrip(){
        setKwhForTrip(kiloWattHours * ((double) SOCDif/1000));
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

    public int getKmDif() {
        return kmDif;
    }

    public void setKmDif(int kmDif) {
        this.kmDif = kmDif;
    }

    public int getSOCDif() {
        return SOCDif;
    }

    public void setSOCDif(int SOCDif) {
        this.SOCDif = SOCDif;
    }

    public double getKwhForTrip() {
        return kwhForTrip;
    }

    public void setKwhForTrip(double kwhForTrip) {
        this.kwhForTrip = kwhForTrip;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
