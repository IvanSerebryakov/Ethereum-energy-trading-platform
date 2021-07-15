package PVClasses;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PVPriceList extends TickerBehaviour {

    private double price = 30000 + Math.random() * 20000;

    private DataStore dataStore;

    public PVPriceList(Agent a, long period, DataStore dataStore) {
        super(a, period);
        this.dataStore = dataStore;
        setDataStore(dataStore);
    }

    @Override
    protected void onTick() {

        if (myAgent.getLocalName().equals("PV1")){
            ACLMessage msg = receiveMsgAboutAuctionFinished(myAgent);
            if (msg!=null){
                System.err.println(myAgent.getLocalName() + " receive msg " + msg.getContent());
            }else {
                pvPriceList(myAgent);
            }

            // Принятие сообщения о начале нового аукциона
            receiveFromDistributorStartAuction(myAgent);
//            pvPriceList(myAgent);
        } else if (myAgent.getLocalName().equals("PV2")){

            ACLMessage msg = receiveMsgAboutAuctionFinished(myAgent);
            if (msg!=null){
                System.err.println(myAgent.getLocalName() + " receive msg " + msg.getContent());
            }else {
                pvPriceList(myAgent);
            }

            // Принятие сообщения о начале нового аукциона
            receiveFromDistributorStartAuction(myAgent);
//            pvPriceList(myAgent);
        } else {

            ACLMessage msg = receiveMsgAboutAuctionFinished(myAgent);
            if (msg!=null){
                System.err.println(myAgent.getLocalName() + " receive msg " + msg.getContent());
            } else {
                pvPriceList(myAgent);
            }

            // Принятие сообщения о начале нового аукциона
            receiveFromDistributorStartAuction(myAgent);
//            pvPriceList(myAgent);
        }

    }

    // TODO: Метод отправки цен от каждого агента PV
    private void pvPriceList(Agent pv){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("HelloFromConsumers");
        ACLMessage msg = pv.receive(messageTemplate);

        if (msg!=null){
            System.out.println(pv.getLocalName() + " receive " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());

            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
            reply.addReceiver(msg.getSender());
            reply.setProtocol("PVPriceList");
            reply.setContent(String.valueOf(price));

            getDataStore().put(pv.getLocalName() + "price", price);

            pv.send(reply);
            System.out.println(pv.getLocalName() + " send price " + reply.getContent() +
                    " to " + msg.getSender().getLocalName());
        }else {
            block();
        }
    }

    // TODO: Метод принятия сообщения о завершении аукциона
    private ACLMessage receiveMsgAboutAuctionFinished(Agent pv){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("AuctionCompleted");
        ACLMessage msg = pv.receive(messageTemplate);

        if (msg!=null){

            System.out.println(pv.getLocalName() + " receive msg : " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());

            // После получения сообщщения о завершении аукциона
            //PV генераторы оотписываются дистрибьютору, что получили сообщение о
            // завершении аукциона
            sendMsg(pv, "ReceiveAuctionCompleted",
                    msg.getSender().getLocalName(),"ReceiveAuctionCompleted");

        }else {
            block();
        }

        return msg;
    }

    // TODO: Метод принятия сообщения от счетчика, что начались новые сутки -
    //  - начинаем новый аукцион - выставляем флаг true, блокируем принятие сообщений о завершении аукциона
    //  ( или выставить флаг false на принятие сообщений о завершении аукциона)
    private void newPVAuction(Agent pv){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("StartNewPVAuction");
        ACLMessage msg = pv.receive(messageTemplate);

        if (msg!=null){
            System.out.println(pv.getLocalName() + " receive " +
                    msg.getContent() + " from " +
                    msg.getSender().getLocalName());
        }else {
            block();
        }

    }

    // TODO: Принятие сообщения от дистрибьютора,
    //  о том, что можно начинать новый аукицион
    private void receiveFromDistributorStartAuction(Agent pv){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("CanStartNewAuction");
        ACLMessage msg = pv.receive(messageTemplate);

        if (msg!=null){
            System.out.println(pv.getLocalName() +
                    " receive " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());

            // После принятия сообщения о старте нового аукциона генератор
            // начинает отправлять цены потребителю
            sendMsg(pv,"PVPriceList",
                    "Consumer1", String.valueOf(price));
            sendMsg(pv,"PVPriceList",
                    "Consumer2", String.valueOf(price));
            sendMsg(pv,"PVPriceList",
                    "Consumer3", String.valueOf(price));
        }else {
            block();
        }
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
