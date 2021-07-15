package ConsumerCounterClasses;

import ConnectionUDP.UDPClass;
import SmartContracts.PV.PVContract;
import SmartContracts.PV.PVPenaltyContract;
import SmartContracts.Service.ServiceContract;
import SmartContracts.Wind.WindContract;
import SmartContracts.Wind.WindPenaltyContract;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

import java.net.DatagramSocket;

public class ConsumerCounterInteractionWithContracts extends TickerBehaviour {

    private DataStore dataStore;
    private Web3j web3j;
    private ContractGasProvider contractGasProvider;

    private String PUBLIC_KEY_ConsumerCounter1;
    private String PUBLIC_KEY_ConsumerCounter2;
    private String PUBLIC_KEY_ConsumerCounter3;

    private Credentials credentialsConsumerCounter1;
    private Credentials credentialsConsumerCounter2;
    private Credentials credentialsConsumerCounter3;

    // Экземпляры контрактов
    //For ConsumerCounter1
    private WindContract windContract1;
    private WindPenaltyContract windPenaltyContract1;

    private PVContract pvContract1;
    private PVPenaltyContract pvPenaltyContract1;

    private ServiceContract serviceContract1;

    // For ConsumerCounter2
    private WindContract windContract2;
    private WindPenaltyContract windPenaltyContract2;

    private PVContract pvContract2;
    private PVPenaltyContract pvPenaltyContract2;

    private ServiceContract serviceContract2;

    // For ConsumerCounter3
    private WindContract windContract3;
    private WindPenaltyContract windPenaltyContract3;

    private PVContract pvContract3;
    private PVPenaltyContract pvPenaltyContract3;

    private ServiceContract serviceContract3;

    private UDPClass udpClass;

    // Порты для принятия сигналов из Матлаба
    private int con1port = 10112;
    private int con2port = 10114;
    private int con3port = 10116;

    private boolean isRegisteredWind1;
    private boolean isRegisteredWind2;
    private boolean isRegisteredWind3;

    private boolean isRegisteredPV1;
    private boolean isRegisteredPV2;
    private boolean isRegisteredPV3;

    // Флаги установки нового соединения
    private boolean flagConnectionWind1;
    private boolean flagConnectionWind2;
    private boolean flagConnectionWind3;

    private boolean flagConnectionPV1;
    private boolean flagConnectionPV2;
    private boolean flagConnectionPV3;


    private DatagramSocket datagramSocketPV1;
    private DatagramSocket datagramSocketPV2;
    private DatagramSocket datagramSocketPV3;


    public ConsumerCounterInteractionWithContracts(Agent a, long period,
                                                   DataStore dataStore, Web3j web3j, ContractGasProvider contractGasProvider,
                                                   String PUBLIC_KEY_ConsumerCounter1,
                                                   String PUBLIC_KEY_ConsumerCounter2, String PUBLIC_KEY_ConsumerCounter3,
                                                   Credentials credentialsConsumerCounter1, Credentials credentialsConsumerCounter2,
                                                   Credentials credentialsConsumerCounter3,
                                                   WindContract windContract1, WindContract windContract2, WindContract windContract3,
                                                   WindPenaltyContract windPenaltyContract1, WindPenaltyContract windPenaltyContract2,
                                                   WindPenaltyContract windPenaltyContract3, PVContract pvContract1, PVContract pvContract2,
                                                   PVContract pvContract3, PVPenaltyContract pvPenaltyContract1,
                                                   PVPenaltyContract pvPenaltyContract2, PVPenaltyContract pvPenaltyContract3,
                                                   UDPClass udpClass, boolean isRegisteredWind1, boolean isRegisteredWind2,
                                                   boolean isRegisteredWind3, boolean isRegisteredPV1,
                                                   boolean isRegisteredPV2, boolean isRegisteredPV3,
                                                   boolean flagConnectionWind1, boolean flagConnectionWind2, boolean flagConnectionWind3,
                                                   boolean flagConnectionPV1, boolean flagConnectionPV2, boolean flagConnectionPV3) {
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

        this.windContract1 = windContract1;
        this.windContract2 = windContract2;
        this.windContract3 = windContract3;

        this.windPenaltyContract1 = windPenaltyContract1;
        this.windPenaltyContract2 = windPenaltyContract2;
        this.windPenaltyContract3 = windPenaltyContract3;

        this.pvContract1 = pvContract1;
        this.pvContract2 = pvContract2;
        this.pvContract3 = pvContract3;

        this.pvPenaltyContract1 = pvPenaltyContract1;
        this.pvPenaltyContract2 = pvPenaltyContract2;
        this.pvPenaltyContract3 = pvPenaltyContract3;

        this.udpClass = udpClass;

        this.isRegisteredWind1 = isRegisteredWind1;
        this.isRegisteredWind2 = isRegisteredWind2;
        this.isRegisteredWind3 = isRegisteredWind3;

        this.isRegisteredPV1 = isRegisteredPV1;
        this.isRegisteredPV2 = isRegisteredPV2;
        this.isRegisteredPV3 = isRegisteredPV3;

        this.flagConnectionWind1 = flagConnectionWind1;
        this.flagConnectionWind2 = flagConnectionWind2;
        this.flagConnectionWind3 = flagConnectionWind3;

        this.flagConnectionPV1 = flagConnectionPV1;
        this.flagConnectionPV2 = flagConnectionPV2;
        this.flagConnectionPV3 = flagConnectionPV3;

    }

