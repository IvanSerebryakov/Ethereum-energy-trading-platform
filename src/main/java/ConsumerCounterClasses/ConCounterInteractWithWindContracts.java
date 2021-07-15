package ConsumerCounterClasses;

import ConnectionUDP.UDPClass;
import MQTTConnection.MQTTSubscriber;
import MQTTConnection.SubscriberCallback;
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

import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ConCounterInteractWithWindContracts extends TickerBehaviour {

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
    //private SubscriberCallback subscriberCallback;

    private boolean isRegisteredPV1;
    private boolean isRegisteredPV2;
    private boolean isRegisteredPV3;

    private boolean flagConnection1;
    private boolean flagConnection2;
    private boolean flagConnection3;

    private DatagramSocket datagramSocket1;
    private DatagramSocket datagramSocket2;
    private DatagramSocket datagramSocket3;

    private WindContract windContract1;
    private WindContract windContract2;
    private WindContract windContract3;

    //
    private WindPenaltyContract windPenaltyContract1;
    private WindPenaltyContract windPenaltyContract2;
    private WindPenaltyContract windPenaltyContract3;

    // Флаги для создания экземпляров коонтрактов
    private boolean isCreating1;
    private boolean isCreating2;
    private boolean isCreating3;

    // Порты для принятия сигналов из Матлаба
    private int con1port = 10112;
    private int con2port = 10114;
    private int con3port = 10116;

    private int iterWind1;
    private int iterWind2;
    private int iterWind3;

    private int nWind1;
    private int nWind2;
    private int nWind3;

    // Значения потребляемой энергии, на время, пока не доделал принятие данных из Матлаба
    private double newEnergy = 2000 + Math.random() * 500;

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


    public ConCounterInteractWithWindContracts(Agent a, long period, DataStore dataStore, Web3j web3j,
                                               ContractGasProvider contractGasProvider, String PUBLIC_KEY_ConsumerCounter1,
                                               String PUBLIC_KEY_ConsumerCounter2, String PUBLIC_KEY_ConsumerCounter3,
                                               Credentials credentialsConsumerCounter1, Credentials credentialsConsumerCounter2,
                                               Credentials credentialsConsumerCounter3,
                                               WindContract windContract1, WindContract windContract2, WindContract windContract3,
                                               WindPenaltyContract windPenaltyContract1, WindPenaltyContract windPenaltyContract2,
                                               WindPenaltyContract windPenaltyContract3,
                                               UDPClass udpClass, boolean isRegisteredPV1, boolean isRegisteredPV2,
                                               boolean isRegisteredPV3, boolean flagConnectionPV1, boolean flagConnectionPV2,
                                               boolean flagConnectionPV3, DatagramSocket datagramSocketPV1, DatagramSocket datagramSocketPV2,
                                               DatagramSocket datagramSocketPV3, boolean isCreating1, boolean isCreating2,
                                               boolean isCreating3, int iterWind1, int iterWind2, int iterWind3,
                                               int nWind1, int nWind2, int nWind3, int counterTime) {
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

        this.windContract1 = windContract1;
        this.windContract2 = windContract2;
        this.windContract3 = windContract3;

        this.windPenaltyContract1 = windPenaltyContract1;
        this.windPenaltyContract2 = windPenaltyContract2;
        this.windPenaltyContract3 = windPenaltyContract3;

        this.isCreating1 = isCreating1;
        this.isCreating2 = isCreating2;
        this.isCreating3 = isCreating3;

        this.iterWind1 = iterWind1;
        this.iterWind2 = iterWind2;
        this.iterWind3 = iterWind3;

        this.nWind1 = nWind1;
        this.nWind2 = nWind2;
        this.nWind3 = nWind3;

        this.counterTime = counterTime;
    }

    @Override
    protected void onTick() {

        //receiveTimeFromMatlab();

        if (myAgent.getLocalName().equals("ConsumerCounter1")) {

            if (getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer") != null) {

                // Проверка, если concludedConsumer соответствует Wind1 || Wind2 || Wind3
                if (getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("Wind1") ||
                        getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("Wind2") ||
                        getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("Wind3")) {

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
                    updateConsumptionWindEnergy(myAgent, PUBLIC_KEY_ConsumerCounter1, datagramSocket1, /*datagramSocket2, datagramSocket3,*/
                            /*windContract1, windContract2, windContract3,*/ con1port, flagConnection1, isRegisteredPV1, isRegisteredPV2, isRegisteredPV3,
                            "Wind1", "Wind2", "Wind3",
                            "ConsumerCounter1", "First", "Consumer1", "WindCounter1",
                            credentialsConsumerCounter1);

                    // Обновление оплаты за потребялемую энергию
                    updatePaymentForWind(myAgent, "PaymentWindCompleted", PUBLIC_KEY_ConsumerCounter1,
                            /*windContract1, windContract2, windContract3,*/ "Wind1", "Wind2", "Wind3",
                            credentialsConsumerCounter1);

                    // проверки условий выполнения оплаты за покупку энергии и
                    // асчет штрафа за недоплату у Wind
                    controlPaymentForWind(myAgent, PUBLIC_KEY_ConsumerCounter1);

                    // Принятие вырабатываемой энергии и оотправка
                    // разницы потребялемой и вырабатываемой энергии
                    // потребителю для оплаты системной услуги
                    receiveWindEnergyForCalc(myAgent, "Consumer1");

                }
            }
        } else if (myAgent.getLocalName().equals("ConsumerCounter2")){

            if (getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer") != null) {

                receiveTimeFromDist(myAgent);

                // Проверка, если concludedConsumer соответствует Wind1 || Wind2 || Wind3
                if (getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("Wind1") ||
                        getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("Wind2") ||
                        getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("Wind3")) {
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
                    updateConsumptionWindEnergy(myAgent, PUBLIC_KEY_ConsumerCounter2,/* datagramSocket1, */datagramSocket2,/* datagramSocket3,*/
                            /*windContract1, windContract2, windContract3,*/ con2port, flagConnection2, isRegisteredPV1, isRegisteredPV2, isRegisteredPV3,
                            "Wind1", "Wind2", "Wind3",
                            "ConsumerCounter2", "Second", "Consumer2", "WindCounter2",
                            credentialsConsumerCounter2);

                    // Обновление оплаты за потребялемую энергию
                    updatePaymentForWind(myAgent, "PaymentWindCompleted", PUBLIC_KEY_ConsumerCounter2,
                            /*windContract1, windContract2, windContract3,*/ "Wind1", "Wind2", "Wind3",
                            credentialsConsumerCounter2);

                    // проверки условий выполнения оплаты за покупку энергии и
                    // асчет штрафа за недоплату у Wind
                    controlPaymentForWind(myAgent, PUBLIC_KEY_ConsumerCounter2);

                    // Принятие вырабатываемой энергии и оотправка
                    // разницы потребялемой и вырабатываемой энергии
                    // потребителю для оплаты системной услуги
                    receiveWindEnergyForCalc(myAgent, "Consumer2");

                }
            }
        }else {

            receiveTimeFromDist(myAgent);

            if (getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer") != null) {

                // Проверка, если concludedConsumer соответствует PV1 || Pv2 || PV3
                if (getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("Wind1") ||
                        getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("Wind2") ||
                        getDataStore().get(myAgent.getLocalName() + "isConcludedConsumer").equals("Wind3")) {
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
                    updateConsumptionWindEnergy(myAgent, PUBLIC_KEY_ConsumerCounter3, /*datagramSocket1, datagramSocket2,*/ datagramSocket3,
                            /*windContract1, windContract2, windContract3,*/ con3port, flagConnection3, isRegisteredPV1, isRegisteredPV2, isRegisteredPV3,
                            "Wind1", "Wind2", "Wind3",
                            "ConsumerCounter3", "Third", "Consumer3","WindCounter3",
                            credentialsConsumerCounter3);

                    // Обновление оплаты за потребялемую энергию
                    updatePaymentForWind(myAgent, "PaymentWindCompleted", PUBLIC_KEY_ConsumerCounter3,
                            /*windContract1, windContract2, windContract3,*/ "Wind1", "Wind2", "Wind3",
                            credentialsConsumerCounter3);

                    // проверки условий выполнения оплаты за покупку энергии и
                    // асчет штрафа за недоплату у Wind
                    controlPaymentForWind(myAgent, PUBLIC_KEY_ConsumerCounter3);

                    // Принятие вырабатываемой энергии и оотправка
                    // разницы потребялемой и вырабатываемой энергии
                    // потребителю для оплаты системной услуги
                    receiveWindEnergyForCalc(myAgent, "Consumer3");
                }
            }
        }

    }

    private void createContractSample(Agent consumerCounter, String publicKey, String consumerCounterName, Credentials credentials, boolean isCreatingSample) {

        // if (isCreatingSample){

        if (consumerCounter.getLocalName().equals(consumerCounterName)) {
            if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer") != null) {

                // Проверка, если concludedConsumer соответствует Wind1
                if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("Wind1")) {

                    // Проверка, если адрес контракта != null
                    if (getDataStore().get(consumerCounter.getLocalName() + "address") != null &&
                            getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress") != null) {

                        windContract1 = WindContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "address")),
                                web3j, credentials, contractGasProvider);

//                        System.out.println(consumerCounter.getLocalName() + " : " + publicKey + " create " +
//                                "windContract1 sample " + windContract1.getContractAddress());

                        windPenaltyContract1 = WindPenaltyContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress")),
                                web3j, credentials, contractGasProvider);

