package ConsumerClasses;

import SmartContracts.Service.ServiceContract;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class ConsumerWithServiceContracts extends TickerBehaviour {

    private DataStore dataStore;
    private Web3j web3j;
    private ContractGasProvider contractGasProvider;

    private String PUBLIC_KEY_Consumer1;
    private String PUBLIC_KEY_Consumer2;
    private String PUBLIC_KEY_Consumer3;

    private Credentials credentialsConsumer1;
    private Credentials credentialsConsumer2;
    private Credentials credentialsConsumer3;

    // Экземпляры смарт-контрактов для потребителей
    private ServiceContract serviceContract1;
    private ServiceContract serviceContract2;
    private ServiceContract serviceContract3;

    // Флаги регистрации в сервисных контрактах
    private boolean flagRegisterService1;
    private boolean flagRegisterService2;
    private boolean flagRegisterService3;

    // Оплата за 1кВт энергии, покупаемой у накопителя (системная услуга)
    private int paymentEnergyFromStorage = 100000;

    // Изменение цвета фона вывода текста
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";

    // Зеленый Storage и StorageCounters
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";

    // Yellow для вывода заключения нового контракта
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";

    // Blue - Pv и PVCounters
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";

    // Purple - windCounter и wind
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";

    // Cyan - для Consumers и ConsumerCounters
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

    // Cyan - для Consumers и ConsumerCounters
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    public ConsumerWithServiceContracts(Agent a, long period, DataStore dataStore,
                                        Web3j web3j, ContractGasProvider contractGasProvider,
                                        String PUBLIC_KEY_Consumer1,
                                        String PUBLIC_KEY_Consumer2, String PUBLIC_KEY_Consumer3,
                                        Credentials credentialsConsumer1, Credentials credentialsConsumer2,
                                        Credentials credentialsConsumer3,
                                        boolean flagRegisterService1, boolean flagRegisterService2,
                                        boolean flagRegisterService3) {
        super(a, period);

        this.dataStore = dataStore;
        setDataStore(dataStore);
        this.contractGasProvider = contractGasProvider;
        this.web3j = web3j;
        this.PUBLIC_KEY_Consumer1 = PUBLIC_KEY_Consumer1;
        this.PUBLIC_KEY_Consumer2 = PUBLIC_KEY_Consumer2;
        this.PUBLIC_KEY_Consumer3 = PUBLIC_KEY_Consumer3;
        this.credentialsConsumer1 = credentialsConsumer1;
        this.credentialsConsumer2 = credentialsConsumer2;
        this.credentialsConsumer3 = credentialsConsumer3;

        this.flagRegisterService1 = flagRegisterService1;
        this.flagRegisterService2 = flagRegisterService2;
        this.flagRegisterService3 = flagRegisterService3;
    }

    @Override
    protected void onTick() {


        if (myAgent.getLocalName().equals("Consumer1")) {

            // Потребители создают экземпляры контрактов в этом классе
            createContractSampleAndRegister(myAgent,
                    credentialsConsumer1, PUBLIC_KEY_Consumer1);

            // Потребители получают сообщение pvCounter -> consumerCounter -> consumer
            // и оплачивают за сисиемную услугу накопителю
            receiveMsgSystemServiceEnergyPV(myAgent,"Consumer1", "ConsumerCounter1");

            // Потребители получают сообщение windCounter -> consumerCounter -> consumer
            // и оплачивают за сисиемную услугу накопителю
            receiveMsgSystemServiceEnergyWind(myAgent,"Consumer1","ConsumerCounter1");

        }else if (myAgent.getLocalName().equals("Consumer2")){

            // Потребители создают экземпляры контрактов в этом классе
            createContractSampleAndRegister(myAgent,
                    credentialsConsumer2, PUBLIC_KEY_Consumer2);

            // Потребители получают сообщение pv -> consumerCounter -> consumer
            // и оплачивают за сисиемную услугу накопителю
            receiveMsgSystemServiceEnergyPV(myAgent,"Consumer2","ConsumerCounter2");

            // Потребители получают сообщение windCounter -> consumerCounter -> consumer
            // и оплачивают за сисиемную услугу накопителю
            receiveMsgSystemServiceEnergyWind(myAgent,"Consumer2","ConsumerCounter2");
        }else {

            // Потребители создают экземпляры контрактов в этом классе
            createContractSampleAndRegister(myAgent,
                    credentialsConsumer3, PUBLIC_KEY_Consumer3);

            // Потребители получают сообщение pv -> consumerCounter -> consumer
            // и оплачивают за сисиемную услугу накопителю
            receiveMsgSystemServiceEnergyPV(myAgent,"Consumer3","ConsumerCounter3");

            // Потребители получают сообщение windCounter -> consumerCounter -> consumer
            // и оплачивают за сисиемную услугу накопителю
            receiveMsgSystemServiceEnergyWind(myAgent,"Consumer3","ConsumerCounter3");
        }

    }


    private void createContractSampleAndRegister(Agent consumer, Credentials credentials,
                                                 String publicKey){

        // Consumer достает из своего datastore адрес сервисного контракта
        // и создает экземпляр контракта в этом классе
        try {

            if (getDataStore().get(consumer.getLocalName() + "serviceAddress") != null &&
            getDataStore().get(consumer.getLocalName() + "conclude")!=null) {

                String serviceDeployedAddress = String.valueOf(getDataStore().
                        get(consumer.getLocalName() + "serviceAddress"));
                String generatorConclude = String.valueOf(getDataStore().
                        get(consumer.getLocalName() + "conclude"));

                // Если потребитель заключил контракт с PV1
                // или c Wind1, то он регистриуется в serviceContract1
                if (generatorConclude.equals("PV1") || generatorConclude.equals("Wind1") && flagRegisterService1) {
                    serviceContract1 = ServiceContract.load(serviceDeployedAddress,
                            web3j, credentials, contractGasProvider);
                    System.out.println(ANSI_CYAN + consumer.getLocalName() + " create " +
                            " contract sample serviceContract1 " +
                            serviceContract1.getContractAddress() + ANSI_RESET);

                    // Consumer регистрируется в serviceContract1
                    if (flagRegisterService1) {
                        if (serviceContract1.registrationConsumer(publicKey).send().isStatusOK()) {
                            System.out.println(ANSI_CYAN + consumer.getLocalName() +
                                    " register in serviceContract1 " +
                                    serviceContract1.getContractAddress() + ANSI_RESET);
                        }
                        flagRegisterService1 = false;
                    }
                }


                if (generatorConclude.equals("PV2") || generatorConclude.equals("Wind2") && flagRegisterService2){

                    serviceContract2 = ServiceContract.load(serviceDeployedAddress,
                            web3j, credentials, contractGasProvider);
                    System.out.println(consumer.getLocalName() + " create " +
                            " contract sample " + serviceContract2.getContractAddress());

                    // Consumer2 регистрируется в serviceContract2
                    if (flagRegisterService2) {
                        if (serviceContract2.registrationConsumer(publicKey).send().isStatusOK()) {
                            System.out.println(ANSI_CYAN + consumer.getLocalName() +
                                    " register in serviceContract2 " +
                                    serviceContract2.getContractAddress() + ANSI_RESET);
                        }
                        flagRegisterService2 = false;
                    }
                }


                if (generatorConclude.equals("PV3") || generatorConclude.equals("Wind3") && flagRegisterService3){

                        serviceContract3 = ServiceContract.load(serviceDeployedAddress,
                                web3j, credentials, contractGasProvider);
                        System.out.println(ANSI_CYAN + consumer.getLocalName() + " create " +
                                " contract sample " +
                                serviceContract3.getContractAddress() + ANSI_RESET);

                        // Consumer3 регистрируется в serviceContract3
                        if (flagRegisterService3) {
                            if (serviceContract3.registrationConsumer(publicKey).send().isStatusOK()) {
                                System.out.println(ANSI_CYAN + consumer.getLocalName() +
                                        " register in serviceContract3 " +
                                        serviceContract3.getContractAddress() + ANSI_RESET);
                            }
                            flagRegisterService3 = false;
                        }
                    }
                }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    // TODO: Consumer получает кол-во энергии системной услуги,
    //  от счетчика потребителя и оплачивает за энергию
    //  (принятие сообщений pv -> consumerCounter -> consumer)
    private void receiveMsgSystemServiceEnergyPV(Agent consumer, String consumerName, String consumerCounter){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("SystemServicePV");
        ACLMessage msg = consumer.receive(messageTemplate);

        if (msg!=null) {

            int difference = (int) Double.parseDouble(msg.getContent().split(":")[0]);
            String pvName = msg.getContent().split(":")[1];

            System.out.println(ANSI_CYAN + consumer.getLocalName() + " receive difference " +
                    difference + " and pvCounterName " + pvName + " from " +
                    msg.getSender().getLocalName() + ANSI_RESET);

            // Расчет текущей оплаты потребителя за энергию системной услуги
            int payment = difference * paymentEnergyFromStorage;
            System.out.println(ANSI_CYAN + consumer.getLocalName() + " calc payment for " +
                    "service energy" + ANSI_RESET);

            // После принятия сообщения потребители оплачивают за
            // системную услугу
            // Сперва проверяем какой потребитель
            try {

            if (consumer.getLocalName().equals(consumerName)) {
                // Проверяем какой генератор
                if (pvName.equals("PVCounter1")) {
                    if (serviceContract1 != null) {
                        if (serviceContract1.paymentForSystemServiceToStorage(BigInteger.valueOf(difference),
                                BigInteger.valueOf(payment)).send().isStatusOK()) {

                            System.out.println(ANSI_CYAN + consumer.getLocalName() + " paid " +
                                    payment +
                                    " for storage1 service energy " + difference + ANSI_RESET);


                            // TODO: После оплаты за системную услугу потребитель
                            //  отписывается счетчику, что оплатил за системную услугу
                            sendMsg(consumer,"ServiceEnergyStorage1Completed",
                                    consumerCounter, String.valueOf(difference));

                        }

                    }
                } else if (pvName.equals("PVCounter2")){
                    if (serviceContract2!=null){
                        if (serviceContract2.paymentForSystemServiceToStorage(BigInteger.valueOf(difference),
                                BigInteger.valueOf(payment)).send().isStatusOK()){

                            System.out.println(ANSI_CYAN +
                                    consumer.getLocalName() + " paid " +
                                    payment + " for storage2 service energy "
                                    + difference + ANSI_RESET);

                            // TODO: После оплаты за системную услугу потребитель
                            //  отписывается счетчику, что оплатил за системную услугу
                            sendMsg(consumer,"ServiceEnergyStorage2Completed",
                                    consumerCounter, String.valueOf(difference));
                        }
                    }
                } else {
                    if (serviceContract3!=null){
                        if (serviceContract3.paymentForSystemServiceToStorage(BigInteger.valueOf(difference),
                                BigInteger.valueOf(payment)).send().isStatusOK()){

                            System.out.println(ANSI_CYAN +
                                    consumer.getLocalName() + " paid " +
                                    payment + " for storage3 service energy "
                                    + difference + ANSI_RESET);

                            // TODO: После оплаты за системную услугу потребитель
                            //  отписывается счетчику, что оплатил за системную услугу
                            sendMsg(consumer,"ServiceEnergyStorage3Completed",
                                    consumerCounter, String.valueOf(difference));
                        }
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


    // TODO: Consumer получает кол-во энергии системной услуги,
    //  от счетчика потребителя и оплачивает за энергию
    //  (принятие сообщений wind -> consumerCounter -> consumer)
    private void receiveMsgSystemServiceEnergyWind(Agent consumer, String consumerName, String consumerCounter){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("SystemServiceWind");
        ACLMessage msg = consumer.receive(messageTemplate);

        if (msg!=null){

            int difference = (int)Double.parseDouble(msg.getContent().split(":")[0]);
            String windName = msg.getContent().split(":")[1];

            System.out.println(ANSI_CYAN + consumer.getLocalName() + " receive difference " +
                    difference + " and windCounterName " + windName +
                    " from " + msg.getSender().getLocalName() + ANSI_RESET);

            // Потребители рассчитывают кол-во оплаты за потребляемую энергию
            int payment = difference * paymentEnergyFromStorage;
            System.out.println(ANSI_CYAN +
                    consumer.getLocalName() + " need to pay " +
                    payment + " for service energy" + ANSI_RESET);

            try {

            if (consumer.getLocalName().equals(consumerName)) {
                if (windName.equals("WindCounter1")) {
                    if (serviceContract1 != null) {
                        System.out.println("+++++++++++++++ " + consumerName +
                                " serviceContract1 " + serviceContract1.getContractAddress());
                        if (serviceContract1.paymentForSystemServiceToStorage(
                                BigInteger.valueOf(difference),BigInteger.valueOf(payment)
                        ).send().isStatusOK()){

                            System.out.println(ANSI_CYAN + consumer.getLocalName() +
                                    " paid " + payment + " for system service" +
                                    " energy " + difference + " to Storage1 " + ANSI_RESET);

                            // TODO: После оплаты за системную услугу потребитель
                            //  отписывается счетчику, что оплатил за системную услугу
                            sendMsg(consumer,"ServiceEnergyStorage1Completed",
                                    consumerCounter, String.valueOf(difference));

                        }

                    }
                }

                if (windName.equals("WindCounter2")) {
                    if (serviceContract2 != null) {
                        System.out.println("+++++++++++++++ " + consumerName +
                                " serviceContract2 " + serviceContract2.getContractAddress());
                        if (serviceContract2.paymentForSystemServiceToStorage(
                         BigInteger.valueOf(difference), BigInteger.valueOf(payment)
                        ).send().isStatusOK()) {
                            System.out.println(ANSI_CYAN + consumer.getLocalName() +
                                    " paid " + payment +
                                    " for system service " +
                                    "energy " + difference +
                                    " to Storage2" + ANSI_RESET);

                            // TODO: После оплаты за системную услугу потребитель
                            //  отписывается счетчику, что оплатил за системную услугу
                            sendMsg(consumer,"ServiceEnergyStorage2Completed",
                                    consumerCounter, String.valueOf(difference));
                        }
                    }
                }

                if (windName.equals("WindCounter3")){
                    if (serviceContract3!=null){
                        System.out.println("+++++++++++++++ " + consumerName +
                                " serviceContract3 " + serviceContract3.getContractAddress());
                        if (serviceContract3.paymentForSystemServiceToStorage(
                                BigInteger.valueOf(difference), BigInteger.valueOf(payment)
                        ).send().isStatusOK()){
                            System.out.println(ANSI_CYAN + consumer.getLocalName() +
                                    " paid " + payment +
                                    " for system service " +
                                    "energy " + difference +
                                    " to Storage3" + ANSI_RESET);

                            // TODO: После оплаты за системную услугу потребитель
                            //  отписывается счетчику, что оплатил за системную услугу
                            sendMsg(consumer,"ServiceEnergyStorage3Completed",
                                    consumerCounter, String.valueOf(difference));
                        }
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
