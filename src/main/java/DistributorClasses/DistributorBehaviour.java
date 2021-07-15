package DistributorClasses;

import MQTTConnection.MQTTSubscriber;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

public class DistributorBehaviour extends TickerBehaviour {

    private List<String> isConcluded;
    private String consumerName;

    private String fromGenerator;

    // Массив, те кто принял сообщение о завершении аукциона
    private List<String> replyAuctionCompleted;

    // Флаг принятия сообщения о старте нового аукциона
    private boolean oneTime;

    // Переиенная времени из Матлаба
    private double time;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private MQTTSubscriber mqttSubscriber = new MQTTSubscriber();

    public DistributorBehaviour(Agent a, long period, List<String> isConcluded,
                                List<String> replyAuctionCompleted,
                                boolean oneTime) {
        super(a, period);
        this.isConcluded = isConcluded;
        this.replyAuctionCompleted = replyAuctionCompleted;
        this.oneTime = oneTime;
    }

    @Override
    protected void onTick() {

        receiveMsg(myAgent, "WindToDistributor");
        receiveMsg(myAgent, "PVToDistributor");

        receiveTimeFromMatlab();

        //auctionCompleted();

    }

    private void receiveMsg(Agent receiver, String protocol){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(protocol);
        ACLMessage msg = receiver.receive(messageTemplate);

        if (msg!=null){
            consumerName = msg.getContent();
            System.out.println();
            System.out.println(receiver.getLocalName() + " receive " +
                    consumerName);
            System.out.println();

            // Если в массиве еще нет имени этого потребителя, то производитель энергии
            // может заключить с ним контракт
            if (!isConcluded.contains(consumerName) && !isConcluded.contains(msg.getSender().getLocalName())){
                // добавляем в массив имя потребителя с кем заключают контракт
                isConcluded.add(consumerName);
                // добавляем в массив имя генератора, который заключаетт контракт
                isConcluded.add(msg.getSender().getLocalName());
                sendMsg(myAgent, msg.getSender(), "MsgFromDistributor", consumerName);
            }else {
                System.out.println("Already conclude!");
                // Сообщение о завершении аукциона получат не заключившие контракты
                // и потребители
                auctionCompleted(msg.getSender());
                // Принятие сообщений о завершении аукциона
                receiveReplyAboutAucCompleted();
                // Принятие сообщений о старте нового аукциона
                receiveMsgAboutNewAuction();

                // TODO: Верное! принятие сообщения о том
                //  , что генераторы могут начинать
                //  новый аукцион
                receiveMsgNewAuction("NewWindAuction");
                receiveMsgNewAuction("NewPVAuction");



            }
        }else {
            block();
        }

    }

    private void sendMsg(Agent sender, AID receiver, String protocol, String content) {

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(receiver);
        msg.setProtocol(protocol);
        msg.setContent(content);
        sender.send(msg);
        System.out.println();
        System.out.println(sender.getLocalName() + " send " +
                msg.getContent() + " to " + receiver.getLocalName());
        System.out.println();
    }

    // TODO: Если в массиве isConcluded уже есть все потребители, то
    //  дистрибьютор отправляет сообщение всем генераторам, что все потребители расторговались -
    //  - аукцион завершен!
    private void auctionCompleted(AID notConcluded) {


        if (isConcluded.contains("Consumer1") && isConcluded.contains("Consumer2")
        && isConcluded.contains("Consumer3")) {

            // Проверка, если receiver есть в массиве replyAuctionCompleted,
            // то больше не отправляем ему сообщение
            if (!replyAuctionCompleted.contains(notConcluded.getLocalName())) {
                sendMsg(myAgent, notConcluded, "AuctionCompleted", "AuctionCompleted");
            }
            // Также отправляем сообщения о завершении аукциона
            // всем потребителям, чтобы те перестали принимать цены и делать ставки
            AID consumer1 = new AID("Consumer1",false);
            AID consumer2 = new AID("Consumer2",false);
            AID consumer3 = new AID("Consumer3",false);

            if (!replyAuctionCompleted.contains(consumer1.getLocalName())) {
                sendMsg(myAgent, consumer1, "AuctionCompleted", "AuctionCompleted");
            }

            if (!replyAuctionCompleted.contains(consumer2.getLocalName())) {
                sendMsg(myAgent, consumer2, "AuctionCompleted", "AuctionCompleted");
            }

            if (!replyAuctionCompleted.contains(consumer3.getLocalName())) {
                sendMsg(myAgent, consumer3, "AuctionCompleted", "AuctionCompleted");
            }

        }
    }

