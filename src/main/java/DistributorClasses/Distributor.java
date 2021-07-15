package DistributorClasses;

import jade.core.Agent;

import java.util.ArrayList;
import java.util.List;

public class Distributor extends Agent {

    // Массив, с кем уже заключен контракт
    private List<String> isConcluded = new ArrayList<>();

    // Массив, те кто принял сообщение о завершении аукциона
    private List<String> replyAuctionCompleted = new ArrayList<>();

    // Флаг принятия сообщения о старте нового аукциона
    private boolean oneTime = true;

    @Override
    protected void setup() {
        addBehaviour(new DistributorBehaviour(this, 7000, isConcluded,
                replyAuctionCompleted, oneTime));
    }
}
