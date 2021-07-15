package WindCounterClasses;

import MQTTConnection.MQTTSubscriber;
import SmartContracts.Wind.WindContract;
import SmartContracts.Wind.WindPenaltyContract;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.eclipse.paho.client.mqttv3.internal.ConnectActionListener;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;

public class WindCounterInteractWithContracts extends TickerBehaviour {

    private DataStore dataStore;
    // Соединение с Ganache
    private Web3j web3j;
    private ContractGasProvider contractGasProvider;

    private String PUBLIC_KEY_WindCounter1;
    private String PUBLIC_KEY_WindCounter2;
    private String PUBLIC_KEY_WindCounter3;

    private Credentials credentials1;
    private Credentials credentials2;
    private Credentials credentials3;

    // Флаги регистрации в основных контрактах
    private boolean flagRegister1;
    private boolean flagRegister2;
    private boolean flagRegister3;

    // Флаги регистрации в штрафных контрактах
    private boolean flagRegisterPenalty1;
    private boolean flagRegisterPenalty2;
    private boolean flagRegisterPenalty3;

    // Экземпляры контрактов
    private WindContract windContract1;
    private WindContract windContract2;
    private WindContract windContract3;

    // Экземпляры штрафных контрактов
    private WindPenaltyContract windPenaltyContract1;
    private WindPenaltyContract windPenaltyContract2;
    private WindPenaltyContract windPenaltyContract3;

    // Рандомайзер вырабаиываемой энергии ( сделаем временно
    //  потом будем принимать энергию из Матлаба по MQTT)
    private double productionEnergy;

    // Кол-во энергии, принимаемое от потребителя
    private double consumeEnergy;
    private double consumeEnergyWind1;
    private double consumeEnergyWind2;
    private double consumeEnergyWind3;

    // Счетчик времени (потом закоментить)
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

    private MQTTSubscriber mqttSubscriber = new MQTTSubscriber();

    public WindCounterInteractWithContracts(Agent a, long period, Web3j web3j, ContractGasProvider contractGasProvider,
                                            String PUBLIC_KEY_WindCounter1, String PUBLIC_KEY_WindCounter2,
                                            String PUBLIC_KEY_WindCounter3, Credentials credentials1,
                                            Credentials credentials2, Credentials credentials3, DataStore dataStore,
                                            boolean flagRegister1, boolean flagRegister2, boolean flagRegister3,
                                            boolean flagRegisterPenalty1, boolean flagRegisterPenalty2,
                                            boolean flagRegisterPenalty3, int counterTime) {
        super(a, period);

        this.dataStore = dataStore;
        setDataStore(dataStore);

        this.web3j = web3j;
        this.contractGasProvider = contractGasProvider;

        this.PUBLIC_KEY_WindCounter1 = PUBLIC_KEY_WindCounter1;
        this.PUBLIC_KEY_WindCounter2 = PUBLIC_KEY_WindCounter2;
        this.PUBLIC_KEY_WindCounter3 = PUBLIC_KEY_WindCounter3;

        this.credentials1 = credentials1;
        this.credentials2 = credentials2;
        this.credentials3 = credentials3;

        this.flagRegister1 = flagRegister1;
        this.flagRegister2 = flagRegister2;
        this.flagRegister3 = flagRegister3;

        this.flagRegisterPenalty1 = flagRegisterPenalty1;
        this.flagRegisterPenalty2 = flagRegisterPenalty2;
        this.flagRegisterPenalty3 = flagRegisterPenalty3;

        this.counterTime = counterTime;
    }

