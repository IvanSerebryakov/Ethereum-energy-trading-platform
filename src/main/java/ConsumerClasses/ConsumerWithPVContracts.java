package ConsumerClasses;

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

/**
 * Consumers взаимодействуют с PV контрактами
 */
public class ConsumerWithPVContracts extends TickerBehaviour {

    private DataStore dataStore;

    private Web3j web3j;
    private ContractGasProvider contractGasProvider;

    private String PUBLIC_KEY_Consumer1;
    private String PUBLIC_KEY_Consumer2;
    private String PUBLIC_KEY_Consumer3;

    private Credentials credentialsConsumer1;
    private Credentials credentialsConsumer2;
    private Credentials credentialsConsumer3;

    // Экземпляры PV контрактов
    private PVContract pvContract1;
    private PVContract pvContract2;
    private PVContract pvContract3;

    private PVPenaltyContract pvPenaltyContract1;
    private PVPenaltyContract pvPenaltyContract2;
    private PVPenaltyContract pvPenaltyContract3;

    // Флаги создания экземпляров контракта и регистрации
    private boolean isRegistered1;
    private boolean isRegistered2;
    private boolean isRegistered3;

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


    public ConsumerWithPVContracts(Agent a, long period, Web3j web3j, ContractGasProvider contractGasProvider,
                                   String PUBLIC_KEY_Consumer1, String PUBLIC_KEY_Consumer2, String PUBLIC_KEY_Consumer3,
                                   Credentials credentialsConsumer1, Credentials credentialsConsumer2,
                                   Credentials credentialsConsumer3, DataStore dataStore,
                                   boolean isRegistered1, boolean isRegistered2, boolean isRegistered3) {
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

    }

    @Override
    protected void onTick() {

//        System.out.println("++++++++++++++++++++++++++++++++++");
//        System.out.println("DADADADADADADDADSTORERERERERERE " + getDataStore());
//        System.out.println("++++++++++++++++++++++++++++++++++");

        // Регистрация в контрактах
        if (myAgent.getLocalName().equals("Consumer1")){
//            if (isRegistered1) {
                registerInPVContracts(myAgent, PUBLIC_KEY_Consumer1, credentialsConsumer1);
//                isRegistered1 = false;
//            }
            // Оплата за текущую потребляемую энергию
            paymentForPVEnergy(myAgent, PUBLIC_KEY_Consumer1 ,"NeedToPayForPVConsumptionEnergy",
                    /*pvContract1, pvContract2, pvContract3,*/ "ConsumerCounter1",credentialsConsumer1);

            // Принятие сообщение от счтечика потребителя
            // и оплата ему комиссии
//            receiveMsgAboutCommission(myAgent,credentialsConsumer1,"ConsumerCounter1");

        } else if (myAgent.getLocalName().equals("Consumer2")){
//            if (isRegistered2) {
                registerInPVContracts(myAgent, PUBLIC_KEY_Consumer2, credentialsConsumer2);
//                isRegistered2 = false;
            // Оплата за текущую потребляемую энергию
            paymentForPVEnergy(myAgent, PUBLIC_KEY_Consumer2,"NeedToPayForPVConsumptionEnergy",
                    /*pvContract1, pvContract2, pvContract3,*/ "ConsumerCounter2",credentialsConsumer2);

            // Принятие сообщение от счтечика потребителя
            // и оплата ему комиссии
//            receiveMsgAboutCommission(myAgent,credentialsConsumer2,"ConsumerCounter2");
//            }
        } else{
//            if (isRegistered3) {
                registerInPVContracts(myAgent, PUBLIC_KEY_Consumer3, credentialsConsumer3);
//                isRegistered3 = false;
//            }
            // Оплата за текущую потребляемую энергию
            paymentForPVEnergy(myAgent, PUBLIC_KEY_Consumer3 ,"NeedToPayForPVConsumptionEnergy",
                    /*pvContract1, pvContract2, pvContract3,*/ "ConsumerCounter3",credentialsConsumer3);

            // Принятие сообщение от счтечика потребителя
            // и оплата ему комиссии
//            receiveMsgAboutCommission(myAgent,credentialsConsumer3,"ConsumerCounter3");
        }

    }

