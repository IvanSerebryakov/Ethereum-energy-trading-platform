package ConsumerCounterClasses;

import MQTTConnection.MQTTSubscriber;
import SmartContracts.Service.ServiceContract;
import SmartContracts.Wind.WindPenaltyContract;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.rmi.MarshalException;
import java.util.List;

public class ConCounterInteractWithServiceContract extends TickerBehaviour {

    private DataStore dataStore;

    private Web3j web3j;
    private ContractGasProvider contractGasProvider;

    private String PUBLIC_KEY_ConsumerCounter1;
    private String PUBLIC_KEY_ConsumerCounter2;
    private String PUBLIC_KEY_ConsumerCounter3;


    private Credentials credentialsConsumerCounter1;
    private Credentials credentialsConsumerCounter2;
    private Credentials credentialsConsumerCounter3;

    // Флаги регистрации в сервисных контрактах
    private boolean flagRegister1;
    private boolean flagRegister2;
    private boolean flagRegister3;

    private double counterTime;

    private MQTTSubscriber mqttSubscriber = new MQTTSubscriber();

    // Массив зарегистрированных в сервисных контрактах
    private List<String> registerList;


    public ConCounterInteractWithServiceContract(Agent a, long period, DataStore dataStore,
                                                 Web3j web3j, ContractGasProvider contractGasProvider,
                                                 String PUBLIC_KEY_ConsumerCounter1, String PUBLIC_KEY_ConsumerCounter2,
                                                 String PUBLIC_KEY_ConsumerCounter3, Credentials credentialsConsumerCounter1,
                                                 Credentials credentialsConsumerCounter2, Credentials credentialsConsumerCounter3,
                                                 boolean flagRegister1, boolean flagRegister2, boolean flagRegister3,
                                                 int counterTime, List<String> registerList) {
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

        this.flagRegister1 = flagRegister1;
        this.flagRegister2 = flagRegister2;
        this.flagRegister3 = flagRegister3;

        this.counterTime = counterTime;

        this.registerList = registerList;
    }


    @Override
    protected void onTick() {

        //receiveTime();

        if (myAgent.getLocalName().equals("ConsumerCounter1")) {

            receiveTimeFromDist(myAgent);

            // Регистрация в сервисном контракте
            registration(myAgent,PUBLIC_KEY_ConsumerCounter1,credentialsConsumerCounter1);

            // Обновление суммарной оплаты за системную энергию и
            // обновление суммарной энергии системной услуги
            // Если сис.услуга от Storage1
            updateStorageService(myAgent,"ServiceEnergyStorage1Completed",
                    credentialsConsumerCounter1,"StorageCounter1");

            // Если сис.услуга от Storage2
            updateStorageService(myAgent,"ServiceEnergyStorage2Completed",
                    credentialsConsumerCounter1,"StorageCounter2");

            // Если сис.услуга от Storage3
            updateStorageService(myAgent,"ServiceEnergyStorage3Completed",
                    credentialsConsumerCounter1,"StorageCounter3");

        }else if (myAgent.getLocalName().equals("ConsumerCounter2")){

            receiveTimeFromDist(myAgent);

            // Регистрация в сервисном контракте
            registration(myAgent, PUBLIC_KEY_ConsumerCounter2,credentialsConsumerCounter2);

            // Обновление суммарной оплаты за системную энергию и
            // обновление суммарной энергии системной услуги
            // Если сис.услуга от Storage1
            updateStorageService(myAgent,"ServiceEnergyStorage1Completed",
                    credentialsConsumerCounter2,"StorageCounter1");

            // Если сис.услуга от Storage2
            updateStorageService(myAgent,"ServiceEnergyStorage2Completed",
                    credentialsConsumerCounter2,"StorageCounter2");

            // Если сис.услуга от Storage3
            updateStorageService(myAgent,"ServiceEnergyStorage3Completed",
                    credentialsConsumerCounter2,"StorageCounter3");

        }else {

            receiveTimeFromDist(myAgent);

            // Регистрация в сервисном контракте
            registration(myAgent,PUBLIC_KEY_ConsumerCounter3,credentialsConsumerCounter3);

            // Обновление суммарной оплаты за системную энергию и
            // обновление суммарной энергии системной услуги
            // Если сис.услуга от Storage1
            updateStorageService(myAgent,"ServiceEnergyStorage1Completed",
                    credentialsConsumerCounter3,"StorageCounter1");

            // Если сис.услуга от Storage2
            updateStorageService(myAgent,"ServiceEnergyStorage2Completed",
                    credentialsConsumerCounter3,"StorageCounter2");

            // Если сис.услуга от Storage3
            updateStorageService(myAgent,"ServiceEnergyStorage3Completed",
                    credentialsConsumerCounter3,"StorageCounter3");
        }
    }


