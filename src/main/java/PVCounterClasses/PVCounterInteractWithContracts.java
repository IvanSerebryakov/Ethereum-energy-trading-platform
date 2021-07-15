package PVCounterClasses;

import ConnectionUDP.UDPClass;
import MQTTConnection.MQTTSubscriber;
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

public class PVCounterInteractWithContracts extends TickerBehaviour {

    private Web3j web3j;
    private ContractGasProvider contractGasProvider;

    private String PUBLIC_KEY_PVCounter1;
    private String PUBLIC_KEY_PVCounter2;
    private String PUBLIC_KEY_PVCounter3;

    private Credentials credentialsPVCounter1;
    private Credentials credentialsPVCounter2;
    private Credentials credentialsPVCounter3;

    private DataStore dataStore;

    // Экземпляры контрактов
    private PVContract pvContract1;
    private PVContract pvContract2;
    private PVContract pvContract3;

    private PVPenaltyContract pvPenaltyContract1;
    private PVPenaltyContract pvPenaltyContract2;
    private PVPenaltyContract pvPenaltyContract3;

    // Флаги регистрации в основных контрактах
    private boolean flagRegister1;
    private boolean flagRegister2;
    private boolean flagRegister3;

    // Флаги регистрации в штрафных контрактах
    private boolean flagRegisterPenalty1;
    private boolean flagRegisterPenalty2;
    private boolean flagRegisterPenalty3;

    // Рандомайзер вырабаиываемой энергии ( сделаем временно
    //  потом будем принимать энергию из Матлаба по MQTT)
    private double productionEnergy;
    private double consumptionEnergy;

    //Суммы потребляемой энергии от счетчиков потребителей
    private double consumptionEnergy1;
    private double consumptionEnergy2;
    private double consumptionEnergy3;

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

    // Счетчики времени (потом закомментить после принятия данных по MQTT о времени)
    private int counter1;
    private int counter2;
    private int counter3;

    private int dayPV1;
    private int dayPV2;
    private int dayPV3;

    private double counterTime;

    private int day;

    private MQTTSubscriber mqttSubscriber;
    private UDPClass udpClass = new UDPClass();

    public PVCounterInteractWithContracts(Agent a, long period, Web3j web3j, ContractGasProvider contractGasProvider,
                                          String PUBLIC_KEY_PVCounter1, String PUBLIC_KEY_PVCounter2, String PUBLIC_KEY_PVCounter3,
                                          Credentials credentialsPVCounter1, Credentials credentialsPVCounter2,
                                          Credentials credentialsPVCounter3,
                                          DataStore dataStore, boolean flagRegister1, boolean flagRegister2,
                                          boolean flagRegister3, boolean flagRegisterPenalty1, boolean flagRegisterPenalty2,
                                          boolean flagRegisterPenalty3, int counter1, int counter2, int counter3,
                                          int n1, int n2, int n3, int counterTime, int day,
                                          MQTTSubscriber mqttSubscriber) {
        super(a, period);

        this.dataStore = dataStore;
        setDataStore(dataStore);

        this.web3j = web3j;
        this.contractGasProvider = contractGasProvider;

        this.PUBLIC_KEY_PVCounter1 = PUBLIC_KEY_PVCounter1;
        this.PUBLIC_KEY_PVCounter2 = PUBLIC_KEY_PVCounter2;
        this.PUBLIC_KEY_PVCounter3 = PUBLIC_KEY_PVCounter3;

        this.credentialsPVCounter1 = credentialsPVCounter1;
        this.credentialsPVCounter2 = credentialsPVCounter2;
        this.credentialsPVCounter3 = credentialsPVCounter3;

        this.flagRegister1 = flagRegister1;
        this.flagRegister2 = flagRegister2;
        this.flagRegister3 = flagRegister3;

        this.flagRegisterPenalty1 = flagRegisterPenalty1;
        this.flagRegisterPenalty2 = flagRegisterPenalty2;
        this.flagRegisterPenalty3 = flagRegisterPenalty3;

        this.counter1 = counter1;
        this.counter2 = counter2;
        this.counter3 = counter3;

        this.dayPV1 = n1;
        this.dayPV2 = n2;
        this.dayPV3 = n3;

        this.counterTime = counterTime;

        this.day = day;

        this.mqttSubscriber = mqttSubscriber;

    }

