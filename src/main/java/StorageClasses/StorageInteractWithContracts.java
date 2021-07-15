package StorageClasses;

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
import java.util.List;

public class StorageInteractWithContracts extends TickerBehaviour {

    // Соединение с Ganache
    private Web3j web3j;
    private ContractGasProvider contractGasProvider;

    // Открытые ключи накопителей
    private String PUBLIC_KEY_Storage1;
    private String PUBLIC_KEY_Storage2;
    private String PUBLIC_KEY_Storage3;

    private Credentials credentialsStorage1;
    private Credentials credentialsStorage2;
    private Credentials credentialsStorage3;

    private DataStore dataStore;

    // Флаги регистрации в основных PV контрактах
    private boolean flagRegisterPV1;
    private boolean flagRegisterPV2;
    private boolean flagRegisterPV3;

    // Флаги регистрации в штрафных PV контрактах
    private boolean flagRegisterFinePV1;
    private boolean flagRegisterFinePV2;
    private boolean flagRegisterFinePV3;

    // Флаги регистрации в основных Wind контрактах
    private boolean flagRegisterWind1;
    private boolean flagRegisterWind2;
    private boolean flagRegisterWind3;

    // Флаги регистрации в штрафных Wind контрактах
    private boolean flagRegisterFineWind1;
    private boolean flagRegisterFineWind2;
    private boolean flagRegisterFineWind3;

    // Флаги регистрации в сервисных коонтрактах
    private boolean flagRegisterService1;
    private boolean flagRegisterService2;
    private boolean flagRegisterService3;

    // Экзмпляры PV контрактов
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

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    // Цена за энергию (1 кВт) для накопителя
    private int priceForStorage = 50000;

    // Массив для того, чтобы проверять, что
    // накопитель купил энергию у одного из своих генераторов
    // и если купил, то пока не получит всю энергию,
    // не может купить у другого
    private List<String> generatorsName;

    public StorageInteractWithContracts(Agent a, long period, Web3j web3j, ContractGasProvider contractGasProvider,
                                        String PUBLIC_KEY_Storage1, String PUBLIC_KEY_Storage2, String PUBLIC_KEY_Storage3,
                                        Credentials credentialsStorage1, Credentials credentialsStorage2,
                                        Credentials credentialsStorage3, DataStore dataStore,
                                        boolean flagRegister1, boolean flagRegister2, boolean flagRegister3,
                                        boolean flagRegisterFine1, boolean flagRegisterFine2, boolean flagRegisterFine3,
                                        boolean flagRegisterService1, boolean flagRegisterService2, boolean flagRegisterService3,
                                        List<String> generatorsName, boolean flagRegisterWind1, boolean flagRegisterWind2,
                                        boolean flagRegisterWind3, boolean flagRegisterFineWind1, boolean flagRegisterFineWind2,
                                        boolean flagRegisterFineWind3) {
        super(a, period);

        this.web3j = web3j;
        this.contractGasProvider = contractGasProvider;

        this.PUBLIC_KEY_Storage1 = PUBLIC_KEY_Storage1;
        this.PUBLIC_KEY_Storage2 = PUBLIC_KEY_Storage2;
        this.PUBLIC_KEY_Storage3 = PUBLIC_KEY_Storage3;

        this.credentialsStorage1 = credentialsStorage1;
        this.credentialsStorage2 = credentialsStorage2;
        this.credentialsStorage3 = credentialsStorage3;

        this.dataStore = dataStore;

        this.flagRegisterPV1 = flagRegister1;
        this.flagRegisterPV2 = flagRegister2;
        this.flagRegisterPV3 = flagRegister3;

        this.flagRegisterFinePV1 = flagRegisterFine1;
        this.flagRegisterFinePV2 = flagRegisterFine2;
        this.flagRegisterFinePV3 = flagRegisterFine3;

        this.flagRegisterService1 = flagRegisterService1;
        this.flagRegisterService2 = flagRegisterService2;
        this.flagRegisterService3 = flagRegisterService3;

        this.generatorsName = generatorsName;

        this.flagRegisterWind1 = flagRegisterWind1;
        this.flagRegisterWind2 = flagRegisterWind2;
        this.flagRegisterWind3 = flagRegisterWind3;

        this.flagRegisterFineWind1 = flagRegisterFineWind1;
        this.flagRegisterFineWind2 = flagRegisterFineWind2;
        this.flagRegisterFineWind3 = flagRegisterFineWind3;

    }

