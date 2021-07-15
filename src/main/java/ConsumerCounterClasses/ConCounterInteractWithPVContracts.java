package ConsumerCounterClasses;

import ConnectionUDP.UDPClass;
import MQTTConnection.MQTTSubscriber;
import MQTTConnection.SubscriberCallback;
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

import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ConCounterInteractWithPVContracts extends TickerBehaviour {


    private DataStore dataStore;
    private Web3j web3j;
    private ContractGasProvider contractGasProvider;

    private String PUBLIC_KEY_ConsumerCounter1;
    private String PUBLIC_KEY_ConsumerCounter2;
    private String PUBLIC_KEY_ConsumerCounter3;

    private Credentials credentialsConsumerCounter1;
    private Credentials credentialsConsumerCounter2;
    private Credentials credentialsConsumerCounter3;

//    private UDPClass udpClass = new UDPClass();

    private MQTTSubscriber mqttSubscriber = new MQTTSubscriber();
    private SubscriberCallback subscriberCallback;

    private boolean isRegisteredPV1;
    private boolean isRegisteredPV2;
    private boolean isRegisteredPV3;

    private boolean flagConnection1;
    private boolean flagConnection2;
    private boolean flagConnection3;

    private DatagramSocket datagramSocket1;
    private DatagramSocket datagramSocket2;
    private DatagramSocket datagramSocket3;

    private PVContract pvContract1;
    private PVContract pvContract2;
    private PVContract pvContract3;

    private PVPenaltyContract pvPenaltyContract1;
    private PVPenaltyContract pvPenaltyContract2;
    private PVPenaltyContract pvPenaltyContract3;

    // Флаги для создания экземпляров коонтрактов
    private boolean isCreating1;
    private boolean isCreating2;
    private boolean isCreating3;

    // Порты для принятия сигналов из Матлаба
    private int con1port = 10112;
    private int con2port = 10114;
    private int con3port = 10116;

    // Значения потребляемой энергии, на время, пока не доделал принятие данных из Матлаба
    private double newEnergy;

//    private int iterPV1;
//    private int iterPV2;
//    private int iterPV3;
//
//    private int nPV1;
//    private int nPV2;
//    private int nPV3;

    private double counterTime;

    // Изменение цвета фона вывода текста
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";

    // Зеленый Storage и StorageCounters
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";

    // Blue - Pv и PVCounters
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";

    // Purple - windCounter и wind
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    // Изменение цвета вывода текста
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";

    // Зеленый Storage и StorageCounters
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    // Blue - Pv и PVCounters
    public static final String ANSI_BLUE = "\u001B[34m";

    // Purple - windCounter и wind
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";



    public ConCounterInteractWithPVContracts(Agent a, long period, DataStore dataStore, Web3j web3j,
                                             ContractGasProvider contractGasProvider, String PUBLIC_KEY_ConsumerCounter1,
                                             String PUBLIC_KEY_ConsumerCounter2, String PUBLIC_KEY_ConsumerCounter3,
                                             Credentials credentialsConsumerCounter1, Credentials credentialsConsumerCounter2,
                                             Credentials credentialsConsumerCounter3,
                                             PVContract pvContract1, PVContract pvContract2, PVContract pvContract3,
                                             PVPenaltyContract pvPenaltyContract1, PVPenaltyContract pvPenaltyContract2,
                                             PVPenaltyContract pvPenaltyContract3,
                                             UDPClass udpClass, boolean isRegisteredPV1, boolean isRegisteredPV2,
                                             boolean isRegisteredPV3, boolean flagConnectionPV1, boolean flagConnectionPV2,
                                             boolean flagConnectionPV3, DatagramSocket datagramSocketPV1, DatagramSocket datagramSocketPV2,
                                             DatagramSocket datagramSocketPV3, boolean isCreating1, boolean isCreating2,
                                             boolean isCreating3, int iterPV1, int iterPV2, int iterPV3,
                                             int nPV1, int nPV2, int nPV3, int counterTime) {
        super(a, period);
        this.dataStore = dataStore;
        setDataStore(dataStore);

        this.web3j = web3j;
        this.contractGasProvider = contractGasProvider;
        this.PUBLIC_KEY_ConsumerCounter1 = PUBLIC_KEY_ConsumerCounter1;
        this.PUBLIC_KEY_ConsumerCounter2 = PUBLIC_KEY_ConsumerCounter2;
        this.PUBLIC_KEY_ConsumerCounter3 = PUBLIC_KEY_ConsumerCounter3;

        this.credentialsConsumerCounter1 = credentialsConsumerCounter1;
        this.credentialsConsumerCounter2 = credentialsConsumerCounter2;
        this.credentialsConsumerCounter3 = credentialsConsumerCounter3;

        //this.udpClass = udpClass;
        this.isRegisteredPV1 = isRegisteredPV1;
        this.isRegisteredPV2 = isRegisteredPV2;
        this.isRegisteredPV3 = isRegisteredPV3;

        this.flagConnection1 = flagConnectionPV1;
        this.flagConnection2 = flagConnectionPV2;
        this.flagConnection3 = flagConnectionPV3;

        this.datagramSocket1 = datagramSocketPV1;
        this.datagramSocket2 = datagramSocketPV2;
        this.datagramSocket3 = datagramSocketPV3;

        this.pvContract1 = pvContract1;
        this.pvContract2 = pvContract2;
        this.pvContract3 = pvContract3;

        this.pvPenaltyContract1 = pvPenaltyContract1;
        this.pvPenaltyContract2 = pvPenaltyContract2;
        this.pvPenaltyContract3 = pvPenaltyContract3;

        this.isCreating1 = isCreating1;
        this.isCreating2 = isCreating2;
        this.isCreating3 = isCreating3;

//        this.iterPV1 = iterPV1;
//        this.iterPV2 = iterPV2;
//        this.iterPV3 = iterPV3;
//
//        this.nPV1 = nPV1;
//        this.nPV2 = nPV2;
//        this.nPV3 = nPV3;

        this.counterTime = counterTime;
    }

    @Override
    protected void onTick() {

        //receiveTimeFromMatlab();

        if (myAgent.getLocalName().equals("ConsumerCounter1")) {

            if (getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer") != null) {

                // Проверка, если concludedConsumer соответствует PV1 || Pv2 || PV3
                if (getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("PV1") ||
                        getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("PV2") ||
                        getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("PV3")) {

                    receiveTimeFromDist(myAgent);
                    // Создание экземпляров контрактов в этом классе
                    createContractSample(myAgent, PUBLIC_KEY_ConsumerCounter1, "ConsumerCounter1", credentialsConsumerCounter1,
                            isCreating1);


                    // Усттановка нового соединения для ConxumerCounter1
                    if (flagConnection1) {
                        try {
                            datagramSocket1 = new DatagramSocket(con1port);
                        } catch (SocketException socketException) {
                            socketException.printStackTrace();
                        }
                        flagConnection1 = false;
                    }

                    // Обновление потребляемой энергии
                    updateConsumptionPVEnergy(myAgent, PUBLIC_KEY_ConsumerCounter1, datagramSocket1, /*datagramSocket2, datagramSocket3,*/
                            /*pvContract1, pvContract2, pvContract3,*/ con1port, flagConnection1, isRegisteredPV1, isRegisteredPV2, isRegisteredPV3,
                            "PV1", "PV2", "PV3", "ConsumerCounter1", "ConsumerCounter1", "Consumer1",
                            credentialsConsumerCounter1);

                    // Обновление оплаты за потребляемую энергию
                    updatePaymentForPV(myAgent, "PaymentPVCompleted",PUBLIC_KEY_ConsumerCounter1,
                            /*pvContract1, pvContract2, pvContract3,*/ "PV1", "PV2", "PV3",
                            credentialsConsumerCounter1);

                    //проверка условий выполнения оплаты за покупку энергии у PV за сутки
                    controlPaymentForPV(myAgent, PUBLIC_KEY_ConsumerCounter1,credentialsConsumerCounter1);

                    // Принятие от счетчика PV кол-ва выработанной энергии
                    receiveProductionPVEnergy(myAgent);

                    // Отправка сообщения о необходимости
                    // выплаты комиссии потребителю
                    sendMsgAboutCommission(myAgent,"Consumer1");

                    // Принятие сообщения об оплате комиссии от потребителя
                    // и проверка оплаты
                    receiveMsgAboutCommissionPayment(myAgent,
                            credentialsConsumerCounter1);
                }
            }
        } else if (myAgent.getLocalName().equals("ConsumerCounter2")){

            if (getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer") != null) {

                // Проверка, если concludedConsumer соответствует PV1 || Pv2 || PV3
                if (getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("PV1") ||
                        getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("PV2") ||
                        getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("PV3")) {

                    receiveTimeFromDist(myAgent);
                    // Создание экземпляров контрактов в этом классе
                    createContractSample(myAgent, PUBLIC_KEY_ConsumerCounter2, "ConsumerCounter2", credentialsConsumerCounter2,
                            isCreating2);

                    // Устновка нового соединения для ConxxumerCounter2
                    if (flagConnection2) {

                        try {
                            datagramSocket2 = new DatagramSocket(con2port);
                        } catch (SocketException socketException) {
                            socketException.printStackTrace();
                        }
                        flagConnection2 = false;
                    }


                    // Обновление потребляемой энергии
                    updateConsumptionPVEnergy(myAgent, PUBLIC_KEY_ConsumerCounter2,/* datagramSocket1, */datagramSocket2,/* datagramSocket3,*/
                            /*pvContract1, pvContract2, pvContract3,*/ con2port, flagConnection2, isRegisteredPV1, isRegisteredPV2, isRegisteredPV3,
                            "PV1", "PV2", "PV3", "ConsumerCounter2", "ConsumerCounter2", "Consumer2",
                            credentialsConsumerCounter2);

                    // Обновление оплаты за потребляемую энергию
                    updatePaymentForPV(myAgent, "PaymentPVCompleted",PUBLIC_KEY_ConsumerCounter2,
                            /*pvContract1, pvContract2, pvContract3,*/ "PV1", "PV2", "PV3",
                    credentialsConsumerCounter2);

                    //проверка условий выполнения оплаты за покупку энергии у PV за сутки
                    controlPaymentForPV(myAgent, PUBLIC_KEY_ConsumerCounter2,credentialsConsumerCounter2);

                    // Принятие от счетчика PV кол-ва выработанной энергии
                    receiveProductionPVEnergy(myAgent);

                    // Отправка сообщения о необходимости
                    // выплаты комиссии потребителю
                    sendMsgAboutCommission(myAgent,"Consumer2");

                    // Принятие сообщения об оплате комиссии от потребителя
                    // и проверка оплаты
                    receiveMsgAboutCommissionPayment(myAgent,
                            credentialsConsumerCounter2);
                }
            }
        }else {


            if (getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer") != null) {

                // Проверка, если concludedConsumer соответствует PV1 || Pv2 || PV3
                if (getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("PV1") ||
                        getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("PV2") ||
                        getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("PV3")) {

                    receiveTimeFromDist(myAgent);
                    // Создание экземпляров контрактов в этом классе
                    createContractSample(myAgent, PUBLIC_KEY_ConsumerCounter3, "ConsumerCounter3", credentialsConsumerCounter3,
                            isCreating3);

                    // Установка нового соединения
                    if (flagConnection3) {
                        try {
                            datagramSocket3 = new DatagramSocket(con3port);
                        } catch (SocketException socketException) {
                            socketException.printStackTrace();
                        }
                        flagConnection3 = false;
                    }


                    // Обновление потребляемой энергии
                    updateConsumptionPVEnergy(myAgent, PUBLIC_KEY_ConsumerCounter3, /*datagramSocket1, datagramSocket2,*/ datagramSocket3,
                            /*pvContract1, pvContract2, pvContract3,*/ con3port, flagConnection3, isRegisteredPV1, isRegisteredPV2, isRegisteredPV3,
                            "PV1", "PV2", "PV3", "ConsumerCounter3", "ConsumerCounter3", "Consumer3",
                            credentialsConsumerCounter3);

                    // Обновление оплаты за потребляемую энергию
                    updatePaymentForPV(myAgent, "PaymentPVCompleted",PUBLIC_KEY_ConsumerCounter3,
                            /*pvContract1, pvContract2, pvContract3,*/ "PV1", "PV2", "PV3",
                    credentialsConsumerCounter3);

                    //проверка условий выполнения оплаты за покупку энергии у PV за сутки
                    controlPaymentForPV(myAgent, PUBLIC_KEY_ConsumerCounter3,credentialsConsumerCounter3);

                    // Принятие от счетчика PV кол-ва выработанной энергии
                    receiveProductionPVEnergy(myAgent);

                    // Отправка сообщения о необходимости
                    // выплаты комиссии потребителю
                    sendMsgAboutCommission(myAgent,"Consumer3");

                    // Принятие сообщения об оплате комиссии от потребителя
                    // и проверка оплаты
                    receiveMsgAboutCommissionPayment(myAgent,
                            credentialsConsumerCounter3);
                }
            }
        }

    }

    private void createContractSample(Agent consumerCounter, String publicKey, String consumerCounterName, Credentials credentials, boolean isCreatingSample) {

        // if (isCreatingSample){

        if (consumerCounter.getLocalName().equals(consumerCounterName)) {
            if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer") != null) {

                // Проверка, если concludedConsumer соответствует PV1
                if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("PV1")) {

                    // Проверка, если адрес контракта != null
                    if (getDataStore().get(consumerCounter.getLocalName() + "address") != null &&
                            getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress") != null) {

                        pvContract1 = PVContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "address")),
                                web3j, credentials, contractGasProvider);

//                        System.out.println(consumerCounter.getLocalName() + " : " + publicKey + " create " +
//                                "pvContract1 sample " + pvContract1.getContractAddress());

                        pvPenaltyContract1 = PVPenaltyContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress")),
                                web3j, credentials, contractGasProvider);