//                        System.out.println(consumerCounter.getLocalName() + " : " + publicKey + " create " +
//                                "windPenaltyContract1 sample " + windPenaltyContract1.getContractAddress());

                        //isCreatingSample = false;
                    }

                }


                // Проверка, если concludedConsumer соответствует Wind2
                if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("Wind2")) {

                    // Проверка, если адрес контракта != null
                    if (getDataStore().get(consumerCounter.getLocalName() + "address") != null &&
                            getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress") != null) {

                        windContract2 = WindContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "address")),
                                web3j, credentials, contractGasProvider);

//                        System.out.println(consumerCounter.getLocalName() + " : " + publicKey + " create " +
//                                "windContract2 sample " + windContract2.getContractAddress());

                        windPenaltyContract2 = WindPenaltyContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress")),
                                web3j, credentials, contractGasProvider);

//                        System.out.println(consumerCounter.getLocalName() + " : " + publicKey + " create " +
//                                "windPenaltyContract2 sample " + windPenaltyContract2.getContractAddress());

                        // isCreatingSample = false;
                    }

                }


                // Проверка, если concludedConsumer соответствует Wind3
                if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("Wind3")) {

                    // Проверка, если адрес контракта != null
                    if (getDataStore().get(consumerCounter.getLocalName() + "address") != null &&
                            getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress") != null) {

                        windContract3 = WindContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "address")),
                                web3j, credentials, contractGasProvider);