    // TODO: Принятие ответа от генераторов о получении сообщения о завершении
    //  аукциона
    // Метод будет возвращать имя от кого пришел ответ о
    // получении сообщения о завершении аукциона
    // и это имя будет сравниваться с именем агента
    // в методе auctionCompleted,
    // если имена будут совпадать, то
    // сообщение больше отправлять не будем,
    // будем просто выводить сообщение, что агент получил сообщение о завершении аукциона
    // или будем отправлять null
    private void receiveReplyAboutAucCompleted(){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("ReceiveAuctionCompleted");
        ACLMessage msg = myAgent.receive(messageTemplate);

        if (msg!=null){

            fromGenerator = msg.getSender().getLocalName();

            // Добавляем в массив, от кого получили ответ о завершении аукциона
            replyAuctionCompleted.add(fromGenerator);

            System.out.println(myAgent.getLocalName() + " receive " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());

            // Выставим флаг о начале нового аукциона, как true,
            // чтобы дистрибьтор мог принимать сообщения
            oneTime = true;
        }else {
            block();
        }

    }


    // TODO: Дистрибьютор получает сообщение о старте нового аукциона,
    //  когда он получит сообщение о старте нового аукциона
    //  и от WInds и от PVs, то тогда он очищает все массивы
    private void receiveMsgAboutNewAuction(){

        // Получение сообщения от WindCounter
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("StartNewWindAuction");
        ACLMessage msg = myAgent.receive(messageTemplate);

        // Получение сообщения от PVCounter
        MessageTemplate messageTemplate1 = MessageTemplate.MatchProtocol("StartNewPVAuction");
        ACLMessage msg2 = myAgent.receive(messageTemplate1);

        // Ставим также флаг, чтобы дистриютбтор
        // принимал только один раз сообщение от генераторов
        if (msg!=null && msg2!=null && oneTime){
            System.out.println(myAgent.getLocalName() +
                    " receive " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());
            System.out.println(myAgent.getLocalName() +
                    " receive " + msg2.getContent() +
                    " from " + msg2.getSender().getLocalName());

            // После получения сообщений о старте новых аукционов
            // от WindCounter и от PVCounter дистриббьютор
            // очищает все массивы
            isConcluded.clear();
            replyAuctionCompleted.clear();

            oneTime = false;
        }else {
            block();
        }
    }


    // TODO: Дистрибьютор принимает сообщения от генераторов
    //  о начале нового аукциона и удаляет их имена из массива
    //  isConcluded
    private void receiveMsgNewAuction(String receiveProtocol){
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(receiveProtocol);
        ACLMessage msg = myAgent.receive(messageTemplate);

        if (msg!=null){
            System.out.println(myAgent.getLocalName() +
                    " receive " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());

            // После получения сообщени о том, что генератор
            // может начать новый аукцион
            // дистрибьютор удаляет его имя
            // из массива заключивших контракты

            isConcluded.remove(msg.getSender().getLocalName());

            // После удаления генератора из массива
            // дистрибьютор отправляет этому генератору
            // сообщение о том, что он может начинать новый аукицон
            AID aid = new AID(msg.getSender().getLocalName(),false);
            sendMsg(myAgent,aid,"CanStartNewAuction","CanStartNewAuction");

            // После удаления всехм генераторов
            // из массива isConcluded там остается
            // 3 потребителя, удаляем их из массива
            if (isConcluded.size() == 3){
                isConcluded.clear();
            }

        }else {
            block();
        }
    }

    // TODO: Дистрибьютор принимает время из Матлаба
    //  и рссылает всем участникам
    private void receiveTimeFromMatlab(){
        time = mqttSubscriber.mqttSubscriber("Time","Dist");
        AID aid = new AID("PVCounter1",false);
        AID aid1 = new AID("PVCounter2",false);
        AID aid2 = new AID("PVCounter3",false);
        AID aid3 = new AID("WindCounter1",false);
        AID aid4 = new AID("WindCounter2",false);
        AID aid5 = new AID("WindCounter3",false);
        AID aid6 = new AID("ConsumerCounter1",false);
        AID aid7 = new AID("ConsumerCounter2",false);
        AID aid8 = new AID("ConsumerCounter3",false);
        AID aid9 = new AID("StorageCounter1",false);
        AID aid10 = new AID("StorageCounter2",false);
        AID aid11 = new AID("StorageCounter3",false);
        sendMsg(myAgent,aid,"TimeFromMatlab", String.valueOf(time));
        sendMsg(myAgent,aid1,"TimeFromMatlab", String.valueOf(time));
        sendMsg(myAgent,aid2,"TimeFromMatlab", String.valueOf(time));
        sendMsg(myAgent,aid3,"TimeFromMatlab", String.valueOf(time));
        sendMsg(myAgent,aid4,"TimeFromMatlab", String.valueOf(time));
        sendMsg(myAgent,aid5,"TimeFromMatlab", String.valueOf(time));
        sendMsg(myAgent,aid6,"TimeFromMatlab", String.valueOf(time));
        sendMsg(myAgent,aid7,"TimeFromMatlab", String.valueOf(time));
        sendMsg(myAgent,aid8,"TimeFromMatlab", String.valueOf(time));
        sendMsg(myAgent,aid9,"TimeFromMatlab", String.valueOf(time));
        sendMsg(myAgent,aid10,"TimeFromMatlab", String.valueOf(time));
        sendMsg(myAgent,aid11,"TimeFromMatlab", String.valueOf(time));

    }

}