    @Override
    protected void onTick() {

        if (myAgent.getLocalName().equals("Storage1")){

            // Регистрация в PV контрактах
            registerInPVContracts(myAgent, PUBLIC_KEY_Storage1, credentialsStorage1,
                    "PVDeployedContractAddress","PVPenaltyDeployedContractAddress");

            // Регситрация в Wind конттрактах
            registerInWindContracts(myAgent, PUBLIC_KEY_Storage1, credentialsStorage1,
                    "WindDeployedContractAddress","WindPenaltyDeployedContractAddress");

            // Регистрация в сервисных контрактах
            registerInServiceContracts(myAgent, "ServiceAddress", PUBLIC_KEY_Storage1, credentialsStorage1);

            // Получение сообщения от счетчика накопителя с энергией и именем PV,
            // оплата за энергию накопителем, ответное письмо счетчику накопителя об оплате
            receivePVEnergyFromCounter(myAgent, "StorageCounter1");

            //Принятие сообщения от счетчика накопителя с Wind energy и оплата за энергию
            receiveWindEnergyAndPayment(myAgent, "Wind1");

            // Получение сообщения от счетчика и покупка энергии у генератора
            //receiveMsgAboutEnergy(myAgent, "EnergyBuy","StorageCounter1");

        } else if (myAgent.getLocalName().equals("Storage2")){

            // Регистрация в PV контрактах
            registerInPVContracts(myAgent, PUBLIC_KEY_Storage2, credentialsStorage2,
                    "PVDeployedContractAddress","PVPenaltyDeployedContractAddress");

            // Регситрация в Wind конттрактах
            registerInWindContracts(myAgent, PUBLIC_KEY_Storage2, credentialsStorage2,
                    "WindDeployedContractAddress","WindPenaltyDeployedContractAddress");

            // Регистрация в сервисных контрактах
            registerInServiceContracts(myAgent, "ServiceAddress", PUBLIC_KEY_Storage2, credentialsStorage2);

            // Получение сообщения от счетчика накопителя с энергией и именем PV,
            // оплата за энергию накопителем, ответное письмо счетчику накопителя об оплате
            receivePVEnergyFromCounter(myAgent, "StorageCounter2");

            //Принятие сообщения от счетчика накопителя с Wind energy и оплата за энергию
            receiveWindEnergyAndPayment(myAgent, "Wind2");

            // Получение сообщения от счетчика и покупка энергии у генератора
            //receiveMsgAboutEnergy(myAgent, "EnergyBuy","StorageCounter2");

        } else {


            // Регистрация в PV контрактах
            registerInPVContracts(myAgent, PUBLIC_KEY_Storage3, credentialsStorage3,
                    "PVDeployedContractAddress","PVPenaltyDeployedContractAddress");

            // Регситрация в Wind конттрактах
            registerInWindContracts(myAgent, PUBLIC_KEY_Storage3, credentialsStorage3,
                    "WindDeployedContractAddress","WindPenaltyDeployedContractAddress");

            // Регистрация в сервисных контрактах
            registerInServiceContracts(myAgent, "ServiceAddress", PUBLIC_KEY_Storage3, credentialsStorage3);

            // Получение сообщения от счетчика накопителя с энергией и именем PV,
            // оплата за энергию накопителем, ответное письмо счетчику накопителя об оплате
            receivePVEnergyFromCounter(myAgent, "StorageCounter3");

            //Принятие сообщения от счетчика накопителя с Wind energy и оплата за энергию
            receiveWindEnergyAndPayment(myAgent, "Wind3");

            // Получение сообщения от счетчика и покупка энергии у генератора
            //receiveMsgAboutEnergy(myAgent, "EnergyBuy","StorageCounter3");

        }

    }


