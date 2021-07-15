package ConsumerClasses;

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

public class ConsumerWithWindContracts extends TickerBehaviour {

    private DataStore dataStore;

    private Web3j web3j;
    private ContractGasProvider contractGasProvider;

    private String PUBLIC_KEY_Consumer1;
    private String PUBLIC_KEY_Consumer2;
    private String PUBLIC_KEY_Consumer3;

    private Credentials credentialsConsumer1;
    private Credentials credentialsConsumer2;
    private Credentials credentialsConsumer3;

    // Экземпляры Wind контрактов
    private WindContract windContract1;
    private WindContract windContract2;
    private WindContract windContract3;

    private WindPenaltyContract windPenaltyContract1;
    private WindPenaltyContract windPenaltyContract2;
    private WindPenaltyContract windPenaltyContract3;

    // Флаги создания экземпляров контракта и регистрации
    private boolean isRegistered1;
    private boolean isRegistered2;
    private boolean isRegistered3;

    private int iter1;
    private int iter2;
    private int iter3;

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

    public ConsumerWithWindContracts(Agent a, long period, Web3j web3j, ContractGasProvider contractGasProvider,
                                     String PUBLIC_KEY_Consumer1, String PUBLIC_KEY_Consumer2, String PUBLIC_KEY_Consumer3,
                                     Credentials credentialsConsumer1, Credentials credentialsConsumer2,
                                     Credentials credentialsConsumer3, DataStore dataStore,
                                     boolean isRegistered1, boolean isRegistered2, boolean isRegistered3,
                                     int iter1, int iter2, int iter3) {
        super(a, period);
        this.web3j = web3j;
        this.contractGasProvider = contractGasProvider;

        this.PUBLIC_KEY_Consumer1 = PUBLIC_KEY_Consumer1;
        this.PUBLIC_KEY_Consumer2 = PUBLIC_KEY_Consumer2;
        this.PUBLIC_KEY_Consumer3 = PUBLIC_KEY_Consumer3;

        this.credentialsConsumer1 = credentialsConsumer1;
        this.credentialsConsumer2 = credentialsConsumer2;
        this.credentialsConsumer3 = credentialsConsumer3;

        this.dataStore = dataStore;
        setDataStore(dataStore);

        this.isRegistered1 = isRegistered1;
        this.isRegistered2 = isRegistered2;
        this.isRegistered3 = isRegistered3;

        this.iter1 = iter1;
        this.iter2 = iter2;
        this.iter3 = iter3;
    }

    @Override
    protected void onTick() {

        if (myAgent.getLocalName().equals("Consumer1")){

            // Регистрация в контракте
            registerInWindContracts(myAgent, PUBLIC_KEY_Consumer1, credentialsConsumer1);

            // Оплата за потребялемую энергию
            paymentForWindWindEnergy(myAgent, PUBLIC_KEY_Consumer1, "NeedToPayForWindConsumptionEnergy",
                    windContract1, windContract2, windContract3, "ConsumerCounter1");
        } else if (myAgent.getLocalName().equals("Consumer2")){

            // регистрация в контракте
            registerInWindContracts(myAgent, PUBLIC_KEY_Consumer2, credentialsConsumer2);

            // Оплата за потребялемую энергию
            paymentForWindWindEnergy(myAgent, PUBLIC_KEY_Consumer2, "NeedToPayForWindConsumptionEnergy",
                    windContract1, windContract2, windContract3, "ConsumerCounter2");
        } else {

            // регистрация в контракте
            registerInWindContracts(myAgent, PUBLIC_KEY_Consumer3, credentialsConsumer3);

            // Оплата за потребялемую энергию
            paymentForWindWindEnergy(myAgent, PUBLIC_KEY_Consumer3, "NeedToPayForWindConsumptionEnergy",
                    windContract1, windContract2, windContract3, "ConsumerCounter3");
        }

    }