//                        System.out.println(consumerCounter.getLocalName() + " : " + publicKey + " create " +
//                                "pvPenaltyContract1 sample " + pvPenaltyContract1.getContractAddress());

                        //isCreatingSample = false;
                    }

                }


                // Проверка, если concludedConsumer соответствует PV2
                if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("PV2")) {

                    // Проверка, если адрес контракта != null
                    if (getDataStore().get(consumerCounter.getLocalName() + "address") != null &&
                            getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress") != null) {

                        pvContract2 = PVContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "address")),
                                web3j, credentials, contractGasProvider);

//                        System.out.println(consumerCounter.getLocalName() + " : " + publicKey + " create " +
//                                "pvContract2 sample " + pvContract2.getContractAddress());

                        pvPenaltyContract2 = PVPenaltyContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress")),
                                web3j, credentials, contractGasProvider);

//                        System.out.println(consumerCounter.getLocalName() + " : " + publicKey + " create " +
//                                "pvPenaltyContract2 sample " + pvPenaltyContract2.getContractAddress());

                        // isCreatingSample = false;
                    }

                }


                // Проверка, если concludedConsumer соответствует PV3
                if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("PV3")) {

                    // Проверка, если адрес контракта != null
                    if (getDataStore().get(consumerCounter.getLocalName() + "address") != null &&
                            getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress") != null) {

                        pvContract3 = PVContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "address")),
                                web3j, credentials, contractGasProvider);

