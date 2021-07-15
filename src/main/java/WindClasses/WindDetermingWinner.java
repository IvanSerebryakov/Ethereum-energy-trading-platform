package WindClasses;

import ConnectionUDP.UDPClass;
import SmartContracts.Wind.WindContract;
import SmartContracts.Wind.WindPenaltyContract;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

import java.net.DatagramSocket;

public class WindDetermingWinner extends TickerBehaviour {

    private DataStore dataStore;
    private double price;
    private double bet;
    private String consumerName;

    // Соединение с Ganache
    private Web3j web3j;
    private ContractGasProvider contractGasProvider;

    // Открытые ключи ветрогенераторов
    private String PUBLIC_KEY_wind1;
    private String PUBLIC_KEY_wind2;
    private String PUBLIC_KEY_wind3;

    private Credentials credentials1;
    private Credentials credentials2;
    private Credentials credentials3;

    //Основной контракт
    private WindContract windContract;
    // Адреса основных контрактов
    private String deployedWindContractAddress;
    private String deployedWindContractAddress2;
    private String deployedWindContractAddress3;

    // штрафной контракт
    private WindPenaltyContract windPenaltyContract;
    // Адреса штрафных контрактов
    private String deployedWindPenaltyContractAddress;
    private String deployedWindPenaltyContractAddress2;
    private String deployedWindPenaltyContractAddress3;

    private UDPClass udpClass;

    // Порты Wind
    private int wind1DestPort = 10101;
    private int wind2DestPort = 10103;
    private int wind3DestPort = 10105;

    // Флаги соединений по UDp
    private boolean flagWind1;
    private boolean flagWind2;
    private boolean flagWind3;


    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    public WindDetermingWinner(Agent a, long period, DataStore dataStore,
                               Web3j web3j, ContractGasProvider contractGasProvider,
                               String PUBLIC_KEY_wind1, String PUBLIC_KEY_wind2, String PUBLIC_KEY_wind3,
                               Credentials credentials1, Credentials credentials2, Credentials credentials3,
                               String deployedWindContractAddress, String deployedWindContractAddress2,
                               String deployedWindContractAddress3, String deployedWindPenaltyContractAddress,
                               String deployedWindPenaltyContractAddress2, String deployedWindPenaltyContractAddress3,
                               UDPClass udpClass, boolean flagWind1, boolean flagWind2, boolean flagWind3) {
        super(a, period);
        this.dataStore = dataStore;
        setDataStore(dataStore);

        this.web3j = web3j;
        this.contractGasProvider = contractGasProvider;

        this.PUBLIC_KEY_wind1 = PUBLIC_KEY_wind1;
        this.PUBLIC_KEY_wind2 = PUBLIC_KEY_wind2;
        this.PUBLIC_KEY_wind3 = PUBLIC_KEY_wind3;

        this.credentials1 = credentials1;
        this.credentials2 = credentials2;
        this.credentials3 = credentials3;

        this.deployedWindContractAddress = deployedWindContractAddress;
        this.deployedWindContractAddress2 = deployedWindContractAddress2;
        this.deployedWindContractAddress3 = deployedWindContractAddress3;

        this.deployedWindPenaltyContractAddress = deployedWindPenaltyContractAddress;
        this.deployedWindPenaltyContractAddress2 = deployedWindPenaltyContractAddress2;
        this.deployedWindPenaltyContractAddress3 = deployedWindPenaltyContractAddress3;

        this.udpClass = udpClass;

        this.flagWind1 = flagWind1;
        this.flagWind2 = flagWind2;
        this.flagWind3 = flagWind3;

    }

    @Override
    protected void onTick() {

        determingWinner(myAgent);
        concludeContracts(myAgent);
    }

