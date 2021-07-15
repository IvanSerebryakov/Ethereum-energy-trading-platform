package PVClasses;

import ConnectionUDP.UDPClass;
import SmartContracts.PV.PVContract;
import SmartContracts.PV.PVPenaltyContract;
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

public class PVDetermingWinner extends TickerBehaviour {

    private DataStore dataStore;
    private double price;
    private double bet;
    private String consumerName;

    private Web3j web3j;
    private ContractGasProvider contractGasProvider;

    // Открытые ключи PV генераторов
    private String PUBLIC_KEY_PV1;
    private String PUBLIC_KEY_PV2;
    private String PUBLIC_KEY_PV3;

    private Credentials credentialsPV1;
    private Credentials credentialsPV2;
    private Credentials credentialsPV3;

    private UDPClass udpClass;

    // Порты PV
    private int pv1DestPort = 10107;
    private int pv2DestPort = 10109;
    private int pv3DestPort = 10111;

    // Флаги соединений UDp
    private boolean flagPV1;
    private boolean flagPV2;
    private boolean flagPV3;

    // Адреса основного контракта PV
    private String deployedPVContractAddress;
    private String deployedPVContractAddress2;
    private String deployedPVContractAddress3;

    // штрафной контракт
    private PVPenaltyContract pvPenaltyContract;
    // Адреса штрафных контрактов
    private String deployedPVPenaltyContractAddress;
    private String deployedPVPenaltyContractAddress2;
    private String deployedPVPenaltyContractAddress3;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public PVDetermingWinner(Agent a, long period, DataStore dataStore, Web3j web3j,
                             ContractGasProvider contractGasProvider, String PUBLIC_KEY_PV1,
                             String PUBLIC_KEY_PV2, String PUBLIC_KEY_PV3,
                             Credentials credentialsPV1, Credentials credentialsPV2,
                             Credentials credentialsPV3, String deployedPVContractAddress,
                             String deployedPVContractAddress2, String deployedPVContractAddress3,
                             String deployedPVPenaltyContractAddress, String deployedPVPenaltyContractAddress2,
                             String deployedPVPenaltyContractAddress3, UDPClass udpClass,
                             boolean flagPV1, boolean flagPV2, boolean flagPV3) {
        super(a, period);
        this.dataStore = dataStore;
        setDataStore(dataStore);

        this.web3j = web3j;
        this.contractGasProvider = contractGasProvider;
        this.PUBLIC_KEY_PV1 = PUBLIC_KEY_PV1;
        this.PUBLIC_KEY_PV2 = PUBLIC_KEY_PV2;
        this.PUBLIC_KEY_PV3 = PUBLIC_KEY_PV3;

        this.credentialsPV1 = credentialsPV1;
        this.credentialsPV2 = credentialsPV2;
        this.credentialsPV3 = credentialsPV3;

        this.deployedPVContractAddress = deployedPVContractAddress;
        this.deployedPVContractAddress2 = deployedPVContractAddress2;
        this.deployedPVContractAddress3 = deployedPVContractAddress3;

        this.deployedPVPenaltyContractAddress = deployedPVPenaltyContractAddress;
        this.deployedPVPenaltyContractAddress2 = deployedPVPenaltyContractAddress2;
        this.deployedPVPenaltyContractAddress3 = deployedPVPenaltyContractAddress3;

        this.udpClass = udpClass;

        this.flagPV1 = flagPV1;
        this.flagPV2 = flagPV2;
        this.flagPV3 = flagPV3;

    }

    @Override
    protected void onTick() {

        determingWinner(myAgent);
        concludeContracts(myAgent);

    }