    @Override
    protected void onTick() {

        receiveDeployedAddresses(myAgent, "DeployedAddress", "PenaltyAddress");
        receivedGeneratorName(myAgent, "isConcludedConsumer");
        receiveDeployedServiceAddress(myAgent, "ServiceAddress");

        // Создание экземпляров контрактов
        if (myAgent.getLocalName().equals("ConsumerCounter1")) {
            createContractSample(myAgent, PUBLIC_KEY_ConsumerCounter1,"ConsumerCounter1", credentialsConsumerCounter1);
//            updateConsumptionPVEnergy(myAgent, PUBLIC_KEY_ConsumerCounter1, datagramSocketPV1, pvContract1,
//                    con1port, flagConnectionPV1);
        } else if (myAgent.getLocalName().equals("ConsumerCounter2")){
            createContractSample(myAgent, PUBLIC_KEY_ConsumerCounter2, "ConsumerCounter2", credentialsConsumerCounter2);
        } else {
            createContractSample(myAgent, PUBLIC_KEY_ConsumerCounter3, "ConsumerCounter3", credentialsConsumerCounter3);
        }


    }

    // получение адресов основного и штрафного контрактов от потребителя,
    // создание экземпляров коонтрактов, регистрация в этих контрактах,
    // добавление адресов этих контрактов в datastore

    private void receiveDeployedAddresses(Agent receiver, String protocol, String penaltyProtocol){

        // Получение адресов основных контрактов
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(protocol);
        ACLMessage msg = receiver.receive(messageTemplate);

        if (msg!=null){

            System.out.println(receiver.getLocalName() + " receive contract addrress: " +
                    msg.getContent() + " from " + msg.getSender().getLocalName());

            // Добавление адреса основного контракта в datastore
            getDataStore().put(receiver.getLocalName() + "address", msg.getContent());
//            System.out.println("getDatatatatataStorererrer in con ccounter class " + getDataStore());
        }else {
            block();
        }

        // Получение адресов штрафных контрактов
        MessageTemplate messageTemplate1 = MessageTemplate.MatchProtocol(penaltyProtocol);
        ACLMessage msg2 = receiver.receive(messageTemplate1);

        if (msg2!=null){

            System.out.println(receiver.getLocalName() + " receive penalty contract address: " +
                    msg2.getContent() + " from " + msg2.getSender().getLocalName());

            // Добавление адреса штрафного контракта в datastore
            getDataStore().put(receiver.getLocalName() + "penaltyAddress", msg2.getContent());
//            System.out.println("getDatatatatataStorererrer in con ccounter class " + getDataStore());
        }else {
            block();
        }

    }