    // Метод регистрации в PV контрактах
    private void registerInPVContracts(Agent consumer, String publicKey, Credentials credentials
                                       ){

        // Из datastore берем адрес контракта и проверяем, что контракт заключен
        // с PV1, PV2 или PV3
        try {
        if (getDataStore().get(consumer.getLocalName() + "address")!=null &&
        getDataStore().get(consumer.getLocalName() + "penaltyAddress")!=null &&
                getDataStore().get(consumer.getLocalName() + "conclude")!=null
         ) {
            // Проверка, что контракт заключен с PV1
            if (getDataStore().get(consumer.getLocalName() + "conclude").equals("PV1") && isRegistered1) {

                String deployedAddress = String.valueOf(getDataStore().get(consumer.getLocalName() + "address"));
                String penaltyAddress = String.valueOf(getDataStore().get(consumer.getLocalName() + "penaltyAddress"));

//                System.out.println();
//                System.out.println("++++++++++++++++++++deployedAddresssss with PV1 " + deployedAddress);
//                System.out.println("--------------------penaltyAddressssss with PV1 " + penaltyAddress);
//                System.out.println();

                // Создаем экземпляр контракта PV1
                pvContract1 = PVContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                System.out.println(ANSI_RED + "---------------------------" +
                        "\n" + consumer.getLocalName() + " create pv1 contract sample with address " +
                        pvContract1.getContractAddress() + ANSI_RESET);

                // Создаем экземпляр штрафного коонтракта PV2
                pvPenaltyContract1 = PVPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);

                System.out.println("---------------------------");
                System.out.println(consumer.getLocalName() + " create pvPenaltyContract1 sample with address " +
                        pvPenaltyContract1.getContractAddress());


                // Регистрация в контракте pvContract1


                if (pvContract1.registrationConsumer(publicKey).send().isStatusOK()) {
                    System.out.println(ANSI_RED + "---------------------------" +
                            "\n" + consumer.getLocalName() + " with public key " + publicKey +
                            " create register in pvContract1 : " +
                            pvContract1.getContractAddress() + ANSI_RESET);
                }

                // Регистрация в конракте pvPenaltyContract1

                //System.out.println("---------------------------");
                if (pvPenaltyContract1.registrationConsumer(publicKey).send().isStatusOK()) {
                    System.out.println(consumer.getLocalName() + " with public key " + publicKey +
                            " register in pvPenaltyContract1 : " +
                            pvPenaltyContract1.getContractAddress());
                }

                isRegistered1 = false;

            }
        }

            if (getDataStore().get(consumer.getLocalName() + "address")!=null &&
                    getDataStore().get(consumer.getLocalName() + "penaltyAddress")!=null &&
                    getDataStore().get(consumer.getLocalName() + "conclude")!=null
            ) {
                // Проверка, что коонтракт заключен с PV2
                if (getDataStore().get(consumer.getLocalName() + "conclude").equals("PV2") && isRegistered2) {

                    String deployedAddress = String.valueOf(getDataStore().get(consumer.getLocalName() + "address"));
                    String penaltyAddress = String.valueOf(getDataStore().get(consumer.getLocalName() + "penaltyAddress"));

//                    System.out.println();
//                    System.out.println("++++++++++++++++++++deployedAddresssss with PV2 " + deployedAddress);
//                    System.out.println("--------------------penaltyAddressssss with PV2 " + penaltyAddress);
//                    System.out.println();

                    // Создаем экземпляр контракта PV2
                    pvContract2 = PVContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                    System.out.println(ANSI_RED + "---------------------------" +
                            "\n" + consumer.getLocalName() + " : " + publicKey + " create pvContract2 sample " +
                            pvContract2.getContractAddress() + ANSI_RESET);

                    // Создаем экземпляр штрафного контракта PV2
                    pvPenaltyContract2 = PVPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);

                    //System.out.println("---------------------------");
                    System.out.println(consumer.getLocalName() + " create pvPenaltyContract2 sample " +
                            pvPenaltyContract2.getContractAddress());

                    // Регитсрация в контракте PV2
                    if (pvContract2.registrationConsumer(publicKey).send().isStatusOK()) {
                        System.out.println(ANSI_RED + "---------------------------" +
                                "\n" + consumer.getLocalName() + " with public key " + publicKey +
                                " create register in pvContract2 : " + pvContract2.getContractAddress() + ANSI_RESET);
                    }

                    // Регистрация в контракте pvPenaltyContract2
                    //System.out.println("---------------------------");
                    if (pvPenaltyContract2.registrationConsumer(publicKey).send().isStatusOK()) {
                        System.out.println(consumer.getLocalName() + " with public key " + publicKey +
                                " register in pvPenaltyContract2 : " + pvPenaltyContract2.getContractAddress());
                    }

                    isRegistered2 = false;

                }
            }