    private void determingWinner(Agent wind){
        // Принятие сообщение со ставкой от потребителя
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("Bets");
        ACLMessage msg = wind.receive(messageTemplate);

        if (msg!=null){
            bet = Double.parseDouble(msg.getContent());
            System.out.println();
            System.out.println(wind.getLocalName() +
                    " received bet " + bet +
                    " from " + msg.getSender().getLocalName());
            System.out.println();

            if (dataStore.get(wind.getLocalName() + "price")!=null) {

                price = Double.parseDouble(
                        String.valueOf(dataStore.get(wind.getLocalName() + "price")));
                // Если ставка от потребителя больше цены от Wind
                // этот Wind отправляет сообщение дистрибьютору,
                // с вопросом можно ли заключить контракт с этим потребителем
                if (price < bet){
                    sendMsg(wind, "WindToDistributor",
                            "Distributor", msg.getSender().getLocalName());

                    // Складываем ставку победителя в datastore для Wind
                    getDataStore().put(wind.getLocalName() + "bet", bet);

                    //System.out.println("DATA STORE with bet Wind " + getDataStore());

                }else {

                    // Если ставка от потребителя меньше цены от Wind,
                    // то этот генератор объявляет новый аукцион
                    // Принятие сообщения о завершении о аукцина - запрет на отправку цен и начале нового аукциона
                    ACLMessage msgCompleted = receiveMsgAboutAuctionFinished(wind);
                    if (msgCompleted!=null){
                        System.err.println(wind.getLocalName() + " receive msg " + msgCompleted.getContent());

                        // Если аукцион для этого агента Wind завершен, то этого агент Wind
                        // отправляет сообщение своему накопителю, нужна ли ему энергия
                        if (wind.getLocalName().equals("Wind1")) {
                            sendMsg(wind, "WindEnergyForStorage", "StorageCounter1", "NeedEnergy?");
                        } else if (wind.getLocalName().equals("Wind2")){
                            sendMsg(wind, "WindEnergyForStorage", "StorageCounter2", "NeedEnergy?");
                        } else {
                            sendMsg(wind, "WindEnergyForStorage", "StorageCounter3", "NeedEnergy?");
                        }

                    }else {
                        sendMsg(myAgent, "WindPriceList", msg.getSender().getLocalName(), String.valueOf(price));
                    }
//                    sendMsg(wind, "WindPriceList", msg.getSender().getLocalName(), String.valueOf(price));
                }

            }


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

    // Метод получения сообщения от дистрибьютора и заключение контракта
    private void concludeContracts(Agent receiver){
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("MsgFromDistributor");
        ACLMessage msg = receiver.receive(messageTemplate);

        if (msg!=null){
            consumerName = msg.getContent();
            System.out.println(receiver.getLocalName() + " receive msg " + consumerName +
                    " from " + msg.getSender().getLocalName());

            System.out.println(ANSI_YELLOW + receiver.getLocalName() + " conclude contract with " +
                    consumerName + ANSI_RESET);

            try {

            // TODO: Wind развертывает контракт
            if (receiver.getLocalName().equals("Wind1")) {


                deployedWindContractAddress = WindContract.deploy(web3j, credentials1, contractGasProvider)
                        .send()
                        .getContractAddress();
                System.out.println(receiver.getLocalName() + " deploy contract with address " +
                        deployedWindContractAddress);

                deployedWindPenaltyContractAddress = WindPenaltyContract.deploy(web3j, credentials1, contractGasProvider)
                        .send()
                        .getContractAddress();
                System.out.println(receiver.getLocalName() + " deploy penalty contract with address " +
                        deployedWindPenaltyContractAddress);

                // добавление в datastore адресов контарктов
                getDataStore().put(receiver.getLocalName() + "address",deployedWindContractAddress);
                getDataStore().put(receiver.getLocalName() + "penaltyAddress", deployedWindPenaltyContractAddress);


            }else if (receiver.getLocalName().equals("Wind2")){

                deployedWindContractAddress2 = WindContract.deploy(web3j, credentials2, contractGasProvider)
                        .send()
                        .getContractAddress();
                System.out.println(receiver.getLocalName() + " deploy contract with address " +
                        deployedWindContractAddress2);

                deployedWindPenaltyContractAddress2 = WindPenaltyContract.deploy(web3j, credentials2, contractGasProvider)
                        .send()
                        .getContractAddress();
                System.out.println(receiver.getLocalName() + " deploy penalty contract with address " +
                        deployedWindPenaltyContractAddress2);

                // добавление в datastore адресов контарктов
                getDataStore().put(receiver.getLocalName() + "address",deployedWindContractAddress2);
                getDataStore().put(receiver.getLocalName() + "penaltyAddress", deployedWindPenaltyContractAddress2);


            } else {

                deployedWindContractAddress3 = WindContract.deploy(web3j, credentials3, contractGasProvider)
                        .send()
                        .getContractAddress();
                System.out.println(receiver.getLocalName() + " deploy contract with address " +
                        deployedWindContractAddress3);

                deployedWindPenaltyContractAddress3 = WindContract.deploy(web3j, credentials3, contractGasProvider)
                        .send()
                        .getContractAddress();
                System.out.println(receiver.getLocalName() + " deploy penalty contract with address " +
                        deployedWindPenaltyContractAddress3);

                // добавление в datastore адресов контарктов
                getDataStore().put(receiver.getLocalName() + "address",deployedWindContractAddress3);
                getDataStore().put(receiver.getLocalName() + "penaltyAddress", deployedWindPenaltyContractAddress3);
            }


            // TODO: Wind отправляет адреса контрактов необходимым участникам
                if (deployedWindContractAddress!=null && deployedWindPenaltyContractAddress!=null){

                    sendMsg(receiver, "WindDeployedContractAddress",
                            consumerName, deployedWindContractAddress);
                    sendMsg(receiver, "WindPenaltyDeployedContractAddress",
                            consumerName, deployedWindPenaltyContractAddress);

                    sendMsg(receiver, "WindDeployedContractAddress",
                            "WindCounter1", deployedWindContractAddress);
                    sendMsg(receiver, "WindPenaltyDeployedContractAddress",
                            "WindCounter1", deployedWindPenaltyContractAddress);

                    sendMsg(receiver, "WindDeployedContractAddress",
                            "Storage1", deployedWindContractAddress);
                    sendMsg(receiver, "WindPenaltyDeployedContractAddress",
                            "Storage1", deployedWindPenaltyContractAddress);

                    // отправка сигнала в Матлаб
                    sendingToMatlab(wind1DestPort, flagWind1);

                } else if (deployedWindContractAddress2!=null && deployedWindPenaltyContractAddress2!=null){

                    sendMsg(receiver, "WindDeployedContractAddress",
                            consumerName, deployedWindContractAddress2);
                    sendMsg(receiver, "WindPenaltyDeployedContractAddress",
                            consumerName, deployedWindPenaltyContractAddress2);

                    sendMsg(receiver, "WindDeployedContractAddress",
                            "WindCounter2", deployedWindContractAddress2);
                    sendMsg(receiver, "WindPenaltyDeployedContractAddress",
                            "WindCounter2", deployedWindPenaltyContractAddress2);

                    sendMsg(receiver, "WindDeployedContractAddress",
                            "Storage2", deployedWindContractAddress2);
                    sendMsg(receiver, "WindPenaltyDeployedContractAddress",
                            "Storage2", deployedWindPenaltyContractAddress2);

                    // отправка сигнала в Матлаб
                    sendingToMatlab(wind2DestPort, flagWind2);

                }else if (deployedWindContractAddress3!=null && deployedWindPenaltyContractAddress3!=null){

                    sendMsg(receiver, "WindDeployedContractAddress",
                            consumerName, deployedWindContractAddress3);
                    sendMsg(receiver, "WindPenaltyDeployedContractAddress",
                            consumerName, deployedWindPenaltyContractAddress3);

                    sendMsg(receiver, "WindDeployedContractAddress",
                            "WindCounter3", deployedWindContractAddress3);
                    sendMsg(receiver, "WindPenaltyDeployedContractAddress",
                            "WindCounter3", deployedWindPenaltyContractAddress3);

                    sendMsg(receiver, "WindDeployedContractAddress",
                            "Storage3", deployedWindContractAddress3);
                    sendMsg(receiver, "WindPenaltyDeployedContractAddress",
                            "Storage3", deployedWindPenaltyContractAddress3);

                    // отправка сигнала в Матлаб
                    sendingToMatlab(wind3DestPort, flagWind3);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            block();
        }
    }


    // TODO: Метод для отправки сигнала в Матлаб о старте передачи сигнала в систему
    private void sendingToMatlab(int port, boolean flag){

        if (flag) {
            // Установка соединения
            DatagramSocket datagramSocket = udpClass.connectionUDP(port + 200);
            udpClass.sendingUDP(datagramSocket, port, "1");

            flag = false;
        }

    }

    // TODO: Метод принятия сообщения о завершении аукциона
    private ACLMessage receiveMsgAboutAuctionFinished(Agent wind){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("AuctionCompleted");
        ACLMessage msg = wind.receive(messageTemplate);

        if (msg!=null){

            System.out.println(wind.getLocalName() + " receive msg : " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());

        }else {
            block();
        }

        return msg;
    }
}