    private void receiveDeployedServiceAddress(Agent receiver, String protocol){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(protocol);
        ACLMessage msg = receiver.receive(messageTemplate);

        if (msg!=null){
            System.out.println(receiver.getLocalName() + " receive service contract address: " +
                    msg.getContent() + " from " + msg.getSender().getLocalName());

            getDataStore().put(receiver.getLocalName() + "serviceContract",msg.getContent());
//            System.out.println("getDatatatatataStorererrer in con ccounter class " + getDataStore());
        }else {
            block();
        }
    }

    // Метод принятия имени ггенератора, с которым потребитель заключил контракт
    private void receivedGeneratorName(Agent receiver, String protocol){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(protocol);
        ACLMessage msg = receiver.receive(messageTemplate);

        if (msg!=null){
            System.out.println(receiver.getLocalName() + " receive generator name: " +
                    msg.getContent() + " from " + msg.getSender().getLocalName());

            getDataStore().put(receiver.getLocalName() + "isConcludedConsumer", msg.getContent());
        }else {
            block();
        }
    }

    // Создание экземпляров контрактов и регистрация в контрактах
    private void createContractSample(Agent consumerCounter, String publicKey, String consumerCounterName, Credentials credentials){
        // Создание экземпляров контрактов для ConsumeCounter
        if (consumerCounter.getLocalName().equals(consumerCounterName)){
            if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer") != null){
                // Проверка, если адрес контракта != null
                if (getDataStore().get(consumerCounter.getLocalName() + "address")!=null &&
                getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress")!=null) {

                    // Проверка, если имя генератора == "Wind1"
                    try {
                    if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("Wind1") && isRegisteredWind1) {

                        windContract1 = WindContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "address")),
                                web3j, credentials, contractGasProvider);

                        // Регистрация в wind контракте
                        registerInWindContract(windContract1, publicKey, consumerCounter);

                        windPenaltyContract1 = WindPenaltyContract.load(String.valueOf(
                                getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress")), web3j,
                                credentials, contractGasProvider);

                        // Регистрация в wind penalty контракте
                       registerInWindPenaltyContract(windPenaltyContract1, publicKey, consumerCounter);

                       getDataStore().put(consumerCounter.getLocalName() + "register", "Wind1");

                       isRegisteredWind1 = false;

                    }

                    // Проверка, если имя генератора == "PV1"
                    if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("PV1") && isRegisteredPV1) {

                        pvContract1 = PVContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "address")),
                                web3j, credentials, contractGasProvider);

                        // Регистрация в PV1 контракте
                        registerInPvContract(pvContract1, publicKey, consumerCounter);

                        pvPenaltyContract1 = PVPenaltyContract.load(String.valueOf(
                                getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress")), web3j,
                                credentials, contractGasProvider);

                        // регистрация в PV1 penalty контракте
                        registerInPVPenaltyContract(pvPenaltyContract1, publicKey, consumerCounter);

                        getDataStore().put(consumerCounter.getLocalName() + "register", "PV1");

                        isRegisteredPV1 = false;
                    }


                    // Проверка, если имя генератора == "Wind2"
                    if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("Wind2") && isRegisteredWind2) {

                        windContract2 = WindContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "address")),
                                web3j, credentials, contractGasProvider);

                        // Регистрация в Wind2 контракте
                        registerInWindContract(windContract2, publicKey, consumerCounter);

                        windPenaltyContract2 = WindPenaltyContract.load(String.valueOf(
                                getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress")), web3j,
                                credentials, contractGasProvider);

                        registerInWindPenaltyContract(windPenaltyContract2, publicKey, consumerCounter);

                        getDataStore().put(consumerCounter.getLocalName() + "register", "Wind2");

                        isRegisteredWind2 = false;
                    }

                    // Проверка, если имя генератора == "PV2"
                    if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("PV2") && isRegisteredPV2) {

                        pvContract2 = PVContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "address")),
                                web3j, credentials, contractGasProvider);

                        // регистрация в PV2 контракте
                        registerInPvContract(pvContract2, publicKey, consumerCounter);

                        pvPenaltyContract2 = PVPenaltyContract.load(String.valueOf(
                                getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress")), web3j,
                                credentials, contractGasProvider);

                        // регистрация в PVPenaltyContract2 контракте
                        registerInPVPenaltyContract(pvPenaltyContract2, publicKey, consumerCounter);

                        getDataStore().put(consumerCounter.getLocalName() + "register", "PV2");

                        isRegisteredPV2 = false;
                    }

                    // Проверка, если имя генератора == "Wind3"
                    if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("Wind3") && isRegisteredWind3){

                        windContract3 = WindContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "address")),
                                web3j, credentials, contractGasProvider);

                        // регистрация в Wind 3 контракте
                        registerInWindContract(windContract3,publicKey, consumerCounter);

                        windPenaltyContract3 = WindPenaltyContract.load(String.valueOf(
                                getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress")), web3j,
                                credentials, contractGasProvider);

                        // регистрация в WindPenalty 3 контракте
                        registerInWindPenaltyContract(windPenaltyContract3, publicKey, consumerCounter);

                        getDataStore().put(consumerCounter.getLocalName() + "register", "Wind3");

                        isRegisteredWind3 = false;
                    }

                    // Проверка, если имя генератора == "PV3"
                    if (getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer").equals("PV3") && isRegisteredPV3){

                        pvContract3 = PVContract.load(String.valueOf(getDataStore().get(consumerCounter.getLocalName() + "address")),
                                web3j, credentials, contractGasProvider);

                        // регистрация в PV3 контракте
                        registerInPvContract(pvContract3, publicKey, consumerCounter);

                        pvPenaltyContract3 = PVPenaltyContract.load(String.valueOf(
                                getDataStore().get(consumerCounter.getLocalName() + "penaltyAddress")), web3j,
                                credentials, contractGasProvider);

                        // регистрация в PV3 penalty контракте
                        registerInPVPenaltyContract(pvPenaltyContract3, publicKey, consumerCounter);

                        getDataStore().put(consumerCounter.getLocalName() + "register", "PV3");

                        isRegisteredPV3 = false;
                    }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // TODO: Методы для контрактов PV
    // методы регистрации в контрактах
    // Pv Contract
    private void registerInPvContract(PVContract pvContract, String publicKey, Agent consumerCounter){
        try {
            if (pvContract.registrationConsumerCounter(publicKey).send().isStatusOK()){
                System.out.println(consumerCounter.getLocalName() + " : " + publicKey +
                        " register in pv contract: " + pvContract.getContractAddress());
            }
        } catch (Exception e) {
            System.out.println();
        }
    }

    // PV penalty contract
    private void registerInPVPenaltyContract(PVPenaltyContract pvPenaltyContract, String publicKey, Agent consumerCounter){
        try {
            if (pvPenaltyContract.registrationConsumerCounter(publicKey).send().isStatusOK()){
                System.out.println(consumerCounter.getLocalName() + " : " + publicKey +
                        " register in pv penalty contract: " + pvPenaltyContract.getContractAddress());
            }
        } catch (Exception e) {
            System.out.println();;
        }
    }

    // WindContract
    private void registerInWindContract(WindContract windContract, String publicKey, Agent consumerCounter){
        try {
            if (windContract.registrationConsumerCounter(publicKey).send().isStatusOK()){
                System.out.println(consumerCounter.getLocalName() + " : " + publicKey +
                        " register in wind contract: " + windContract.getContractAddress());
            }
        } catch (Exception e) {
            System.out.println();;
        }
    }

    // WindPenaltyContract
    private void registerInWindPenaltyContract(WindPenaltyContract windPenaltyContract, String publicKey, Agent consumerCounter){
        try {
            if (windPenaltyContract.registrationConsumerCounter(publicKey).send().isStatusOK()){
                System.out.println(consumerCounter.getLocalName() + " : " + publicKey +
                        " register in wind penalty contract: " + windPenaltyContract.getContractAddress());
            }
        } catch (Exception e) {
            System.out.println();;
        }
    }



}