            if (getDataStore().get(consumer.getLocalName() + "address")!=null &&
                    getDataStore().get(consumer.getLocalName() + "penaltyAddress")!=null &&
                    getDataStore().get(consumer.getLocalName() + "conclude")!=null
            ){
            // Проверка, что контракт заключен с PV3
            if (getDataStore().get(consumer.getLocalName() + "conclude").equals("PV3") && isRegistered3){

                String deployedAddress = String.valueOf(getDataStore().get(consumer.getLocalName() + "address"));
                String penaltyAddress = String.valueOf(getDataStore().get(consumer.getLocalName() + "penaltyAddress"));

//                System.out.println();
//                System.out.println("++++++++++++++++++++deployedAddresssss with PV3 " + deployedAddress);
//                System.out.println("--------------------penaltyAddressssss with PV3 " + penaltyAddress);
//                System.out.println();

                // Создаем экземпляр контракта PV3
                pvContract3 = PVContract.load(deployedAddress, web3j, credentials, contractGasProvider);

                System.out.println(ANSI_RED + "---------------------------" +
                        "\n" + consumer.getLocalName() + " : " + publicKey + " create pvContract3 sample " +
                        pvContract3.getContractAddress() + ANSI_RESET);

                // Создаем экземпляр конракта pvPenaltyContract3
                pvPenaltyContract3 = PVPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);

                //System.out.println("---------------------------");
                System.out.println(consumer.getLocalName() + " : " + publicKey + " create pvPenaltyContract3 sample " +
                        pvPenaltyContract3.getContractAddress());

                // Регистрация в контракте PV3

                if (pvContract3.registrationConsumer(publicKey).send().isStatusOK()){
                    System.out.println(ANSI_RED + "---------------------------" +
                            "\n" + consumer.getLocalName() + " with public key " + publicKey +
                            " register in pvContract3 : " + pvContract3.getContractAddress() + ANSI_RESET);
                }

                // Регистрация в контракте pvPenaltyContract3
                //System.out.println("---------------------------");
                if (pvPenaltyContract3.registrationConsumer(publicKey).send().isStatusOK()){
                    System.out.println(consumer.getLocalName() + " with public key " + publicKey +
                            " register in pvPenaltyContract3 : " + pvPenaltyContract3.getContractAddress());
                }

                isRegistered3 = false;

            }

        }

        } catch (Exception e) {
            System.out.println();
        }
    }


    // TODO: Метод оплаты за энергию потребителями
    private void paymentForPVEnergy(Agent consumer, String publicKey, String receiveProtocol,/* PVContract pvContract1,
                                    /*PVContract pvContract2, PVContract pvContract3,*/ String consumerCounterName,
                                    Credentials credentials) {

        // Получаем от счетчика, сколько потребили
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(receiveProtocol);
        ACLMessage msg = consumer.receive(messageTemplate);

        if (msg!=null){

//            System.out.println("=========== " + consumer.getLocalName() + " receive value of ");
            System.out.println(ANSI_RED + consumer.getLocalName() + " receive value of consumption energy " +
                    msg.getContent() + " from " + msg.getSender().getLocalName() + ANSI_RESET);

            // Оплата за текущую потребляемую энергию
            try {

            if (getDataStore().get(consumer.getLocalName() + "bet")!=null &&
                    getDataStore().get(consumer.getLocalName() + "conclude")!=null) {

                int curEnergy = (int) Double.parseDouble(msg.getContent());
                int bet = (int) Double.parseDouble(String.valueOf(getDataStore().get(consumer.getLocalName() + "bet")));
                int curPayment = bet * curEnergy;

//                System.out.println(consumer.getLocalName() + "-=-=-=-=-=-=-=-=- BETTTTTTTTTTTTT PV=-=-=-=-=-=--=- " + bet);
//                System.out.println(consumer.getLocalName() + "-=-=-=-==--=-=-=- curPayment PV-=-=-=-=-=-=-=-==-=- " + curPayment);

                // Проверка, чтобы оплата не была равна нулю (в случае нулевой энергии)
                //if (curPayment != 0) {
                    // Если контракт заключен с PV1
                    if (getDataStore().get(consumer.getLocalName() + "conclude")!=null &&
                getDataStore().get(consumer.getLocalName() + "conclude").equals("PV1")/* && pvContract1 != null*/) {

                        // Достаем из datastore адрес контракта
                        if (getDataStore().get(consumer.getLocalName() + "address") != null) {

                            String deployedAddress = String
                                    .valueOf(getDataStore().get(consumer.getLocalName() + "address"));

                            PVContract pvContract1 = PVContract.load(deployedAddress, web3j,
                                    credentials, contractGasProvider);


//                            int payment = (int) Double.parseDouble(String.valueOf(pvContract1.viewPaymentForPVEnergy().send().intValue()));
                            int payment = pvContract1.viewPaymentForPVEnergy().send().intValue();

                            System.out.println(ANSI_RED_BACKGROUND + " ======= Payment value in  pvContract1 ======= " +
                                    "\n" + payment + " other my calc " + curPayment + ANSI_RESET);

//                        System.out.println(consumer.getLocalName() + " need to pay+++++++++111111111 " +
//                                pvContract1.viewPaymentForPVEnergy().send().toString() +
//                                " for current consumption energy " + curEnergy);
//                        System.out.println("==========currr_paymenttttt PVVV11: ========== " + curPayment);

                            // Пишем внутри BigInteger.valueOf(0), т.к. в функции уже расчитывается и заложена
                            // сумма для перевода
                            if (pvContract1.paymentForPVEnergy(BigInteger.valueOf((int) payment)).send().isStatusOK()) {
                                System.out.println(ANSI_RED + "=== PAYMENT FOR CONSUMPTION PV1 ENERGY ====" +
                                        "\n" + consumer.getLocalName() + " with public key : "
                                        + publicKey + " paid " + curPayment + " for consumption energy from PV1" + ANSI_RESET);

                                // TODO: После совершения оплаты потребитель должен отписаться счетчику, что оплатил
                                //  ( и затем счетчик потребителя обновит суммаруню оплату за потребляемую энергию)
                                sendMsg(consumer, "PaymentPVCompleted", consumerCounterName, String.valueOf(payment));

                            }




                        }
                        //}

                        // Если контракт заключен с PV2
                        if (getDataStore().get(consumer.getLocalName() + "conclude")!=null &&
                                getDataStore().get(consumer.getLocalName() + "conclude").equals("PV2")/* && pvContract2 != null*/) {

//                        System.out.println(consumer.getLocalName() + " need to pay++++++++222222222 " +
//                                pvContract2.viewPaymentForPVEnergy().send().toString() +
//                                " for current consumption energy " + curEnergy);
//                        System.out.println("=========curr_paymenttttt PVVV2: ============== " + curPayment);

                            // Достаем из datastore адрес контракта
                            if (getDataStore().get(consumer.getLocalName() + "address") != null) {

                                String deployedAddress = String
                                        .valueOf(getDataStore().get(consumer.getLocalName() + "address"));

                                // Создаем локальный экземпляр контракта PV2
                                PVContract pvContract2 = PVContract.load(deployedAddress,
                                        web3j, credentials, contractGasProvider);

//                                int payment = (int) Double.parseDouble(String.valueOf(pvContract2.viewPaymentForPVEnergy().send().intValue()));
                                int payment = pvContract2.viewPaymentForPVEnergy().send().intValue();

                                System.out.println(ANSI_RED_BACKGROUND + " ======= Payment value in  pvContract2 ======= " +
                                        "\n" + payment +" other my calc " + curPayment +  ANSI_RESET);

                                if (pvContract2.paymentForPVEnergy(BigInteger.valueOf((int) payment)).send().isStatusOK()) {
                                    System.out.println(ANSI_RED + "=== PAYMENT FOR CONSUMPTION PV2 ENERGY ====" +
                                            "\n" + consumer.getLocalName() + " with public key : "
                                            + publicKey + " paid " + curPayment + " for consumption energy from PV2" + ANSI_RESET);
                                    // TODO: После совершения оплаты потребитель должен отписаться счетчику, что оплатил
                                    //  ( и затем счетчик потребителя обновит суммаруню оплату за потребляемую энергию)
                                    sendMsg(consumer, "PaymentPVCompleted", consumerCounterName, String.valueOf(payment));
                                }



                            }
                        }


                        // Если контракт заключен с PV3
                        if (getDataStore().get(consumer.getLocalName() + "conclude")!=null &&
                                getDataStore().get(consumer.getLocalName() + "conclude").equals("PV3")/* && pvContract3 != null*/) {

                            // Достаем из datastore адрес контракта
                            if (getDataStore().get(consumer.getLocalName() + "address") != null) {

                                String deployedAddress = String
                                        .valueOf(getDataStore().get(consumer.getLocalName() + "address"));

                                // Создаем локальный экземпляр контракта
                                PVContract pvContract3 = PVContract.load(deployedAddress,
                                        web3j, credentials, contractGasProvider);
//                                int payment = (int) Double.parseDouble(String.valueOf(pvContract3.viewPaymentForPVEnergy().send().intValue()));

                                int payment = pvContract3.viewPaymentForPVEnergy().send().intValue();

                                System.out.println(ANSI_RED_BACKGROUND + " ======= Payment value in  pvContract3 ======= " +
                                        "\n" + payment + " other my calc " + curPayment + ANSI_RESET);

//                        System.out.println(consumer.getLocalName() + " need to pay++++++++33333333 " +
//                                pvContract3.viewPaymentForPVEnergy().send().toString() +
//                                " for current consumption energy " + curEnergy);
//                        System.out.println("========= currr_paymenttttt PVVV3: ========= " + curPayment);

                                if (pvContract3.paymentForPVEnergy(BigInteger.valueOf((int) payment)).send().isStatusOK()) {
                                    System.out.println(ANSI_RED + "=== PAYMENT FOR CONSUMPTION PV3 ENERGY ====" +
                                            "\n" + consumer.getLocalName() + " with public key : "
                                            + publicKey + " pay " + curPayment + " for consumption energy from PV3" + ANSI_RESET);

                                    // TODO: После совершения оплаты потребитель должен отписаться счетчику, что оплатил
                                    //  ( и затем счетчик потребителя обновит суммаруню оплату за потребляемую энергию)
                                    sendMsg(consumer, "PaymentPVCompleted", consumerCounterName, String.valueOf(payment));
                                }



                            }
                        }

                    }
                }
            //}

            } catch (Exception e) {
                System.out.println();
            }
        }else {
            block();
        }
    }


    // TODO: Потребитель принимает сообщение от своего счетчика
    //  что необходимо выплатить ему комиссию
    private void receiveMsgAboutCommission(Agent consumer, Credentials credentials,
                                           String consumerCounter){

        MessageTemplate messageTemplate = MessageTemplate.
                MatchProtocol("commissionToConsumerCounter");
        ACLMessage msg = consumer.receive(messageTemplate);

        if (msg!=null){

            System.out.println(consumer.getLocalName() +
                    " receive " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());

            // После принятия сообщения о необходимости
            // выплаты комиссии потребитель
            // достает из своего datastore адрес контракта
            // и создает экземпляр контаркта.
            // Проверяем, что контаркт заключен
            // с PV1 или PV2 или PV3
            if (getDataStore().get(consumer.getLocalName() + "conclude")!=null &&
            getDataStore().get(consumer.getLocalName() + "conclude").equals("PV1") ||
                    getDataStore().get(consumer.getLocalName() + "conclude").equals("PV2") ||
                    getDataStore().get(consumer.getLocalName() + "conclude").equals("PV3")){

                if (getDataStore().get(consumer.getLocalName() + "address")!=null){
                    String deployedAddress =
                            String.valueOf(
                                    getDataStore().get(consumer.getLocalName() + "address")
                            );

                    // ConsumerCounter создает экземпляр PV контракта
                    // в этом методе
                    PVContract pvContract =
                            PVContract.load(deployedAddress,web3j,
                                    credentials,contractGasProvider);

                    System.out.println(consumer.getLocalName() +
                            " create pvContract sample " +
                            pvContract.getContractAddress() +
                            " in receiveMsgAboutCommission() method ");

                    // После создания экземпляра PV
                    // контаркта в этом методе потребитель оплаччивает комиссию
                    // своему счетчику
                    try {
                        int commissionToConsumerCounter =
                                pvContract.viewComissionConsumerCounter().send().intValue();

                        if (pvContract.commissionToConsumerCounter(
                                BigInteger.valueOf(commissionToConsumerCounter)
                        ).send().isStatusOK()){
                            System.out.println(consumer.getLocalName() +
                                    " paid commission to " +
                                    consumerCounter);
                        }

                        // После оплаты комиссии потребитель отписывается
                        // своему счетчику, что оплатил комиссию
                        sendMsg(consumer,"commissionConsumerCounterCompleted",
                                consumerCounter,"commissionConsumerCounterCompleted");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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