    @Override
    protected void onTick() {

        // Вызов метода счетчика времении
        //receiveTimeFromMatlab();

        if (myAgent.getLocalName().equals("PVCounter1")){

            receiveTimeFromDist(myAgent);

            // Регисттрация в контрактах
            pvCounterRegisterInContracts(myAgent, PUBLIC_KEY_PVCounter1, "PVDeployedContractAddress",
                    "PVPenaltyDeployedContractAddress", credentialsPVCounter1);

            // Обновление вырабатываемой энергии
            updateProductionPV(myAgent, PUBLIC_KEY_PVCounter1);

            // Принятие сообщение от счетчика потребителя
            consumptionEnergy1 = receiveConsumptionPVEnergy(myAgent);

            // Отправка сигнала в Матлаб о передаче энергии накопиетлю, в случае профицита энергии
            //sendProficitSignalToPV(myAgent);

            //Контроль поставки электроэнергии за сутки для PV
            controlProductionEnergyPerDayPV(myAgent);

            // Обновление оплаты за перезакупку энергии у Storage1
            updatePaymentRePurchase(myAgent);

            // Проверка условий выполнения оплаты за перезакупку энергии у накопителя для PV1
            controlPaymentPVForStorage(myAgent);

            // Отправка сообщения своему генератору о необходимости
            // выплаты комисси счетчику
            //sendMsgAboutCommission(myAgent, "PV1");

            // Получение сообщения о выплате комиссий и вывод
            // результатов проверки выплаты на экран
            //receiveMsgAboutCommissionPayment(myAgent,
              //      credentialsPVCounter1);

            // Отправка сообщения дистрибьютору о начале нового аукциона
            //sendMsgNewAuction(myAgent);

        }else if (myAgent.getLocalName().equals("PVCounter2")){

            receiveTimeFromDist(myAgent);

            // Регисттрация в контрактах
            pvCounterRegisterInContracts(myAgent, PUBLIC_KEY_PVCounter2, "PVDeployedContractAddress",
                    "PVPenaltyDeployedContractAddress", credentialsPVCounter2);

            // Обновление вырабатываемой энергии
            updateProductionPV(myAgent, PUBLIC_KEY_PVCounter2);

            // Принятие сообщение от счетчика потребителя
            consumptionEnergy2 = receiveConsumptionPVEnergy(myAgent);

            // Отправка сигнала в Матлаб о передаче энергии накопиетлю, в случае профицита энергии
            //sendProficitSignalToPV(myAgent);

            //Контроль поставки электроэнергии за сутки для PV
            controlProductionEnergyPerDayPV(myAgent);

            // Обновление оплаты за перезакупку энергии у Storage2
            updatePaymentRePurchase(myAgent);

            // Проверка условий выполнения оплаты за перезакупку энергии у накопителя для PV2
            controlPaymentPVForStorage(myAgent);

            // Отправка сообщения своему генератору о необходимости
            // выплаты комисси счетчику
            //sendMsgAboutCommission(myAgent, "PV2");

            // Получение сообщения о выплате комиссий и вывод
            // результатов проверки выплаты на экран
            //receiveMsgAboutCommissionPayment(myAgent,
             //       credentialsPVCounter2);

            // Отправка сообщения дистрибьютору о начале нового аукциона
            //sendMsgNewAuction(myAgent);

        }else {

            receiveTimeFromDist(myAgent);

            // Регисттрация в контрактах
            pvCounterRegisterInContracts(myAgent, PUBLIC_KEY_PVCounter3, "PVDeployedContractAddress",
                    "PVPenaltyDeployedContractAddress", credentialsPVCounter3);

            // Обновление вырабатываемой энергии
            updateProductionPV(myAgent, PUBLIC_KEY_PVCounter3);

            // Принятие сообщение от счетчика потребителя
            consumptionEnergy3 = receiveConsumptionPVEnergy(myAgent);

            // Отправка сигнала в Матлаб о передаче энергии накопиетлю, в случае профицита энергии
            //sendProficitSignalToPV(myAgent);

            //Контроль поставки электроэнергии за сутки для PV
            controlProductionEnergyPerDayPV(myAgent);

            // Обновление оплаты за перезакупку энергии у Storage3
            updatePaymentRePurchase(myAgent);

            // Проверка условий выполнения оплаты за перезакупку энергии у накопителя для PV3
            controlPaymentPVForStorage(myAgent);

            // Отправка сообщения своему генератору о необходимости
            // выплаты комисси счетчику
            //sendMsgAboutCommission(myAgent, "PV3");

            // Получение сообщения о выплате комиссий и вывод
            // результатов проверки выплаты на экран
            //receiveMsgAboutCommissionPayment(myAgent,
                 //   credentialsPVCounter3);

            // Отправка сообщения дистрибьютору о начале нового аукциона
            //sendMsgNewAuction(myAgent);
        }


    }

    //TODO: PVCounters принимают адреса контрактов от PV, создают экземпляры контрактов
    // и регистрируются в контрактах
    private void pvCounterRegisterInContracts(Agent pvCounter, String publicKey, String mainProtocol,
                                              String penaltyProtocol, Credentials credentials){

        // PVCounters принимают адреса основных контрактов
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(mainProtocol);
        ACLMessage msg = pvCounter.receive(messageTemplate);
        try {

        if (msg!=null){

            String deployedAddress = msg.getContent();

            // Складываем адреса контрактов в datastore для счетчиков PV
            getDataStore().put(pvCounter.getLocalName() + "deployedAddress",deployedAddress);

            System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " receive deployedAddress " +
                    deployedAddress + " from " + msg.getSender().getLocalName() + ANSI_RESET);


            // Если PVCounter1
            if (pvCounter.getLocalName().equals("PVCounter1") && flagRegister1){

                // PVCounter1 создает экземпляр смарт-контракта pvContract1
                pvContract1 = PVContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " with publicKey " + publicKey
                        + " create pvContract1 sample " +
                        pvContract1.getContractAddress() + ANSI_RESET);

                // PVCounter1 регистрируется в контракте

                    if (pvContract1.registrationPVCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " with publicKey : " +
                                publicKey + " register in pvContract1 " + pvContract1.getContractAddress() + ANSI_RESET);
                    }

                    flagRegister1 = false;

            }


