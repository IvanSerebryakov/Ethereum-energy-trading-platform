package StorageCounterClasses;

import MQTTConnection.MQTTSubscriber;
import SmartContracts.PV.PVContract;
import SmartContracts.PV.PVPenaltyContract;
import SmartContracts.Service.ServiceContract;
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

public class StorageCounterInteractWithContracts extends TickerBehaviour {

    private Web3j web3j;
    private ContractGasProvider contractGasProvider;

    private String PUBLIC_KEY_StorageCounter1;
    private String PUBLIC_KEY_StorageCounter2;
    private String PUBLIC_KEY_StorageCounter3;

    private Credentials credentialsStorageCounter1;
    private Credentials credentialsStorageCounter2;
    private Credentials credentialsStorageCounter3;

    private DataStore dataStore;

    // Флаги ррегистрации в основных PV контрактах
    private boolean flagRegisterPV1;
    private boolean flagRegisterPV2;
    private boolean flagRegisterPV3;

    // Флаги регистрации в штрафных PV контарутах
    private boolean flagRegisterFinePV1;
    private boolean flagRegisterFinePV2;
    private boolean flagRegisterFinePV3;

    // Флаги ррегистрации в основных Wind контрактах
    private boolean flagRegisterWind1;
    private boolean flagRegisterWind2;
    private boolean flagRegisterWind3;

    // Флаги регистрации в штрафных WInd контарутах
    private boolean flagRegisterFineWind1;
    private boolean flagRegisterFineWind2;
    private boolean flagRegisterFineWind3;

    // Флаги регистрации в сервисных контрактах
    private boolean flagRegisterServiceContract1;
    private boolean flagRegisterServiceContract2;
    private boolean flagRegisterServiceContract3;

    // Экзмепляры PV контрактов
    private PVContract pvContract1;
    private PVContract pvContract2;
    private PVContract pvContract3;

    private PVPenaltyContract pvPenaltyContract1;
    private PVPenaltyContract pvPenaltyContract2;
    private PVPenaltyContract pvPenaltyContract3;

    // Экземпляры Wind контрактов
    private WindContract windContract1;
    private WindContract windContract2;
    private WindContract windContract3;

    private WindPenaltyContract windPenaltyContract1;
    private WindPenaltyContract windPenaltyContract2;
    private WindPenaltyContract windPenaltyContract3;

    // Экземпляры сервисных контрактов
    private ServiceContract serviceContract1;
    private ServiceContract serviceContract2;
    private ServiceContract serviceContract3;

    // Установим переменную, что накопителю не хватает столько то
    // энергии (рандом), затем когда будем принимать сигнал по MQTT,
    // закомментить это число
    //private double needEnergy = 1 + (Math.random() * 3000); (это не надо скорее всего!)

    // кол -во заряда накопителя (сперва рандомно устаналвиваем в системе, потом закомментить)
    private double storageCharge;

    // TODO: Пока не получаем сигналы из Матлаба ( PV energy) установим случайное значение
    private double pvEnergyFromMatlab = 100 + Math.random() * 5000;

    // TODO: Временные счетчики времени (закомментить после принятия времени из Матлаба)
    private int pv1Counter;
    private int pv2Counter;
    private int pv3Counter;

    private int n1;
    private int n2;
    private int n3;

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

    // TODO: Случайное значение ( потом закомментить) принятие
    //  данных из Матлаба от Wind
    private double energyFromWindProficit = 500 + Math.random() * 1000;

    // TODO: Счетчики времени (потом закомментить)
    private int counterWind1;
    private int counterWind2;
    private int counterWind3;

    // Счетчики суток
    private int windDay1;
    private int windDay2;
    private int windDay3;

    private double counterTime;

    private MQTTSubscriber mqttSubscriber = new MQTTSubscriber();

    public StorageCounterInteractWithContracts(Agent a, long period, Web3j web3j, ContractGasProvider contractGasProvider,
                                               String PUBLIC_KEY_StorageCounter1, String PUBLIC_KEY_StorageCounter2,
                                               String PUBLIC_KEY_StorageCounter3, Credentials credentialsStorageCounter1,
                                               Credentials credentialsStorageCounter2, Credentials credentialsStorageCounter3,
                                               DataStore dataStore, boolean flagRegister1, boolean flagRegister2, boolean flagRegister3,
                                               boolean flagRegisterFine1, boolean flagRegisterFine2, boolean flagRegisterFine3,
                                               boolean flagRegisterServiceContract1, boolean flagRegisterServiceContract2,
                                               boolean flagRegisterServiceContract3, int pv1Counter, int pv2Counter, int pv3Counter,
                                               int n1, int n2, int n3, boolean flagRegisterWind1, boolean flagRegisterWind2,
                                               boolean flagRegisterWind3, boolean flagRegisterFineWind1, boolean flagRegisterFineWind2,
                                               boolean flagRegisterFineWind3, int counterWind1, int counterWind2, int counterWind3,
                                               int windDay1, int windDay2, int windDay3, int counterTime) {
        super(a, period);
        this.dataStore = dataStore;
        setDataStore(dataStore);

        this.web3j = web3j;
        this.contractGasProvider = contractGasProvider;

        this.PUBLIC_KEY_StorageCounter1 = PUBLIC_KEY_StorageCounter1;
        this.PUBLIC_KEY_StorageCounter2 = PUBLIC_KEY_StorageCounter2;
        this.PUBLIC_KEY_StorageCounter3 = PUBLIC_KEY_StorageCounter3;

        this.credentialsStorageCounter1 = credentialsStorageCounter1;
        this.credentialsStorageCounter2 = credentialsStorageCounter2;
        this.credentialsStorageCounter3 = credentialsStorageCounter3;

        this.flagRegisterPV1 = flagRegister1;
        this.flagRegisterPV2 = flagRegister2;
        this.flagRegisterPV3 = flagRegister3;

        this.flagRegisterFinePV1 = flagRegisterFine1;
        this.flagRegisterFinePV2 = flagRegisterFine2;
        this.flagRegisterFinePV3 = flagRegisterFine3;

        this.flagRegisterServiceContract1 = flagRegisterServiceContract1;
        this.flagRegisterServiceContract2 = flagRegisterServiceContract2;
        this.flagRegisterServiceContract3 = flagRegisterServiceContract3;

        this.flagRegisterWind1 = flagRegisterWind1;
        this.flagRegisterWind2 = flagRegisterWind2;
        this.flagRegisterWind3 = flagRegisterWind3;

        this.flagRegisterFineWind1 = flagRegisterFineWind1;
        this.flagRegisterFineWind2 = flagRegisterFineWind2;
        this.flagRegisterFineWind3 = flagRegisterFineWind3;

        this.pv1Counter = pv1Counter;
        this.pv2Counter = pv2Counter;
        this.pv3Counter = pv3Counter;

        this.n1 = n1;
        this.n2 = n2;
        this.n3 = n3;

        this.counterWind1 = counterWind1;
        this.counterWind2 = counterWind2;
        this.counterWind3 = counterWind3;

        this.windDay1 = windDay1;
        this.windDay2 = windDay2;
        this.windDay3 = windDay3;

        this.counterTime = counterTime;

    }

