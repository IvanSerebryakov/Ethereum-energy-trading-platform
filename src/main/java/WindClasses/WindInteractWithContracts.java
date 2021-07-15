package WindClasses;

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

public class WindInteractWithContracts extends TickerBehaviour {

    private DataStore dataStore;

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

    // Флаги регистрации в контрактах
    private boolean flagRegisterContract1;
    private boolean flagRegisterContract2;
    private boolean flagRegisterContract3;

    // Флаги регистрации ставок
    private boolean flagRegisterBet1;
    private boolean flagRegisterBet2;
    private boolean flagRegisterBet3;

    // Стоимость 1 кВт энергии, перезакупаемой у накопителя
    private int paymentEnergyFromStorage = 60000;

    // Экземпляры контарктов
    private WindContract windContract1;
    private WindContract windContract2;
    private WindContract windContract3;

    private WindPenaltyContract windPenaltyContract1;
    private WindPenaltyContract windPenaltyContract2;
    private WindPenaltyContract windPenaltyContract3;

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

    public WindInteractWithContracts(Agent a, long period, Web3j web3j, ContractGasProvider contractGasProvider,
                                     String PUBLIC_KEY_wind1, String PUBLIC_KEY_wind2, String PUBLIC_KEY_wind3,
                                     Credentials credentials1, Credentials credentials2, Credentials credentials3,
                                     DataStore dataStore, boolean flagRegisterContract1, boolean flagRegisterContract2,
                                     boolean flagRegisterContract3, boolean flagRegisterBet1, boolean flagRegisterBet2,
                                     boolean flagRegisterBet3) {
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

        this.flagRegisterContract1 = flagRegisterContract1;
        this.flagRegisterContract2 = flagRegisterContract2;
        this.flagRegisterContract3 = flagRegisterContract3;

        this.flagRegisterBet1 = flagRegisterBet1;
        this.flagRegisterBet2 = flagRegisterBet2;
        this.flagRegisterBet3 = flagRegisterBet3;


    }

    @Override
    protected void onTick() {

        if (myAgent.getLocalName().equals("Wind1")){

            // Регистрация в контарктах
            if (flagRegisterContract1) {
                windRegisterInContracts(myAgent, PUBLIC_KEY_wind1, credentials1);
                //flagRegisterContract1 = false;
            }

            // Регистрация ставки победителя в контракте
            registerBetForWind(myAgent, PUBLIC_KEY_wind1);

            // Оплата за перезакупку энергии у Storage1
            rePurchaseStorageEnergyForWind(myAgent);

        }else if (myAgent.getLocalName().equals("Wind2")){

            // Регистрация в контарктах
            if (flagRegisterContract2) {
                windRegisterInContracts(myAgent, PUBLIC_KEY_wind2, credentials2);
                //flagRegisterContract2 = false;
            }

            // Регистрация ставки победителя в контракте
            registerBetForWind(myAgent, PUBLIC_KEY_wind2);

            // Оплата за перезакупку энергии у Storage2
            rePurchaseStorageEnergyForWind(myAgent);

        } else {

            // Регистрация в контрактах
            if (flagRegisterContract3) {
                windRegisterInContracts(myAgent, PUBLIC_KEY_wind3, credentials3);
//                flagRegisterContract3 = false;
            }

            // Регистрация ставки победителя в контракте
            registerBetForWind(myAgent, PUBLIC_KEY_wind3);

            // Оплата за перезакупку энергии у Storage1
            rePurchaseStorageEnergyForWind(myAgent);

        }

//        if (windContract1!=null){
//            System.out.println("========Wind1Contract addesssss======== " + windContract1.getContractAddress());
//        }
//        if (windContract2!=null){
//            System.out.println("========Wind2Contract addresss========= " + windContract2.getContractAddress());
//        }
//        if (windContract3!=null){
//            System.out.println("========Wind3Contract address=========  " + windContract3.getContractAddress());
//        }

    }

