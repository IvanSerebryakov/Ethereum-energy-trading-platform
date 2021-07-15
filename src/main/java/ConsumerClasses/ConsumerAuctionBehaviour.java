package ConsumerClasses;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.web3j.crypto.SignedRawTransaction;

public class ConsumerAuctionBehaviour extends TickerBehaviour {

    private double bet = 80000 + Math.random() * 20000;
    private boolean flagWind;
    private boolean flagPV;

    private DataStore dataStore;
    private String auctionCompleted;
    private String auctionCompleted1;
    private String auctionCompleted2;
    private String auctionCompleted3;

    public ConsumerAuctionBehaviour(Agent a, long period, boolean flagWind, boolean flagPV,
                                    DataStore dataStore, String auctionCompleted1,
                                    String auctionCompleted2, String auctionCompleted3) {
        super(a, period);
        this.flagWind = flagWind;
        this.flagPV = flagPV;

        this.dataStore = dataStore;
        setDataStore(dataStore);

        this.auctionCompleted1 = auctionCompleted1;
        this.auctionCompleted2 = auctionCompleted2;
        this.auctionCompleted3 = auctionCompleted3;
    }

    @Override
    protected void onTick() {

        if (myAgent.getLocalName().equals("Consumer1") && auctionCompleted1 == null) {
            receivingPriceList(myAgent, "WindPriceList", flagWind);
            receivingPriceList(myAgent, "PVPriceList", flagPV);

            // Принятие сообщений от дистрибьтора об оконччании аукциона
            auctionCompleted1 = receiveMsgAboutAuctionCompleted(myAgent);

            // Принятие сообщения о начале нового аукциона
            String newAuction = receiveMsgAboutNewAuction(myAgent);
            if (newAuction!=null){
                // Выставляем auctionCompleted1 = null, чтобы Consumer1
                // смог принимать цены и делать ставки
                // (потом после принятия об окончании текущего аукциона
                // auctionCompleted1 снова будет !=null
                auctionCompleted1 = null;
            }

        } else if (myAgent.getLocalName().equals("Consumer2") && auctionCompleted2 == null) {
            receivingPriceList(myAgent, "WindPriceList", flagWind);
            receivingPriceList(myAgent, "PVPriceList", flagPV);

            // Принятие сообщений от дистрибьтора об оконччании аукциона
            auctionCompleted2 = receiveMsgAboutAuctionCompleted(myAgent);

            // Принятие сообщения о начале нового аукциона
            String newAuction = receiveMsgAboutNewAuction(myAgent);
            if (newAuction!=null){
                // Выставляем auctionCompleted1 = null, чтобы Consumer1
                // смог принимать цены и делать ставки
                // (потом после принятия об окончании текущего аукциона
                // auctionCompleted1 снова будет !=null
                auctionCompleted2 = null;
            }

        } else if (myAgent.getLocalName().equals("Consumer3") && auctionCompleted3 == null){
            receivingPriceList(myAgent, "WindPriceList", flagWind);
            receivingPriceList(myAgent, "PVPriceList", flagPV);

            // Принятие сообщений от дистрибьтора об оконччании аукциона
            auctionCompleted3 = receiveMsgAboutAuctionCompleted(myAgent);

            // Принятие сообщения о начале нового аукциона
            String newAuction = receiveMsgAboutNewAuction(myAgent);
            if (newAuction!=null){
                // Выставляем auctionCompleted1 = null, чтобы Consumer1
                // смог принимать цены и делать ставки
                // (потом после принятия об окончании текущего аукциона
                // auctionCompleted1 снова будет !=null
                auctionCompleted3 = null;
            }
        }

    }

    private void receivingPriceList(Agent consumerName, String protocol, boolean flagBet){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(protocol);
        ACLMessage msg = consumerName.receive(messageTemplate);


        if (msg!=null /*&& flagBet*/){
            System.out.println(consumerName.getLocalName() + " received price " +
                    msg.getContent() + " from " + msg.getSender().getLocalName());

            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
            reply.addReceiver(msg.getSender());
            reply.setProtocol("Bets");
            reply.setContent(String.valueOf(bet));

            // Сложим в datastore ставку для каждого потребителя
            getDataStore().put(consumerName.getLocalName() + "bet", bet);

            consumerName.send(reply);
            System.out.println(consumerName.getLocalName() + " send bet " +
                    bet + " to " + msg.getSender().getLocalName());
            flagBet = false;
        }else {
            block();
        }

    }

    // TODO: Метод принятия сообщения о завершении аукциона от дистрибьютора
    //  , чтобы завершить принятие сообщений о уенах и отправку ставок
    private String receiveMsgAboutAuctionCompleted(Agent consumer){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("AuctionCompleted");
        ACLMessage msg = consumer.receive(messageTemplate);

        if (msg!=null){

            auctionCompleted = msg.getContent();
            System.out.println(consumer.getLocalName() +
                    " receive " + auctionCompleted +
                    " from " + msg.getSender().getLocalName());

            // Потребитель отправляет ответ, что получил
            // сообщение о завершении аукциона
            sendMsg(consumer, "ReceiveAuctionCompleted",
                    msg.getSender().getLocalName(),"ReceiveAuctionCompleted");

        }else {
            block();
        }

        return  auctionCompleted;
    }

    // TODO: Метод принятия сообщения о начале нового аукциона
    //  чтобы потребители снова начали принимать цены и делать ставки
    private String receiveMsgAboutNewAuction(Agent consumer){

        // Принятие сообщения о старте нового аукциона от WindCounter
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("StartNewWindAuction");
        ACLMessage msg = consumer.receive(messageTemplate);

        // Принятие сообщение о старте нового аукциона от PVCounter
        MessageTemplate messageTemplate1 = MessageTemplate.MatchProtocol("StartNewPVAuction");
        ACLMessage msg2 = consumer.receive(messageTemplate1);


        if (msg!=null && msg2!=null){
            auctionCompleted = msg.getContent();
            System.out.println(consumer.getLocalName() + " receive " +
                    msg.getContent() +
                    " from " + msg.getSender().getLocalName());
        }else {
            block();
        }

        return auctionCompleted;
    }


    private void sendMsg(Agent sender, String protocol, String receiver, String content){

        AID aid = new AID(receiver, false);

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(aid);
        msg.setProtocol(protocol);
        msg.setContent(content);
        sender.send(msg);
        System.out.println();
        System.out.println(sender.getLocalName() + " send " + msg.getContent() +
                " to " + receiver);
        System.out.println();
    }
}