//                        System.out.println(consumerCounter.getLocalName() + " : " + publicKey + " create " +
//                                "windContract3 sample " + windContract3.getContractAddress());


                        windPenaltyContract3 = WindPenaltyContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress")),
                                web3j, credentials, contractGasProvider);

//                        System.out.println(consumerCounter.getLocalName() + " : " + publicKey + " create " +
//                                "windPenaltyContract3 sample " + windPenaltyContract3.getContractAddress());

                        //isCreatingSample = false;

                    }

                }

            }
        }
    }

    // TODO: Метод принятия времени из Матлаба
    private void receiveTimeFromMatlab(){
        counterTime = mqttSubscriber.mqttSubscriber("Time",
                "ConsumerCounterWind");
    }

    // TODO: Метод принятия времени от Дистрибьютора
    private void receiveTimeFromDist(Agent conCounter){
        // Пока просто сделаем счетчик времени (потом закомментить)
//        counterTime = mqttSubscriber.mqttSubscriber("Time","WindCounter");
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("TimeFromMatlab");
        ACLMessage msg = conCounter.receive(messageTemplate);

        if (msg!=null){
            counterTime = Double.parseDouble(msg.getContent());
            System.out.println(ANSI_GREEN + conCounter.getLocalName() +
                    " receive " + counterTime +
                    " from " + counterTime + ANSI_RESET);
        }else {
            block();
        }

    }

    // метод обновления потребляемой энергии от Wind - принятие сигналов из Матлаба
    private void updateConsumptionWindEnergy(Agent consumerCounter, String publicKey, DatagramSocket datagramSocket,
            /*DatagramSocket datagramSocket2, DatagramSocket datagramSocket3,*/
                                           /*WindContract windContract,
                                             WindContract windContract2, WindContract windContract3,*/
                                             int destPort, boolean flagConnection,
                                             boolean isRegistered, boolean isRegistered2, boolean
                                                   isRegistered3, String windName, String windName2, String windName3,
                                             String topic, String clientID, String consumerName,
                                             String windCounterName, Credentials credentials) {

        // контракт с Wind1
        if (windContract1 != null && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer") != null
                && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals(windName) &&
                getDataStore().get(consumerCounter.getLocalName() + "register").equals(windName)) {


            //mqttSubscriber = new MQTTSubscriber(topic, clientID);
//            double newEnergy = mqttSubscriber.receivedMsg();

           // double newEnergy = subscriberCallback.energy;

            //subscriberCallback = mqttSubscriber.MQTTSubscriber(clientID);

            //double newEnergy = mqttSubscriber.receiveMqttMsg(topic, subscriberCallback);


//            System.out.println("=====================");
//            System.out.println("newnewnenwnew enenenrgyrg from Wind1 " +  newEnergy);
//            System.out.println("========================");

            // После регистрации флаг isRegistered вытавляется как false, чтобы не зайти снова в этот метод
            // выставим его снова как true, чтобы счетчик потребителя смог обновлять количество потребляемой энергии
            if (newEnergy!=0) {

                double energy = newEnergy;

                // После того как в систему пришла потребряемая энергия
                // счетчик потребителя отписывается счетчику ветряка, сколько потребляет
                sendMsg(consumerCounter, "ConsumeWindEnergy", windCounterName, String.valueOf(energy));

//                double energy = newEnergy;
                System.out.println();
                System.out.println(ANSI_RED + consumerCounter.getLocalName() + " : " + publicKey +
                        "\n received new wind energy " + energy + " from Matlab" + ANSI_RESET);
                System.out.println();

                // Достаем из datastore адрес контракта
                if (getDataStore().get(consumerCounter.getLocalName() + "address") != null) {

                    String deployedAddress = String
                            .valueOf(getDataStore().get(consumerCounter.getLocalName() + "address"));

                    // Создаем экземпляр коонтракта
                    WindContract windContract = WindContract.load(deployedAddress,
                            web3j,credentials,contractGasProvider);

                    try {
                        if (windContract.updateConsumptionWindEnergy(BigInteger.valueOf((int) energy)).send().isStatusOK()) {
                            System.out.println(ANSI_RED + consumerCounter.getLocalName() + " : " + publicKey +
                                    " update wind energy in " + windContract.getContractAddress() + ANSI_RESET);

                            // TODO: После того, как счетчик потребителя (Wind) обновил кол-во потребляемой энергии, он отправляет сообщение потребителю,
                            //  о том, что нужно заплатить за потребленную энергию
                            sendMsg(myAgent, "NeedToPayForWindConsumptionEnergy", consumerName, String.valueOf(energy));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // контракт с Wind2
        if (windContract2!=null && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer") != null
                && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals(windName2) &&
                getDataStore().get(consumerCounter.getLocalName() + "register").equals(windName2)){



            //mqttSubscriber = new MQTTSubscriber(topic, clientID);
//            double newEnergy = mqttSubscriber.receivedMsg();
            //double newEnergy = subscriberCallback.energy;

            //subscriberCallback = mqttSubscriber.MQTTSubscriber(clientID);

            //double newEnergy = mqttSubscriber.receiveMqttMsg(topic, subscriberCallback);

//            System.out.println("=====================");
//            System.out.println("newnewnenwnew enenenrgyrg from Wind2 " +  newEnergy);
//            System.out.println("========================");

            // После регистрации флаг isRegistered вытавляется как false, чтобы не зайти снова в этот метод
            // выставим его снова как true, чтобы счетчик потребителя смог обновлять количество потребляемой энергии
            if (newEnergy!=0) {

//                double energy = Double.parseDouble(newEnergy);
                double energy = newEnergy;

                // После того как в систему пришла потребряемая энергия
                // счетчик потребителя отписывается счетчику ветряка, сколько потребляет
                sendMsg(consumerCounter, "ConsumeWindEnergy", windCounterName, String.valueOf(energy));

                System.out.println();
                System.out.println(ANSI_RED + consumerCounter.getLocalName() + " : " + publicKey +
                        "\n received new wind energy " + energy + " from Matlab" + ANSI_RESET);
                System.out.println();

                if (getDataStore().get(consumerCounter.getLocalName() + "address") != null) {

                    String deployedAddress = String
                            .valueOf(getDataStore().get(consumerCounter.getLocalName() + "address"));

                    WindContract windContract2 = WindContract.load(deployedAddress,
                            web3j,credentials,contractGasProvider);

                    try {
                        if (windContract2.updateConsumptionWindEnergy(BigInteger.valueOf((int) energy)).send().isStatusOK()) {
                            System.out.println(ANSI_RED + consumerCounter.getLocalName() + " : " + publicKey +
                                    " update wind energy in " + windContract2.getContractAddress() + ANSI_RESET);

                            // TODO: После того, как счетчик потребителя (Wind) обновил кол-во потребляемой энергии, он отправляет сообщение потребителю,
                            //  о том, что нужно заплатить за потребленную энергию
                            sendMsg(myAgent, "NeedToPayForWindConsumptionEnergy", consumerName, String.valueOf(energy));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        // контракт с PV3
        if (windContract3!=null && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer") != null
                && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals(windName3) &&
                getDataStore().get(consumerCounter.getLocalName() + "register").equals(windName3)){



            //mqttSubscriber = new MQTTSubscriber(topic, clientID);
            //double newEnergy = mqttSubscriber.receivedMsg();

           // double newEnergy = subscriberCallback.energy;

            //subscriberCallback = mqttSubscriber.MQTTSubscriber(clientID);

            //double newEnergy = mqttSubscriber.receiveMqttMsg(topic, subscriberCallback);

//            System.out.println("=====================");
//            System.out.println("newnewnenwnew enenenrgyrg from Wind3 " +  newEnergy);
//            System.out.println("========================");

            // После регистрации флаг isRegistered вытавляется как false, чтобы не зайти снова в этот метод
            // выставим его снова как true, чтобы счетчик потребителя смог обновлять количество потребляемой энергии
            if (newEnergy!=0) {

//                double energy = Double.parseDouble(newEnergy);
                double energy = newEnergy;

                // После того как в систему пришла потребряемая энергия
                // счетчик потребителя отписывается счетчику ветряка, сколько потребляет
                sendMsg(consumerCounter, "ConsumeWindEnergy", windCounterName, String.valueOf(energy));

                System.out.println();
                System.out.println(ANSI_RED + consumerCounter.getLocalName() + " : " + publicKey +
                        " received new wind energy " + energy + " from Matlab" + ANSI_RESET);
                System.out.println();

                if (getDataStore().get(consumerCounter.getLocalName() + "address") != null) {

                    String deployedAddress = String
                            .valueOf(getDataStore().get(consumerCounter.getLocalName() + "address"));

                    WindContract windContract3 = WindContract.load(deployedAddress,
                            web3j,credentials,contractGasProvider);

                    try {
                        if (windContract3.updateConsumptionWindEnergy(BigInteger.valueOf((int) energy)).send().isStatusOK()) {
                            System.out.println(ANSI_RED + consumerCounter.getLocalName() + " : " + publicKey +
                                    "\n update wind energy in " + windContract3.getContractAddress() + ANSI_RESET);

                            // TODO: После того, как счетчик потребителя (Wind) обновил кол-во потребляемой энергии, он отправляет сообщение потребителю,
                            //  о том, что нужно заплатить за потребленную энергию
                            sendMsg(myAgent, "NeedToPayForWindConsumptionEnergy", consumerName, String.valueOf(energy));
                        }
                    } catch (Exception e) {
                        System.out.println();
                    }
                }
            }
        }

        // TODO: Потом закомментить после принятия данных из Матлаба
//        newEnergy = 500 + Math.random() * 500;

    }


    // TODO: Принятие вырабаиываемой энергии от
    //  счетчика Wind
    private void receiveWindEnergyForCalc(Agent consumerCounter, String consumerName){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("ProductionEnergyWind");
        ACLMessage msg = consumerCounter.receive(messageTemplate);

        if (msg!=null){

            int productionEnergy = (int)Double.parseDouble(msg.getContent());
            System.out.println(ANSI_RED + consumerCounter.getLocalName() + " receive " +
                    productionEnergy + " from " +
                    msg.getSender().getLocalName() + ANSI_RESET);

            // После принятия выработки энергии
            // счетчики потребителей считают разницу между
            // потреблением и выработкой, и если
            // эта разница > 0, то отправляют энергию
            // потребителю для оплаты системной услуги
            int difference = (int)newEnergy - productionEnergy;
            System.out.println(ANSI_RED + consumerCounter.getLocalName() + " calc " +
                    " difference " + difference +
                    " between production and consumptionEnergy" + ANSI_RESET);

            if (difference > 0){
                sendMsg(consumerCounter,"SystemServiceWind",
                        consumerName, difference + ":" + msg.getSender().getLocalName());
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
        System.out.println(ANSI_RED + sender.getLocalName() + " send " + msg.getContent() +
                " to " + receiver + ANSI_RESET);
        System.out.println();
    }


    // TODO: Метод обновления суммарной оплаты за потребляемую энергию для Wind

    private void updatePaymentForWind(Agent consumerCounter, String receiveProtocol, String publicKey,
                                      /*WindContract windContract1,
                                      WindContract windContract2, WindContract windContract3,*/
                                      String windName1, String windName2, String windName3,
                                      Credentials credentials) {

        // Принятие сообщения о том, что потребитель оплатил за потребленную энергию
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(receiveProtocol);
        ACLMessage msg = consumerCounter.receive(messageTemplate);

        if (msg != null) {

            System.out.println(ANSI_RED + consumerCounter.getLocalName() +
                    "\n receive payment value " + msg.getContent() +
                    " from " + msg.getSender().getLocalName() +
                    ANSI_RESET);


            try {
                // контракт с Wind1
                if (windContract1 != null && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer") != null
                        && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals(windName1) &&
                        getDataStore().get(consumerCounter.getLocalName() + "register").equals(windName1)) {

                    // Достаем из datastore адрес контракта
                    if (getDataStore().get(consumerCounter.getLocalName() + "address") != null) {

                        String deployedAddress = String
                                .valueOf(getDataStore().get(consumerCounter.getLocalName() + "address"));

                        WindContract windContract1 = WindContract.load(
                                deployedAddress,web3j,credentials,contractGasProvider
                        );

                        // Обновление оплаты за потребляемую энергию от Wind1
                        //System.out.println("======== " + consumerCounter.getLocalName() + " update payment for Wind1 consumption energy =======");
                        if (windContract1.updatePaymentForWindEnergy().send().isStatusOK()) {
                            System.out.println(ANSI_RED + consumerCounter.getLocalName() + " with publicKey : " + publicKey
                                    + "\n update payment fo consumption energy " +
                                    " for Consumer1" + ANSI_RESET);

                            // Потом закомментить (итерация подсчета времени для контроля оплаты и подведения итогов)
                            //iterWind1+=1;
                        }

                    }
                }


                // Контракт с Wind2
                if (windContract2 !=null && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer") != null
                        && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals(windName2) &&
                        getDataStore().get(consumerCounter.getLocalName() + "register").equals(windName2)) {


                    // Достаем из datastore адрес контракта
                    if (getDataStore().get(consumerCounter.getLocalName() + "address") != null) {

                        String deployedAddress = String
                                .valueOf(getDataStore().get(consumerCounter.getLocalName() + "address"));

                        WindContract windContract2 = WindContract.load(
                                deployedAddress,web3j,credentials,contractGasProvider
                        );

                        // Обновление оплаты за потребляемую энергию от Wind2

                        if (windContract2.updatePaymentForWindEnergy().send().isStatusOK()) {
                            System.out.println(ANSI_RED + "======== " + consumerCounter.getLocalName() +
                                    " update payment for Wind2  consumption energy =======" +
                                    "\n" + consumerCounter.getLocalName() + " with publicKey : " + publicKey
                                    + " update payment for consumption energy " +
                                    " for Consumer2" + ANSI_RESET);
                            //iterWind2+=1;
                        }

                    }
                }

                // Контракт с Wind3
                if (windContract3!=null && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer") != null
                        && getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals(windName3) &&
                        getDataStore().get(consumerCounter.getLocalName() + "register").equals(windName3)) {

                    // Достаем из datastore адрес контракта
                    if (getDataStore().get(consumerCounter.getLocalName() + "address") != null) {

                        String deployedAddress = String
                                .valueOf(getDataStore().get(consumerCounter.getLocalName() + "address"));

                        WindContract windContract3 = WindContract.load(
                                deployedAddress,web3j,credentials,contractGasProvider
                        );

                        // Обновление оплаты за потребляемую энергию от Wind3

                        if (windContract3.updatePaymentForWindEnergy().send().isStatusOK()) {
                            System.out.println(ANSI_RED + "======== " + consumerCounter.getLocalName() +
                                    " update payment for Wind3 consumption energy =======" +
                                    "\n" + consumerCounter.getLocalName() + " with publicKey : " + publicKey
                                    + " update payment fo consumption energy " +
                                    " for Consumer3" + ANSI_RESET);
                            //iterWind3+=1;
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


    // TODO: Метод проверки условий выполнения оплаты за покупку энергии у Wind
    //  !Проверка условий осуществляется каждые сутки (
    //  ( пока сделаем счетчик здесь, чтобы отсчитывать сутки (24 итерации обновления оплаты)
    //  , затем будем принимать сигналы по MQTT ( если пришло число 24 x n, то проверяем итоги оплаты
    //   за сутки
    private void controlPaymentForWind(Agent consumerCounter, String publicKey) {

        try {

            if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer") != null) {

               // if isConcludedConsumer == Wind1
                if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("Wind1")) {

                    // Потом эти выводы iterWind1 закомментить, когда буду принимать сигналы о времени по MQTT
                    System.out.println("===================");
                    System.out.println("iterWind1 " + iterWind1);
                    System.out.println("===================");
                    if (windContract1 != null && (int)counterTime == 24) {

                        if (windContract1.controlPaymentForWind().send().isStatusOK()) {
                            System.out.println(ANSI_RED + consumerCounter.getLocalName() + " with publicKey " + publicKey
                                    + " control payment per day from Consumer1" + ANSI_RESET);

                            // ConsumerCounter отправляет сообщение to Wind1, что проверил оплату за энергию
                            sendMsg(consumerCounter, "ControlWindCompleted1",
                                    "WindCounter1","ControlWindCompleted1");

                            // Проверяем значение штрафа и баланса PV1
//                            System.out.println("===================== CHECK FINE AND Wind1 BALANCE ===================");
//                            System.out.println("Must be pay " + windContract1.viewPaymentForWind().send().component2());
//                            System.out.println("Fact payment " + windContract1.viewPaymentForWind().send().component4());
//                            System.out.println("fine " + windContract1.viewPaymentForWind().send().component6());
//                            System.out.println("controlBalanceOfWind " + windContract1.viewPaymentForWind().send().component8());
                        }

                        nWind1+=1;
                    }
                }


                // if isConcludedConsumer == Wind2
                if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("Wind2")) {

                    // Потом закоььентить
                    System.out.println("===================");
                    System.out.println("iterWind2 " + iterWind2);
                    System.out.println("===================");
                    if (windContract2 != null && (int)counterTime == 24) {

                        if (windContract2.controlPaymentForWind().send().isStatusOK()) {
                            System.out.println(ANSI_RED + consumerCounter.getLocalName() + " with publicKey " + publicKey
                                    + " control payment per day from Consumer2" + ANSI_RESET);

                            sendMsg(consumerCounter, "ControlWindCompleted2",
                                    "WindCounter2","ControlWindCompleted2");
                            // Проверяем значение штрафа и баланса PV2 (это выпоолняет WindCounter!!!!!)
//                            System.out.println("===================== CHECK FINE AND Wind2 BALANCE ===================");
//                            System.out.println("Must be pay " + windContract2.viewPaymentForWind().send().component2());
//                            System.out.println("Fact payment " + windContract2.viewPaymentForWind().send().component4());
//                            System.out.println("fine " + windContract2.viewPaymentForWind().send().component6());
//                            System.out.println("controlBalanceOfWind " + windContract2.viewPaymentForWind().send().component8());
                        }

                        nWind2+=1;
                    }

                }


                // if isConcludedConsumer == Wind3
                if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("Wind3")) {

                    // Потом закомментить
                    System.out.println("===================");
                    System.out.println("iterWind3 " + iterWind3);
                    System.out.println("===================");
                    if (windContract3 != null && (int)counterTime == 24) {

                        if (windContract3.controlPaymentForWind().send().isStatusOK()) {
                            System.out.println(ANSI_RED + consumerCounter.getLocalName() + " with publicKey " + publicKey
                                    + " control payment per day from Consumer3" + ANSI_RESET);

                            sendMsg(consumerCounter, "ControlWindCompleted3",
                                    "WindCounter3","ControlWindCompleted");

                            // Проверяем значение штрафа и баланса Wind3
//                            System.out.println("===================== CHECK FINE AND Wind3 BALANCE ===================");
//                            System.out.println("Must be pay " + windContract3.viewPaymentForWind().send().component2());
//                            System.out.println("Fact payment " + windContract3.viewPaymentForWind().send().component4());
//                            System.out.println("fine " + windContract3.viewPaymentForWind().send().component6());
//                            System.out.println("controlBalanceOfWind " + windContract3.viewPaymentForWind().send().component8());

                        }

                        nWind3+=1;

                    }

                }
            }

            } catch(Exception e){
                e.printStackTrace();
            }


        }


}