    // Метод регистрации в Wind контрактах
    private void registerInWindContracts(Agent consumer, String publicKey, Credentials credentials){

        if (getDataStore().get(consumer.getLocalName() + "address")!=null &&
        getDataStore().get(consumer.getLocalName() + "penaltyAddress")!=null &&
        getDataStore().get(consumer.getLocalName() + "conclude")!=null) {

            // Проверка, что конракт заключен с Wind1
            try {
                if (getDataStore().get(consumer.getLocalName() + "conclude").equals("Wind1") && isRegistered1) {

                    String deployedAddress = String.valueOf(getDataStore().get(consumer.getLocalName() + "address"));
                    String penaltyAddress = String.valueOf(getDataStore().get(consumer.getLocalName() + "penaltyAddress"));

                    // Создаем экземпляр контракта с Wind1
                    windContract1 = WindContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(ANSI_RED + "-------------------------" +
                            "\n" + consumer.getLocalName() + " create wind1 contract sample with address " +
                            windContract1.getContractAddress() + ANSI_RESET);

                    // Создаем экземпляр штрафного контракта
                    windPenaltyContract1 = WindPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);

                   // System.out.println("------------------------");
                    System.out.println(consumer.getLocalName() + " create windPenaltyContract1 sample with address " +
                            windPenaltyContract1.getContractAddress());


                    if (windContract1.registrationConsumer(publicKey).send().isStatusOK()) {
                        // Регистрация в контракте windContract1
                        System.out.println(ANSI_RED + "Registration in windContract1..." +
                                "\n" + consumer.getLocalName() + " with public key " + publicKey +
                                " register in windContract1 : " +
                                windContract1.getContractAddress() + ANSI_RESET);
                    }

                    if (windPenaltyContract1.registrationConsumer(publicKey).send().isStatusOK()) {
                        // Регистрация в контракте windPenaltyContract1
                        System.out.println("Registration in windPenaltyContract1..." +
                                "\n" + consumer.getLocalName() + " with public key " + publicKey +
                                " register in windPenaltyContract1 : " +
                                windPenaltyContract1.getContractAddress());
                    }

                    isRegistered1 = false;

                }

                // Проверка, что контракт заключен с Wind2
                if (getDataStore().get(consumer.getLocalName() + "conclude").equals("Wind2") && isRegistered2) {

                    String deployedAddress = String.valueOf(getDataStore().get(consumer.getLocalName() + "address"));
                    String penaltyAddress = String.valueOf(getDataStore().get(consumer.getLocalName() + "penaltyAddress"));

                    // Создаем экземпляр контракта Wind2
                    windContract2 = WindContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(ANSI_RED + "----------------------" +
                            "\n" + consumer.getLocalName() + " : " + publicKey + " create windContract2 sample " +
                            windContract2.getContractAddress() + ANSI_RESET);

                    // Создаем экземпляр штрафного контракта Wind2
                    windPenaltyContract2 = WindPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);

                    System.out.println("----------------------");
                    System.out.println(consumer.getLocalName() + " : " + publicKey + " create windPenaltyContract2 sample " +
                            windPenaltyContract2.getContractAddress());


                    if (windContract2.registrationConsumer(publicKey).send().isStatusOK()){
                        // Регистрация в контракте Wind2
                        System.out.println(ANSI_RED + " Registration in windContract2..." +
                                "\n" + consumer.getLocalName() + " with public key " + publicKey +
                                " register in windContract2 : " +
                                windContract2.getContractAddress() + ANSI_RESET);
                    }

                    // Регистрация в штрафном контракте Wind2
                    System.out.println("Registration in wind penaltyContract2...");
                    if (windPenaltyContract2.registrationConsumer(publicKey).send().isStatusOK()){
                        System.out.println(consumer.getLocalName() + " with public key " + publicKey +
                                " register in windPenaltyContract2 : " +
                                windPenaltyContract2.getContractAddress());
                    }

                    isRegistered2 = false;

                }


                // Проверка, что контракт заключен с Wind3
                if (getDataStore().get(consumer.getLocalName() + "conclude").equals("Wind3") && isRegistered3){

                    String deployedAddress = String.valueOf(getDataStore().get(consumer.getLocalName() + "address"));
                    String penaltyAddress = String.valueOf(getDataStore().get(consumer.getLocalName() + "penaltyAddress"));

                    // Создаем экземпляр контракта Wind3
                    windContract3 = WindContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(ANSI_RED + consumer.getLocalName() + " with public key : " +
                            publicKey + " create wind3 contract sample " +
                            "with address " + windContract3.getContractAddress() + ANSI_RESET
                    );

                    // Создаем экземпляр шттрафного контракта Wind3
                    windPenaltyContract3 = WindPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);

                    System.out.println(consumer.getLocalName() + " with public key : " + publicKey + " create windPenaltyContract3 sample " +
                            "with address " + windPenaltyContract3.getContractAddress());

                    // Регитсрация в контракте Wind3
                    if (windContract3.registrationConsumer(publicKey).send().isStatusOK()){
                        System.out.println(ANSI_RED + consumer.getLocalName() +
                                " with public key : " + publicKey + " register in" +
                                " windContract3 with address : " + windContract3.getContractAddress()
                         + ANSI_RESET);
                    }

                    // Регситрация в штрафном контракте Wind3
                    if (windPenaltyContract3.registrationConsumer(publicKey).send().isStatusOK()){
                        System.out.println(consumer.getLocalName() + " with public key : " + publicKey + " register in " +
                                "windPenaltyContract3 with address : " + windPenaltyContract3.getContractAddress());
                    }

                    isRegistered3 = false;

                }
            } catch (Exception e) {
                System.out.println();
            }
        }
    }


    // TODO: Метод оплаты за потребляемую энергию от Wind потребителями
    private void paymentForWindWindEnergy(Agent consumer, String publicKey, String receiveProtocol, WindContract windContract1,
                                          WindContract windContract2, WindContract windContract3, String consumerCounterName){

        // Получаем от счетчика, сколько потребили
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(receiveProtocol);
        ACLMessage msg = consumer.receive(messageTemplate);

        if (msg!=null){

            System.out.println(ANSI_RED + consumer.getLocalName() + " receive value of consumption energy " +
                    msg.getContent() + " from " + msg.getSender().getLocalName() + ANSI_RESET);

            // Оплата за текщую потребляемую энергию
            if (getDataStore().get(consumer.getLocalName() + "bet")!=null &&
                    getDataStore().get(consumer.getLocalName() + "conclude")!=null) {

                int curEnergy = (int)Double.parseDouble(msg.getContent());
                int bet = (int)Double.parseDouble(String.valueOf(getDataStore().get(consumer.getLocalName() + "bet")));
                int curPayment = bet * curEnergy;

//                System.out.println(consumer.getLocalName() + "-=-=-=-=-=-=-=-=- BETTTTTTTTTTTTT Wind=-=-=-=-=-=--=- " + bet);
//                System.out.println(consumer.getLocalName() + "-=-=-=-==--=-=-=- curPayment Wind -=-=-=-=-=-=-=-==-=- " + curPayment);

                try {

                    // Проверка, чтобы оплата не была равна нулю
                    if (curPayment!=0) {
                // Если контракт заключен с Wind1
                if (getDataStore().get(consumer.getLocalName() + "conclude").equals("Wind1") && windContract1!=null) {


                        if (windContract1.paymentForWindEnergy(BigInteger.valueOf(curPayment)).send().isStatusOK()) {
                            System.out.println(ANSI_RED + "===== PAYMENT FOR CONSUMPTION Wind1 ENERGY ======"
                                    + "\n" + consumer.getLocalName() + " with public key : "
                                    + publicKey + " paid " + curPayment + " for consumption energy to Wind1"
                            + ANSI_RESET);
                            iter1 +=1;
                            System.out.println("========================== iter1 PAYMENT TO WIND1 : " + iter1);
                        }else {
//                            System.out.println(" Transaction PAYMENT FOR CONSUMPTION Wind1 ENERGY : " +
//                                    windContract1.paymentForWindEnergy(BigInteger.valueOf(/*curPayment*/1)).send().getStatus());
                        }


                    // TODO: После совершения оплаты потребитель должен отписаться счетчику, что оплатил
                    //  ( и затем счетчик потребителя обновит суммаруню оплату за потребляемую энергию)
                    sendMsg(consumer, "PaymentWindCompleted",consumerCounterName, String.valueOf(curPayment));

                }else if(getDataStore().get(consumer.getLocalName() + "conclude").equals("Wind2") && windContract2!=null) {

                    if (windContract2.paymentForWindEnergy(BigInteger.valueOf(curPayment)).send().isStatusOK()){
                        System.out.println(ANSI_RED + "===== PAYMENT FOR CONSUMPTION Wind2 ENERGY ======" +
                                "\n" + consumer.getLocalName() + " with publicKey : " + publicKey +
                                " pay " + curPayment + " for consumption energy to Wind2" + ANSI_RESET);
                        iter2 += 1;
                        System.out.println(" ============================= iter2 PAYMENT TO WIND2 : " + iter2);
                    }else {
//                        System.out.println(" Transaction PAYMENT FOR CONSUMPTION Wind2 ENERGY : " +
//                                windContract2.paymentForWindEnergy(BigInteger.valueOf(/*curPayment*/1)).send().getStatus());
                    }

                    // TODO: После совершения оплаты потребитель должен отписаться счетчику, что оплатил
                    //  ( и затем счетчик потребителя обновит суммаруню оплату за потребляемую энергию)
                    sendMsg(consumer, "PaymentWindCompleted",consumerCounterName, String.valueOf(curPayment));

                } else if (getDataStore().get(consumer.getLocalName() + "conclude").equals("Wind3") && windContract3!=null){

                    //if (windContract3!=null) {

                        if (windContract3.paymentForWindEnergy(BigInteger.valueOf(curPayment)).send().isStatusOK()) {
                            System.out.println(ANSI_RED + "===== PAYMENT FOR CONSUMPTION Wind3 ENERGY ======" +
                                    consumer.getLocalName() + " with publicKey : " + publicKey +
                                    " pay " + curPayment + " for consumption energy to Wind3" + ANSI_RESET);

                            iter3 +=1;
                            System.out.println(" ======================== iter3 PAYMENT TO WIND3 : " + iter3);
                        }else {
//                            System.out.println(" Transaction PAYMENT FOR CONSUMPTION Wind3 ENERGY : " +
//                                    windContract3.paymentForWindEnergy(BigInteger.valueOf(/*curPayment*/1)).send().getStatus());
                        }

                    //}
                    // TODO: После совершения оплаты потребитель должен отписаться счетчику, что оплатил
                    //  ( и затем счетчик потребителя обновит суммаруню оплату за потребляемую энергию)
                    sendMsg(consumer, "PaymentWindCompleted",consumerCounterName, String.valueOf(curPayment));
                }

                    }

                } catch (Exception e) {
                    System.out.println();
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
        System.out.println(ANSI_RED + sender.getLocalName() + " send " + msg.getContent() +
                " to " + receiver + ANSI_RESET);
        System.out.println();
    }
}