    private void determingWinner(Agent pv){
        // Принятие сообщение со ставкой от потребителя
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("Bets");
        ACLMessage msg = pv.receive(messageTemplate);

        if (msg!=null){
            bet = Double.parseDouble(msg.getContent());
            System.out.println();
            System.out.println(pv.getLocalName() +
                    " received bet " + bet +
                    " from " + msg.getSender().getLocalName());
            System.out.println();

            if (dataStore.get(pv.getLocalName() + "price")!=null) {

                price = Double.parseDouble(
                        String.valueOf(dataStore.get(pv.getLocalName() + "price")));
                // Если ставка от потребителя больше цены от PV
                // этот Wind отправляет сообщение дистрибьютору,
                // с вопросом можно ли заключить контракт с этим потребителем
                if (price < bet){
                    sendMsg(pv, "PVToDistributor",
                            "Distributor", msg.getSender().getLocalName());

                    // Если consumer - победитель, складываем его ставку в datastore PV
                    // По имени PV будем вызывать ставку победителя для этого PV
                    getDataStore().put(pv.getLocalName() + "bet", bet);

                    //System.out.println("DATA STORE with bet PV " + getDataStore());

                } else {

                    ACLMessage msgCompleted = receiveMsgAboutAuctionFinished(pv);
                    if (msgCompleted!=null) {
                        System.err.println(pv.getLocalName() + " receive msg " + msgCompleted.getContent());

                        // Если аукцион для этого агента PV завершен, то этого агент PV
                        // отправляет сообщение своему счетчику накопителя, нужна ли ему энергия
                        if (pv.getLocalName().equals("PV1")) {
                            sendMsg(pv, "PVEnergyForStorage", "StorageCounter1", "NeedEnergy?");
                        } else if (pv.getLocalName().equals("PV2")){
                            sendMsg(pv, "PVEnergyForStorage", "StorageCounter2", "NeedEnergy?");
                        } else {
                            sendMsg(pv, "PVEnergyForStorage", "StorageCounter3", "NeedEnergy?");
                        }

                    }else {
                        // Если ставка от потребителя меньше цены от PV,
                        // то этот генератор объявляет новый аукцион
                        sendMsg(pv, "PVPriceList", msg.getSender().getLocalName(), String.valueOf(price));
                    }

//                    sendMsg(pv, "PVPriceList", msg.getSender().getLocalName(), String.valueOf(price));

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

            // TODO: PV развертывает контракт
            try {
                if (receiver.getLocalName().equals("PV1")) {

                    deployedPVContractAddress = PVContract.deploy(web3j, credentialsPV1, contractGasProvider)
                            .send()
                            .getContractAddress();
                    System.out.println(receiver.getLocalName() + " deploy contract with address " +
                            deployedPVContractAddress);

                    deployedPVPenaltyContractAddress = PVPenaltyContract.deploy(web3j, credentialsPV1, contractGasProvider)
                            .send()
                            .getContractAddress();
                    System.out.println(receiver.getLocalName() + " deploy penalty contract with address " +
                            deployedPVPenaltyContractAddress);

                    // Складываем адрес контрактов в datastore
                    getDataStore().put(receiver.getLocalName() + "address", deployedPVContractAddress);
                    getDataStore().put(receiver.getLocalName() + "penaltyAddress", deployedPVPenaltyContractAddress);



                } else if (receiver.getLocalName().equals("PV2")){

                    deployedPVContractAddress2 = PVContract.deploy(web3j, credentialsPV2, contractGasProvider)
                            .send()
                            .getContractAddress();
                    System.out.println(receiver.getLocalName() + " deploy contract with address " +
                            deployedPVContractAddress2);

                    deployedPVPenaltyContractAddress2 = PVPenaltyContract.deploy(web3j, credentialsPV2, contractGasProvider)
                            .send()
                            .getContractAddress();
                    System.out.println(receiver.getLocalName() + " deploy penalty contract with address " +
                            deployedPVPenaltyContractAddress2);

                    // Складываем адрес контрактов в datastore
                    getDataStore().put(receiver.getLocalName() + "address", deployedPVContractAddress2);
                    getDataStore().put(receiver.getLocalName() + "penaltyAddress", deployedPVPenaltyContractAddress2);

                }else {

                    deployedPVContractAddress3 = PVContract.deploy(web3j, credentialsPV3, contractGasProvider)
                            .send()
                            .getContractAddress();
                    System.out.println(receiver.getLocalName() + " deploy contract with address " +
                            deployedPVContractAddress3);

                    deployedPVPenaltyContractAddress3 = PVPenaltyContract.deploy(web3j, credentialsPV3, contractGasProvider)
                            .send()
                            .getContractAddress();
                    System.out.println(receiver.getLocalName() + " deploy penalty contract with address " +
                            deployedPVPenaltyContractAddress3);

                    // Складываем адрес контрактов в datastore
                    getDataStore().put(receiver.getLocalName() + "address", deployedPVContractAddress3);
                    getDataStore().put(receiver.getLocalName() + "penaltyAddress", deployedPVPenaltyContractAddress3);

                }

                // TODO: PV отправляет адреса контрактов необходимым участникам
                if (deployedPVContractAddress!=null && deployedPVPenaltyContractAddress!=null){

                    sendMsg(receiver, "PVDeployedContractAddress",
                            consumerName, deployedPVContractAddress);
                    sendMsg(receiver, "PVPenaltyDeployedContractAddress",
                            consumerName, deployedPVPenaltyContractAddress);

                    sendMsg(receiver, "PVDeployedContractAddress",
                            "PVCounter1", deployedPVContractAddress);
                    sendMsg(receiver, "PVPenaltyDeployedContractAddress",
                            "PVCounter1", deployedPVPenaltyContractAddress);

                    sendMsg(receiver, "PVDeployedContractAddress",
                            "Storage1", deployedPVContractAddress);
                    sendMsg(receiver, "PVPenaltyDeployedContractAddress",
                            "Storage1", deployedPVPenaltyContractAddress);

                    // отправка сигнала в Матлаб
                    sendingToMatlab(pv1DestPort, flagPV1);

                }else if (deployedPVContractAddress2!=null && deployedPVPenaltyContractAddress2!=null){

                    sendMsg(receiver, "PVDeployedContractAddress",
                            consumerName, deployedPVContractAddress2);
                    sendMsg(receiver, "PVPenaltyDeployedContractAddress",
                            consumerName, deployedPVPenaltyContractAddress2);

                    sendMsg(receiver, "PVDeployedContractAddress",
                            "PVCounter2", deployedPVContractAddress2);
                    sendMsg(receiver, "PVPenaltyDeployedContractAddress",
                            "PVCounter2", deployedPVPenaltyContractAddress2);

                    sendMsg(receiver, "PVDeployedContractAddress",
                            "Storage2", deployedPVContractAddress2);
                    sendMsg(receiver, "PVPenaltyDeployedContractAddress",
                            "Storage2", deployedPVPenaltyContractAddress2);

                    // отправка сигнала в Матлаб
                    sendingToMatlab(pv2DestPort, flagPV2);

                } else if (deployedPVContractAddress3!=null && deployedPVPenaltyContractAddress3!=null){

                    sendMsg(receiver, "PVDeployedContractAddress",
                            consumerName, deployedPVContractAddress3);
                    sendMsg(receiver, "PVPenaltyDeployedContractAddress",
                            consumerName, deployedPVPenaltyContractAddress3);

                    sendMsg(receiver, "PVDeployedContractAddress",
                            "PVCounter3", deployedPVContractAddress3);
                    sendMsg(receiver, "PVPenaltyDeployedContractAddress",
                            "PVCounter3", deployedPVPenaltyContractAddress3);

                    sendMsg(receiver, "PVDeployedContractAddress",
                            "Storage3", deployedPVContractAddress3);
                    sendMsg(receiver, "PVPenaltyDeployedContractAddress",
                            "Storage3", deployedPVPenaltyContractAddress3);

                    // отправка сигнала в Матлаб
                    sendingToMatlab(pv3DestPort, flagPV3);

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
    private ACLMessage receiveMsgAboutAuctionFinished(Agent pv){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("AuctionCompleted");
        ACLMessage msg = pv.receive(messageTemplate);

        if (msg!=null){

            System.out.println(pv.getLocalName() + " receive msg : " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());

        }else {
            block();
        }

        return msg;
    }

    // TODO: Метод регистрации в контракте для PV (эти методы будут в классе PVInteractWithContracts)
    private void registerPVNameInContract(){

    }

    // TODO: Метод регистрации ставки от поотребителя-победителя в контракте (эти методы будут в классе PVInteractWithContracts)
    private void registerAuctionBetInContract(){

    }
}