            // Если PVCounter2
               if (pvCounter.getLocalName().equals("PVCounter2") && flagRegister2){

                   // PVCounter2 создает экземпляр смарт-контракта
                   pvContract2 = PVContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                   System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " with publicKey " + publicKey
                           + " create pvContract2 sample " +
                           pvContract2.getContractAddress() + ANSI_RESET);

                   //PVCounter2 регистрируется в контакте
                   if (pvContract2.registrationPVCounter(publicKey).send().isStatusOK()){
                       System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " with publicKey " + publicKey
                       + " register in pvContract2 " + pvContract2.getContractAddress() + ANSI_RESET);
                   }

                   flagRegister2 = false;
               }

               // Если PVCounter3
                if (pvCounter.getLocalName().equals("PVCounter3") && flagRegister3){

                    // PVCounter3 создает экземпляр контракта
                    pvContract3 = PVContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " with publicKey " + publicKey +
                            " create pvContract3 sample " + pvContract3.getContractAddress() + ANSI_RESET);

                    // PVCounter3 регистрируется в контаркте
                    if (pvContract3.registrationPVCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " with publicKey " + publicKey +
                                " register in pvContract3 " + pvContract3.getContractAddress() + ANSI_RESET);
                    }

                    flagRegister3 = false;
                }


        }else {
            block();
        }


        // PVCounters принимают адреса штрафных контрактов
        MessageTemplate messageTemplate1 = MessageTemplate.MatchProtocol(penaltyProtocol);
        ACLMessage msg1 = pvCounter.receive(messageTemplate1);

        if (msg1!=null){

            String penaltyAddress = msg1.getContent();

            System.out.println(pvCounter.getLocalName() + " receive penaltyAddress " +
                    penaltyAddress + " from " + msg1.getSender().getLocalName());

            // Если PVCounter1
            if (pvCounter.getLocalName().equals("PVCounter1") && flagRegisterPenalty1){

                //PVCounter1 создает экземпляр контаркта
                pvPenaltyContract1 = PVPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);

                System.out.println(pvCounter.getLocalName() + " with publicKey " + publicKey +
                        " create pvPenaltyContract1 sample " + pvPenaltyContract1.getContractAddress());

                // PVCounter1 регистрируется в штрафном коонтракте
                if (pvPenaltyContract1.registrationPVCounter(publicKey).send().isStatusOK()){
                    System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " with publicKey " + publicKey +
                            " register in pvPenaltyContract1 " + pvPenaltyContract1.getContractAddress() + ANSI_RESET);
                }

                flagRegisterPenalty1 = false;
            }

            // Если PVCounter2
            if (pvCounter.getLocalName().equals("PVCounter2") && flagRegisterPenalty2){

                //PVCounter2 создает экземпляр контракта
                pvPenaltyContract2 = PVPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);

                System.out.println(pvCounter.getLocalName() + " with publicKey " + publicKey +
                        " create pvPenaltyContract2 sample " + pvPenaltyContract2.getContractAddress());

                // PVCounter2 регистрируется в контракте
                if (pvPenaltyContract2.registrationPVCounter(publicKey).send().isStatusOK()){
                    System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " with publicKey " + publicKey +
                            " register in pvPenaltyContract2 " + pvPenaltyContract2.getContractAddress() + ANSI_RESET);
                }

                flagRegisterPenalty2 = false;
            }

            // Если PVCounter3
            if (pvCounter.getLocalName().equals("PVCounter3") && flagRegisterPenalty3){

                // PVCounter3 создает экземпляр контракта
                pvPenaltyContract3 = PVPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);

                System.out.println(pvCounter.getLocalName() + " with publicKey " + publicKey +
                        " create pvPenaltyContract3 sample " + pvPenaltyContract3.getContractAddress());

                // PVCounter3 вписывает себя в контракт
                if (pvPenaltyContract3.registrationPVCounter(publicKey).send().isStatusOK()){
                    System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " with publicKey " + publicKey +
                            " register in pvPenaltyContract3 " + pvPenaltyContract3.getContractAddress() + ANSI_RESET);
                }

                flagRegisterPenalty3 = false;
            }


        }else {
            block();
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: Метод принятия потребляемой энергии от счетчиков потребителй
    private Double receiveConsumptionPVEnergy(Agent receiver){
        // Принимаем данные от счетчиков потребителей о кол-ве потребляемой энергии
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("ExchangePVEnergy");
        ACLMessage msg = receiver.receive(messageTemplate);

        if (msg!=null) {

            consumptionEnergy = Double.parseDouble(msg.getContent());
            System.out.println(ANSI_BLUE + receiver.getLocalName() + " receive consumption energy " +
                    consumptionEnergy + " from " + msg.getSender().getLocalName() + ANSI_RESET);

            // После принятия сообщения от счетчика потребителя о потребляемой энергии
            // добавляем в datastore имя счетчика потребителя
            getDataStore().put(receiver.getLocalName() + "consumerCounter", msg.getSender().getLocalName());

//            System.out.println("PRODUCTION PV1 ENERGY: " + productionEnergy);
//            System.out.println("CONSUMPTION PV1 ENERGY: " + consumptionEnergy);

        }else {
            block();
        }

        return consumptionEnergy;
    }


    // TODO: Метод для обновления количества вырабатываемой энергии для PV
    //  PVCounters являются подписчиками MQTT, они принимают
    //  сигналы вырабатываемой энергии из Матлаба (пока сделаем, что вырабатываемая энергия
    //  случайным образом меняется в этом классе и принимается на вход метода
    //  updateProductionPV ( рандомайзер вырабатываемой энергии)
    private void updateProductionPV(Agent pvCounter, String publicKey) {

        try {

            // Если PVCounter1
        if (pvCounter.getLocalName().equals("PVCounter1")) {


            productionEnergy =  mqttSubscriber.mqttSubscriber("PVCounter1","PVCounter1");

            System.out.println(ANSI_CYAN_BACKGROUND + " ++++++++++++++++++++++++++ " +
                    " PRODUCTION ENERGY PVCounter1 " + productionEnergy + ANSI_RESET);

            //productionEnergy = 1000 + Math.random() * 200;

            // После получения вырабатываемой энергии из Матлаба
            // счетчик PV отправляет сообщение счетчику потребителя,
            // чтобы тот сравнил и посчитал разницу
            // Сперва берем имя ConsumerCounter из datastore
            if (getDataStore().get(pvCounter.getLocalName() + "consumerCounter")!=null) {
                String consumerCounter =
                        String.valueOf(getDataStore().get(pvCounter.getLocalName() + "consumerCounter"));
                sendMsg(pvCounter, "ProductionEnergyPV",
                        consumerCounter, String.valueOf(productionEnergy));
            }

            // Если PVCounter1 посчитает, что вырабатываемая энергия > потребляемой энергии, то
            // PVCounter1 отправит сообщение счетчику накопителя to StorageCounter1
            if (pvContract1!=null && consumptionEnergy1!=0 && productionEnergy > consumptionEnergy1){
                System.out.println("PRODUCTION PV1 ENERGY: " + productionEnergy);
                System.out.println("CONSUMPTION PV1 ENERGY: " + consumptionEnergy1);
                // Отправка сообщени счетчику накопителя StorageCounter1
                sendMsg(pvCounter, "ProficitEnergy","StorageCounter1","ProficitEnergy");

                // TODO: Отправка сообщения накопителю с разницуй выработки и потребления
                //  энергии в случае профицита
                double difference = productionEnergy - consumptionEnergy1;
                sendProficitSignalToStorageCounter(pvCounter,difference);
            }


            // После принятия данных по MQTT из Матлаба о выработке энергии PVCounter1
            // отправляет сигнал to Consumer

            // Если кол-во текущей вырабатываемой энергии != null, то принимаем на вход функции
            // updateProductionPV контракта

            // TODO: Если counterTime > 7, то начинаем контролировать заключивших контракты PV
            if (pvContract1!=null && (int)counterTime > 7){
                if (pvContract1.updateProductionPV(BigInteger.valueOf((int)productionEnergy)).send().isStatusOK()){
                    System.out.println();
                    System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " with publicKey : " + publicKey
                            + " update productionEnergy for pv1 " + ANSI_RESET);

                    // TODO: Временно ставим счетчик времени, чтобы отсчитывать сутки для подведения итогов
                    //  поставки энергии за сутки для PV (потом будем принимать сигналы о времени по MQTT)
                    //counter1+=1;
//                    System.out.println(ANSI_BLUE + " ======== COUNTER1 PVCOUNTER1 ======== " + counter1 + ANSI_RESET);

                }
            }

        }

        // Если PVCounter2
            if (pvCounter.getLocalName().equals("PVCounter2")) {

                productionEnergy = mqttSubscriber.mqttSubscriber("PVCounter2","PVCounter2");

                System.out.println(ANSI_CYAN_BACKGROUND + " ++++++++++++++++++++++++++ " +
                        " PRODUCTION ENERGY PVCounter2 " + productionEnergy + ANSI_RESET);

                // После получения вырабатываемой энергии из Матлаба
                // счетчик PV отправляет сообщение счетчику потребителя,
                // чтобы тот сравнил и посчитал разницу
                // Сперва берем имя ConsumerCounter из datastore
                if (getDataStore().get(pvCounter.getLocalName() + "consumerCounter")!=null){
                    String consumerCounter =
                            String.valueOf(getDataStore().get(pvCounter.getLocalName() + "consumerCounter"));
                    sendMsg(pvCounter, "ProductionEnergyPV",
                            consumerCounter, String.valueOf(productionEnergy));
                }

                // Если PVCounter1 посчитает, что вырабатываемая энергия > потребляемой энергии, то
                // PVCounter1 отправит сообщение счетчику накопителя to StorageCounter1
                if (pvContract2!=null && consumptionEnergy2!=0 && productionEnergy > consumptionEnergy2){
                    System.out.println("PRODUCTION PV2 ENERGY: " + productionEnergy);
                    System.out.println("CONSUMPTION PV2 ENERGY: " + consumptionEnergy2);
                    // Отправка сообщени счетчику накопителя StorageCounter1
                    sendMsg(pvCounter, "ProficitEnergy","StorageCounter2","ProficitEnergy");

                    // TODO: Отправка сообщения накопителю с разницуй выработки и потребления
                    //  энергии в случае профицита
                    double difference = productionEnergy - consumptionEnergy1;
                    sendProficitSignalToStorageCounter(pvCounter,difference);
                }


                // TODO: Если counterTime > 7, то начинаем контролировать заключивших контракты PV
                if (pvContract2!=null && (int)counterTime > 7){
                    if (pvContract2.updateProductionPV(BigInteger.valueOf((int)productionEnergy)).send().isStatusOK()){
                        System.out.println();
                        System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " with publicKey : " + publicKey
                                + " update productionEnergy for pv2" + ANSI_RESET);

                        // TODO: Временно ставим счетчик времени, чтобы отсчитывать сутки для подведения итогов
                        //  поставки энергии за сутки для PV (потом будем принимать сигналы о времени по MQTT)
                        //counter2+=1;

//                        System.out.println(ANSI_BLUE + " ======== COUNTER2 PVCOUNTER2 ======== " + counter2 + ANSI_RESET);
                    }
                }
            }

            // Если PVCounter3
            if (pvCounter.getLocalName().equals("PVCounter3")){

                productionEnergy = mqttSubscriber.mqttSubscriber("PVCounter3","PVCounter3");

                System.out.println(ANSI_CYAN_BACKGROUND + " ++++++++++++++++++++++++++ " +
                        " PRODUCTION ENERGY PVCounter3 " + productionEnergy + ANSI_RESET);

                // После получения вырабатываемой энергии из Матлаба
                // счетчик PV отправляет сообщение счетчику потребителя,
                // чтобы тот сравнил и посчитал разницу
                // Сперва берем имя ConsumerCounter из datastore
                if (getDataStore().get(pvCounter.getLocalName() + "consumerCounter")!=null){
                    String consumerCounter =
                            String.valueOf(getDataStore().get(pvCounter.getLocalName() + "consumerCounter"));
                    sendMsg(pvCounter, "ProductionEnergyPV",
                            consumerCounter, String.valueOf(productionEnergy));
                }

                // Если PVCounter1 посчитает, что вырабатываемая энергия > потребляемой энергии, то
                // PVCounter1 отправит сообщение счетчику накопителя to StorageCounter1
                if (pvContract3!=null && consumptionEnergy3!=0 && productionEnergy > consumptionEnergy3){
                    System.out.println("PRODUCTION PV3 ENERGY: " + productionEnergy);
                    System.out.println("CONSUMPTION PV3 ENERGY: " + consumptionEnergy3);
                    // Отправка сообщени счетчику накопителя StorageCounter1
                    sendMsg(pvCounter, "ProficitEnergy","StorageCounter3","ProficitEnergy");

                    // TODO: Отправка сообщения накопителю с разницуй выработки и потребления
                    //  энергии в случае профицита
                    double difference = productionEnergy - consumptionEnergy1;
                    sendProficitSignalToStorageCounter(pvCounter,difference);
                }


                // TODO: Если counterTime > 7, то начинаем контролировать заключивших контракты PV
                if (pvContract3!=null && (int)counterTime > 7){

                    if (pvContract3.updateProductionPV(BigInteger.valueOf((int)productionEnergy)).send().isStatusOK()){
                        System.out.println();
                        System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " with publicKey : " + publicKey
                                + " update productionEnergy for pv3" + ANSI_RESET);

                        //counter3 +=1;
//                        System.out.println(ANSI_BLUE + " ======== COUNTER3 PVCOUNTER3 ======== " + counter3 + ANSI_RESET);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println();;
        }

    }

    // TODO: Метод принятия сигналов о времени из Матлаба по MQTT
    private void receiveTimeFromMatlab(){

        counterTime = mqttSubscriber.mqttSubscriber("Time","PVCounter");

    }

    // TODO: Метод принятия времени от Дистрибьютора
    private void receiveTimeFromDist(Agent pvCounter){
        // Пока просто сделаем счетчик времени (потом закомментить)
//        counterTime = mqttSubscriber.mqttSubscriber("Time","WindCounter");
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("TimeFromMatlab");
        ACLMessage msg = pvCounter.receive(messageTemplate);

        if (msg!=null){
            counterTime = Double.parseDouble(msg.getContent());
            System.out.println(ANSI_GREEN + pvCounter.getLocalName() +
                    " receive " + counterTime +
                    " from " + counterTime + ANSI_RESET);
        }else {
            block();
        }

    }


    //TODO: Метод для контроля поставки электроэнергии по истечении дня (когда PV
    // перестал вырабатывать энергию)
    private void controlProductionEnergyPerDayPV(Agent pvCounter){

        //
        try {

            // Для PV1
            // timeFinish - время окончания солнечного дня - проверка выполнения условий контракта
            // Сравнение количество выработанной энергии и законтрактованной
        if ((int)counterTime == 18 /*+ 24 * day*/){
            if (pvContract1!=null && pvCounter.getLocalName().equals("PVCounter1")){

                String result = pvContract1.controlProductionEnergyPerDayPV
                        (BigInteger.valueOf(dayPV1)).send().component1();
                BigInteger difference = pvContract1.controlProductionEnergyPerDayPV
                        (BigInteger.valueOf(dayPV1)).send().component2();
                System.out.println(ANSI_BLUE_BACKGROUND + "========= " + pvCounter.getLocalName() + " CONTROL PRODUCTION " +
                        "ENERGY PER DAY PV1 ========" +
                        "\nFact production energy " + result +
                        "\nDifference between contracted energy and transmitted energy " +
                        difference + ANSI_RESET);

                // PVCounter1 проверяет нужна ли презакупка энергии у накопителя
                // в случае меньшей поставки
                if (result.equals("less")){
                    // PVCounter1 отправляет разницу энергии своему генератору,
                    // за которую необходимо заплатить PV1 генератору
                    sendMsg(pvCounter,"RePurchaseStorageEnergyPV","PV1", String.valueOf(difference));
                }

                // TODO: По истечении суток счетчик PV отправляет сообщение своему PV
                //  генератору о подведении итогов
//                sendMsg(pvCounter,"StartNewPVAuction", "PV1","StartNewPVAuction");

                // TODO: Отправка сообщения дистрибьютору
//                sendMsg(pvCounter,"StartNewPVAuction","Distributor","StartNewPVAuction");

            }
        }

        // Для PV2
            if ((int)counterTime == 18 /*+ 24 * day*/){
                if (pvContract2!=null && pvCounter.getLocalName().equals("PVCounter2")){
                    String result = pvContract2.controlProductionEnergyPerDayPV
                            (BigInteger.valueOf(dayPV2)).send().component1();
                    BigInteger difference = pvContract2.controlProductionEnergyPerDayPV
                            (BigInteger.valueOf(dayPV2)).send().component2();
                    System.out.println(ANSI_BLUE_BACKGROUND + "========= " + pvCounter.getLocalName() + " CONTROL PRODUCTION " +
                            "ENERGY PER DAY PV2 ========" +
                            "\nFact production energy " + result +
                            "\nDifference between contracted energy and transmitted energy " +
                            difference + ANSI_RESET);

                    // PVCounter2 отписывается своему генератору о необходимости
                    // перезакупки энергии у накопителя (отправляем difference для оплаты)
                    if (result.equals("less")){
                        sendMsg(pvCounter,"RePurchaseStorageEnergyPV","PV2", String.valueOf(difference));
                    }

                    // TODO: По истечении суток счетчик PV отправляет сообщение своему PV
                    //  генератору о подведении итогов
//                    sendMsg(pvCounter,"StartNewPVAuction", "PV2","StartNewPVAuction");

                    // TODO: Отправка сообщения дистрибьютору
//                    sendMsg(pvCounter,"StartNewPVAuction","Distributor","StartNewPVAuction");
                }
            }

            // Для PV3
            if ((int)counterTime == 18 /*+ 24 * day*/){
                if (pvContract3!=null && pvCounter.getLocalName().equals("PVCounter3")){

                    String result = pvContract3.controlProductionEnergyPerDayPV
                            (BigInteger.valueOf(dayPV3)).send().component1();
                    BigInteger difference = pvContract3.controlProductionEnergyPerDayPV
                            (BigInteger.valueOf(dayPV3)).send().component2();

                    System.out.println(ANSI_BLUE_BACKGROUND + "========= " + pvCounter.getLocalName() + " CONTROL PRODUCTION " +
                            "ENERGY PER DAY PV3 ========"
                            + "\nFact production energy " + result +
                            "\nDifference between contracted energy and transmitted energy " +
                            difference + ANSI_RESET);

                    // PVCounter3 отписывается своему генератору о необходимости
                    // перезакупки энергии у накопителя (отправляем difference для оплаты)
                    if (result.equals("less")){
                        sendMsg(pvCounter,"RePurchaseStorageEnergyPV","PV3", String.valueOf(difference));
                    }

                    // TODO: По истечении суток счетчик PV отправляет сообщение своему PV
                    //  генератору о подведении итогов
//                    sendMsg(pvCounter,"StartNewPVAuction", "PV3","StartNewPVAuction");

                    // TODO: Отправка сообщения дистрибьютору
//                    sendMsg(pvCounter,"StartNewPVAuction","Distributor","StartNewPVAuction");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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
        System.out.println(ANSI_BLUE + sender.getLocalName() + " send " + msg.getContent() +
                " to " + receiver + ANSI_RESET);
        System.out.println();
    }


    // TODO: Отправка разницы энергии (профицит) накопителю
    private void sendProficitSignalToStorageCounter(Agent pvCounter, double differenceProfocitEnergy){

        // Принимаем сообщение от StorageCounter с согласием на поставку энергии накопителю
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("AgreeProficitPVEnergy");
        ACLMessage msg = pvCounter.receive(messageTemplate);

        if (msg!=null){

            System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " receive " + msg.getContent() +
                    " from " + msg.getSender().getLocalName() + ANSI_RESET);

            // Никаких сигналов в Матлаб о передаче профицита мощности
            // передавать не надо, так как эта мощность и так уже плюсуется к накопителю
            // Пока не принимаем сигнал из Матлаба - отправим сообщение ( как начало передачи энергии)
            sendMsg(pvCounter, "StartPVEnergy", msg.getSender().getLocalName(),
                    String.valueOf(differenceProfocitEnergy));


        }else {
            block();
        }

    }


    // TODO: Принятие сообщение от PV, что тот оплатил за перезакупку
    //  энергии у накопителя
    private void updatePaymentRePurchase(Agent pvCounter){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("PaymentToStorageCompletedPV");
        ACLMessage msg = pvCounter.receive(messageTemplate);

        if (msg!=null){
            System.out.println(pvCounter.getLocalName() +
                    " receive " + msg.getContent() + " from " +
                    msg.getSender().getLocalName());
            try {

            if (pvCounter.getLocalName().equals("PVCounter1")){
                if (pvContract1!=null){

                    if (pvContract1.updatePaymentRePurchaseStorageEnergyForPV().send().isStatusOK()){
                        System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " update payment for " +
                                "rePurchase energy from Storage1" + ANSI_RESET);
                    }

                }
            }

            if (pvCounter.getLocalName().equals("PVCounter2")){
                if (pvContract2!=null){
                    if (pvContract2.updatePaymentRePurchaseStorageEnergyForPV().send().isStatusOK()){
                        System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " update payment for " +
                                "rePurchase energy from Storage2" + ANSI_RESET);
                    }
                }
            }

            if (pvCounter.getLocalName().equals("PVCounter3")){
                if (pvContract3!=null){
                    if (pvContract3.updatePaymentRePurchaseStorageEnergyForPV().send().isStatusOK()){
                        System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " update payment for " +
                                "rePurchase energy from Storage3" + ANSI_RESET);
                    }
                }
            }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            block();
        }

    }


    // TODO: Метод проверки условий выполнения оплаты за
    //  перезакупку энергии у накопителя для PV
    private void controlPaymentPVForStorage(Agent pvCounter){

        try {

        if (pvCounter.getLocalName().equals("PV1")){
            if ((int)counterTime == 18) {
                if (pvContract1 != null) {
                    if (pvContract1.controlPaymentPVForStorage().send().isStatusOK()) {
                        System.out.println(pvCounter.getLocalName() + " control " +
                                "payment for repurchase energy from Storage1");

                        // После проверки условий выполнения оплаты за
                        // перезакупку энергии у накопителя для PV1
                        // выводим на экран итоги
                        System.out.println(ANSI_BLUE_BACKGROUND +
                                "========= Total payment for PV1 for " +
                                "repurchase energy from Storage1 =========" +
                                "\nMust be pay for storage energy (pv) " +
                                pvContract1.viewPaymentPVForStorage().send().component2() +
                                "\nfact payment " +
                                pvContract1.viewPaymentPVForStorage().send().component4() +
                                "\npenny " +
                                pvContract1.viewPaymentPVForStorage().send().component6() +
                                "\nstorage1 balance " +
                                pvContract1.viewPaymentPVForStorage().send().component8() + ANSI_RESET);
                    }
                }
            }
        }


        if (pvCounter.getLocalName().equals("PV2")){
            if ((int)counterTime == 18){
                if (pvContract2!=null){
                    if (pvContract2.controlPaymentPVForStorage().send().isStatusOK()){
                        System.out.println(ANSI_BLUE + pvCounter.getLocalName() + " control " +
                                "payment for repurchase energy from Storage2" + ANSI_RESET);

                        // После проверки условий выполнения оплаты за
                        // перезакупку энергии у накопителя для PV1
                        // выводим на экран итоги
                        System.out.println(ANSI_BLUE_BACKGROUND +
                                "========= Total payment for PV2 for " +
                                "repurchase energy from Storage2 =========" +
                                "\nMust be pay for storage energy (pv) " +
                                pvContract2.viewPaymentPVForStorage().send().component2() +
                                "\nfact payment " +
                                pvContract2.viewPaymentPVForStorage().send().component4() +
                                "\npenny " +
                                pvContract2.viewPaymentPVForStorage().send().component6() +
                                "\nstorage2 balance " +
                                pvContract2.viewPaymentPVForStorage().send().component8() + ANSI_RESET);
                    }
                }
            }
        }


        if (pvCounter.getLocalName().equals("PV3")){
            if ((int)counterTime == 18){
                if (pvContract3!=null){
                    if (pvContract3.controlPaymentPVForStorage().send().isStatusOK()){
                        System.out.println(pvCounter.getLocalName() + " control " +
                                "payment for repurchase energy from Storage3");

                        // После проверки условий выполнения оплаты за
                        // перезакупку энергии у накопителя для PV1
                        // выводим на экран итоги
                        System.out.println(ANSI_BLUE_BACKGROUND +
                                "========= Total payment for PV3 for " +
                                "repurchase energy from Storage3 =========" +
                                "\nMust be pay for storage energy (pv) " +
                                pvContract3.viewPaymentPVForStorage().send().component2() +
                                "\nfact payment " +
                                pvContract3.viewPaymentPVForStorage().send().component4() +
                                "\npenny " +
                                pvContract3.viewPaymentPVForStorage().send().component6() +
                                "\nstorage3 balance " +
                                pvContract3.viewPaymentPVForStorage().send().component8() + ANSI_RESET);
                    }
                }
            }
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // TODO: PVCounter в 18 00 ( конец солнечного дня) отправит
    //  сообщение своему генератору, чтобы тот выплатил
    //  ему комиссию за контроль процесса торгов и выполнение транзакций контроля
    private void sendMsgAboutCommission(Agent pvCounter, String pvName){

        // Создаем в этом методе экземпляр контракта
        if (counterTime == 18) {
            // PVCounter достает из своего datastore адрес развернутого основного
            // контракта
            // TODO: Делаем эту проверку для того, чтобы только
            //  учавствующий в контаркте PVCounter
            //  отправлял сообщение своему PV генератору
            if (getDataStore().get(pvCounter.getLocalName() + "deployedAddress") != null) {
//                String deployedAddress =
//                        String.valueOf(
//                                getDataStore().get(pvCounter.getLocalName() + "deployedAddress"));
//                PVContract pvContract = PVContract.load(deployedAddress,
//                        web3j, credentials, contractGasProvider);
//
//                System.out.println(pvCounter.getLocalName() +
//                        " create pvContract sample " + pvContract.getContractAddress());
//
//                // PVCounter вы
//            }
                // TODO: По истечении дня и проверки условий выполнения контракта
                //  PVCounter отпрвляет сообщение своему PV генератору,
                //  чтобы тот выплатил ему комиссию
                sendMsg(pvCounter, "NeedToPayCommission",
                        pvName, "NeedToPayCommission");
            }
        }

    }


    // TODO: PVCounters получают сообщение от своих
    //  потребителей, что те оплатили им комиссию
    private void receiveMsgAboutCommissionPayment(Agent pvCounter,
                                                  Credentials credentials){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(
                "CommissionToPVCounterPaid");
        ACLMessage msg = pvCounter.receive(messageTemplate);

        if (msg!=null){
            System.out.println(pvCounter.getLocalName() +
                    " receive msg " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());

            // После получения сообщения об успешной оплате
            // комиссии, счетчики PV создают
            // экземпляры контрактов в этом методе
            if (getDataStore().get(pvCounter.getLocalName() + "deployedAddress") != null){
                String deployedAddress =
                        String.valueOf(
                                getDataStore().get(pvCounter.getLocalName() + "deployedAddress")
                        );
                PVContract pvContract = PVContract.load(
                        deployedAddress,web3j,credentials,contractGasProvider
                );
                System.out.println(pvCounter.getLocalName() +
                        " create pvContract sample " + pvContract.getContractAddress() +
                        " in receiveMsgAboutCommissionPayment() method");

                // После создания экземпляра контракта
                // PVCounter проверяет выплату комиссии
                // и начисляет штраф в случае невыплаты
                try {
                    BigInteger mustPayment = pvContract.controlCommissionPVCounter().send().component2();
                    BigInteger factPayment = pvContract.controlCommissionPVCounter().send().component4();

                    System.out.println(ANSI_BLUE_BACKGROUND + "======== Control commission payment" +
                            " to " + pvCounter.getLocalName() + " ========" +
                            "\nMust be pay " + mustPayment +
                            "\nfact payment " + factPayment + ANSI_RESET);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }else {
            block();
        }
    }


    // TODO: По окончании суток PVCounter отправляет
    //  сообщение дистрибьютору о начале нового аукциона
    private void sendMsgNewAuction(Agent pvCounter){

        // Пока временно ставим отрезок времени в течение которого
        // pvCounters будут отправлять сообщение о начале нового аукицона
        if (counterTime >= 24 && counterTime <= 26){
            sendMsg(pvCounter,"NewPVAuction",
                    "Distributor","NewPVAuction");
        }
    }


    // TODO: Метод для отправки сигнала в Матлаб о старте передачи сигнала в систему
    private void sendingToMatlab(int port, String signal){

        // Установка соединения
        DatagramSocket datagramSocket = udpClass.connectionUDP(port + 200);
        udpClass.sendingUDP(datagramSocket, port, signal);


    }


}