    @Override
    protected void onTick() {

        //receiveTimeFromMatlab();

        if (myAgent.getLocalName().equals("StorageCounter1")){

            receiveTimeFromDist(myAgent);

            // Регистрация в PV контрактах
            registerInPVContracts(myAgent, PUBLIC_KEY_StorageCounter1, credentialsStorageCounter1,
                    "DeployedPVAddressFromStorage", "PVPenaltyAddressFromStorage");

            // Регистрация в Wind контрактах
            registerInWindContracts(myAgent, PUBLIC_KEY_StorageCounter1, credentialsStorageCounter1,
                    "DeployedWindAddressFromStorage", "WindPenaltyAddressFromStorage");

            // Регистрация в сервисных контрактах
            registerInServiceContract(myAgent, PUBLIC_KEY_StorageCounter1, credentialsStorageCounter1);

            // Принятие сообщения о профиците энерги, отправка согласия на получение энергии
            receiveProficitPVMsg(myAgent, "Storage1","StorageCharge1",myAgent.getLocalName());

            // Принятие сообщение от счетчика ветрогенератора о профиците энергии
            receiveWindProficitMSG(myAgent, "Storage1","StorageCharge1",myAgent.getLocalName());

            // Принятие данных PV energy из Матлаба и отправка данных накопителю для оплаты
            receivePVEnergyFromMatlab(myAgent, "Storage1");

            // Принятие данных Wind energy из Матлаба и отправка данных накопителю для оплаты
            receiveEnergyFromWindProfocit(myAgent,"Wind1","Storage1");

            // Принятие данных о заряде накопителя в % из Матлаба
            //receiveStorageChargeFromMatlab();

            // Принятие сообщения об оплате за энергию от
            // своего накопителя и обновляют суммарно принятую энергию и
            // суммарную оплату за эту PV энергию
            updatePaymentPVEnergyForStorage(myAgent);

            // Получение сообщения от своего накопителя об
            // оплате энергии и обновление за оплату энергии
            receiveMsgAboutPaymentForWindEnergy(myAgent);

            // Подведение итогов оплаты энергии за сутки для Storage1
            controlPaymentStorageForPV(myAgent);

            // Подведение итогов оплаты энергии от Wind1 за сутки для Storage1
            controlPaymentStorageForWind(myAgent);

            // Проверка выполнения сервисного контракта (оплата накопителю)
            controlPaymentForStorage(myAgent,credentialsStorageCounter1, "Storage1");

            // Отправка сообщения накопителю о выплате
            // комиссии
            //sendMsgAboutCommission(myAgent,"Storage1");

            // StorageCounter1 получает сообщение от накопителя,
            // что тот оплатил и проверяет оплату
            //receiveMsgAboutCommissionCompleted(myAgent,credentialsStorageCounter1);

            // Принятие данных, о том что Wind генератор может продать энергию
            //receiveMsgAboutEnergy(myAgent, "WindEnergyForStorage", "Storage1");

            // Принятие данных, о том что PV генератор может продать энергию
            //receiveMsgAboutEnergy(myAgent, "PVEnergyForStorage", "Storage1");

            // Обновление данных об оплате энергии накопителем
            //updatePaymentEnergyForStorage(myAgent);

        } else if (myAgent.getLocalName().equals("StorageCounter2")) {

            receiveTimeFromDist(myAgent);

            // Регистрация в PV контрактах
            registerInPVContracts(myAgent, PUBLIC_KEY_StorageCounter2, credentialsStorageCounter2,
                    "DeployedPVAddressFromStorage", "PVPenaltyAddressFromStorage");

            // Регистрация в Wind контрактах
            registerInWindContracts(myAgent, PUBLIC_KEY_StorageCounter2, credentialsStorageCounter2,
                    "DeployedWindAddressFromStorage", "WindPenaltyAddressFromStorage");

            // Регистрация в сервисных контрактах
            registerInServiceContract(myAgent, PUBLIC_KEY_StorageCounter2, credentialsStorageCounter2);

            receiveProficitPVMsg(myAgent, "Storage2","StorageCharge2",myAgent.getLocalName());

            //Принятие сообщение от счетчика ветрогенератора о профиците энергии
            receiveWindProficitMSG(myAgent, "Storage2","StorageCharge2",myAgent.getLocalName());

            // Принятие данных PV energy из Матлаба и отправка данных накопителю для оплаты
            receivePVEnergyFromMatlab(myAgent, "Storage2");

            // Принятие данных Wind energy из Матлаба и отправка данных накопителю для оплаты
            receiveEnergyFromWindProfocit(myAgent,"Wind2","Storage2");

            // Принятие данных о заряде накопителя в % из Матлаба
            //receiveStorageChargeFromMatlab();

            // Принятие сообщения об оплате за энергию от
            // своего накопителя и обновляют суммарно принятую энергию и
            // суммарную оплату за эту энергию
            updatePaymentPVEnergyForStorage(myAgent);

            // Получение сообщения от своего накопителя об
            // оплате энергии и обновление за оплату энергии
            receiveMsgAboutPaymentForWindEnergy(myAgent);

            // Подведение итогов оплаты энергии за сутки для Storage2
            controlPaymentStorageForPV(myAgent);

            // Подведение итогов оплаты энергии от Wind2 за сутки для Storage2
            controlPaymentStorageForWind(myAgent);

            // Проверка выполнения сервисного контракта (оплата накопителю)
            controlPaymentForStorage(myAgent,credentialsStorageCounter2, "Storage2");

            // Отправка сообщения накопителю о выплате
            // комиссии
            //sendMsgAboutCommission(myAgent,"Storage2");

            // StorageCounter2 получает сообщение от накопителя,
            // что тот оплатил и проверяет оплату
            //receiveMsgAboutCommissionCompleted(myAgent,credentialsStorageCounter2);

            // Принятие данных, о том что генератор может продать энергию
            //receiveMsgAboutEnergy(myAgent, "WindEnergyForStorage", "Storage2");

            // Принятие данных, о том что PV генератор может продать энергию
            //receiveMsgAboutEnergy(myAgent, "PVEnergyForStorage", "Storage2");

            // Обновление данных об оплате энергии накопителем
            //updatePaymentEnergyForStorage(myAgent);

        }else {

            receiveTimeFromDist(myAgent);


            // Регистрация в PV контрактах
            registerInPVContracts(myAgent, PUBLIC_KEY_StorageCounter3, credentialsStorageCounter3,
                    "DeployedPVAddressFromStorage", "PVPenaltyAddressFromStorage");

            // Регистрация в Wind контрактах
            registerInWindContracts(myAgent, PUBLIC_KEY_StorageCounter3, credentialsStorageCounter3,
                    "DeployedWindAddressFromStorage", "WindPenaltyAddressFromStorage");

            // Регистрация в сервисных контрактах
            registerInServiceContract(myAgent, PUBLIC_KEY_StorageCounter3, credentialsStorageCounter3);

            // Принятие сообщения от счетчика PV о профиците энергии
            receiveProficitPVMsg(myAgent, "Storage3","StorageCharge3",myAgent.getLocalName());

            // Принятие сообщение от счетчика ветрогенератора о профиците энергии
            receiveWindProficitMSG(myAgent, "Storage3","StorageCharge3",myAgent.getLocalName());

            // Принятие данных PV energy из Матлаба и отправка данных накопителю для оплаты
            receivePVEnergyFromMatlab(myAgent, "Storage3");

            // Принятие данных Wind energy из Матлаба и отправка данных накопителю для оплаты
            receiveEnergyFromWindProfocit(myAgent,"Wind3","Storage3");

            // Принятие данных о заряде накопителя в % из Матлаба
            //receiveStorageChargeFromMatlab();

            // Принятие сообщения об оплате за энергию от
            // своего накопителя и обновляют суммарно принятую энергию и
            // суммарную оплату за эту энергию
            updatePaymentPVEnergyForStorage(myAgent);

            // Получение сообщения от своего накопителя об
            // оплате энергии и обновление за оплату энергии
            receiveMsgAboutPaymentForWindEnergy(myAgent);

            // Подведение итогов оплаты энергии за сутки для Storage3
            controlPaymentStorageForPV(myAgent);

            // Подведение итогов оплаты энергии от Wind3 за сутки для Storage3
            controlPaymentStorageForWind(myAgent);

            // Проверка выполнения сервисного контракта (оплата накопителю)
            controlPaymentForStorage(myAgent,credentialsStorageCounter3,"Storage3");

            // Отправка сообщения накопителю о выплате
            // комиссии
            //sendMsgAboutCommission(myAgent,"Storage3");

            // StorageCounter3 получает сообщение от накопителя,
            // что тот оплатил и проверяет оплату
            //receiveMsgAboutCommissionCompleted(myAgent,credentialsStorageCounter3);

            // Принятие данных, о том что генератор может продать энергию
            //receiveMsgAboutEnergy(myAgent, "WindEnergyForStorage", "Storage3");

            // Принятие данных, о том что PV генератор может продать энергию
            //receiveMsgAboutEnergy(myAgent, "PVEnergyForStorage", "Storage3");

            // Обновление данных об оплате энергии накопителем
            //updatePaymentEnergyForStorage(myAgent);
        }

    }