    // TODO: Накопиители получают адреса контрактов от PV, создают экземпляры контрактов
    //  и регистрируются в них
    private void registerInPVContracts(Agent storage, String publicKey, Credentials credentials,
                                     String mainProtocol, String penaltyProtocol){

        // Накопители получают адреса контрактов от PV
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(mainProtocol);
        ACLMessage msg = storage.receive(messageTemplate);
        try {
        if (msg!=null){

            String deployedAddress = msg.getContent();

            System.out.println(storage.getLocalName() + " receive deployed contract address " +
                    deployedAddress + " from " + msg.getSender().getLocalName());

            // Складываем в datastore каждого накопителя
            // адрес разверрнутого контракта PV
            getDataStore().put(storage.getLocalName() + "deployedAddressPV",deployedAddress);


                // Если Storage1
            if (storage.getLocalName().equals("Storage1") && flagRegisterPV1){

                // Storage1 создает экземпляр контракта
                pvContract1 = PVContract.load(deployedAddress, web3j, credentialsStorage1, contractGasProvider);

                System.out.println(storage.getLocalName() + " with publicKey " + publicKey
                        + " create pvContract1 sample " +
                        pvContract1.getContractAddress());

                // Storage1 регистрируется в контракте pvContract1


                    if (pvContract1.registrationStorage(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storage.getLocalName() + " with public key " +
                                publicKey + " register in pvContract1 " + pvContract1.getContractAddress() + ANSI_RESET);
                    }

                    // Накопитель1 отправляет адрес своему счетчику
                sendMsg(storage, "DeployedPVAddressFromStorage", "StorageCounter1",deployedAddress);

                    flagRegisterPV1 = false;
            }


            // Если Storage2
                if (storage.getLocalName().equals("Storage2") && flagRegisterPV2){

                    // Storage2 создает экземпляр контракта
                    pvContract2 = PVContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(storage.getLocalName() + " with public key " + publicKey +
                            " create pvContract2 sample " + pvContract2.getContractAddress());

                    // Storage2 регистрируется в контракте
                    if (pvContract2.registrationStorage(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storage.getLocalName() + " with public key " +
                                publicKey + " register in pvContract2 " +
                                pvContract2.getContractAddress() + ANSI_RESET);
                    }

                    // накопитель 2 отправляет адрес своему счетчику
                    sendMsg(storage, "DeployedPVAddressFromStorage", "StorageCounter2",deployedAddress);

                    flagRegisterPV2 = false;
                }

                // Если Storage3
                if (storage.getLocalName().equals("Storage3") && flagRegisterPV3){

                    // Storage3 созлает экземпляр контракта
                    pvContract3 = PVContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(storage.getLocalName() + " with public key " + publicKey +
                            " create pvContract3 sample " + pvContract3.getContractAddress());

                    // Storage 3 регистрируется в контракте
                    if (pvContract3.registrationStorage(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storage.getLocalName() + " with public key " + publicKey +
                                " register in pvContract3 " + pvContract3.getContractAddress() + ANSI_RESET);
                    }

                    // накопитель 3 отправляет адрес своему счетчику
                    sendMsg(storage, "DeployedPVAddressFromStorage", "StorageCounter3",deployedAddress);

                    flagRegisterPV3 = false;
                }


        }else {
            block();
        }


        // Накопители получают адреса штрафных контрактов от PV
        MessageTemplate messageTemplate1 = MessageTemplate.MatchProtocol(penaltyProtocol);
        ACLMessage msg1 = storage.receive(messageTemplate1);

        if (msg1!=null){

            String penaltyAddress = msg1.getContent();

            System.out.println(storage.getLocalName() + " with publicKey " + publicKey +
                    " receive penaltyAddress " + penaltyAddress + " from " +
                    msg1.getSender().getLocalName());

            // Если Storage1
            if (storage.getLocalName().equals("Storage1") && flagRegisterFinePV1){

                // Storaage1 создает экземпляр контракта
                pvPenaltyContract1 = PVPenaltyContract.load(penaltyAddress, web3j,
                        credentials, contractGasProvider);

                System.out.println(storage.getLocalName() + " with publicKey " + publicKey +
                        " create pvPenaltyContract1 contract sample " + pvPenaltyContract1.getContractAddress());

                // Storage1 регистрируется в pvPenaltyContract1
                if (pvPenaltyContract1.registrationStorage(publicKey).send().isStatusOK()){
                    System.out.println(ANSI_GREEN + storage.getLocalName() + " with publicKey " + publicKey +
                            " register in pvPenaltyContract1 " + pvPenaltyContract1.getContractAddress()+ ANSI_RESET);
                }

                // Storage1 отправляет адрес своему счетчику
                sendMsg(storage, "PVPenaltyAddressFromStorage", "StorageCounter1", penaltyAddress);

                flagRegisterFinePV1 = false;

            }

            // Если Storage2
            if (storage.getLocalName().equals("Storage2") && flagRegisterFinePV2){

                // Storage2 создает экземпляр контракта
                pvPenaltyContract2 = PVPenaltyContract.load(penaltyAddress, web3j,
                        credentials, contractGasProvider);

                System.out.println(storage.getLocalName() + " with public key " + publicKey +
                        " create pvPenaltyContract2 sample " + pvPenaltyContract2.getContractAddress());

                // Storage2 регистрируется в pvPenaltyContract2 контракте
                if (pvPenaltyContract2.registrationStorage(publicKey).send().isStatusOK()){
                    System.out.println(ANSI_GREEN + storage.getLocalName() + " with public key " + publicKey +
                            " register in pvPenaltyContract2 " + pvPenaltyContract2.getContractAddress() + ANSI_RESET);
                }

                // Storage2 отправляет адрес своему счетчику
                sendMsg(storage, "PVPenaltyAddressFromStorage", "StorageCounter2", penaltyAddress);

                flagRegisterFinePV2 = false;
            }


            // Если Storage3
            if (storage.getLocalName().equals("Storage3") && flagRegisterFinePV3){

                // Storage3 создает экземпляр контракта
                pvPenaltyContract3 = PVPenaltyContract.load(penaltyAddress, web3j,
                        credentials, contractGasProvider);

                System.out.println(storage.getLocalName() + " with public key " + publicKey +
                        " create pvPenaltyContract3 sample " + pvPenaltyContract3.getContractAddress());

                // Storage3 регистрируется в pvPenaltyContract3 контракте
                if (pvPenaltyContract3.registrationStorage(publicKey).send().isStatusOK()){
                    System.out.println(ANSI_GREEN + storage.getLocalName() + " with public key " + publicKey +
                            " register in pvPenaltyContract3 " + pvPenaltyContract3.getContractAddress() + ANSI_RESET);
                }

                // Storage3 отправляет адрес своему счетчику
                sendMsg(storage, "PVPenaltyAddressFromStorage", "StorageCounter3", penaltyAddress);

                flagRegisterFinePV3 = false;
            }
        }else {
            block();
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // TODO: Накопители получают адреса контрактов от Wind, создают экземпляры контрактов
    //  и регистрируются в них
    private void registerInWindContracts(Agent storage, String publicKey, Credentials credentials,
                                       String mainProtocol, String penaltyProtocol){

        // Накопители получают адреса контрактов от Wind
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(mainProtocol);
        ACLMessage msg = storage.receive(messageTemplate);
        try {
            if (msg!=null){

                String deployedAddress = msg.getContent();

                System.out.println(storage.getLocalName() + " receive deployed contract address " +
                        deployedAddress + " from " + msg.getSender().getLocalName());


                // Если Storage1
                if (storage.getLocalName().equals("Storage1") && flagRegisterWind1){

                    // Storage1 создает экземпляр контракта
                    windContract1 = WindContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(storage.getLocalName() + " with publicKey " + publicKey
                            + " create windContract1 sample " +
                            windContract1.getContractAddress());

                    // Storage1 регистрируется в контракте windContract1
                    if (windContract1.registrationStorage(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storage.getLocalName() + " with public key " +
                                publicKey + " register in windContract1 " + windContract1.getContractAddress() + ANSI_RESET);
                    }

                    // Storage1 отправляет адрес своему счетчику
                    sendMsg(storage, "DeployedWindAddressFromStorage", "StorageCounter1", deployedAddress);

                    flagRegisterWind1 = false;

                }


                // Если Storage2
                if (storage.getLocalName().equals("Storage2") && flagRegisterWind2){

                    // Storage2 создает экземпляр контракта
                    windContract2 = WindContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(storage.getLocalName() + " with public key " + publicKey +
                            " create windContract2 sample " + windContract2.getContractAddress());

                    // Storage2 регистрируется в контракте
                    if (windContract2.registrationStorage(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storage.getLocalName() + " with public key " +
                                publicKey + " register in windContract2 " +
                                windContract2.getContractAddress() + ANSI_RESET);
                    }

                    // Storage2 отправляет адрес своему счетчику
                    sendMsg(storage, "DeployedWindAddressFromStorage", "StorageCounter2", deployedAddress);

                    flagRegisterWind2 = false;
                }

                // Если Storage3
                if (storage.getLocalName().equals("Storage3") && flagRegisterWind3){

                    // Storage3 созлает экземпляр контракта
                    windContract3 = WindContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(storage.getLocalName() + " with public key " + publicKey +
                            " create windContract3 sample " + windContract3.getContractAddress());

                    // Storage 3 регистрируется в контракте
                    if (windContract3.registrationStorage(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storage.getLocalName() + " with public key " + publicKey +
                                " register in windContract3 " + windContract3.getContractAddress() + ANSI_RESET);
                    }

                    // Storage3 отправляет адрес своему счетчику
                    sendMsg(storage, "DeployedWindAddressFromStorage", "StorageCounter3", deployedAddress);

                    flagRegisterWind3 = false;
                }


            }else {
                block();
            }


            // Накопители получают адреса штрафных контрактов от Wind
            MessageTemplate messageTemplate1 = MessageTemplate.MatchProtocol(penaltyProtocol);
            ACLMessage msg1 = storage.receive(messageTemplate1);

            if (msg1!=null){

                String penaltyAddress = msg1.getContent();

                System.out.println(storage.getLocalName() + " with publicKey " + publicKey +
                        " receive penaltyAddress " + penaltyAddress + " from " +
                        msg1.getSender().getLocalName());

                // Если Storage1
                if (storage.getLocalName().equals("Storage1") && flagRegisterFineWind1){

                    // Storaage1 создает экземпляр контракта
                    windPenaltyContract1 = WindPenaltyContract.load(penaltyAddress, web3j,
                            credentials, contractGasProvider);

                    System.out.println(storage.getLocalName() + " with publicKey " + publicKey +
                            " create windPenaltyContract1 contract sample " + windPenaltyContract1.getContractAddress());

                    // Storage1 регистрируется в windPenaltyContract1
                    if (windPenaltyContract1.registrationStorage(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storage.getLocalName() + " with publicKey " + publicKey +
                                " register in windPenaltyContract1 " + windPenaltyContract1.getContractAddress() + ANSI_RESET);
                    }

                    sendMsg(storage, "WindPenaltyAddressFromStorage","StorageCounter1", penaltyAddress);

                    flagRegisterFineWind1 = false;
                }

                // Если Storage2
                if (storage.getLocalName().equals("Storage2") && flagRegisterFineWind2){

                    // Storage2 создает экземпляр контракта
                    windPenaltyContract2 = WindPenaltyContract.load(penaltyAddress, web3j,
                            credentials, contractGasProvider);

                    System.out.println(storage.getLocalName() + " with public key " + publicKey +
                            " create windPenaltyContract2 sample " + windPenaltyContract2.getContractAddress());

                    // Storage2 регистрируется в pvPenaltyContract2 контракте
                    if (windPenaltyContract2.registrationStorage(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storage.getLocalName() + " with public key " + publicKey +
                                " register in windPenaltyContract2 " + windPenaltyContract2.getContractAddress() + ANSI_RESET);
                    }

                    sendMsg(storage, "WindPenaltyAddressFromStorage","StorageCounter2", penaltyAddress);
                    flagRegisterFineWind2 = false;
                }


                // Если Storage3
                if (storage.getLocalName().equals("Storage3") && flagRegisterFineWind3){

                    // Storage3 создает экземпляр контракта
                    windPenaltyContract3 = WindPenaltyContract.load(penaltyAddress, web3j,
                            credentials, contractGasProvider);

                    System.out.println(storage.getLocalName() + " with public key " + publicKey +
                            " create windPenaltyContract3 sample " + windPenaltyContract3.getContractAddress());

                    // Storage3 регистрируется в windPenaltyContract3 контракте
                    if (windPenaltyContract3.registrationStorage(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storage.getLocalName() + " with public key " + publicKey +
                                " register in windPenaltyContract3 " + windPenaltyContract3.getContractAddress() + ANSI_RESET);
                    }

                    sendMsg(storage, "WindPenaltyAddressFromStorage","StorageCounter3", penaltyAddress);

                    flagRegisterFineWind3 = false;
                }
            }else {
                block();
            }

        } catch (Exception e) {
            System.out.println();
        }
    }


    // TODO: Накопители принимают адреса сервисных контрактов от потребителей
    //  , создают экземпляры контрактов и регистрируются в контрактах
    private void registerInServiceContracts(Agent storage, String receiveProtocol,
                                            String publicKey, Credentials credentials){

       // Накопители принимают адреса сервичных контрактов
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(receiveProtocol);
        ACLMessage msg = storage.receive(messageTemplate);

        if (msg!=null){

            String serviceAddress = msg.getContent();

            System.out.println(storage.getLocalName() + " with public key " + publicKey +
                    " receive deployed service address " + serviceAddress + " from " +
                    msg.getSender().getLocalName());

            // Если Storage1
            try {

            if (storage.getLocalName().equals("Storage1") && flagRegisterService1){

                // Storage1 отправляет адрес сервисного контракта своему счетчику
                sendMsg(storage, "ServiceAddressFromStorage", "StorageCounter1", serviceAddress);

                // Storage1 создает экземпляр serviceContract1
                serviceContract1 = ServiceContract.load(serviceAddress, web3j, credentials,
                        contractGasProvider);

                System.out.println(storage.getLocalName() + " with public key " + publicKey +
                        " create serviceContract1 sample " + serviceContract1.getContractAddress());

                // Storage1 регистрируется в сервисном контракте service contract1

                    if (serviceContract1.registrationStorage(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storage.getLocalName() + " with publicKey " + publicKey +
                                " register in serviceContract1 " + serviceContract1.getContractAddress() + ANSI_RESET);
                    }

                    flagRegisterService1 = false;

            }


            // Если Storage2
                if (storage.getLocalName().equals("Storage2") && flagRegisterService2){

                    // Storage2 отправляет адрес сервисного контракта своему счетчику
                    sendMsg(storage, "ServiceAddressFromStorage", "StorageCounter2", serviceAddress);

                    // Storage2 создает экземпляр serviceContract2
                    serviceContract2 = ServiceContract.load(serviceAddress, web3j, credentials,
                            contractGasProvider);

                    System.out.println(storage.getLocalName() + " with public key " + publicKey +
                            " create serviceContract2 sample " + serviceContract2.getContractAddress());

                    // Storage2 регистрируется в сервисном контракте serviceContract2
                    if (serviceContract2.registrationStorage(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storage.getLocalName() + " with public key " + publicKey +
                                " register in serviceContract2 " + serviceContract2.getContractAddress() + ANSI_RESET);
                    }

                    flagRegisterService2 = false;
                }


                // Если Storage3
                if (storage.getLocalName().equals("Storage3") && flagRegisterService3){

                    // Storage3 отправляет адрес сервисного контракта своему счетчику
                    sendMsg(storage, "ServiceAddressFromStorage", "StorageCounter3", serviceAddress);

                    // Storage3 создает экземпляр контракта serviceContract3
                    serviceContract3 = ServiceContract.load(serviceAddress, web3j, credentials, contractGasProvider);

                    System.out.println(storage.getLocalName() + " with public key " + publicKey +
                            " create serviceContract3 sample " + serviceContract3.getContractAddress());

                    // Storage3 регистрируется в контракте serviceContract3
                    if (serviceContract3.registrationStorage(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storage.getLocalName() + " with public key " + publicKey +
                                " register in serviceContract3 " + serviceContract3.getContractAddress() + ANSI_RESET);
                    }

                    flagRegisterService3 =false;
                }


            } catch (Exception e) {
                System.out.println();
            }
        }else {
            block();
        }
    }


    // TODO: Принятие энергии от счетчика накопителя и оплата за энергию накопителем
    private void receivePVEnergyFromCounter(Agent storageName, String storageCounterName){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("PVEnergyFromMatlab");
        ACLMessage msg = storageName.receive(messageTemplate);

        if (msg!=null){

            int energy = (int)Double.parseDouble(msg.getContent());
            //String pvName = msg.getContent().split(":")[1];

            // Текущая цена за полученную энергию от PV
            int payment = energy * priceForStorage;

            System.out.println(storageName.getLocalName() + " receive " + energy +
                    " from " + msg.getSender().getLocalName());

            System.out.println(storageName.getLocalName() + " need to pay " +
                    payment);

            // Если энергия от PV1
            try {

            if (storageName.getLocalName().equals("Storage1")){

                if (pvContract1!=null){

                        if (pvContract1.paymentPVEnergyForStorage(BigInteger.valueOf(energy),
                                BigInteger.valueOf(payment)).send().isStatusOK()){
                            System.out.println();
                            System.out.println("========= PAYMENT FOR PV1 ENERGY FROM STORAGE1 ==========");
                            System.out.println(storageName.getLocalName() + " paid " +
                                    payment + " to PV1 " + " for energy " + energy);
                            System.out.println();

                            // После совершения оплаты накопитель отправляет сообщение своему счетчику,
                            // что оплатил
                            sendMsg(storageName, "PaymentPVEnergyCompleted",
                                    storageCounterName, String.valueOf(payment));
                        }
                }
            } else if (storageName.getLocalName().equals("Storage2")){
                if (pvContract2!=null){
                    if (pvContract2.paymentPVEnergyForStorage(BigInteger.valueOf(energy),
                            BigInteger.valueOf(payment)).send().isStatusOK()){
                        System.out.println();
                        System.out.println("========= PAYMENT FOR PV2 ENERGY FROM STORAGE2 ==========");
                        System.out.println(storageName.getLocalName() + " paid " +
                                payment + " to PV2  for energy " + energy);
                        System.out.println();

                        // После совершения оплаты накопитель отправляет сообщение своему счетчику,
                        // что оплатил
                        sendMsg(storageName, "PaymentPVEnergyCompleted",
                                storageCounterName, String.valueOf(payment));
                    }
                }
            } else {
                if (pvContract3!=null){
                    if (pvContract3.paymentPVEnergyForStorage(BigInteger.valueOf(energy),
                            BigInteger.valueOf(payment)).send().isStatusOK()){
                        System.out.println();
                        System.out.println("========= PAYMENT FOR PV3 ENERGY FROM STORAGE3 ==========");
                        System.out.println(storageName.getLocalName() + " paid " +
                                payment + " to PV3 for energy " + energy);
                        System.out.println();

                        // После совершения оплаты накопитель отправляет сообщение своему счетчику,
                        // что оплатил
                        sendMsg(storageName, "PaymentPVEnergyCompleted",
                                storageCounterName, String.valueOf(payment));
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


    // TODO: Принятие сообщения от счетчика накопителя с Wind energy и оплата за энергию
    private void receiveWindEnergyAndPayment(Agent storage, String windName){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("ProficitWindEnergyForPayment");
        ACLMessage msg = storage.receive(messageTemplate);

        if (msg!=null){

            int windProficitEnergy = (int)Double.parseDouble(msg.getContent());
            System.out.println(ANSI_GREEN + storage.getLocalName() + " receive " + windProficitEnergy +
                    " from " + msg.getSender().getLocalName() + ANSI_RESET);

            int payment = windProficitEnergy * priceForStorage;

            System.out.println(ANSI_GREEN + storage.getLocalName() + " need to pay " +
                    payment + " for energy " + windProficitEnergy +
                    " from " + windName + ANSI_RESET);

            try {
            // Проверка какой storage
            if (storage.getLocalName().equals("Storage1")){

                if (windContract1!=null){
                    if (windContract1.paymentWindEnergyForStorage(BigInteger.valueOf(windProficitEnergy),
                            BigInteger.valueOf(payment)).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storage.getLocalName() + " paid for energy Wind1 energy " +
                        payment + " wei " + ANSI_RESET);

                        // После оплаты профицитной энергии накопитель
                        // отписывается счетчику накопителя об оплате энергии
                        sendMsg(storage,"PaymentForWindCompleted",
                                "StorageCounter1","PaymentForWindCompleted");
                    }
                }
            } else if (storage.getLocalName().equals("Storage2")){
                if (windContract2!=null){
                    if (windContract2.paymentWindEnergyForStorage(BigInteger.valueOf(windProficitEnergy),
                            BigInteger.valueOf(payment)).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storage.getLocalName() + " paid for energy Wind2 energy " +
                                payment + " wei " + ANSI_RESET);

                        // После оплаты профицитной энергии накопитель
                        // отписывается счетчику накопителя об оплате энергии
                        sendMsg(storage,"PaymentForWindCompleted",
                                "StorageCounter2","PaymentForWindCompleted");
                    }
                }
            }else {
                if (windContract3!=null){
                    if(windContract3.paymentWindEnergyForStorage(BigInteger.valueOf(windProficitEnergy),
                            BigInteger.valueOf(payment)).send().isStatusOK()){
                        System.out.println(ANSI_GREEN + storage.getLocalName() + " paid for Wind3 energy " +
                                payment + " wei " + ANSI_RESET);

                        // После оплаты профицитной энергии накопитель
                        // отписывается счетчику накопителя об оплате энергии
                        sendMsg(storage,"PaymentForWindCompleted",
                                "StorageCounter3","PaymentForWindCompleted");
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


    // TODO: Принятие сообщения от своего счетчика о необходимости выплаты комиссии
    //  и оплата комиссии
    private void receiveMsgAboutCommission(Agent storage, Credentials credentials,
                                           String storageCounterName){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("commissionToStorageCounter");
        ACLMessage msg = storage.receive(messageTemplate);

        if (msg!=null){

            System.out.println(storage.getLocalName() +
                    " receive msg " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());

            // После принятия сообщения от своего счетчика о необходимости
            // выплаты комиссии накопитель создает
            // экземпляр контракта в этом методе и оплачивает комиссиию.
            // После оплаты накопитель отправляет сообщение своему счетчику,
            // что оплатил
            // Для начала достаем из datastore адрес PV контракта
            // для каждого накопителя
            if (getDataStore().get(storage.getLocalName() + "deployedAddressPV")!=null){
                String deployedAddress =
                        String.valueOf(
                                getDataStore().get(storage.getLocalName() + "deployedAddressPV"));

                // Накопитель создает экземпляр контракта PV
                PVContract pvContract = PVContract.load(deployedAddress,
                        web3j,credentials,contractGasProvider);
                System.out.println(storage.getLocalName() +
                        " create pvContract sample " +
                        pvContract.getContractAddress() +
                        " receiveMsgAboutCommission() "
                        );
                // TODO: Накопитель оплачивает комиссию своему счетчику
                try {
                    int commissionToStorageCounter = pvContract.viewComissionStorageCounter().
                            send().intValue();
                    System.out.println(storage.getLocalName() +
                            " need to pay " + commissionToStorageCounter +
                            " to " + storageCounterName);

                    if (pvContract.commissionToStorageCounter(
                            BigInteger.valueOf(commissionToStorageCounter)).send().isStatusOK()){
                        System.out.println(storage.getLocalName() +
                                " paid commission to " + storageCounterName);
                    }

                    // После оплаты комиссии каждый накопитель
                    // отписывается своему счетчику, что
                    // оплатил
                    sendMsg(storage,"commissionPaidStorageCounterPV",
                            storageCounterName,"commissionPaidStorageCounterPV");

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }else {
            block();
        }
    }


    // TODO: Метод принятия сообщения от счетчика накопителя, что
    //  генератор может продать энергию у нужно заплатить за неё
    private void receiveMsgAboutEnergy(Agent storageReceiver, String receiveProtocol, String storageCounter){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(receiveProtocol);
        ACLMessage msg = storageReceiver.receive(messageTemplate);

        if (msg!=null){

            int energy = (int)Double.parseDouble(msg.getContent().split(":")[0]);
            String generatorName = msg.getContent().split(":")[1];

            generatorsName.add(generatorName);

            System.out.println(storageReceiver.getLocalName() + " receive " + energy +
                    " from " + msg.getSender().getLocalName());

            // Рассчитаем сумму оплаты за энергию
            int sumPaymentForEnergy = energy * priceForStorage;

            // Накопитель оплачивает энергию to generatorName
            try {
            if (generatorName.equals("PV1") && !generatorsName.contains("Wind1")){

                if (pvContract1!=null){

                    if (pvContract1.paymentPVEnergyForStorage(BigInteger.valueOf(energy),
                            BigInteger.valueOf(sumPaymentForEnergy)).send().isStatusOK()){
                        System.out.println(storageReceiver.getLocalName() + " paid for pv1 energy");
                    }

                    // Очистили массив после оплаты
                    generatorsName.clear();

                }

            }else if (generatorName.equals("PV2") && !generatorsName.contains("Wind2")){

                if (pvContract2!=null){
                    if (pvContract2.paymentPVEnergyForStorage(BigInteger.valueOf(energy),
                            BigInteger.valueOf(sumPaymentForEnergy)).send().isStatusOK()){
                        System.out.println(storageReceiver.getLocalName() + " paid for pv2 energy");
                    }

                    // Очистили массив после оплаты
                    generatorsName.clear();
                }
            }else if (generatorName.equals("PV3") && !generatorsName.contains("Wind3")){

                if (pvContract3!=null){
                    if (pvContract3.paymentPVEnergyForStorage(BigInteger.valueOf(energy),
                            BigInteger.valueOf(sumPaymentForEnergy)).send().isStatusOK()){
                        System.out.println(storageReceiver.getLocalName() + " paid for pv3 energy");
                    }

                    // Очистили массив после оплаты
                    generatorsName.clear();
                }

            }else if (generatorName.equals("Wind1") && !generatorsName.contains("PV1")){

                if (windContract1!=null){
                    if (windContract1.paymentWindEnergyForStorage(BigInteger.valueOf(energy),
                            BigInteger.valueOf(sumPaymentForEnergy)).send().isStatusOK()){
                        System.out.println(storageReceiver.getLocalName() + " paid for wind1 energy");
                    }

                    // Очистили массив после оплаты
                    generatorsName.clear();
                }

            }else if (generatorName.equals("Wind2") && !generatorsName.contains("PV2")){

                if (windContract2!=null){
                    if (windContract2.paymentWindEnergyForStorage(BigInteger.valueOf(energy),
                            BigInteger.valueOf(sumPaymentForEnergy)).send().isStatusOK()){
                        System.out.println(storageReceiver.getLocalName() + " paid for wind2 energy");
                    }

                    // Очистили массив после оплаты
                    generatorsName.clear();
                }

            }else if (generatorName.equals("Wind3") && !generatorsName.contains("PV3")){

                if (windContract3!=null){
                    if (windContract3.paymentWindEnergyForStorage(BigInteger.valueOf(energy),
                            BigInteger.valueOf(sumPaymentForEnergy)).send().isStatusOK()){
                        System.out.println(storageReceiver.getLocalName() + " paid for wind3 energy");
                    }

                    // Очистили массив после оплаты
                    generatorsName.clear();
                }

            }

            // После совершения оплаты за энергию накопитель отправляет сообщение своему счетчику,
                // , чтобы тот обновил оплату за покупку энергии (отправляем в сообщении, кому оплатили энергию)
                sendMsg(storageReceiver,"PaymentCompleted",storageCounter,generatorName);

            } catch (Exception e) {
                e.printStackTrace();
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
        System.out.println(ANSI_GREEN + sender.getLocalName() + " send " + msg.getContent() +
                " to " + receiver + ANSI_RESET);
        System.out.println();
    }

}