    @Override
    protected void onTick() {

        //receiveTimeFromMatlab();

        if (myAgent.getLocalName().equals("WindCounter1")){

            receiveTimeFromDist(myAgent);

            // Регистрация в контракте
            windCounterRegisterInContracts(myAgent, PUBLIC_KEY_WindCounter1, "WindDeployedContractAddress",
                    "WindPenaltyDeployedContractAddress", credentials1);

            // Обновление вырабатываемой энергии в контракте
            updateProductionWind(myAgent, PUBLIC_KEY_WindCounter1);

            // Контроль поставки электроэнергии за сутки для Wind1
            controlProductionEnergyPerDayWind(myAgent);

            // Обновление оплаты за перезакупку энергии у накопителя для Wind1
            updateRePurchaseEnergy(myAgent);

            //Проверка выполнения оплаты за перезакупку энергии у накопителя для Wind
            // и подведение итогов выполнения условий оплаты
            //controlPaymentWindForStorage(myAgent);

            // Просмотр итогов контракта по оплате потребленной энергии для потребителя (в сутки)
            viewPaymentForWind(myAgent);

            // Принятие от счетчика потребителя потребляемой энергии
            consumeEnergyWind1 = receiveConsumeEnergy(myAgent);

            // Принятие согласия о подаче энергии накопителю
            receiveAgreeFromStorageCounter(myAgent,"StorageCounter1");

            // После окончания суток - отправка
            // сообщения о начале нового аукциона
            sendMsgNewAuction(myAgent);

        } else if (myAgent.getLocalName().equals("WindCounter2")){


            receiveTimeFromDist(myAgent);
            //receiveTimeFromMatlab();

            // Регистрация в контракте
            windCounterRegisterInContracts(myAgent, PUBLIC_KEY_WindCounter2, "WindDeployedContractAddress",
                    "WindPenaltyDeployedContractAddress", credentials2);

            // Обновление вырабатываемой энергии в контракте
            updateProductionWind(myAgent, PUBLIC_KEY_WindCounter2);

            // Контроль поставки электроэнергии за сутки для Wind2
            controlProductionEnergyPerDayWind(myAgent);

            // Обновление оплаты за перезакупку энергии у накопителя для Wind2
            updateRePurchaseEnergy(myAgent);

            //Проверка выполнения оплаты за перезакупку энергии у накопителя для Wind
            // и подведение итогов выполнения условий оплаты
            //controlPaymentWindForStorage(myAgent);

            // Просмотр итогов контракта по оплате потребленной энергии для потребителя (в сутки)
            viewPaymentForWind(myAgent);

            // Принятие от счетчика потребителя потребляемой энергии
            consumeEnergyWind2 = receiveConsumeEnergy(myAgent);

            // Принятие согласия о подаче энергии накопителю
            receiveAgreeFromStorageCounter(myAgent,"StorageCounter2");

            // После окончания суток - отправка
            // сообщения о начале нового аукциона
            sendMsgNewAuction(myAgent);

        }else {

            //receiveTimeFromMatlab();
            receiveTimeFromDist(myAgent);

            // Регистрация в контракте
            windCounterRegisterInContracts(myAgent, PUBLIC_KEY_WindCounter3, "WindDeployedContractAddress",
                    "WindPenaltyDeployedContractAddress", credentials3);

            // Обновление вырабатываемой энергии в контракте
            updateProductionWind(myAgent, PUBLIC_KEY_WindCounter3);

            // Контроль поставки электроэнергии за сутки для Wind3
            controlProductionEnergyPerDayWind(myAgent);

            // Обновление оплаты за перезакупку энергии у накопителя для Wind3
            updateRePurchaseEnergy(myAgent);

            //Проверка выполнения оплаты за перезакупку энергии у накопителя для Wind
            // и подведение итогов выполнения условий оплаты
           // controlPaymentWindForStorage(myAgent);

            // Просмотр итогов контракта по оплате потребленной энергии для потребителя (в сутки)
            viewPaymentForWind(myAgent);

            // Принятие от счетчика потребителя потребляемой энергии
            consumeEnergyWind3 = receiveConsumeEnergy(myAgent);

            // Принятие согласия о подаче энергии накопителю
            receiveAgreeFromStorageCounter(myAgent,"StorageCounter3");

            // После окончания суток - отправка
            // сообщения о начале нового аукциона
            sendMsgNewAuction(myAgent);
        }

    }