    // TODO: StorageCounters принимают адреса основных и штраных контрактов от своих накопителей
    private void registerInPVContracts(Agent storageCounter, String publicKey, Credentials credentials,
                                       String mainProtocol, String penaltyProtocol){

        // Счетчики накопителей получают адреса контрактов от накопителей
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(mainProtocol);
        ACLMessage msg = storageCounter.receive(messageTemplate);
        try {
            if (msg!=null){

                String deployedAddress = msg.getContent();

                System.out.println(storageCounter.getLocalName() + " receive deployed contract address " +
                        deployedAddress + " from " + msg.getSender().getLocalName());

                // StorageCounter складывает в свой datastore
                // адрес основного PV контракта
                getDataStore().put(storageCounter.getLocalName() + "deployedAddress",
                        deployedAddress);


                // Если StorageCounter1
                if (storageCounter.getLocalName().equals("StorageCounter1") && flagRegisterPV1){

                    // StorageCounter1 создает экземпляр контракта
                    pvContract1 = PVContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(storageCounter.getLocalName() + " with publicKey " + publicKey
                            + " create pvContract1 sample " +
                            pvContract1.getContractAddress());

                    // StorageCounter1 регистрируется в контракте pvContract1


                    if (pvContract1.registrationStorageCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() + " with public key " +
                                publicKey + " register in pvContract1 " + pvContract1.getContractAddress() + ANSI_RESET);
                    }


                    flagRegisterPV1 = false;
                }


                // Если StorageCounter2
                if (storageCounter.getLocalName().equals("StorageCounter2") && flagRegisterPV2){

                    // StorageCounter2 создает экземпляр контракта
                    pvContract2 = PVContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(storageCounter.getLocalName() + " with public key " + publicKey +
                            " create pvContract2 sample " + pvContract2.getContractAddress());

                    // StorageCounter2 регистрируется в контракте
                    if (pvContract2.registrationStorageCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() + " with public key " +
                                publicKey + " register in pvContract2 " +
                                pvContract2.getContractAddress() + ANSI_RESET);
                    }


                    flagRegisterPV2 = false;
                }

                // Если StorageCounter3
                if (storageCounter.getLocalName().equals("StorageCounter3") && flagRegisterPV3){

                    //StorageCounter3 созлает экземпляр контракта
                    pvContract3 = PVContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(storageCounter.getLocalName() + " with public key " + publicKey +
                            " create pvContract3 sample " + pvContract3.getContractAddress());

                    // StorageCounter3 регистрируется в контракте
                    if (pvContract3.registrationStorageCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() + " with public key " + publicKey +
                                " register in pvContract3 " + pvContract3.getContractAddress() + ANSI_RESET);
                    }

                    flagRegisterPV3 = false;
                }


            }else {
                block();
            }


            // Накопители получают адреса штрафных контрактов от PV
            MessageTemplate messageTemplate1 = MessageTemplate.MatchProtocol(penaltyProtocol);
            ACLMessage msg1 = storageCounter.receive(messageTemplate1);

            if (msg1!=null){

                String penaltyAddress = msg1.getContent();

                System.out.println(storageCounter.getLocalName() + " with publicKey " + publicKey +
                        " receive penaltyAddress " + penaltyAddress + " from " +
                        msg1.getSender().getLocalName());

                // Если StorageCounter1
                if (storageCounter.getLocalName().equals("StorageCounter1") && flagRegisterFinePV1){

                    // StorageCounter1 создает экземпляр контракта
                    pvPenaltyContract1 = PVPenaltyContract.load(penaltyAddress, web3j,
                            credentials, contractGasProvider);

                    System.out.println(storageCounter.getLocalName() + " with publicKey " + publicKey +
                            " create pvPenaltyContract1 contract sample " + pvPenaltyContract1.getContractAddress());

                    // StorageCounter1 регистрируется в pvPenaltyContract1
                    if (pvPenaltyContract1.registrationStorageCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() + " with publicKey " + publicKey +
                                " register in pvPenaltyContract1 " + pvPenaltyContract1.getContractAddress() + ANSI_RESET);
                    }

                    flagRegisterFinePV1 = false;

                }

                // Если StorageCounter2
                if (storageCounter.getLocalName().equals("StorageCounter2") && flagRegisterFinePV2){

                    // StorageCounter2 создает экземпляр контракта
                    pvPenaltyContract2 = PVPenaltyContract.load(penaltyAddress, web3j,
                            credentials, contractGasProvider);

                    System.out.println(storageCounter.getLocalName() + " with public key " + publicKey +
                            " create pvPenaltyContract2 sample " + pvPenaltyContract2.getContractAddress());

                    // StorageCounter2 регистрируется в pvPenaltyContract2 контракте
                    if (pvPenaltyContract2.registrationStorageCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() + " with public key " + publicKey +
                                " register in pvPenaltyContract2 " + pvPenaltyContract2.getContractAddress() + ANSI_RESET);
                    }

                    flagRegisterFinePV2 = false;
                }


                // Если StorageCounter3
                if (storageCounter.getLocalName().equals("StorageCounter3") && flagRegisterFinePV3){

                    // StorageCounter3 создает экземпляр контракта
                    pvPenaltyContract3 = PVPenaltyContract.load(penaltyAddress, web3j,
                            credentials, contractGasProvider);

                    System.out.println(storageCounter.getLocalName() + " with public key " + publicKey +
                            " create pvPenaltyContract3 sample " + pvPenaltyContract3.getContractAddress());

                    // StorageCounter3 регистрируется в pvPenaltyContract3 контракте
                    if (pvPenaltyContract3.registrationStorageCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() + " with public key " + publicKey +
                                " register in pvPenaltyContract3 " + pvPenaltyContract3.getContractAddress() + ANSI_RESET);
                    }

                    flagRegisterFinePV3 = false;
                }
            }else {
                block();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // TODO: StorageCounters принимают адреса основных и штрафных контрактов от своих накопителей
    private void registerInWindContracts(Agent storageCounter, String publicKey, Credentials credentials,
                                         String mainProtocol, String penaltyProtocol){

        // Счетчики накопителей получают адреса контрактов от Wind
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(mainProtocol);
        ACLMessage msg = storageCounter.receive(messageTemplate);
        try {
            if (msg!=null){

                String deployedAddress = msg.getContent();

                System.out.println(storageCounter.getLocalName() + " receive deployed contract address " +
                        deployedAddress + " from " + msg.getSender().getLocalName());


                // Если StorageCounter1
                if (storageCounter.getLocalName().equals("StorageCounter1") && flagRegisterWind1){

                    // StorageCounter1 создает экземпляр контракта
                    windContract1 = WindContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(storageCounter.getLocalName() + " with publicKey " + publicKey
                            + " create windContract1 sample " +
                            windContract1.getContractAddress());

                    // StorageCounter1 регистрируется в контракте windContract1
                    if (windContract1.registrationStorageCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() + " with public key " +
                                publicKey + " register in windContract1 " + windContract1.getContractAddress() + ANSI_RESET);
                    }

                    flagRegisterWind1 = false;

                }


                // Если StorageCounter2
                if (storageCounter.getLocalName().equals("StorageCounter2") && flagRegisterWind2){

                    // Storage2 создает экземпляр контракта
                    windContract2 = WindContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(storageCounter.getLocalName() + " with public key " + publicKey +
                            " create windContract2 sample " + windContract2.getContractAddress());

                    // StorageCounter2 регистрируется в контракте
                    if (windContract2.registrationStorageCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() + " with public key " +
                                publicKey + " register in windContract2 " +
                                windContract2.getContractAddress() + ANSI_RESET);
                    }

                    flagRegisterWind2 = false;
                }

                // Если StorageCounter3
                if (storageCounter.getLocalName().equals("StorageCounter3") && flagRegisterWind3){

                    // StorageCounter3 созлает экземпляр контракта
                    windContract3 = WindContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(storageCounter.getLocalName() + " with public key " + publicKey +
                            " create windContract3 sample " + windContract3.getContractAddress());

                    //StorageCounter3 регистрируется в контракте
                    if (windContract3.registrationStorageCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() + " with public key " + publicKey +
                                " register in windContract3 " + windContract3.getContractAddress() + ANSI_RESET);
                    }

                    flagRegisterWind3 = false;
                }


            }else {
                block();
            }


            // Накопители получают адреса штрафных контрактов от Wind
            MessageTemplate messageTemplate1 = MessageTemplate.MatchProtocol(penaltyProtocol);
            ACLMessage msg1 = storageCounter.receive(messageTemplate1);

            if (msg1!=null){

                String penaltyAddress = msg1.getContent();

                System.out.println(storageCounter.getLocalName() + " with publicKey " + publicKey +
                        " receive penaltyAddress " + penaltyAddress + " from " +
                        msg1.getSender().getLocalName());

                // Если StorageCounter1
                if (storageCounter.getLocalName().equals("StorageCounter1") && flagRegisterFineWind1){

                    // StorageCounter1 создает экземпляр контракта
                    windPenaltyContract1 = WindPenaltyContract.load(penaltyAddress, web3j,
                            credentials, contractGasProvider);

                    System.out.println(storageCounter.getLocalName() + " with publicKey " + publicKey +
                            " create windPenaltyContract1 contract sample " + windPenaltyContract1.getContractAddress());

                    // StorageCounter1 регистрируется в windPenaltyContract1
                    if (windPenaltyContract1.registrationStorageCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() + " with publicKey " + publicKey +
                                " register in windPenaltyContract1 " + windPenaltyContract1.getContractAddress() + ANSI_RESET);
                    }

                    flagRegisterFineWind1 = false;
                }

                // Если StorageCounter2
                if (storageCounter.getLocalName().equals("StorageCounter2") && flagRegisterFineWind2){

                    // StorageCounter2 создает экземпляр контракта
                    windPenaltyContract2 = WindPenaltyContract.load(penaltyAddress, web3j,
                            credentials, contractGasProvider);

                    System.out.println(storageCounter.getLocalName() + " with public key " + publicKey +
                            " create windPenaltyContract2 sample " + windPenaltyContract2.getContractAddress());

                    // StorageCounter2 регистрируется в pvPenaltyContract2 контракте
                    if (windPenaltyContract2.registrationStorageCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() + " with public key " + publicKey +
                                " register in windPenaltyContract2 " + windPenaltyContract2.getContractAddress() + ANSI_RESET);
                    }

                    flagRegisterFineWind2 = false;
                }


                // Если StorageCounter3
                if (storageCounter.getLocalName().equals("StorageCounter3") && flagRegisterFineWind3){

                    // StorageCounter3 создает экземпляр контракта
                    windPenaltyContract3 = WindPenaltyContract.load(penaltyAddress, web3j,
                            credentials, contractGasProvider);

                    System.out.println(storageCounter.getLocalName() + " with public key " + publicKey +
                            " create windPenaltyContract3 sample " + windPenaltyContract3.getContractAddress());

                    // StorageCounter3 регистрируется в windPenaltyContract3 контракте
                    if (windPenaltyContract3.registrationStorageCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() + " with public key " + publicKey +
                                " register in windPenaltyContract3 " + windPenaltyContract3.getContractAddress() + ANSI_RESET);
                    }

                    flagRegisterFineWind3 = false;
                }
            }else {
                block();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //TODO: Счетчики накопителей принимают адреса сервисных контрактов от накопителей
    // создают экзмепляры контрактов и регистрируются в контарктах
    private void registerInServiceContract(Agent storageCounter, String publicKey, Credentials credentials){

        // Счетчики накопителей принимают адреса сервисных контарков от накопителй
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("ServiceAddressFromStorage");
        ACLMessage msg = storageCounter.receive(messageTemplate);

        if (msg!=null){

            String serviceAddress = msg.getContent();

            System.out.println(storageCounter.getLocalName() + " with public Key " + publicKey +
                    " receive serviceAddress " + serviceAddress + " from " +
                    msg.getSender().getLocalName());

            // Складываем адрес сервисного контракта в datastore,
            // чтобы затем везде можно было создавать экземпляры
            // контрактов
            getDataStore().put(storageCounter.getLocalName()+"serviceAddress",serviceAddress);

            try {
            // Если StorageCounter1
            if (storageCounter.getLocalName().equals("StorageCounter1") && flagRegisterServiceContract1){

                // StorageCounter1 создает экземпляр serviceContract1
                serviceContract1 = ServiceContract.load(serviceAddress, web3j,
                        credentials, contractGasProvider);

                System.out.println(storageCounter.getLocalName() + " with public key " +
                        publicKey + " create serviceContract1 sample " +
                        serviceContract1.getContractAddress());

                // StorageCounter1 регистрируется в serviceContract1

                    if (serviceContract1.registrationStorageCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() + " with public key " +
                                publicKey + " register in serviceContract1 " +
                                serviceContract1.getContractAddress() + ANSI_RESET);
                    }

                    flagRegisterServiceContract1 = false;
            }


            // Если StorageCounter2
                if (storageCounter.getLocalName().equals("StorageCounter2") && flagRegisterServiceContract2){

                    // StorageCounter2 создает экзмпляр контракта serviceContract2
                    serviceContract2 = ServiceContract.load(serviceAddress, web3j,
                            credentials, contractGasProvider);

                    System.out.println(storageCounter.getLocalName() + " with public key " +
                            publicKey + " create serviceContract2 sample " +
                            serviceContract2.getContractAddress());

                    // StorageCounter2 регистрируется в serviceContract2
                    if (serviceContract2.registrationStorageCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() + " with public key " +
                                publicKey + " create serviceContract2 sample " +
                                serviceContract2.getContractAddress() + ANSI_RESET);
                    }

                    flagRegisterServiceContract2 = false;
                }


                // Если StorageCounter3
                if (storageCounter.getLocalName().equals("StorageCounter3") && flagRegisterServiceContract3){

                    //StorageCounter3 создает экземпляр контракта serviceContract3
                    serviceContract3 = ServiceContract.load(serviceAddress, web3j,
                            credentials, contractGasProvider);

                    System.out.println(storageCounter.getLocalName() + " with publicKey " +
                            publicKey + " create serviceContract3 sample " +
                            serviceContract3.getContractAddress());

                    //StorageCounter3 регистрируется в serviceContract3
                    if (serviceContract3.registrationStorageCounter(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() + " with public key " +
                                publicKey + " register in serviceContract3 " +
                                serviceContract3.getContractAddress() + ANSI_RESET);
                    }

                    flagRegisterServiceContract3 = false;
                }

            } catch (Exception e) {
                System.out.println();
            }
        }else {
            block();
        }
    }

    // TODO: Метод принятия данных из Матлаба о емкости накопителя (и о накопленной мощности)
    //  (принимаем суммы по MQTT всегда)
    //  !пока сделаем рандомайзер, что накопителю не хватает столько-то энергии
    //  потом закомментить!!!
    //  пока даже здесь ничего не пишем, число уже установлено
    private Double receiveStorageChargeFromMatlab(String topic, String clientID){

        // Если накопитель полностью заряжен или заряжается, то принимаем "0" из Матлаба
        //,cледовательно энергию закупать не нужно (следовательно сообщение накопителю о покупке
        // не отправляется - needEnergy присвоить значение 0)

        //TODO: Здесь принимаем число заряда накопителя в % по MQTT
        storageCharge = mqttSubscriber.mqttSubscriber(topic,clientID);
        return storageCharge;

    }


    // TODO: Метод принятия сообщения от PVCounter, что профицит энергии,
    //  отправка согласия на получение энергии
    private void receiveProficitPVMsg(Agent storageCounter, String storageName,
                                      String topic, String clientID){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("ProficitEnergy");
        ACLMessage msg = storageCounter.receive(messageTemplate);

        if (msg!=null){

            System.out.println(storageCounter.getLocalName() + " receive msg " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());

            // Добавим в datastore имя PV от которого приняли сообщение о профиците энергии
            getDataStore().put(storageCounter.getLocalName() + "pvNameProficit", msg.getSender().getLocalName());

            // Если storageCharge < 95, то энергия накопителю нужна, отправляем согласие
            // на принятие энергии от PV генератора
            // TODO: Каждый счетчик накопителя принимает
            //  сигнал из Матлаба о заряде своего накопителя
            storageCharge = receiveStorageChargeFromMatlab(topic,clientID);
            if (storageCharge < 95){
                System.out.println();
                sendMsg(storageCounter, "AgreeProficitPVEnergy",
                        msg.getSender().getLocalName(), "AgreeProficitPVEnergy");
            } else {
                System.out.println();
                System.err.println(storageName + " is charged!!!");
            }
        }else {
            block();
        }
    }


    // TODO: Метод получения энергии от PVCounter
    private void receivePVEnergyFromMatlab(Agent storageCounterName, String storage) {

        // Пока временно будем принимать сообщение от PVCounter о старте передачи энергии
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("StartPVEnergy");
        ACLMessage msg = storageCounterName.receive(messageTemplate);

        if (msg!=null) {

            int pvProficitEnergy = (int)Double.parseDouble(msg.getContent());

            if (pvProficitEnergy != 0) {
                // StorageCounter берет из datastore имя pv от кого накопитель получает энергию
                // и отправляет имя и энергию накопителю
                //if (getDataStore().get(storageCounterName.getLocalName() + "pvNameProficit")!=null) {
                //String pvName = String.valueOf(getDataStore().get(storageCounterName.getLocalName() + "pvNameProficit"));
                // TODO: Счетчик накопителя отправляет своему накопителю
                //  кол-во профицита энергии, за которое нужно оплатить
                sendMsg(storageCounterName, "PVEnergyFromMatlab", storage, String.valueOf(pvProficitEnergy));
                //}
            }

        }else {
            block();
        }

    }


    // TODO: StorageCounters принимают сообщение об оплате за энергию от
    //  своего накопителя и обновляют суммарно принятую энергию и
    //  суммарную оплату за эту энергию
    private void updatePaymentPVEnergyForStorage(Agent storageCounter){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("PaymentPVEnergyCompleted");
        ACLMessage msg = storageCounter.receive(messageTemplate);

        if (msg!=null){

            int payment = (int)Double.parseDouble(msg.getContent());
            //String pvName = msg.getContent().split(":")[1];

            System.out.println(storageCounter.getLocalName() + " receive " + payment +
                    " from " + msg.getSender().getLocalName());

            // После принятия сообщения от своего накопителя об оплате за энергию
            // счетчик накопителя обновляют суммарно принятую энергию и
            // суммарную оплату за эту энергию
            try {
            if (storageCounter.getLocalName().equals("StorageCounter1")){
                if (pvContract1!=null){
                    if (pvContract1.updatePaymentPVEnergyForStorage().send().isStatusOK()){
                        System.out.println(storageCounter.getLocalName() + " update sumStoragePVEnergy and " +
                                "sumPaymentPVEnergyForStorage");
                    }

                    // Временно ставим счетчик для хода времени (часы)
                    //pv1Counter+=1;
                }
            } else if (storageCounter.getLocalName().equals("StorageCounter2")){
                if (pvContract2!=null){
                    if (pvContract2.updatePaymentPVEnergyForStorage().send().isStatusOK()){
                        System.out.println(storageCounter.getLocalName() + " update sumStoragePVEnergy and " +
                                "sumPaymentPVEnergyForStorage");
                    }

                    //pv2Counter+=1;
                }
            }else {
                if (pvContract3!=null){
                    if (pvContract3.updatePaymentPVEnergyForStorage().send().isStatusOK()){
                        System.out.println(storageCounter.getLocalName() + " update sumStoragePVEnergy and " +
                                "sumPaymentPVEnergyForStorage");
                    }

                    //pv3Counter+=1;
                }
            }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            block();
        }
    }


    // TODO: При приходе сигнала из Матлаба по MQTT о прошедших сутках
    //  счетчик накопителя проверяет выполнение оплаты накопителем
    //  за покупку энергии у PV
    private void controlPaymentStorageForPV(Agent storageCounter) {

        // Принимаем сигналы о времени из Матлаба по MQTT

        try {
        if ((int)counterTime == 24/* * n1*/){

            if (pvContract1!=null) {
                // Сделаем здесь
                // Если storageCounter1
                if (storageCounter.getLocalName().equals("StorageCounter1")) {

                    // Выполняем проверку выполнения оплаты накопителем
                    // за покупку энергии у PV
                    if (pvContract1.controlPaymentStorageForPV().send().isStatusOK()){
                        System.out.println(storageCounter.getLocalName() + " control storage1 payment " +
                                " for PV1 ");
                    }

                    // После выполнения проверки оплаты накопителем за покупку энергии у PV1
                    // выводим на экран результаты
                    System.out.println(ANSI_GREEN_BACKGROUND + "============ VIEW STORAGE1 PAYMENT FOR PV1 ENERGY" +
                            " ===========" + "\nMust be pay for pv1 energy (storage) " +
                                    pvContract1.viewPaymentStorageForPV().send().component2() +
                            "\nfact payment " + pvContract1.viewPaymentStorageForPV().send().component4() +
                            "\npenny " + pvContract1.viewPaymentStorageForPV().send().component6() +
                            "\npv1 balance " + pvContract1.viewPaymentStorageForPV().send().component8() + ANSI_RESET);

                   // n1+=1;

                }
            }

        }

        if ((int)counterTime == 24/* * n2*/){

            if (pvContract2!=null) {
                // Сделаем здесь
                // Берем из datastore имя PV от кого накопитель принимает энергию
                if (storageCounter.getLocalName().equals("StorageCounter2")) {

                    // Выполняем проверку выполнения оплаты накопителем
                    // за покупку энергии у PV
                    if (pvContract2.controlPaymentStorageForPV().send().isStatusOK()){
                        System.out.println(storageCounter.getLocalName() + " control storage2 payment " +
                                " for PV2 ");
                    }

                    // После выполнения проверки оплаты накопителем за покупку энергии у PV2
                    // выводим на экран результаты
                    System.out.println(ANSI_GREEN_BACKGROUND + "============ VIEW STORAGE2 PAYMENT FOR PV2 ENERGY ===========" +
                            "\nMust be pay for pv2 energy (storage) " +
                            pvContract2.viewPaymentStorageForPV().send().component2() +
                            "\nfact payment " + pvContract2.viewPaymentStorageForPV().send().component4() +
                            "\npenny " + pvContract2.viewPaymentStorageForPV().send().component6() +
                            "\npv2 balance " + pvContract2.viewPaymentStorageForPV().send().component8() + ANSI_RESET);

                    //n2+=1;

                }
            }

        }

        if ((int)counterTime == 24/* * n3*/){

            if (pvContract3!=null) {
                // Сделаем здесь
                // Берем из datastore имя PV от кого накопитель принимает энергию
                if (storageCounter.getLocalName().equals("StorageCounter3")) {

                    // Выполняем проверку выполнения оплаты накопителем
                    // за покупку энергии у PV
                    if (pvContract3.controlPaymentStorageForPV().send().isStatusOK()){
                        System.out.println(storageCounter.getLocalName() + " control storage3 payment " +
                                " for PV3 ");
                    }

                    // После выполнения проверки оплаты накопителем за покупку энергии у PV3
                    // выводим на экран результаты
                    System.out.println(ANSI_GREEN_BACKGROUND + "============ VIEW STORAGE3 PAYMENT FOR PV3 ENERGY ===========" +
                            "\nMust be pay for pv3 energy (storage) " +
                            pvContract3.viewPaymentStorageForPV().send().component2() +
                            "\nfact payment " + pvContract3.viewPaymentStorageForPV().send().component4() +
                            "\npenny " + pvContract3.viewPaymentStorageForPV().send().component6() +
                            "\npv3 balance " + pvContract3.viewPaymentStorageForPV().send().component8() + ANSI_RESET);


                    //n3+=1;

                }
            }

        }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    // TODO: Принятие сообщение от счетчика ветрогенератора о профиците энергии
    private void receiveWindProficitMSG(Agent storageCounter, String storageName,
                                        String topic, String clientID){
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("ProficitWindEnergy");
        ACLMessage msg = storageCounter.receive(messageTemplate);

        if (msg!=null){
            System.out.println(storageCounter.getLocalName() +
                    " receive " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());

            // Счетчик нокопителя смотрит какой заряд у накопителя,
            // если < 95 %, то  энергия нужна
            storageCharge = receiveStorageChargeFromMatlab(topic,clientID);
            if ((int)storageCharge < 95){

                // Отправляем сообщение обратно счетчику ветряка, что
                // о согласии получения энергии
                sendMsg(storageCounter,"AgreeWindEnergy",
                        msg.getSender().getLocalName(),"AgreeWindEnergy");
            }else {
                System.err.println(storageName + " is charghed!");
            }
        }else {
            block();
        }
    }


    // TODO: Принятие сигнала от Wind из Матлаба по MQTT
    private void receiveEnergyFromWindProfocit(Agent storageCounter, String windName,
                                               String storageName){
        // Пока временно принимаем сообщения о старте подачи энергии (потом закоментить)
        // и начинаем принимать энергию и отпрвлять её накопителю для оплаты
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("StartWindEnergyToStorage");
        ACLMessage msg = storageCounter.receive(messageTemplate);

        if (msg!=null){
            System.out.println(ANSI_GREEN + storageCounter.getLocalName() + " receive " +
                    msg.getContent() + " from " +
                    msg.getSender().getLocalName() + ANSI_RESET);

            // Пока счетчик Wind будет всегда слать это сообщение, затем просто
            // уберем принятие сообщения и оставим только принятие сигнала по MQTT от Wind
            if (energyFromWindProficit!=0){
                System.out.println(ANSI_GREEN + storageCounter.getLocalName() +
                        " receive profocit energy " + energyFromWindProficit +
                                " from " + windName + ANSI_RESET
                        );

                // После принятия энергии из Матлаба счетчик накопителя отправляет
                // эту энергию для оплаты накопителю
                sendMsg(storageCounter,"ProficitWindEnergyForPayment",
                        storageName, String.valueOf(energyFromWindProficit));
            }
        }else {
            block();
        }
    }


    // TODO: Принятие сообщения о совершении оплаты от накопителя и обновления суммарной оплаты
    private void receiveMsgAboutPaymentForWindEnergy(Agent storageCounter){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("PaymentForWindCompleted");
        ACLMessage msg = storageCounter.receive(messageTemplate);

        if (msg!=null){
            System.out.println(ANSI_GREEN + storageCounter.getLocalName() +
                    " receive " + msg.getContent() + " from " +
                    msg.getSender().getLocalName() + ANSI_RESET);

            try {
            // После принятия сообщения счетчик накопителя обновляет оплату за энергию от Wind
            if (storageCounter.getLocalName().equals("StorageCounter1")){
                if (windContract1!=null){
                    if (windContract1.updatePaymentWindEnergyForStorage().send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() +
                                " update storage1 payment for Wind1 energy " + ANSI_RESET);
                    }

                    //counterWind1 +=1;

                }
            } else if (storageCounter.getLocalName().equals("StorageCounter2")){
                if (windContract2!=null){
                    if (windContract2.updatePaymentWindEnergyForStorage().send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() +
                                " update storage2 payment for Wind2 energy " + ANSI_RESET);
                    }

                    //counterWind2 +=1;
                }
            }else {
                if (windContract3!=null){
                    if (windContract3.updatePaymentWindEnergyForStorage().send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() +
                                " update storage3 payment for Wind3 energy " + ANSI_RESET);
                    }

                    //counterWind3 +=1;
                }
            }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            block();
        }
    }


    // TODO: Метод проверки оплаты накопителем за покупку энергии у Wind
    //  контроль за оплату по истечении каждых суток
    private void controlPaymentStorageForWind(Agent storageCounter){

        try {

        if ((int)counterTime == 24/* * windDay1*/){
            if (storageCounter.getLocalName().equals("StorageCounter1")){
                if (windContract1!=null){
                    if (windContract1.controlPaymentStorageForWind().send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() +
                                " control payment for storage1 per day" + ANSI_RESET);
                    }

                    // Выводим на экран итоги выполнения условий по оплате
                    // накопителем за покупку энергии у Wind1
                    System.out.println(ANSI_GREEN_BACKGROUND + "======= Total storage1 payment for Win1 energy ===== Day"
                            + windDay1+" ======" +
                            "\nMust be pay for wind energy1 (storage) " + windContract1.
                            viewPaymentStorageForWind().send().component2() +
                            "\nfact payment " + windContract1.viewPaymentStorageForWind().send().component4() +
                            "\npenny " + windContract1.viewPaymentStorageForWind().send().component6() +
                            "\nwind balance " + windContract1.viewPaymentStorageForWind().send().component8() +
                            ANSI_RESET);

                    //windDay1 +=1;
                }
            }
        }

        if ((int)counterTime == 24/* * windDay2*/){
            if (storageCounter.getLocalName().equals("StorageCounter2")){
                if (windContract2!=null){
                    if (windContract2.controlPaymentStorageForWind().send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() +
                                " control payment for storage2 per day "  + ANSI_RESET);
                    }

                    // Выводим на экран итоги выполнения условий по оплате
                    // накопителем за покупку энергии у Wind2
                    System.out.println(ANSI_GREEN_BACKGROUND + "======= Total storage2 payment for Win2 energy ===== Day"
                            + windDay2+" ======" +
                            "\nMust be pay for wind2 energy (storage) " + windContract2.
                            viewPaymentStorageForWind().send().component2() +
                            "\nfact payment " + windContract2.viewPaymentStorageForWind().send().component4() +
                            "\npenny " + windContract2.viewPaymentStorageForWind().send().component6() +
                            "\nwind balance " + windContract2.viewPaymentStorageForWind().send().component8() +
                            ANSI_RESET);

                    //windDay2 +=1;
                }
            }
        }

        if ((int)counterTime == 24 /* windDay3*/){
            if (storageCounter.getLocalName().equals("StorageCounter3")){
                if (windContract3!=null){
                    if (windContract3.controlPaymentStorageForWind().send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storageCounter.getLocalName() +
                                " control payment for storage3 per day " + ANSI_RESET);
                    }

                    // Выводим на экран итоги выполнения условий по оплате
                    // накопителем за покупку энергии у Wind2
                    System.out.println(ANSI_GREEN_BACKGROUND + "======= Total storage3 payment for Wind3 energy ===== Day"
                            + windDay3 +" ======" +
                            "\nMust be pay for wind3 energy (storage) " + windContract3.
                            viewPaymentStorageForWind().send().component2() +
                            "\nfact payment " + windContract3.viewPaymentStorageForWind().send().component4() +
                            "\npenny " + windContract3.viewPaymentStorageForWind().send().component6() +
                            "\nwind balance " + windContract3.viewPaymentStorageForWind().send().component8() +
                            ANSI_RESET);

                    //windDay3 +=1;
                }
            }
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // TODO: StorageCounter принимает сообщение от счетчика
    //  накопителя  проверяет выполнение сервисного
    //  контракта (проверка установленной оплаты для накопителя)
    private void controlPaymentForStorage(Agent storageCounter, Credentials credentials,
                                          String storageName){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("controlPaymentForStorage");
        ACLMessage msg = storageCounter.receive(messageTemplate);

        if (msg!=null){

            System.out.println(ANSI_GREEN + storageCounter.getLocalName() +
                    " receive " + msg.getContent() +
                    " from " + msg.getSender().getLocalName() + ANSI_RESET);

            // После принятия сообщения о необходимости
            // выполнения проверки счетчик накопителя
            // создает экземпляр контракта
            // Сначала достает адрес сервисного контракта из datastore
            try {

            if (getDataStore().get(storageCounter.getLocalName()+"serviceAddress")!=null){
                String serviceAddress =
                        String.valueOf(
                                getDataStore().get(storageCounter.getLocalName()+"serviceAddress"));
                System.out.println(ANSI_GREEN + storageCounter.getLocalName() +
                        " create service contract sample " +
                        serviceAddress + ANSI_RESET);

                ServiceContract serviceContract = ServiceContract.load(
                        serviceAddress,web3j,credentials,contractGasProvider
                );

                // Счетчик накопителя выполняет транзакцию
                // по проверке выполнения контракта
                if (serviceContract.controlPaymentForStorage().send().isStatusOK()){
                    System.out.println(ANSI_GREEN + storageCounter.getLocalName() +
                            " control sum payment for storage " + ANSI_RESET);

                    // Счетчик накопителя выводит на экран
                    // выполнение условий контракта
                    System.out.println(ANSI_GREEN_BACKGROUND + "======= Total control payment for" +
                                    storageName + " ========" +
                            "\nMust be pay " + serviceContract.
                            viewControlPaymentForStorage().send().component2()
                    + "\nfact payment " + serviceContract.
                            viewControlPaymentForStorage().send().component4() +
                            "\nfine " + serviceContract.
                            viewControlPaymentForStorage().send().component6() + ANSI_RESET);
                }

            }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            block();
        }
    }

    // TODO: StorageCounter по истечении суток отправляет
    //  сообщение о выплате комиссии своему накопителю
    private void sendMsgAboutCommission(Agent storageCounter, String storageName){

        // пока временно пользуемся счетчиком времени в самой системе
        // пока не настроил принятие сигналов из Матлаба
        if ((int)counterTime == 24){
            if (getDataStore().get(storageCounter.getLocalName() + "deployedAddress")!=null) {
                sendMsg(storageCounter, "commissionToStorageCounter",
                        storageName, "commissionToStorageCounter");
            }
        }

    }


    // TODO: StorageCounter принимает сообщение
    //  об успешной выплате комиссии
    private void receiveMsgAboutCommissionCompleted(Agent storageCounter,
                                                    Credentials credentials){

        MessageTemplate messageTemplate =
                MessageTemplate.MatchProtocol("commissionPaidStorageCounterPV");
        ACLMessage msg = storageCounter.receive(messageTemplate);

        if (msg!=null){
            System.out.println(storageCounter.getLocalName() +
                    " receive msg " +
                    msg.getContent() +
                    " from " + msg.getSender().getLocalName());

            // StorageCounter достает из datastore
            // адрес основного PV контракта
            if (getDataStore().get(storageCounter.getLocalName() + "deployedAddress")!=null){
                String deployedAddress =
                        String.valueOf(
                                getDataStore().get(storageCounter.getLocalName() + "deployedAddress")
                        );

                // StorageCounter создает экземпляр контракта
                // в этом методе
                PVContract pvContract = PVContract.load(deployedAddress,
                        web3j,credentials,contractGasProvider);
                System.out.println(storageCounter.getLocalName() +
                        " create pvContract sample " +
                        pvContract.getContractAddress() +
                        " in method " +
                        "receiveMsgAboutCommissionCompleted()");

                //StorageCounter выполняет транзакцию
                // по проверке оплаты ему комиссии
                try {
                    BigInteger mustPayment = pvContract.controlCommisionStorageCounter().send().component2();
                    BigInteger factPayment = pvContract.controlCommisionStorageCounter().send().component4();

                    System.out.println(ANSI_GREEN_BACKGROUND + "======= Control commission payment " +
                            "to " + storageCounter.getLocalName() + " =======" +
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


    // TODO: Счетчики накопителей принимают сообщение от генераторов, что эти генераторы могут продать
    //  энергию накопителю. Счетчики ответным письмом будут отправлять сколько надо энергии. А
    //  также отправят сообщение накопителю, что нужно заплатить за покупаемую энергию
//    private void receiveMsgAboutEnergy(Agent storageCounter, String receiveProtocol, String storageReceiver){
//
//        // Принятие сообщение от генератора - NeedEnergy?
//        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(receiveProtocol);
//        ACLMessage msg = storageCounter.receive(messageTemplate);
//
//        //System.err.println("=================== need Energy in storageCounterBeh class ================ " + needEnergy);
//
//        if (msg!=null){
//
//            System.out.println(storageCounter.getLocalName() + " receive msg " + msg.getContent() +
//                    " from " + msg.getSender().getLocalName());
//
//            // Ответным письмом счетчики накопителй отправляют сообщение, что
//            // у накопителя не хватает столько то энергии - накопитель покупает
//            // это кол-во у этого генератора (отправляем {value of Energy})
//            AID storage = new AID(storageReceiver, false);
//            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
//            reply.addReceiver(msg.getSender());
//            reply.addReceiver(storage);
//            reply.setProtocol("EnergyBuy");
//            // Отправляем кол-во энергии, которое необходимо купить и того генератора, у которого покупаем
//            reply.setContent(needEnergy + ":" + msg.getSender().getLocalName());
//
//            if (needEnergy!=0) {
//                storageCounter.send(reply);
//                System.out.println(storageCounter.getLocalName() + " send reply to " +
//                        storage.getLocalName() + " and " + msg.getSender().getLocalName());
//            } else {
//                System.err.println(storageReceiver + " is charged or charging!");
//            }
//        }else {
//            block();
//        }
//    }

    // TODO: StorageCounter принимает сообщение от своего накопителя, что тот оплатил за энергию
    //  и счетчик накопителя обновляет оплату за эту энергию
    private void updatePaymentEnergyForStorage(Agent storageCounter) {

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("PaymentCompleted");
        ACLMessage msg = storageCounter.receive(messageTemplate);

        if (msg!=null){

            String generatorName = msg.getContent();

            System.out.println(storageCounter.getLocalName() + " receive msg " +
                    generatorName + " from " + msg.getSender().getLocalName());

            // Обновляем оплату за энергию в нужном контракте
            try {
            if (generatorName.equals("PV1")){

                if (pvContract1!=null){

                    if (pvContract1.updatePaymentPVEnergyForStorage().send().isStatusOK()){
                        System.out.println("========== Update payment for PV1 energy from "
                                + msg.getSender().getLocalName() + " ==============");
                        System.out.println(storageCounter.getLocalName() + " update payment for " +
                                "pv1 energy ");
                    }

                    // После обновления оплаты за энергию StorageCounter1 проверяет
                    // выполнение оплаты за покупку энергии у PV1
                    if (pvContract1.controlPaymentStorageForPV().send().isStatusOK()){
                        System.out.println(storageCounter.getLocalName() + " control payment for " +
                                " energy buy from PV1 ");
                    }

                    //Просмотр выполнения оплаты накопителем за покупку энергии у PV1
                    System.out.println("==========Total storage payment for PV1 =========");
                    System.out.println("Must be pay for pv energy (storage) " + pvContract1.viewPaymentStorageForPV().send().component2());
                    System.out.println("fact payment " + pvContract1.viewPaymentStorageForPV().send().component4());
                    System.out.println("penny " + pvContract1.viewPaymentStorageForPV().send().component6());
                    System.out.println("pv balance " + pvContract1.viewPaymentStorageForPV().send().component8());
                }
            } else if (generatorName.equals("PV2")){
                if (pvContract2!=null){
                    if (pvContract2.updatePaymentPVEnergyForStorage().send().isStatusOK()){
                        System.out.println("========== Update payment for PV2 energy from "
                                + msg.getSender().getLocalName() + " ==============");
                        System.out.println(storageCounter.getLocalName() + " update payment for " +
                                "pv2 energy ");
                    }

                    // После обновления оплаты за энергию StorageCounter2 проверяет
                    // выполнение оплаты за покупку энергии у PV2
                    if (pvContract2.controlPaymentStorageForPV().send().isStatusOK()){
                        System.out.println(storageCounter.getLocalName() + " control payment for " +
                                " energy buy from PV2 ");
                    }

                    //Просмотр выполнения оплаты накопителем за покупку энергии у PV1
                    System.out.println("==========Total storage payment for PV2 =========");
                    System.out.println("Must be pay for pv energy (storage) " + pvContract2.viewPaymentStorageForPV().send().component2());
                    System.out.println("fact payment " + pvContract2.viewPaymentStorageForPV().send().component4());
                    System.out.println("penny " + pvContract2.viewPaymentStorageForPV().send().component6());
                    System.out.println("pv balance " + pvContract2.viewPaymentStorageForPV().send().component8());
                }
            }else if (generatorName.equals("PV3")){
                if (pvContract3!=null){
                    if (pvContract3.updatePaymentPVEnergyForStorage().send().isStatusOK()){
                        System.out.println("========== Update payment for PV3 energy from "
                                + msg.getSender().getLocalName() + " ==============");
                        System.out.println(storageCounter.getLocalName() + " update payment for " +
                                "pv3 energy ");
                    }

                    // После обновления оплаты за энергию StorageCounter3 проверяет
                    // выполнение оплаты за покупку энергии у PV3
                    if (pvContract3.controlPaymentStorageForPV().send().isStatusOK()){
                        System.out.println(storageCounter.getLocalName() + " control payment for " +
                                " energy buy from PV3 ");
                    }

                    //Просмотр выполнения оплаты накопителем за покупку энергии у PV3
                    System.out.println("==========Total storage payment for PV3 =========");
                    System.out.println("Must be pay for pv energy (storage) " + pvContract3.viewPaymentStorageForPV().send().component2());
                    System.out.println("fact payment " + pvContract3.viewPaymentStorageForPV().send().component4());
                    System.out.println("penny " + pvContract3.viewPaymentStorageForPV().send().component6());
                    System.out.println("pv balance " + pvContract3.viewPaymentStorageForPV().send().component8());
                }
            } else if (generatorName.equals("Wind1")){
                if (windContract1!=null){
                    if (windContract1.updatePaymentWindEnergyForStorage().send().isStatusOK()){
                        System.out.println("========== Update payment for Wind1 energy from "
                                + msg.getSender().getLocalName() + " ==============");
                        System.out.println(storageCounter.getLocalName() + " update payment for " +
                                "wind1 energy ");
                    }

                    // После обновления оплаты за энергию StorageCounter1 проверяет
                    // выполнение оплаты за покупку энергии у Wind1
                    if (windContract1.controlPaymentStorageForWind().send().isStatusOK()){
                        System.out.println(storageCounter.getLocalName() + " control payment for " +
                                " energy buy from Wind1 ");
                    }

                    // Просмотр выполнения оплаты накопителем за покупку энергии у Wind3
                    System.out.println("==========Total storage payment for Wind1 =========");
                    System.out.println("Must be pay for wind energy (storage) " +
                            windContract1.viewPaymentStorageForWind().send().component2());
                    System.out.println("fact payment " + windContract1.viewPaymentStorageForWind().send().component4());
                    System.out.println("penny " + windContract1.viewPaymentStorageForWind().send().component6());
                    System.out.println("wind balance " + windContract1.viewPaymentStorageForWind().send().component8());
                }
            } else if (generatorName.equals("Wind2")){
                if (windContract2!=null){
                    if(windContract2.updatePaymentWindEnergyForStorage().send().isStatusOK()){
                        System.out.println("========== Update payment for Wind2 energy from "
                                + msg.getSender().getLocalName() + " ==============");
                        System.out.println(storageCounter.getLocalName() + " update payment for " +
                                "wind2 energy ");
                    }

                    // После обновления оплаты за энергию StorageCounter2 проверяет
                    // выполнение оплаты за покупку энергии у Wind2
                    if (windContract2.controlPaymentStorageForWind().send().isStatusOK()){
                        System.out.println(storageCounter.getLocalName() + " control payment for " +
                                " energy buy from Wind2 ");
                    }

                    // Просмотр выполнения оплаты накопителем за покупку энергии у Wind3
                    System.out.println("==========Total storage payment for Wind2 =========");
                    System.out.println("Must be pay for wind energy (storage) " +
                            windContract2.viewPaymentStorageForWind().send().component2());
                    System.out.println("fact payment " + windContract2.viewPaymentStorageForWind().send().component4());
                    System.out.println("penny " + windContract2.viewPaymentStorageForWind().send().component6());
                    System.out.println("wind balance " + windContract2.viewPaymentStorageForWind().send().component8());
                }
            }else {
                if (windContract3!=null){
                    if(windContract3.updatePaymentWindEnergyForStorage().send().isStatusOK()){
                        System.out.println("========== Update payment for Wind3 energy from "
                                + msg.getSender().getLocalName() + " ==============");
                        System.out.println(storageCounter.getLocalName() + " update payment for " +
                                "wind3 energy ");
                    }

                    // После обновления оплаты за энергию StorageCounter3 проверяет
                    // выполнение оплаты за покупку энергии у Wind3
                    if (windContract3.controlPaymentStorageForWind().send().isStatusOK()){
                        System.out.println(storageCounter.getLocalName() + " control payment for " +
                                " energy buy from Wind3 ");
                    }

                    // Просмотр выполнения оплаты накопителем за покупку энергии у Wind3
                    System.out.println("==========Total storage payment for Wind3 =========");
                    System.out.println("Must be pay for wind energy (storage) " +
                            windContract3.viewPaymentStorageForWind().send().component2());
                    System.out.println("fact payment " + windContract3.viewPaymentStorageForWind().send().component4());
                    System.out.println("penny " + windContract3.viewPaymentStorageForWind().send().component6());
                    System.out.println("wind balance " + windContract3.viewPaymentStorageForWind().send().component8());
                }
            }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            block();
        }
    }

    // TODO: Счетчик времени
    private void receiveTimeFromMatlab(){
        counterTime = mqttSubscriber.mqttSubscriber("Time","Storage");
    }

    // TODO: Метод принятия времени от Дистрибьютора
    private void receiveTimeFromDist(Agent storageCounter){
        // Пока просто сделаем счетчик времени (потом закомментить)
//        counterTime = mqttSubscriber.mqttSubscriber("Time","WindCounter");
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("TimeFromMatlab");
        ACLMessage msg = storageCounter.receive(messageTemplate);

        if (msg!=null){
            counterTime = Double.parseDouble(msg.getContent());
            System.out.println(ANSI_GREEN + storageCounter.getLocalName() +
                    " receive " + counterTime +
                    " from " + counterTime + ANSI_RESET);
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
        System.out.println(ANSI_GREEN + sender.getLocalName() + " send " + msg.getContent() +
                " to " + receiver + ANSI_RESET);
        System.out.println();
    }


}
