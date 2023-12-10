package net.cloudescape.skyblock.miscellaneous.minions.enums;

public enum  InvestmentType {
    HOURLY(0.02, 60), DAILY(0.30, 60 * 24), WEEKLY(2.40, (60 * 24) * 7), MONTHLY(12.30, ((60 * 24) * 7) * 4);

    private double interest;
    private int seconds;

    InvestmentType(double interest, int seconds){
        this.interest = interest;
    }

    public double getInterest() {
        return interest;
    }

    public int getSeconds() {
        return seconds;
    }
}
