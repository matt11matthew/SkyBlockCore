package net.cloudescape.skyblock.miscellaneous.minions.minions;

import com.cloudescape.utilities.CustomChatMessage;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.boost.MinionBoost;
import net.cloudescape.skyblock.miscellaneous.minions.enums.InvestmentType;
import net.cloudescape.skyblock.miscellaneous.minions.enums.MinionType;
import net.cloudescape.skyblock.miscellaneous.minions.suit.type.IronManSuit;
import net.cloudescape.skyblock.miscellaneous.minions.suit.Suit;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Banker extends Minion {
    private double minimumInvestment;
    private double maximumInvestment;
    private double investment;

    private InvestmentType investmentType;

    private long canWithdraw;
    private boolean investmentStarted;

    public Banker(int id, Island island, Location location, String name, MinionType type, int health, int hunger, MinionBoost boost, Suit suit, InvestmentType investmentType){
        super(id, island, location, name, type, health, hunger, boost);

        this.investmentType = investmentType;
        this.canWithdraw = System.currentTimeMillis() + investmentType.getSeconds() * 1000;

        setSuit(suit == null ? new IronManSuit(this) : suit);
    }

    public double calculateInterest(double amount){
        return investment + (investment * investmentType.getInterest());
    }

    public boolean cansInvest(double amount){
        if(minimumInvestment>amount)
            return true;
        return false;
    }

    public boolean canWithdraw(){
        return System.currentTimeMillis() >= canWithdraw;
    }

    public void stopInvestment(){
        getIsland().setBalance((int) (getIsland().getBalance() + investment));
        this.canWithdraw = 0;
        this.investmentStarted = false;
        this.investment = 0;

        getIsland().getIslandMembers().forEach((uuid, islandRank) -> {
            if(Bukkit.getPlayer(uuid).isOnline()) {
                CustomChatMessage.sendMessage(Bukkit.getPlayer(uuid), getName(), "The investment of &c$" + investment + " has been canceled.");
            }
        });
    }

    public void finishInvestment(){
        double amount = calculateInterest(investment);
        getIsland().setBalance((int) (getIsland().getBalance() + amount));
        this.canWithdraw = 0;
        this.investmentStarted = false;
        this.investment = 0;

        getIsland().getIslandMembers().forEach((uuid, islandRank) -> {
            if(Bukkit.getPlayer(uuid).isOnline()) {
                CustomChatMessage.sendMessage(Bukkit.getPlayer(uuid), getName(), "Finished investing, you collected &c$" + amount);
            }
        });
    }

    public double getInvestment() {
        return investment;
    }

    public double getMinimumInvestment() {
        return minimumInvestment;
    }

    public double getMaximumInvestment() {
        return maximumInvestment;
    }

    public InvestmentType getInvestmentType() {
        return investmentType;
    }

    public long getCanWithdraw() {
        return canWithdraw;
    }

    public void setInvestment(double investment) {
        this.investment = investment;
    }

    public void setMinimumInvestment(double minimumInvestment) {
        this.minimumInvestment = minimumInvestment;
    }

    public void setMaximumInvestment(double maximumInvestment) {
        this.maximumInvestment = maximumInvestment;
    }

    public void setInvestmentType(InvestmentType investmentType) {
        this.investmentType = investmentType;
    }

    public void setInvestmentStarted(boolean investmentStarted) {
        this.investmentStarted = investmentStarted;
    }

    public void setCanWithdraw(long canWithdraw) {
        this.canWithdraw = canWithdraw;
    }
}