//                        System.out.println(consumerCounter.getLocalName() + " : " + publicKey + " create " +
//                                "pvContract3 sample " + pvContract3.getContractAddress());


                        pvPenaltyContract3 = PVPenaltyContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress")),
                                web3j, credentials, contractGasProvider);

//                        System.out.println(consumerCounter.getLocalName() + " : " + publicKey + " create " +
//                                "pvPenaltyContract3 sample " + pvPenaltyContract3.getContractAddress());

                        //isCreatingSample = false;

                    }

                }

            }
        }
    }

            // метод обновления потребляемой энергии от PV - принятие сигналов из Матлаба
    private void updateConsumptionPVEnergy(Agent consumerCounter, String publicKey, DatagramSocket datagramSocket,
                                           /*DatagramSocket datagramSocket2, DatagramSocket datagramSocket3,*/
                                           /*PVContract pvContract,
                                           PVContract pvContract2, PVContract pvContract3,*/
                                           int destPort, boolean flagConnection,
                                           boolean isRegistered, boolean isRegistered2, boolean
                                                   isRegistered3, String pvName, String pvName2, String pvName3,
                                           String topic, String clientID, String consumerName, Credentials credentials) {

        // контракт с PV1
        if (/*pvContract1 != null && */getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer") != null
                && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals(pvName) &&
                getDataStore().get(consumerCounter.getLocalName() + "register") !=null &&
        getDataStore().get(consumerCounter.getLocalName() + "register").equals(pvName)) {


            // После регистрации флаг isRegistered вытавляется как false, чтобы не зайти снова в этот метод
            // выставим его снова как true, чтобы счетчик потребителя смог обновлять количество потребляемой энергии

            //TODO: Принимаем потребляемую энергию из Матлаба по MQTT
            newEnergy = mqttSubscriber.mqttSubscriber(topic,clientID);
            if (newEnergy!=0) {

                //double energy = Double.parseDouble(newEnergy);
                double energy = newEnergy;
                System.out.println();
                System.out.println(consumerCounter.getLocalName() + " : "
                        + publicKey + " received new energy " + energy + " from Matlab");
                System.out.println();

                // ConsumerCounter отправляет кол-во текущей потреблямой энергии счетчику PVCounter1
                sendMsg(consumerCounter, "ExchangePVEnergy", "PVCounter1", String.valueOf(newEnergy));

                // Достаем из datastore адрес основного контракта PV
                if (getDataStore().get(consumerCounter.getLocalName() + "address") != null) {

                    String deployedAddress = String
                            .valueOf(getDataStore().get(consumerCounter.getLocalName() + "address"));

                    // Создаем локальный экземпляр контракта
                    PVContract pvContract = PVContract.load(deployedAddress,
                            web3j,credentials,contractGasProvider);


                    try {
                        if (pvContract.updateConsumptionPVenergy(BigInteger.valueOf((int) newEnergy)).send().isStatusOK()) {
                            System.out.println(ANSI_RED + consumerCounter.getLocalName() + " : " + publicKey +
                                    "\n update pv energy in " + pvContract.getContractAddress() + ANSI_RESET);

                            // TODO: После того, как счетчик потребителя обновил кол-во потребляемой энергии,
                            //  он отправляет сообщение потребителю,
                            //  о том, что нужно заплатить за потребленную энергию
                            //System.out.println("======= SENDING MSG ABOUT PAYMENT FOR PV1 CONSUMPTION ENERGY ======= ");
                            sendMsg(consumerCounter, "NeedToPayForPVConsumptionEnergy", consumerName, String.valueOf(newEnergy));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // контракт с PV2
        if (/*pvContract2!=null && */getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer") != null
                && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals(pvName2) &&
                getDataStore().get(consumerCounter.getLocalName() + "register")!=null &&
                getDataStore().get(consumerCounter.getLocalName() + "register").equals(pvName2)){


            // После регистрации флаг isRegistered вытавляется как false, чтобы не зайти снова в этот метод
            // выставим его снова как true, чтобы счетчик потребителя смог обновлять количество потребляемой энергии
            newEnergy = mqttSubscriber.mqttSubscriber(topic,clientID);

            if (newEnergy!=0) {

//                double energy = Double.parseDouble(newEnergy);
                double energy = newEnergy;
                System.out.println();
                System.out.println(ANSI_RED + consumerCounter.getLocalName() + " : " +
                        publicKey + " received new energy " + energy + " from Matlab" + ANSI_RESET);
                System.out.println();

                // ConsumerCounter отправляет кол-во текущей потреблямой энергии счетчику PVCounter1
                sendMsg(consumerCounter, "ExchangePVEnergy", "PVCounter2", String.valueOf(newEnergy));

                if (getDataStore().get(consumerCounter.getLocalName() + "address") != null) {

                    String deployedAddress = String
                            .valueOf(getDataStore().get(consumerCounter.getLocalName() + "address"));

                    PVContract pvContract2 = PVContract.load(deployedAddress,
                            web3j,credentials,contractGasProvider);

                    try {
                        if (pvContract2.updateConsumptionPVenergy(BigInteger.valueOf((int) newEnergy)).send().isStatusOK()) {
                            System.out.println(ANSI_RED + consumerCounter.getLocalName() + " : " + publicKey +
                                    "\n update pv energy in " + pvContract2.getContractAddress() + ANSI_RESET);

                            // TODO: После того, как счетчик потребителя обновил кол-во потребляемой энергии, он отправляет сообщение потребителю,
                            //  о том, что нужно заплатить за потребленную энергию
                            //System.out.println("======= SENDING MSG ABOUT PAYMENT FOR PV3 CONSUMPTION ENERGY ======= ");
                            sendMsg(consumerCounter, "NeedToPayForPVConsumptionEnergy", consumerName, String.valueOf(newEnergy));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        // контракт с PV3
        if (/*pvContract3!=null && */getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer") != null
                && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals(pvName3) &&
                getDataStore().get(consumerCounter.getLocalName() + "register")!=null &&
                getDataStore().get(consumerCounter.getLocalName() + "register").equals(pvName3)){


            // После регистрации флаг isRegistered вытавляется как false, чтобы не зайти снова в этот метод
            // выставим его снова как true, чтобы счетчик потребителя смог обновлять количество потребляемой энергии
            newEnergy = mqttSubscriber.mqttSubscriber(topic,clientID);

            if (newEnergy!=0) {

//                double energy = Double.parseDouble(newEnergy);
                double energy = newEnergy;
                System.out.println();
                System.out.println(ANSI_RED + consumerCounter.getLocalName() + " : " +
                        publicKey + " received new energy " + energy + " from Matlab" + ANSI_RESET);
                System.out.println();
                // ConsumerCounter отправляет кол-во текущей потреблямой энергии счетчику PVCounter3
                sendMsg(consumerCounter, "ExchangePVEnergy", "PVCounter3", String.valueOf(newEnergy));

                if (getDataStore().get(consumerCounter.getLocalName() + "address") != null) {

                    String deployedAddress = String
                            .valueOf(getDataStore().get(consumerCounter.getLocalName() + "address"));

                    PVContract pvContract3 = PVContract.load(
                            deployedAddress,web3j,credentials,contractGasProvider
                    );

                    try {
                        if (pvContract3.updateConsumptionPVenergy(BigInteger.valueOf((int) newEnergy)).send().isStatusOK()) {
                            System.out.println(ANSI_RED + consumerCounter.getLocalName() + " : " + publicKey +
                                    " update pv energy in " + pvContract3.getContractAddress() + ANSI_RESET);

                            // TODO: После того, как счетчик потребителя обновил кол-во потребляемой энергии, он отправляет сообщение потребителю,
                            //  о том, что нужно заплатить за потребленную энергию
                            //System.out.println("======= SENDING MSG ABOUT PAYMENT FOR PV3 CONSUMPTION ENERGY ======= ");
                            sendMsg(consumerCounter, "NeedToPayForPVConsumptionEnergy", consumerName, String.valueOf(newEnergy));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // TODO: Потом закомментить после принятия данных из Матлаба
        //newEnergy = 500 + Math.random() * 100;

    }


    // TODO: Метод обновления оплаты за потребляемую энергию от PV
    private void updatePaymentForPV(Agent consumerCounter, String receiveProtocol, String publicKey,
                                    /*PVContract pvContract1,
                                    PVContract pvContract2, PVContract pvContract3,*/
                                     String pvName, String pvName2, String pvName3, Credentials credentials) {

        // Принятие сообщения о том, что потребитель оплатил за потребленную энергию
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(receiveProtocol);
        ACLMessage msg = consumerCounter.receive(messageTemplate);

        if (msg != null) {

            System.out.println(ANSI_RED + consumerCounter.getLocalName() + " receive payment value " + msg.getContent() +
                    " from " + msg.getSender().getLocalName() + ANSI_RESET);


            try {
                // контракт с PV1
                    if (/*pvContract1 != null && */getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer") != null
                            && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals(pvName) &&
                            getDataStore().get(consumerCounter.getLocalName() + "register")!=null &&
                            getDataStore().get(consumerCounter.getLocalName() + "register").equals(pvName)) {

                        // Обновление оплаты за потребляемую энергию от PV1

                        // Достаем из datastore адрес основного контракта PV
                        if (getDataStore().get(consumerCounter.getLocalName() + "address") != null) {

                            String deployedAddress = String.valueOf(
                                    getDataStore().get(consumerCounter.getLocalName() + "address")
                            );

                            // Создаем лакальный экземпляр контракта
                            PVContract pvContract1 = PVContract.load(deployedAddress,web3j,
                                    credentials,contractGasProvider);

                            if (pvContract1.updatePaymentForPVEnergy().send().isStatusOK()) {
                                System.out.println(ANSI_RED + "======== " + consumerCounter.getLocalName() +
                                        " update payment for PV1 consumption energy =======" +
                                        "\n" + consumerCounter.getLocalName() + " with publicKey : " + publicKey
                                        + " update payment fo consumption energy " +
                                        " for Consumer1" + ANSI_RESET);

                                // TODO: Временно сделаем итерацию для подсчета количества транзакций обновления оплаты
                                //  для подведения итогов оплаты за сутки (потом закомментить!!!!)
//                                iterPV1 +=1;
                            }

                        }
                    }


                    // Контракт с PV2
                if (/*pvContract2 !=null && */getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer") != null
                        && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals(pvName2) &&
                        getDataStore().get(consumerCounter.getLocalName() + "register")!=null &&
                        getDataStore().get(consumerCounter.getLocalName() + "register").equals(pvName2)) {

                    // Достаем из datastore адрес основного контракта PV
                    if (getDataStore().get(consumerCounter.getLocalName() + "address") != null) {

                        String deployedAddress = String.valueOf(
                                getDataStore().get(consumerCounter.getLocalName() + "address")
                        );

                        // Создаем локальный экземпляр контракта
                        PVContract pvContract2 = PVContract.load(deployedAddress,
                                web3j,credentials,contractGasProvider);
                        // Обновление оплаты за потребляемую энергию от PV2

                        if (pvContract2.updatePaymentForPVEnergy().send().isStatusOK()) {
                            System.out.println(ANSI_RED + "======== " + consumerCounter.getLocalName() +
                                    " update payment for PV2 consumption energy =======" +
                                    "\n" + consumerCounter.getLocalName() + " with publicKey : " + publicKey
                                    + " update payment fo consumption energy " +
                                    " for Consumer2" + ANSI_RESET);

//                        iterPV2 +=1;
                        }

                    }
                }

                // Контракт с PV3
                if (/*pvContract3!=null && */getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer") != null
                        && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals(pvName3) &&
                        getDataStore().get(consumerCounter.getLocalName() + "register")!=null &&
                        getDataStore().get(consumerCounter.getLocalName() + "register").equals(pvName3)) {

                    // Достаем из datastore адрес основного контракта PV
                    if (getDataStore().get(consumerCounter.getLocalName() + "address") != null) {

                        String deployedAddress = String.valueOf(
                                getDataStore().get(consumerCounter.getLocalName() + "address")
                        );

                        // Создаем локальный экземпляр контракта
                        PVContract pvContract3 = PVContract.load(deployedAddress,
                                web3j, credentials, contractGasProvider);
                        // Обновление оплаты за потребляемую энергию от PV3

                        if (pvContract3.updatePaymentForPVEnergy().send().isStatusOK()) {
                            System.out.println(ANSI_RED + "======== " + consumerCounter.getLocalName() +
                                    " update payment for PV3 consumption energy =======" +
                                    "\n" + consumerCounter.getLocalName() + " with publicKey : " + publicKey
                                    + " update payment fo consumption energy " +
                                    " for Consumer3" + ANSI_RESET);
//                        iterPV3 +=1;
                        }


                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


        }else{
                block();
            }
    }


    private void receiveTimeFromMatlab(){
        counterTime += mqttSubscriber.mqttSubscriber("Time",
                "ConsumerCounterPV");
    }

    // TODO: Метод принятия времени от Дистрибьютора
    private void receiveTimeFromDist(Agent conCounter){
        // Пока просто сделаем счетчик времени (потом закомментить)
//        counterTime = mqttSubscriber.mqttSubscriber("Time","WindCounter");
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("TimeFromMatlab");
        ACLMessage msg = conCounter.receive(messageTemplate);

        if (msg!=null){
            counterTime = Double.parseDouble(msg.getContent());
            System.out.println(conCounter.getLocalName() +
                    " receive " + counterTime +
                    " from " + counterTime);
        }else {
            block();
        }

    }

    // TODO: Метод проверки условий выполнения оплаты за покупку энергии у PV
    //  !Проверка условий осуществляется каждые сутки (
    //  ( пока сделаем счетчик здесь, чтобы отсчитывать сутки (24 итерации обновления оплаты)
    //  , затем будем принимать сигналы по MQTT ( если пришло число 24 x n, то проверяем итоги оплаты
    //   за сутки
    private void controlPaymentForPV(Agent consumerCounter, String publicKey, Credentials credentials){

        try {

            if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer")!=null) {

                // Если isConcludedConsumer == PV1
                if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("PV1")) {

                    // Потом эти выводы iterPV1 закомментить, когда буду принимать сигналы о времени по MQTT
                    //System.out.println("===================");
//                    System.out.println("iterPV1 " + iterPV1);
                    //System.out.println("===================");

                    if (getDataStore().get(consumerCounter.getLocalName() + "address") != null) {

                        String deployedAddress = String.valueOf(
                                getDataStore().get(consumerCounter.getLocalName() + "address")
                        );

                        // Создаем локальный экземпляр контракта
                        PVContract pvContract1 = PVContract.load(deployedAddress,
                                web3j, credentials, contractGasProvider);

                        if (/*pvContract1 != null && */(int)counterTime == 18) {

                            if (pvContract1.controlPaymentForPV().send().isStatusOK()) {
                                System.out.println(ANSI_RED + consumerCounter.getLocalName() + " with publicKey " + publicKey
                                        + " control payment per day from Consumer1" + ANSI_RESET);

                                // Проверяем значение штрафа и баланса PV1
                                //System.out.println("===================== CHECK FINE AND PV1 BALANCE ===================");
                                int mustPayment = (int) Double.parseDouble(String.valueOf(pvContract1.viewPaymentForPV().send().component2()));
                                int factPayment = (int) Double.parseDouble(String.valueOf(pvContract1.viewPaymentForPV().send().component4()));

                                System.out.println(ANSI_RED_BACKGROUND + "====== Total pvContract1 for day ==========" +
                                        "\nMust be pay : " + mustPayment +
                                        "\nFact payment : " + factPayment + ANSI_RESET);

                                // Считаем штраф (если потребитель недоплатил to Wind3)
                                int fine = mustPayment - factPayment;
                                if (fine > 0) {
                                    System.err.println("========== FINE =============");
                                    System.out.println(" must pay fine to PV1 " +
                                            fine + " wei");
                                } else {
                                    System.out.println(" paid to PV1 need sum!");
                                }
                            }

//                        nPV1+=1;
                        }
                    }
                }


                // Если isConcludedConsumer == PV2
                if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("PV2")) {

                    // Потом закоььентить
                    //System.out.println("===================");
//                    System.out.println("iterPV2 " + iterPV2);
                    //System.out.println("===================");

                    if (getDataStore().get(consumerCounter.getLocalName() + "address") != null) {

                        String deployedAddress = String.valueOf(
                                getDataStore().get(consumerCounter.getLocalName() + "address")
                        );

                        // Создаем локальный экземпляр контракта
                        PVContract pvContract2 = PVContract.load(deployedAddress,
                                web3j, credentials, contractGasProvider);

                        if (/*pvContract2 != null && */(int)counterTime == 18) {

                            if (pvContract2.controlPaymentForPV().send().isStatusOK()) {
                                System.out.println(ANSI_RED + consumerCounter.getLocalName() + " with publicKey " + publicKey
                                        + " control payment per day from Consumer2" + ANSI_RESET);

                                // Проверяем значение штрафа и баланса PV2
                                //System.out.println("===================== CHECK FINE AND PV2 BALANCE ===================");
                                int mustPayment = (int) Double.parseDouble(String.valueOf(pvContract2.viewPaymentForPV().send().component2()));
                                int factPayment = (int) Double.parseDouble(String.valueOf(pvContract2.viewPaymentForPV().send().component4()));

                                System.out.println(ANSI_RED_BACKGROUND + "====== Total pvContract2 for day ==========" +
                                        "\nMust be pay : " + mustPayment +
                                        "\nFact payment : " + factPayment + ANSI_RESET);

                                // Считаем штраф (если потребитель недоплатил to Wind3)
                                int fine = mustPayment - factPayment;
                                if (fine > 0) {
                                    System.err.println("========== FINE =============");
                                    System.out.println(" must pay fine to PV2 " +
                                            fine + " wei");
                                } else {
                                    System.out.println(" paid to PV2 need sum!");
                                }
                            }

//                        nPV2+=1;
                        }

                    }
                }


                // Если isConcludedConsumer == PV3
                if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("PV3")) {

                    // Потом закомментить
                    //System.out.println("===================");
//                    System.out.println("iterPV3 " + iterPV3);
                    //System.out.println("===================");

                    if (getDataStore().get(consumerCounter.getLocalName() + "address") != null) {

                        String deployedAddress = String.valueOf(
                                getDataStore().get(consumerCounter.getLocalName() + "address")
                        );

                        // Создаем локальный экземпляр контракта
                        PVContract pvContract3 = PVContract.load(deployedAddress,
                                web3j, credentials, contractGasProvider);

                    if (/*pvContract3 != null && */(int)counterTime == 18) {

                        if (pvContract3.controlPaymentForPV().send().isStatusOK()) {
                            System.out.println(ANSI_RED + consumerCounter.getLocalName() + " with publicKey " + publicKey
                                    + " control payment per day from Consumer3" + ANSI_RESET);

                            // Проверяем значение штрафа и баланса PV3
                            //System.out.println("===================== CHECK FINE AND PV3 BALANCE ===================");
                            int mustPayment = (int) Double.parseDouble(String.valueOf(pvContract3.viewPaymentForPV().send().component2()));
                            int factPayment = (int) Double.parseDouble(String.valueOf(pvContract3.viewPaymentForPV().send().component4()));

                            System.out.println(ANSI_RED_BACKGROUND + "====== Total pvContract3 for day ==========" +
                                    "\nMust be pay : " + mustPayment +
                                    "\nFact payment : " + factPayment + ANSI_RESET);

                            // Считаем штраф (если потребитель недоплатил to Wind3)
                            int fine = mustPayment - factPayment;
                            if (fine > 0) {
                                System.err.println("========== FINE =============");
                                System.out.println(" must pay fine to PV3 " +
                                        fine + " wei");
                            } else {
                                System.out.println(" paid to PV3 need sum!");
                            }

                        }
                    }

//                        nPV3+=1;

                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // TODO: Счетчик потребителя принимает от счетчика PV
    //  кол-во выработки энергии и считает разницу
    //  выработки и потребления, если потребление
    //  больше выработки, то отправляет разницу своему
    //  потребителю для оплаты системной услуги
    private void receiveProductionPVEnergy(Agent consumerCounter){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("ProductionEnergyPV");
        ACLMessage msg = consumerCounter.receive(messageTemplate);

        if (msg!=null){

            double productionEnergy = Double.parseDouble(msg.getContent());
            System.out.println(consumerCounter.getLocalName() +
                    " receive " + productionEnergy +
                    " from " + msg.getSender().getLocalName());

            // После получения кол-ва выработанной энергии от счетчика PV
            // ConsumerCounter1 считает разницу выработки и потребления
            if (consumerCounter.getLocalName().equals("ConsumerCounter1")){

                // Пока будем вызывать newEnergy, как потребляемую энергию, потом
                // будем принимать данные по MQTT о потреблении
                double difference = newEnergy - productionEnergy;
                System.out.println(consumerCounter + " calc difference between production " +
                        "and consumption energy " + difference);
                // Если разница больше ноля, то
                // счетчик потребителя отправляет эту разницу
                // своему потребителю, чтобы тот оплатил за системную усслугу от накопителя
                if (difference > 0){
                    sendMsg(consumerCounter,"SystemServicePV","Consumer1",
                            difference + ":" + msg.getSender().getLocalName());
                }
            } else if (consumerCounter.getLocalName().equals("ConsumerCounter2")){
                // Пока будем вызывать newEnergy, как потребляемую энергию, потом
                // будем принимать данные по MQTT о потреблении
                double difference = newEnergy - productionEnergy;
                System.out.println(consumerCounter + " calc difference between production " +
                        "and consumption energy " + difference);
                // Если разница больше ноля, то
                // счетчик потребителя отправляет эту разницу
                // своему потребителю, чтобы тот оплатил за системную усслугу от накопителя
                if (difference > 0){
                    sendMsg(consumerCounter,"SystemServicePV","Consumer2",
                            difference + ":" + msg.getSender().getLocalName());
                }
            }else {

                // Пока будем вызывать newEnergy, как потребляемую энергию, потом
                // будем принимать данные по MQTT о потреблении
                double difference = newEnergy - productionEnergy;
                System.out.println(consumerCounter + " calc difference between production " +
                        "and consumption energy " + difference);
                // Если разница больше ноля, то
                // счетчик потребителя отправляет эту разницу
                // своему потребителю, чтобы тот оплатил за системную усслугу от накопителя
                if (difference > 0) {
                    sendMsg(consumerCounter, "SystemServicePV", "Consumer3",
                            difference + ":" + msg.getSender().getLocalName());
                }
            }
        }else {
            block();
        }


    }


    // TODO: По истечении 18 00 (подвдение итогов выполнения PV контракта) ConsumerCounter отправялет
    //  сообщение своему потребителю, что нужно оплатить комиссию
    private void sendMsgAboutCommission(Agent consumerCounter, String consumerName){
        // Пока временно делаем такой диапазон,
        // затем будет единое время для всех
        // принимаемой из Матлаба
        if (counterTime >= 18 && counterTime <= 20){
            sendMsg(consumerCounter,"commissionToConsumerCounter",
                    consumerName,"commissionToConsumerCounter");
        }
    }


    // TODO: ConsumerCounter принимает сообщение
    //  от потребителя об оплате и проверяет выполнение оплаты
    private void receiveMsgAboutCommissionPayment(Agent consumerCounter,
                                                  Credentials credentials/*,
                                                  String consumerName*/){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(
                "commissionConsumerCounterCompleted"
        );
        ACLMessage msg = consumerCounter.receive(messageTemplate);

        if (msg!=null){

            System.out.println(consumerCounter.getLocalName() +
                    " receive " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());

            // После принятия сообщения об успешной
            // оплате от поотребителя
            // счетчик потребителя создает экземпяляр контракта
            // в этом методе
            if (getDataStore().get(consumerCounter.getLocalName() + "address")!=null){
                String deployedAddress =
                        String.valueOf(
                                getDataStore().get(consumerCounter.getLocalName() + "address"));
                PVContract pvContract = PVContract.load(
                        deployedAddress, web3j, credentials, contractGasProvider
                );

                System.out.println(consumerCounter.getLocalName() +
                        " create contract sample " + pvContract.getContractAddress() +
                        " in receiveMsgAboutCommissionPayment() method ");

                // После создания экземпляра контракта
                // в этом методе счетчик потребителя
                // выполняет транзакцию по проверке
                // выплаты комиссии
                try {
                    BigInteger mustPayment = pvContract.controlCommissionConsumerCounter()
                            .send().component2();
                BigInteger factPayment = pvContract.controlCommissionConsumerCounter()
                        .send().component4();

                    System.out.println("||||||||||||||||||||||||||||||||||||||||" +
                            "\n======== Control commission payment to " +
                            consumerCounter.getLocalName() + " ========" +
                            "\nMust be pay " + mustPayment +
                            "\nfact payment " + factPayment +
                            "\n||||||||||||||||||||||||||||||||||||||||");
                } catch (Exception e) {
                    e.printStackTrace();
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

}