    // TODO: Методы регистрации в контрактах
    private void windRegisterInContracts(Agent wind, String publicKey, Credentials credentials
                                        ){

        try {
        // Проверка, что в datastore значения != null
        if (getDataStore().get(wind.getLocalName() + "address")!=null &&
        getDataStore().get(wind.getLocalName() + "penaltyAddress")!=null){

            String deployedAddress = String.valueOf(getDataStore().get(wind.getLocalName() + "address"));
            String penaltyAddress = String.valueOf(getDataStore().get(wind.getLocalName() + "penaltyAddress"));

            // Проверка, если Wind1
            if (wind.getLocalName().equals("Wind1") && flagRegisterContract1) {


                windContract1 = WindContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                System.out.println(ANSI_PURPLE + wind.getLocalName() + " with public key " + publicKey +
                        " create windContract sample " + windContract1.getContractAddress() + ANSI_RESET);

                windPenaltyContract1 = WindPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);
                System.out.println(ANSI_PURPLE + wind.getLocalName() + " with public key " +
                        publicKey + " create windPenaltyContract sample " +
                        windPenaltyContract1.getContractAddress() + ANSI_RESET);



                if (windContract1.registrationWind(publicKey).send().isStatusOK()) {
                    // Регистрация в контарктах
                    System.out.println(ANSI_PURPLE + "========= " + wind.getLocalName() + " REGISTER IN CONTRACTS ========== " +
                            "\n" + wind.getLocalName() + " register in wind contract " +
                            windContract1.getContractAddress() + ANSI_RESET);
                }

                if (windPenaltyContract1.registrationWind(publicKey).send().isStatusOK()) {
                    System.out.println(wind.getLocalName() + " register in windPenaltyContract " +
                            windPenaltyContract1.getContractAddress());
                }

                flagRegisterContract1 = false;
            }

            // Проверка, если Wind2
            if (wind.getLocalName().equals("Wind2") && flagRegisterContract2){

                windContract2 = WindContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                System.out.println(ANSI_PURPLE + wind.getLocalName() + " with public key " + publicKey +
                        " create windContract2  sample " + windContract2.getContractAddress() + ANSI_RESET);

                windPenaltyContract2 = WindPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);

                System.out.println(ANSI_PURPLE + wind.getLocalName() + " with public key " + publicKey +
                        " create windPenaltyContract2  sample " +
                        windPenaltyContract2.getContractAddress() + ANSI_RESET);


                if (windContract2.registrationWind(publicKey).send().isStatusOK()) {
                    // Регистрация в контарктах
                    System.out.println(ANSI_PURPLE + "========= " + wind.getLocalName() + " REGISTER IN CONTRACTS ==========" +
                            "\n" + wind.getLocalName() + " register in wind contract " +
                            windContract2.getContractAddress() + ANSI_RESET);
                }

                if (windPenaltyContract2.registrationWind(publicKey).send().isStatusOK()) {
                    System.out.println(wind.getLocalName() + " register in windPenaltyContract " +
                            windPenaltyContract2.getContractAddress());
                }

                flagRegisterContract2 = false;

            }


            // Проверка, если Wind3
            if (wind.getLocalName().equals("Wind3") && flagRegisterContract3){

                windContract3 = WindContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                System.out.println(ANSI_PURPLE + wind.getLocalName() + " with public key " + publicKey +
                        " create windContract3  sample " + windContract3.getContractAddress() +
                        ANSI_RESET);

                windPenaltyContract3 = WindPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);

                System.out.println(ANSI_PURPLE + wind.getLocalName() + " with public key " + publicKey +
                        " create windPenaltyContract3  sample "
                        + windPenaltyContract3.getContractAddress() + ANSI_RESET);


                if (windContract3.registrationWind(publicKey).send().isStatusOK()) {
                    // Регистрация в контарктах
                    System.out.println(ANSI_PURPLE + "========= " + wind.getLocalName() + " REGISTER IN CONTRACTS ==========" +
                            "\n" + wind.getLocalName() + " register in wind contract " +
                            windContract3.getContractAddress() + ANSI_RESET);
                }

                if (windPenaltyContract3.registrationWind(publicKey).send().isStatusOK()) {
                    System.out.println(wind.getLocalName() + " register in windPenaltyContract " +
                            windPenaltyContract3.getContractAddress());
                }

                flagRegisterContract3 = false;
            }

        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // TODO: Метод регистрации ставки победителя в контракте
    private void registerBetForWind(Agent wind, String publicKey) {

        // Проверяем, что ставка есть в datastore
        if (getDataStore().get(wind.getLocalName() + "bet")!=null){

            double bet = Double.parseDouble(String.valueOf(getDataStore().get(wind.getLocalName() + "bet")));


            try {

                // Если Wind1
            if (wind.getLocalName().equals("Wind1")){

                // Проверка, что экземпляр контракта создан
                if (windContract1!=null && windContract1.getContractAddress()!=null && flagRegisterBet1){

                        if (windContract1.registerBetForWind(BigInteger.valueOf((int)bet)).send().isStatusOK()){
                            System.out.println(ANSI_PURPLE + wind.getLocalName() + " with publicKey : " + publicKey +
                                    " register bet " + bet + " in windContract1 "
                                    + windContract1.getContractAddress() + ANSI_RESET);
                        }

                    flagRegisterBet1 = false;
                }


            }

            // Еслли Wind2
                if (wind.getLocalName().equals("Wind2")){

                    if (windContract2!=null && windContract2.getContractAddress()!=null && flagRegisterBet2){

                        if (windContract2.registerBetForWind(BigInteger.valueOf((int) bet)).send().isStatusOK()){
                            System.out.println(ANSI_PURPLE + wind.getLocalName() + " with publicKey : " + publicKey +
                                    " register bet " + bet + " in windContract2 "
                                    + windContract2.getContractAddress() + ANSI_RESET);
                        }

                        flagRegisterBet2 = false;

                    }


                }


                // Если Wind3
                if (wind.getLocalName().equals("Wind3")){

                    if (windContract3!=null && windContract3.getContractAddress()!=null && flagRegisterBet3){

                        if (windContract3.registerBetForWind(BigInteger.valueOf((int) bet)).send().isStatusOK()){
                            System.out.println(ANSI_PURPLE + wind.getLocalName() + "with publicKey : " + publicKey +
                                    " register bet " + bet + " in windContract3 "
                                    + windContract3.getContractAddress() + ANSI_RESET);
                        }

                        flagRegisterBet3 = false;
                    }


                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // TODO: Wind принимает сообщение о том, что нужно заплатить за перезакупку
    //  энергии накопителю
    private void rePurchaseStorageEnergyForWind(Agent wind){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("PaymentRePurchaseEnergyWind");
        ACLMessage msg = wind.receive(messageTemplate);

        if (msg!=null){

            try {

            if (wind.getLocalName().equals("Wind1")) {
                int difference = (int)Double.parseDouble(msg.getContent());
                System.out.println(ANSI_PURPLE + wind.getLocalName() + " receive " +
                        difference + " from " +
                        msg.getSender().getLocalName() + ANSI_RESET);

                int payment = difference * paymentEnergyFromStorage;
                System.out.println(ANSI_PURPLE + wind.getLocalName() + " need to pay " +
                        payment + ANSI_RESET);
                // После получения сообщения Wind1 оплачивает за энергию
                if (windContract1!=null){

                    if (windContract1.rePurchaseStorageEnergyForWind(BigInteger.valueOf(difference),
                            BigInteger.valueOf(payment)).send().isStatusOK()){
                        System.out.println(ANSI_PURPLE + wind.getLocalName() +
                                " paid for Storage1 repurchase energy" + ANSI_RESET);

                        // После оплаты за перезакупку энергии у накопителя Wind1 отправляет
                        // сообщение to WindCounter1
                        sendMsg(wind,"PaymentForRepurchaseCompleted",
                                "WindCounter1", String.valueOf(difference));
                    }

                }
            }


            if (wind.getLocalName().equals("Wind2")){
                int difference = (int)Double.parseDouble(msg.getContent());
                System.out.println(wind.getLocalName() + " receive " +
                        difference + " from " +
                        msg.getSender().getLocalName());

                int payment = difference * paymentEnergyFromStorage;
                System.out.println(ANSI_PURPLE + wind.getLocalName() + " need to pay " +
                        payment + ANSI_RESET);
                // После получения сообщения Wind2 оплачивает за энергию
                if (windContract2!=null){
                    if (windContract2.rePurchaseStorageEnergyForWind(BigInteger.valueOf(difference),
                            BigInteger.valueOf(payment)).send().isStatusOK()){
                        System.out.println(ANSI_PURPLE + wind.getLocalName() +
                                " paid for Storage2 repurchase energy" + ANSI_RESET);

                        // После оплаты за перезакупку энергии у накопителя Wind2 отправляет
                        // сообщение to WindCounter2
                        sendMsg(wind,"PaymentForRepurchaseCompleted",
                                "WindCounter2",String.valueOf(difference));
                    }
                }
            }

            if (wind.getLocalName().equals("Wind3")){
                int difference = (int)Double.parseDouble(msg.getContent());
                System.out.println(wind.getLocalName() + " receive " +
                        difference + " from " +
                        msg.getSender().getLocalName());

                int payment = difference * paymentEnergyFromStorage;
                System.out.println(ANSI_PURPLE + wind.getLocalName() + " need to pay " +
                        payment + ANSI_RESET);
                // После получения сообщения Wind3 оплачивает за энергию
                if (windContract3!=null){
                    if (windContract3.rePurchaseStorageEnergyForWind(BigInteger.valueOf(difference),
                            BigInteger.valueOf(payment)).send().isStatusOK()){
                        System.out.println(ANSI_PURPLE + wind.getLocalName()
                                + " paid for Storage3 repurchase energy" + ANSI_RESET);

                        // После оплаты за перезакупку энергии у накопителя Wind1 отправляет
                        // сообщение to WindCounter3
                        sendMsg(wind,"PaymentForRepurchaseCompleted",
                                "WindCounter3",String.valueOf(difference));
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