    // TODO: Счетчики потребителей достают из datastore
    //  адреса развернутых сервисных контрактов
    //  , создание экземпляров контрактов,
    //  регистрация в контрактах
    private void registration(Agent consumerCounter,String publicKey,
                                          Credentials credentials){

        if (getDataStore().get(consumerCounter.getLocalName() + "serviceContract")!=null &&
                getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer")!=null){
            String deployedServiceAddress = String.valueOf(
                    getDataStore().get(consumerCounter.getLocalName() + "serviceContract"));
            // Присваиваем переменной generatorConcludeName имя, с кем поотребитель
            // заключил контракт
            String generatorConcludeName = String.valueOf(
                    getDataStore().get(consumerCounter.getLocalName() + "isConcludedConsumer"));

            // Счетчик потребителя создает локальный экземпляр контракта
            // в этом методе
            ServiceContract serviceContract = ServiceContract.load(deployedServiceAddress,
                    web3j,credentials,contractGasProvider);


            // Регистрация в сервисном контракте
            // Если такой открытый ключ не зарегестриван,
            // то регистриуем его
            if (!registerList.contains(publicKey)) {
                registerInServiceContract(serviceContract, publicKey, consumerCounter);
            }

        }
    }

    // Метод регистрации в сервисном контракте
    private void registerInServiceContract(ServiceContract serviceContract, String publicKey, Agent consumerCounter){
        try {
            if (serviceContract.registrationConsumerCounter(publicKey).send().isStatusOK()){
                System.out.println(consumerCounter.getLocalName() + " : " + publicKey +
                        " register in service contract: " + serviceContract.getContractAddress());

                // Открытый ключ каждого счетчика потребителя после регистрации
                // складываем в массив, и затем проверяем,
                // что если в массиве зарегистрированных содержится этот
                // открытый ключ, то не регистриуем больше
                // этого счетчика потребителя

                registerList.add(publicKey);
            }
        } catch (Exception e) {
            System.out.println();;
        }
    }


    // TODO: Метод обновления суммарной оплаты за системные услуги от накопителя, а также
    //  суммарной (системной) энергии, полученной от накопителя
    private void updateStorageService(Agent consumerCounter, String receiveProtocol,
                                      Credentials credentials, String storageCounterName){

        // Принятие сообщения от потребителя об оплате
        // за энергию системной услуги
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(receiveProtocol);
        ACLMessage msg = consumerCounter.receive(messageTemplate);

        if (msg!=null){

            int difference = (int)Double.parseDouble(msg.getContent());

            System.out.println(consumerCounter.getLocalName() +
                    " receive difference "+ difference +
                    " from " + msg.getSender().getLocalName());

            // Счетчик потребителя после получения
            // энергии системной услуги от потребителя
            // после оплаты за эту энергию создает
            // локальный экземпляр контракта и
            // выполняет обновление оплаты и
            // суммарной энергии
            try {

            if (getDataStore().get(consumerCounter.getLocalName() + "serviceContract")!=null){
                String deployedServiceAddress =
                        String.valueOf(getDataStore().
                                get(consumerCounter.getLocalName() + "serviceContract"));

                ServiceContract serviceContract = ServiceContract.
                        load(deployedServiceAddress,web3j,credentials,
                                contractGasProvider);

                System.out.println(consumerCounter.getLocalName() +
                        " create local service contract sample " +
                        serviceContract.getContractAddress() +
                        " in updateStorageService() method ");

                // Счетчик потребителя выполняет
                // транзакцию по обновлению суммарной оплаты
                // за системную услугу и суммарной
                // энергии

                if (serviceContract.updateStorageSevice(
                        BigInteger.valueOf(difference)).send().isStatusOK()){
                    System.out.println(consumerCounter.getLocalName() +
                            " update sum service energy and " +
                            "sum payment for this energy ");

                    // TODO: По истечении суток отправляем счетчику накопителя
                    //  сообщение, чтобы тот проверил выполнение условий контракта
                    //  по оплате для накопителя
                    if ((int)counterTime == 24){
                        sendMsg(consumerCounter,"controlPaymentForStorage",
                                storageCounterName,"controlPaymentForStorage");
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


    // TODO: Метод принятия времени из Матлаба по MQTT
    //  (через интерфейс из класса SubscriberCallback)
    private void receiveTime(){
        // Пока сделаем счетчик
        counterTime = mqttSubscriber.mqttSubscriber("Time",
                "ConsumerCounterService");
    }

    // TODO: Метод принятия времени от Дистрибьютора
    private void receiveTimeFromDist(Agent conCounter){
        // Пока просто сделаем счетчик времени (потом закомментить)
//        counterTime = mqttSubscriber.mqttSubscriber("Time","WindCounter");
        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("TimeFromMatlab");
        ACLMessage msg = conCounter.receive(messageTemplate);

        if (msg!=null){
            counterTime = Double.parseDouble(msg.getContent());
            System.out.println(conCounter.getLocalName() +
                    " receive " + counterTime +
                    " from " + counterTime);
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