    // TODO: WindCounters принимают адреса контрактов, создают экземпляры контрактов
    //  и регистрируются в контрактах
    private void windCounterRegisterInContracts(Agent windCounter, String publicKey, String mainProtocol,
                                              String penaltyProtocol, Credentials credentials){

        // PVCounters принимают адреса основных контрактов
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(mainProtocol);
        ACLMessage msg = windCounter.receive(messageTemplate);
        try {

            if (msg!=null){

                String deployedAddress = msg.getContent();

                System.out.println(windCounter.getLocalName() + " receive deployedAddress " +
                        deployedAddress + " from " + msg.getSender().getLocalName());


                // Если WindCounter1
                if (windCounter.getLocalName().equals("WindCounter1") && flagRegister1){

                    // WindCounter1 создает экземпляр смарт-контракта pvContract1
                    windContract1 = WindContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(ANSI_PURPLE + windCounter.getLocalName() + " with publicKey " + publicKey
                            + " create pvContract1 sample " +
                            windContract1.getContractAddress() + ANSI_RESET);

                    // WindCounter1 регистрируется в контракте

                    if (windContract1.registrationWindCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_PURPLE + windCounter.getLocalName() + " with publicKey : " +
                                publicKey + " register in WindContract1 " + windContract1.getContractAddress() +
                                ANSI_RESET);
                    }

                    flagRegister1 = false;

                }


                // Если WindCounter2
                if (windCounter.getLocalName().equals("WindCounter2") && flagRegister2){

                    // WindCounter2 создает экземпляр смарт-контракта
                    windContract2 = WindContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(ANSI_PURPLE + windCounter.getLocalName() + " with publicKey " + publicKey
                            + " create windContract2 sample " +
                            windContract2.getContractAddress() + ANSI_RESET);

                    //WindCounter2 регистрируется в контакте
                    if (windContract2.registrationWindCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_PURPLE + windCounter.getLocalName() + " with publicKey " + publicKey
                                + " register in windContract2 " + windContract2.getContractAddress() +
                                ANSI_RESET);
                    }

                    flagRegister2 = false;
                }

                // Если WindCounter3
                if (windCounter.getLocalName().equals("WindCounter3") && flagRegister3){

                    // WindCounter3 создает экземпляр контракта
                    windContract3 = WindContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(ANSI_PURPLE + windCounter.getLocalName() + " with publicKey " + publicKey +
                            " create windContract3 sample " + windContract3.getContractAddress() + ANSI_RESET);

                    // WindCounter3 регистрируется в контаркте
                    if (windContract3.registrationWindCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_PURPLE + windCounter.getLocalName() + " with publicKey " + publicKey +
                                " register in windContract3 " + windContract3.getContractAddress() + ANSI_RESET);
                    }

                    flagRegister3 = false;
                }


            }else {
                block();
            }


            // PVCounters принимают адреса штрафных контрактов
            MessageTemplate messageTemplate1 = MessageTemplate.MatchProtocol(penaltyProtocol);
            ACLMessage msg1 = windCounter.receive(messageTemplate1);

            if (msg1!=null){

                String penaltyAddress = msg1.getContent();

                System.out.println(ANSI_PURPLE + windCounter.getLocalName() + " receive penaltyAddress " +
                        penaltyAddress + " from " + msg1.getSender().getLocalName() + ANSI_RESET);

                // Если WindCounter1
                if (windCounter.getLocalName().equals("WindCounter1") && flagRegisterPenalty1){

                    //WindCounter1 создает экземпляр контаркта
                    windPenaltyContract1 = WindPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);

                    System.out.println(windCounter.getLocalName() + " with publicKey " + publicKey +
                            " create windPenaltyContract1 sample "
                            + windPenaltyContract1.getContractAddress());

                    // WindCounter1 регистрируется в штрафном коонтракте
                    if (windPenaltyContract1.registrationWindCounter(publicKey).send().isStatusOK()){
                        System.out.println(windCounter.getLocalName() + " with publicKey " + publicKey +
                                " register in windPenaltyContract1 " + windPenaltyContract1.getContractAddress());
                    }

                    flagRegisterPenalty1 = false;
                }

                // Если WindCounter2
                if (windCounter.getLocalName().equals("WindCounter2") && flagRegisterPenalty2){

                    //WindCounter2 создает экземпляр контракта
                    windPenaltyContract2 = WindPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);

                    System.out.println(windCounter.getLocalName() + " with publicKey " + publicKey +
                            " create windPenaltyContract2 sample " + windPenaltyContract2.getContractAddress());

                    // WindCounter2 регистрируется в контракте
                    if (windPenaltyContract2.registrationWindCounter(publicKey).send().isStatusOK()){
                        System.out.println(windCounter.getLocalName() + " with publicKey " + publicKey +
                                " register in windPenaltyContract2 " + windPenaltyContract2.getContractAddress());
                    }

                    flagRegisterPenalty2 = false;
                }

                // Если PVCounter3
                if (windCounter.getLocalName().equals("WindCounter3") && flagRegisterPenalty3){

                    // PVCounter3 создает экземпляр контракта
                    windPenaltyContract3 = WindPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);

                    System.out.println(windCounter.getLocalName() + " with publicKey " + publicKey +
                            " create windPenaltyContract3 sample " + windPenaltyContract3.getContractAddress());

                    // WindCounter3 вписывает себя в контракт
                    if (windPenaltyContract3.registrationWindCounter(publicKey).send().isStatusOK()){
                        System.out.println(windCounter.getLocalName() + " with publicKey " + publicKey +
                                " register in windPenaltyContract3 " + windPenaltyContract3.getContractAddress());
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

    // TODO: Метод принятия времени от Дистрибьютора
    private void receiveTimeFromDist(Agent windCounter){
        // Пока просто сделаем счетчик времени (потом закомментить)
//        counterTime = mqttSubscriber.mqttSubscriber("Time","WindCounter");
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("TimeFromMatlab");
        ACLMessage msg = windCounter.receive(messageTemplate);

        if (msg!=null){
            counterTime = Double.parseDouble(msg.getContent());
            System.out.println(ANSI_PURPLE + windCounter.getLocalName() +
                    " receive " + counterTime +
                    " from " + counterTime + ANSI_RESET);
        }else {
            block();
        }

    }


    // TODO: Метод для принятия текущей потребляемой энергии от потребителя
    private Double receiveConsumeEnergy(Agent windCounter){
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("ConsumeWindEnergy");
        ACLMessage msg = windCounter.receive(messageTemplate);

        if (msg!=null){
            consumeEnergy = Double.parseDouble(msg.getContent());
            System.out.println(ANSI_PURPLE + windCounter.getLocalName() + " receive consume energy " +
                    msg.getContent() +
                    " from " + msg.getSender().getLocalName() + ANSI_RESET);

            // WindCounter добавляет в datastore имя счетчика потребителя,
            // от кого получает энергию потребляемую энергию
            getDataStore().put(windCounter.getLocalName() + "consumerCounter",msg.getSender().getLocalName());
        }else {
            block();
        }

        return consumeEnergy;
    }


    // TODO: Метод для обновления количества вырабатываемой энергии для Wind
    //  PVCounters являются подписчиками MQTT, они принимают
    //  сигналы вырабатываемой энергии из Матлаба (пока сделаем, что вырабатываемая энергия
    //  случайным образом меняется в этом классе и принимается на вход метода
    //  updateProductionWind ( рандомайзер вырабатываемой энергии)
    private void updateProductionWind(Agent windCounter, String publicKey) {

        try {
            // Если WindCounter1
            if (windCounter.getLocalName().equals("WindCounter1")) {

                productionEnergy = mqttSubscriber.mqttSubscriber("WindCounter1","WindCounter1");

                System.out.println(ANSI_PURPLE_BACKGROUND + " ++++++++++++++++++++++++++ " +
                        " PRODUCTION ENERGY WindCounter1 " + productionEnergy + ANSI_RESET);

                // WindCounter достает из datastore имя счетчика потребителя
                // и отправляет ему кол-во текущей потребляемой энергии
                // чтобы тот посчитал разницу для системной услуги
                if (getDataStore().get(windCounter.getLocalName() + "consumerCounter")!=null){

                    String consumerCounterName = String.valueOf(
                            getDataStore().get(windCounter.getLocalName() + "consumerCounter"));
                    sendMsg(windCounter, "ProductionEnergyWind",consumerCounterName,
                            String.valueOf(productionEnergy));
                }

                // Сравниваем выработку и потребление энергии, если выработка в профиците,
                // то отправляем сообщение счетчику накопителя, что можем продать энергию
                if (consumeEnergyWind1!=0 && productionEnergy > consumeEnergyWind1){
                    System.out.println(ANSI_PURPLE+"Wind1 has proficit energy!" +
                    "\nWind1 production " + productionEnergy+
                    "\nConsumption frm Wind1 " + consumeEnergyWind1+ANSI_RESET);

                    // Отправляем сообщение счетчку накопителя
                    sendMsg(windCounter, "ProficitWindEnergy",
                            "StorageCounter1","ProficitWindEnergy");

                    // TODO: Отправка сообщения счетчику накопителя с разницей выработки
                    //  и потребления
                    double difference = productionEnergy - consumeEnergyWind1;
                    sendProficitSignalToStorageCounter(windCounter,difference);

                }

                if (productionEnergy <= consumeEnergyWind1){

                    System.out.println(ANSI_PURPLE + "Wind1 produce energy less than consume! "+
                            "\nWind1 production " + productionEnergy +
                            "\nConsumption from Wind1 " + consumeEnergyWind1 + ANSI_RESET);

                }



                // Если кол-во текущей вырабатываемой энергии != null, то принимаем на вход функции
                // updateProductionPV контракта

                if (windContract1!=null && productionEnergy != 0){
                    if (windContract1.updateProductionWind(BigInteger.valueOf((int)productionEnergy)).send().isStatusOK()){
                        System.out.println();
                        System.out.println(windCounter.getLocalName() + " with publicKey : " + publicKey
                                + " update productionEnergy for wind1 ");

                    }
                }

            }

            // Если WindCounter2
            if (windCounter.getLocalName().equals("WindCounter2")) {

                productionEnergy = mqttSubscriber.mqttSubscriber("WindCounter2","WindCounter2");

                System.out.println(ANSI_PURPLE_BACKGROUND + " ++++++++++++++++++++++++++ " +
                        " PRODUCTION ENERGY WindCounter1 " + productionEnergy + ANSI_RESET);

                // WindCounter достает из datastore имя счетчика потребителя
                // и отправляет ему кол-во текущей потребляемой энергии
                // чтобы тот посчитал разницу для системной услуги
                if (getDataStore().get(windCounter.getLocalName() + "consumerCounter")!=null){

                    String consumerCounterName = String.valueOf(
                            getDataStore().get(windCounter.getLocalName() + "consumerCounter"));
                    sendMsg(windCounter, "ProductionEnergyWind",consumerCounterName,
                            String.valueOf(productionEnergy));
                }

                //Если Wind2 вырабатыввает юольше энергии, чем у него потребляют
                // то можем продать накопителю
                if (consumeEnergyWind2!=0 && productionEnergy > consumeEnergyWind2){
                    System.out.println(ANSI_PURPLE + "Wind2 has profocit energy!"+
                            "\nWind2 production energy " + productionEnergy +
                            "\nConsumption energy from Wind2 " + consumeEnergyWind2 + ANSI_RESET);

                    // Отправка сообщения счетчику накопителя
                    sendMsg(windCounter, "ProficitWindEnergy",
                            "StorageCounter2","ProficitWindEnergy");

                    // TODO: Отправка сообщения счетчику накопителя с разницей выработки
                    //  и потребления
                    double difference = productionEnergy - consumeEnergyWind2;
                    sendProficitSignalToStorageCounter(windCounter,difference);
                }

                if (consumeEnergyWind2!=0 && productionEnergy <= consumeEnergyWind2){
                    System.out.println(ANSI_PURPLE + "Wind2 produce energy less than consume!" +
                            "\nWind2 production " + productionEnergy +
                            "\nConsumption energy from Wind2 " + consumeEnergyWind2 + ANSI_RESET);

                }

                if (windContract2!=null && productionEnergy != 0){

                    if (windContract2.updateProductionWind(BigInteger.valueOf((int)productionEnergy)).send().isStatusOK()){
                        System.out.println();
                        System.out.println(windCounter.getLocalName() + " with publicKey : " + publicKey
                                + " update productionEnergy for wind2");
                    }
                }
            }

            // Если WindCounter3
            if (windCounter.getLocalName().equals("WindCounter3")){

                productionEnergy = mqttSubscriber.mqttSubscriber("WindCounter3","WindCounter3");

                System.out.println(ANSI_PURPLE_BACKGROUND + " ++++++++++++++++++++++++++ " +
                        " PRODUCTION ENERGY WindCounter1 " + productionEnergy + ANSI_RESET);

                // WindCounter достает из datastore имя счетчика потребителя
                // и отправляет ему кол-во текущей потребляемой энергии
                // чтобы тот посчитал разницу для системной услуги
                if (getDataStore().get(windCounter.getLocalName() + "consumerCounter")!=null){

                    String consumerCounterName = String.valueOf(
                            getDataStore().get(windCounter.getLocalName() + "consumerCounter"));
                    sendMsg(windCounter, "ProductionEnergyWind",consumerCounterName,
                            String.valueOf(productionEnergy));
                }

                // Если Wind3 имеет профицит энергии, то отправляем сообщение счетику накопителя
                if (consumeEnergyWind3!=0 && productionEnergy > consumeEnergyWind3){
                    System.out.println(ANSI_PURPLE + "Wind3 has proficit energy " +
                            "\nWind3 production energy " + productionEnergy +
                            "\nConsumption energy from Wind3 " + consumeEnergyWind3 +ANSI_RESET);

                    // отправка сообщения ссчетчику накопителя
                    sendMsg(windCounter,"ProficitWindEnergy",
                            "StorageCounter3","ProficitWindEnergy");

                    // TODO: Отправка сообщения счетчику накопителя с разницей выработки
                    //  и потребления
                    double difference = productionEnergy - consumeEnergyWind2;
                    sendProficitSignalToStorageCounter(windCounter,difference);
                }

                if (consumeEnergyWind3!=0 && productionEnergy <=consumeEnergyWind3){
                    System.out.println(ANSI_PURPLE + "Wind3 produce energy less than consume!" +
                            "\nWind3 production " + productionEnergy +
                            "\nConsumption energy from Wind3 " + consumeEnergyWind3 + ANSI_RESET);

                }

                if (windContract3!=null && productionEnergy != 0){

                    if (windContract3.updateProductionWind(BigInteger.valueOf((int)productionEnergy)).send().isStatusOK()){
                        System.out.println();
                        System.out.println(ANSI_PURPLE + windCounter.getLocalName() + " with publicKey : " + publicKey
                                + " update productionEnergy for wind3" + ANSI_RESET);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println();
        }

    }

    // TODO: Отправка разницы энергии (профицит) накопителю
    private void sendProficitSignalToStorageCounter(Agent windCounter, double differenceProfocitEnergy) {

        // Принимаем сообщение от StorageCounter с согласием на поставку энергии накопителю
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("AgreeWindEnergy");
        ACLMessage msg = windCounter.receive(messageTemplate);

        if (msg != null) {

            System.out.println(ANSI_PURPLE + windCounter.getLocalName() + " receive " + msg.getContent() +
                    " from " + msg.getSender().getLocalName() + ANSI_RESET);

            // Никаких сигналов в Матлаб о передаче профицита мощности
            // передавать не надо, так как эта мощность и так уже плюсуется к накопителю
            // Пока не принимаем сигнал из Матлаба - отправим сообщение ( как начало передачи энергии)
            sendMsg(windCounter, "StartWindEnergy", msg.getSender().getLocalName(),
                    String.valueOf(differenceProfocitEnergy));


        } else {
            block();
        }
    }


    // TODO: Метод контроля поставки электроэнергии за сутки для Wind
    private void controlProductionEnergyPerDayWind(Agent windCounter){

        try {

        if (windCounter.getLocalName().equals("WindCounter1")){
            if ((int)counterTime == 24){
                if (windContract1!=null){

                    String result = windContract1.controlProductionEnergyPerDayWind(
                                BigInteger.valueOf(1)).send().component1();

                    BigInteger difference = windContract1.controlProductionEnergyPerDayWind(
                            BigInteger.valueOf(1)).send().component2();

                    System.out.println(ANSI_PURPLE_BACKGROUND + "=========== Control Production" +
                            " energy for Wind1 ===========" +
                            "\nFact production energy Wind1 " + result +
                            "\nDifference between contracted energy and transmitted energy Wind1 " +
                            difference + ANSI_RESET);

                    // TODO: В случае недопоставки нужного кол-ва
                    //  энергии WindCounter1 отправляет to Wind1
                    //  кол-во энергии, за которое нужно заплатить накопителю
                    sendMsg(windCounter,"PaymentRePurchaseEnergyWind",
                            "Wind1", String.valueOf(difference));
                }

            }
        }


        // Если WindCounter2
            if (windCounter.getLocalName().equals("WindCounter2")){
                if ((int)counterTime  == 24){
                    if (windContract2!=null){

                        String result = windContract2.controlProductionEnergyPerDayWind(
                                BigInteger.valueOf(1)).send().component1();
                        BigInteger difference = windContract2.controlProductionEnergyPerDayWind(
                                BigInteger.valueOf(1)).send().component2();

                        System.out.println(ANSI_PURPLE_BACKGROUND + "=========== Control Production" +
                                " energy for Wind2 ===========" +
                                "\nFact production energy Wind2 " + result +
                                "\nDifference between contracted energy and transmitted energy Wind2 " +
                                difference + ANSI_RESET);

                        // TODO: В случае недопоставки нужного кол-ва
                        //  энергии WindCounter2 отправляет to Wind2
                        //  кол-во энергии, за которое нужно заплатить накопителю
                        sendMsg(windCounter,"PaymentRePurchaseEnergyWind",
                                "Wind2", String.valueOf(difference));

                    }
                }
            }


            // Если WindCounter3
            if (windCounter.getLocalName().equals("WindCounter3")){
                if ((int)counterTime == 24){
                    if (windContract3!=null){

                        String result = windContract3.controlProductionEnergyPerDayWind(
                                BigInteger.valueOf(1)).send().component1();
                        BigInteger difference = windContract3.controlProductionEnergyPerDayWind(
                                BigInteger.valueOf(1)).send().component2();

                        System.out.println(ANSI_PURPLE_BACKGROUND + "=========== Control Production" +
                                " energy for Wind3 ===========" +
                                "\nFact production energy Wind3 " + result +
                                "\nDifference between contracted energy and transmitted energy Wind3 " +
                                difference + ANSI_RESET);

                        // TODO: В случае недопоставки нужного кол-ва
                        //  энергии WindCounter3 отправляет to Wind3
                        //  кол-во энергии, за которое нужно заплатить накопителю
                        sendMsg(windCounter,"PaymentRePurchaseEnergyWind",
                                "Wind3", String.valueOf(difference));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //TODO: Счетчики ветряков принимают сообщения от ветряков,
    // что те оплатили за презакупку энергии
    // После этого счетчики ветряков обновляют перезакупку
    // эннергии для накопителя
    private void updateRePurchaseEnergy(Agent windCounter){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("PaymentForRepurchaseCompleted");
        ACLMessage msg = windCounter.receive(messageTemplate);

        if (msg!=null){
            BigInteger difference = BigInteger.valueOf((int)Double.parseDouble(msg.getContent()));

            System.out.println(windCounter.getLocalName() + " receive " +
                    difference + " from " +
                    msg.getSender().getLocalName());

            try {

            if (windCounter.getLocalName().equals("WindCounter1")){
                if (windContract1!=null){

                    if (windContract1.updatePaymentRePurchaseStorageEnergyForWind(difference).send().isStatusOK()){
                        System.out.println(ANSI_PURPLE + windCounter.getLocalName() + " updated payment for " +
                                "repurchase energy for Wind1" + ANSI_RESET);
                    }

                    if (windContract1.controlPaymentWindForStorage().send().isStatusOK()){
                        System.out.println(ANSI_PURPLE + windCounter.getLocalName() + " control payment "+
                                "for repurchase energy from Storage1" + ANSI_RESET);

                        // После проверки выполнения оплаты за перезакупку
                        // энергии у накопителя для Wind1 выводим на экран
                        // вывод итогов выполнения контракта
                        System.out.println(ANSI_PURPLE_BACKGROUND +
                                "========== Total payment for wind1 repurchase energy =========" +
                                "\nMust be pay for storage energy (wind) " +
                                windContract1.viewPaymentWindForStorage().send().component2() +
                                "\nfact payment " + windContract1.viewPaymentWindForStorage().send().component4() +
                                "\npenny " + windContract1.viewPaymentWindForStorage().send().component6() +
                                "\nstorage balance " + windContract1.viewPaymentWindForStorage().send().component8() +
                                ANSI_RESET);
                    }

                }
            }

            if (windCounter.getLocalName().equals("WindCounter2")){
                if (windContract2!=null){
                    if (windContract2.updatePaymentRePurchaseStorageEnergyForWind(difference).send().isStatusOK()){
                        System.out.println(ANSI_PURPLE + windCounter.getLocalName() + " updated payment for " +
                                "repurchase energy for Wind2" + ANSI_RESET);
                    }

                    if (windContract2.controlPaymentWindForStorage().send().isStatusOK()){
                        System.out.println(ANSI_PURPLE + windCounter.getLocalName() + "control payment " +
                                "for repurchase energy from Storage2" + ANSI_RESET);

                        // После проверки выполнения оплаты за перезакупку
                        // энергии у накопителя для Wind2 выводим на экран
                        // вывод итогов выполнения контракта
                        System.out.println(ANSI_PURPLE_BACKGROUND +
                                "========== Total payment for wind2 repurchase energy =========" +
                                "\nMust be pay for storage energy (wind) " +
                                windContract2.viewPaymentWindForStorage().send().component2() +
                                "\nfact payment " + windContract2.viewPaymentWindForStorage().send().component4() +
                                "\npenny " + windContract2.viewPaymentWindForStorage().send().component6() +
                                "\nstorage balance " + windContract2.viewPaymentWindForStorage().send().component8() +
                                ANSI_RESET);

                    }
                }
            }

            if (windCounter.getLocalName().equals("WindCounter3")){
                if (windContract3!=null){
                    if (windContract3.updatePaymentRePurchaseStorageEnergyForWind(difference).send().isStatusOK()){
                        System.out.println(ANSI_PURPLE + windCounter.getLocalName() + " updated payment for " +
                                "repurchase energy for Wind3" + ANSI_RESET);
                    }

                    if (windContract3.controlPaymentWindForStorage().send().isStatusOK()){
                        System.out.println(ANSI_PURPLE + windCounter.getLocalName() + " control payment " +
                                "for repurchase energy from Storage3" + ANSI_RESET);

                        // После проверки выполнения оплаты за перезакупку
                        // энергии у накопителя для Wind3 выводим на экран
                        // вывод итогов выполнения контракта
                        System.out.println(ANSI_PURPLE_BACKGROUND +
                                "========== Total payment for wind3 repurchase energy =========" +
                                "\nMust be pay for storage energy (wind) " +
                                windContract3.viewPaymentWindForStorage().send().component2() +
                                "\nfact payment " + windContract3.viewPaymentWindForStorage().send().component4() +
                                "\npenny " + windContract3.viewPaymentWindForStorage().send().component6() +
                                "\nstorage balance " + windContract3.viewPaymentWindForStorage().send().component8() +
                                ANSI_RESET);

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

    // TODO: Метод проверки выполнения оплаты за перезакупку энергии у накопителя для Wind
    private void controlPaymentWindForStorage(Agent windCounter){
        try {

        if (windCounter.getLocalName().equals("WindCounter1")){
            if (windContract1!=null){
                if (counterTime == 24){
                    if (windContract1.controlPaymentWindForStorage().send().isStatusOK()){
                        System.out.println(windCounter.getLocalName() + " control payment "+
                                "for repurchase energy from Storage1");

                        // После проверки выполнения оплаты за перезакупку
                        // энергии у накопителя для Wind1 выводим на экран
                        // вывод итогов выполнения контракта
                        System.out.println(ANSI_PURPLE_BACKGROUND +
                                "========== Total payment for wind1 repurchase energy =========" +
                                "\nMust be pay for storage energy (wind) " +
                                windContract1.viewPaymentWindForStorage().send().component2() +
                                "\nfact payment " + windContract1.viewPaymentWindForStorage().send().component4() +
                                "\npenny " + windContract1.viewPaymentWindForStorage().send().component6() +
                                "\nstorage balance " + windContract1.viewPaymentWindForStorage().send().component8() +
                        ANSI_RESET);
                    }
                }
            }
        }

        if (windCounter.getLocalName().equals("WindCounter2")){
            if (windContract2!=null){
                if (counterTime == 24){
                    if (windContract2.controlPaymentWindForStorage().send().isStatusOK()){
                        System.out.println(windCounter.getLocalName() + "control payment " +
                                "for repurchase energy from Storage2");

                        // После проверки выполнения оплаты за перезакупку
                        // энергии у накопителя для Wind2 выводим на экран
                        // вывод итогов выполнения контракта
                        System.out.println(ANSI_PURPLE_BACKGROUND +
                                "========== Total payment for wind2 repurchase energy =========" +
                                "\nMust be pay for storage energy (wind) " +
                                windContract2.viewPaymentWindForStorage().send().component2() +
                                "\nfact payment " + windContract2.viewPaymentWindForStorage().send().component4() +
                                "\npenny " + windContract2.viewPaymentWindForStorage().send().component6() +
                                "\nstorage balance " + windContract2.viewPaymentWindForStorage().send().component8() + ANSI_RESET);

                    }
                }
            }
        }

        if (windCounter.getLocalName().equals("WindCounter3")){
            if (windContract3!=null){
                if (counterTime == 24){
                    if (windContract3.controlPaymentWindForStorage().send().isStatusOK()){
                        System.out.println(windCounter.getLocalName() + " control payment " +
                                "for repurchase energy from Storage3");

                        // После проверки выполнения оплаты за перезакупку
                        // энергии у накопителя для Wind3 выводим на экран
                        // вывод итогов выполнения контракта
                        System.out.println(ANSI_PURPLE_BACKGROUND +
                                "========== Total payment for wind3 repurchase energy =========" +
                                "\nMust be pay for storage energy (wind) " +
                                windContract3.viewPaymentWindForStorage().send().component2() +
                                "\nfact payment " + windContract3.viewPaymentWindForStorage().send().component4() +
                                "\npenny " + windContract3.viewPaymentWindForStorage().send().component6() +
                                "\nstorage balance " + windContract3.viewPaymentWindForStorage().send().component8() + ANSI_RESET);

                    }
                }
            }
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: Счетчики ветряков принимают сообщения от счетчиков накопителей,
    //  о согласии на подачу энергии
    //  После согласия счетчики ветряков отправляют сигналы по UDP
    //  в Матлаб, о подаче лишней! энергии накопителю
    private void receiveAgreeFromStorageCounter(Agent windCounter,String storageCounter){
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("AgreeWindEnergy");
        ACLMessage msg = windCounter.receive(messageTemplate);

        if (msg!=null){
            System.out.println(ANSI_PURPLE + windCounter.getLocalName() +
                    " receive " + msg.getContent() +
                    " from " + msg.getSender().getLocalName() + ANSI_RESET);

            // Отправка сигнала в Матлаб
            // о старте подачи сигнала счетчику накопителя
            // !!!Пока отправим сообщение о старте подачи сигнала (потом закомментим)
            sendMsg(windCounter,"StartWindEnergyToStorage",
                    storageCounter,"StartWindEnergyToStorage");

        }else {
            block();
        }
    }


    // TODO: Просмотр итогов контракта по оплате потребленной энергии для потребителя (в сутки)
    public void viewPaymentForWind(Agent windCounter){

        //Принятие сообщения от ConsumerCounter to WindCounter1
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("ControlWindCompleted1");
        ACLMessage msg = windCounter.receive(messageTemplate);

        try {
        if (msg!=null){

            System.out.println(windCounter.getLocalName() + " receive msg : " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());


            if (windContract1!=null){

                //Просмотр итогов контракта по оплате потребленной энергии для потребителя (в сутки)

                int mustPayment = (int)Double.parseDouble(String.valueOf(windContract1.viewPaymentForWind().send().component2()));
                int factPayment = (int)Double.parseDouble(String.valueOf(windContract1.viewPaymentForWind().send().component4()));
//                int penny = (int)Double.parseDouble(String.valueOf(windContract1.viewPaymentForWind().send().component6()));
                int penny2 = mustPayment - factPayment;

                System.out.println(ANSI_PURPLE_BACKGROUND + "====== Total windContract1 consumption energy for day ==========" +
                        "\nMust be pay : " + mustPayment +
                        "\nFact payment : " + factPayment +
                        "\npenny : " + penny2 + ANSI_RESET);


//                // Отправка сообщения от счетчика ветряка к своему ветрогенератору,
//                //  чтобы тот начинал новый аукцион (т.к. подведены итоги суток)
//                sendMsg(windCounter,"StartNewWindAuction","Wind1","StartNewWindAuction");


                //TODO: Счетчик ветряка отправляет сообщение дистрибьтору
                sendMsg(windCounter, "StartNewWindAuction","Distributor","StartNewWindAuction");

            }


        }else {
            block();
        }


        // Принятие сообщения для WindCounter2
        MessageTemplate messageTemplate1 = MessageTemplate.MatchProtocol("ControlWindCompleted2");
        ACLMessage msg2 = windCounter.receive(messageTemplate1);

        if (msg2!=null){

            System.out.println(windCounter.getLocalName() + " receive msg : " + msg2.getContent() +
                    " from " + msg2.getSender().getLocalName());

            if (windContract2!=null){

                //Просмотр итогов контракта windContract2 по оплате потребленной энергии для потребителя (в сутки)
                int mustPayment = (int)Double.parseDouble(String.valueOf(windContract2.viewPaymentForWind().send().component2()));
                int factPayment = (int)Double.parseDouble(String.valueOf(windContract2.viewPaymentForWind().send().component4()));

//                int penny = (int)Double.parseDouble(String.valueOf(windContract2.viewPaymentForWind().send().component6()));
                int penny2 = mustPayment - factPayment;

                System.out.println(ANSI_PURPLE_BACKGROUND + "====== Total windContract2 consumption energy for day ==========" +
                        "\nMust be pay : " + mustPayment +
                        "\nFact payment : " + factPayment +
                        "\npenny : " + penny2 + ANSI_RESET);


//                // TODO: Отправка сообщения от счетчика ветряка к своему ветрогенератору,
//                //  чтобы тот начинал новый аукцион (т.к. подведены итоги суток)
//                sendMsg(windCounter,"StartNewWindAuction","Wind2","StartNewWindAuction");


                //TODO: Счетчик ветряка отправляет сообщение дистрибьтору
//                sendMsg(windCounter, "StartNewWindAuction","Distributor","StartNewWindAuction");

            }

        }else {
            block();
        }


        // Принятие сообщения для WindCounter3
            MessageTemplate messageTemplate2 = MessageTemplate.MatchProtocol("ControlWindCompleted3");
        ACLMessage msg3 = windCounter.receive(messageTemplate2);

        if (msg3!=null){

            System.out.println(windCounter.getLocalName() + " receive msg : " + msg3.getContent() +
                    " from " + msg3.getSender().getLocalName());

            if (windContract3!=null){

                //Просмотр итогов контракта windContract3 по оплате потребленной энергии для потребителя (в сутки)
                int mustPayment = (int)Double.parseDouble(String.valueOf(windContract3.viewPaymentForWind().send().component2()));
                int factPayment = (int)Double.parseDouble(String.valueOf(windContract3.viewPaymentForWind().send().component4()));
//                int penny = (int)Double.parseDouble(String.valueOf(windContract3.viewPaymentForWind().send().component6()));
                int penny2 = mustPayment - factPayment;

                System.out.println(ANSI_PURPLE_BACKGROUND + "====== Total windContract3 consumption energy for day ==========" +
                        "\nMust be pay : " + mustPayment +
                        "\nFact payment : " + factPayment +
                        "\npenny : " + penny2 + ANSI_RESET);

//                // TODO: Отправка сообщения от счетчика ветряка к своему ветрогенератору,
//                //  чтобы тот начинал новый аукцион (т.к. подведены итоги суток)
//                sendMsg(windCounter,"StartNewWindAuction","Wind3","StartNewWindAuction");


                //TODO: Счетчик ветряка отправляет сообщение дистрибьтору
//                sendMsg(windCounter, "StartNewWindAuction","Distributor","StartNewWindAuction");

            }
        }else {
            block();
        }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: После окончания суток счетчик ветряка
    //  отправляет сообщение дистрибьютору
    //  о начале нового аукциона
    private void sendMsgNewAuction(Agent windCounter){

        // Пока временно ставим отрезок времени в течение которого
        // pvCounters будут отправлять сообщение о начале нового аукицона
        if (counterTime == 24){
            sendMsg(windCounter,"NewWindAuction",
                    "Distributor","NewWindAuction");
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
