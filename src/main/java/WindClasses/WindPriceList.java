package WindClasses;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class WindPriceList extends TickerBehaviour {

    private double price = 40000 + Math.random() * 20000;

    private DataStore dataStore;

    // Флаг об окончании аукциона - запрет отправки цен
    private boolean auctionCompleted1;
    private boolean auctionCompleted2;
    private boolean auctionCompleted3;

    private String startNewAuction;
    private String startNewAuctionWind1;
    private String startNewAuctionWind2;
    private String startNewAuctionWind3;

    // Изменение цвета фона вывода текста
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    // Изменение цвета вывода текста
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";



    public WindPriceList(Agent a, long period, DataStore dataStore, boolean auctionCompleted1,
                         boolean auctionCompleted2, boolean auctionCompleted3, String startNewAuction,
                         String startNewAuctionWind1, String startNewAuctionWind2,
                         String startNewAuctionWind3) {
        super(a, period);
        this.dataStore = dataStore;
        setDataStore(dataStore);

        this.auctionCompleted1 = auctionCompleted1;
        this.auctionCompleted2 = auctionCompleted2;
        this.auctionCompleted3 = auctionCompleted3;

        this.startNewAuction = startNewAuction;
        this.startNewAuctionWind1 = startNewAuctionWind1;
        this.startNewAuctionWind2 = startNewAuctionWind2;
        this.startNewAuctionWind3 = startNewAuctionWind3;
    }

    @Override
    protected void onTick() {

        if (myAgent.getLocalName().equals("Wind1")) {

            // Отправка сообщения о цене на энергию (блокируется при получении сообщения о завершении аукциона)
            if (auctionCompleted1) {
                windPriceList(myAgent);
            }

            // Принятие сообщения о старте нового аукицона
            startNewAuctionWind1 = newAuction(myAgent);

            ACLMessage msg = receiveMsgAboutAuctionFinished(myAgent);
            if (msg!=null && startNewAuctionWind1 == null){
                auctionCompleted1 = false;
                System.err.println(myAgent.getLocalName() + " received " + msg.getContent());
            }else {
//                windPriceList(myAgent);
                // Выставляем флаг true, чтобы ветряки снова отправляли цены
                auctionCompleted1 = true;
                // Присваиваем переменной startNewAuctionWind1 = null,
                // чтобы потом принять сообщение о старте нового аукциона
                startNewAuctionWind1 = null;
            }
//            windPriceList(myAgent);

            // Принятие сообщения о начале нового аукциона
            receiveFromDistributorStartAuction(myAgent);

        } else if (myAgent.getLocalName().equals("Wind2")) {

            if (auctionCompleted2) {
                windPriceList(myAgent);
            }

            // Принятие сообщения о старте нового аукицона
            startNewAuctionWind2 = newAuction(myAgent);

            ACLMessage msg = receiveMsgAboutAuctionFinished(myAgent);
            if (msg!=null && startNewAuctionWind2 == null){
                auctionCompleted2 = false;
                System.err.println(myAgent.getLocalName() + " received " + msg.getContent());
            }else {
                //windPriceList(myAgent);
                auctionCompleted2 = true;
                startNewAuctionWind2 = null;
            }
//            windPriceList(myAgent);

            // Принятие сообщения о начале нового аукциона
            receiveFromDistributorStartAuction(myAgent);

        } else {

            if (auctionCompleted3) {
                windPriceList(myAgent);
            }

            // Принятие сообщения о старте нового аукицона
            startNewAuctionWind3 = newAuction(myAgent);

            ACLMessage msg = receiveMsgAboutAuctionFinished(myAgent);
            if (msg!=null && startNewAuctionWind3 == null){
                auctionCompleted3 = false;

                System.err.println(myAgent.getLocalName() + " received " + msg.getContent());
            }else {
                //windPriceList(myAgent);
                auctionCompleted3 = true;
                startNewAuctionWind3 = null;
            }

            // Принятие сообщения о начале нового аукциона
            receiveFromDistributorStartAuction(myAgent);
//            windPriceList(myAgent);
        }

    }


    // TODO: Метод отправки цен от каждого агента Wind
    private void windPriceList(Agent wind){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("HelloFromConsumers");
        ACLMessage msg = wind.receive(messageTemplate);

        if (msg!=null){
            System.out.println(wind.getLocalName() + " receive " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());

            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
            reply.addReceiver(msg.getSender());
            reply.setProtocol("WindPriceList");
            reply.setContent(String.valueOf(price));

            getDataStore().put(wind.getLocalName() + "price",price);

            wind.send(reply);
            System.out.println(wind.getLocalName() + " send price " + reply.getContent() +
                    " to " + msg.getSender().getLocalName());
        }else {
            block();
        }

    }


    // TODO: Метод принятия сообщения о завершении аукциона
    private ACLMessage receiveMsgAboutAuctionFinished(Agent wind){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("AuctionCompleted");
        ACLMessage msg = wind.receive(messageTemplate);

        if (msg!=null){

            System.err.println(ANSI_PURPLE + wind.getLocalName() + " receive msg : " + msg.getContent() +
                    " from " + msg.getSender().getLocalName() + ANSI_RESET);

            // Ветряк после получения сообщения о завершении аукциона
            // отпрвляет ответ дистрибьтору, что
            // получил сообщение о завершении аукциона, чтобы
            // больше это сообщение не получать
            sendMsg(wind,"ReceiveAuctionCompleted",
                    msg.getSender().getLocalName(),"ReceiveAuctionCompleted");

        }else {
            block();
        }

        return msg;
    }

    // TODO: Метод принятия сообщения от счетчика, что начались новые сутки -
    //  - начинаем новый аукцион - выставляем флаг true, блокируем принятие сообщений о завершении аукциона
    private String newAuction(Agent wind){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("StartNewWindAuction");
        ACLMessage msg = wind.receive(messageTemplate);

        if (msg!=null){

            startNewAuction = msg.getContent();
            System.out.println(ANSI_PURPLE + wind.getLocalName() + " receive " + startNewAuction +
                    " from " + msg.getSender().getLocalName() + ANSI_RESET);
        }else {
            block();
        }

        return startNewAuction;
    }


    // TODO: Принятие сообщения от дистрибьютора,
    //  о том, что можно начинать новый аукицион
    private void receiveFromDistributorStartAuction(Agent wind){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("CanStartNewAuction");
        ACLMessage msg = wind.receive(messageTemplate);

        if (msg!=null){
            System.out.println(wind.getLocalName() +
                    " receive " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());

            // После принятия сообщения о старте нового аукциона генератор
            // начинает отправлять цены потребителю
            sendMsg(wind,"WindPriceList",
                    "Consumer1", String.valueOf(price));
            sendMsg(wind,"WindPriceList",
                    "Consumer2", String.valueOf(price));
            sendMsg(wind,"WindPriceList",
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
        System.out.println(ANSI_PURPLE + sender.getLocalName() + " send " + msg.getContent() +
                " to " + receiver + ANSI_RESET);
        System.out.println();
    }
}
