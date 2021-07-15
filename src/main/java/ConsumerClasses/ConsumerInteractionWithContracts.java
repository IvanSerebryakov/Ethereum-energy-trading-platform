package ConsumerClasses;

import ConnectionUDP.UDPClass;
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

import java.net.DatagramSocket;

public class ConsumerInteractionWithContracts extends TickerBehaviour {

    private Web3j web3j;
    private ContractGasProvider contractGasProvider;

    private String PUBLIC_KEY_Consumer1;
    private String PUBLIC_KEY_Consumer2;
    private String PUBLIC_KEY_Consumer3;

    private Credentials credentialsConsumer1;
    private Credentials credentialsConsumer2;
    private Credentials credentialsConsumer3;

    // Адреса основных контрактов
    private String deployedAddress1;
    private String deployedAddress2;
    private String deployedAddress3;

    //Адреса штрафных контрактов
    private String deployedPenaltyAddress1;
    private String deployedPenaltyAddress2;
    private String deployedPenaltyAddress3;

    // Адреса сервисных контрактов
    private String serviceContractAddress1;
    private String serviceContractAddress2;
    private String serviceContractAddress3;

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

    private DataStore dataStore;
    private UDPClass udpClass;

    // Флаги для установки соединения и отправки сигнала в Матлаб
    private boolean flag1Connection;
    private boolean flag2Connection;
    private boolean flag3Connection;

    // Порты для отправки сигнала в Матлаб (destPort)
    // wind1
    private int con1Wind1port = 10113;
    private int con2Wind1port = 10115;
    private int con3Wind1port = 10117;
    // pv1
    private int con1PV1port = 10131;
    private int con2PV1port = 10133;
    private int con3PV1port = 10135;

    // wind2
    private int con1Wind2port = 10119;
    private int con2Wind2port = 10121;
    private int con3Wind2port = 10123;
    // pv2
    private int con1PV2port = 10137;
    private int con2PV2port = 10139;
    private int con3PV2port = 10141;

    //wind3
    private int con1Wind3port = 10125;
    private int con2Wind3port = 10127;
    private int con3Wind3port = 10129;
    //pv3
    private int con1PV3port = 10143;
    private int con2PV3port = 10145;
    private int con3PV3port = 10147;

    // Сигналы в Матлаб
    // Wind1
    private String Wind1sig = "10";
    private String Wind2sig = "20";
    private String Wind3sig = "30";

    private String PV1sig = "40";
    private String PV2sig = "50";
    private String PV3sig = "60";


    public ConsumerInteractionWithContracts(Agent a, long period, DataStore dataStore, UDPClass udpClass,
                                            boolean flag1Connection, boolean flag2Connection, boolean flag3Connection,
                                            Web3j web3j, ContractGasProvider contractGasProvider, String PUBLIC_KEY_Consumer1,
                                            String PUBLIC_KEY_Consumer2, String PUBLIC_KEY_Consumer3,
                                            Credentials credentialsConsumer1, Credentials credentialsConsumer2,
                                            Credentials credentialsConsumer3) {
        super(a, period);
        this.dataStore = dataStore;
        setDataStore(dataStore);
        this.udpClass = udpClass;

        this.flag1Connection = flag1Connection;
        this.flag2Connection = flag2Connection;
        this.flag3Connection = flag3Connection;

        this.web3j = web3j;
        this.contractGasProvider = contractGasProvider;

        this.PUBLIC_KEY_Consumer1 = PUBLIC_KEY_Consumer1;
        this.PUBLIC_KEY_Consumer2 = PUBLIC_KEY_Consumer2;
        this.PUBLIC_KEY_Consumer3 = PUBLIC_KEY_Consumer3;

        this.credentialsConsumer1 = credentialsConsumer1;
        this.credentialsConsumer2 = credentialsConsumer2;
        this.credentialsConsumer3 = credentialsConsumer3;

    }

    @Override
    protected void onTick() {

        // !!!!Здесь пока получаю только адреса основных контрактов
        if (myAgent.getLocalName().equals("Consumer1")) {
            receivedDeployedAddresses("WindDeployedContractAddress",
                    "WindPenaltyDeployedContractAddress", myAgent, deployedAddress1);
            receivedDeployedAddresses("PVDeployedContractAddress",
                    "PVPenaltyDeployedContractAddress", myAgent, deployedAddress1);
        }

        if (myAgent.getLocalName().equals("Consumer2")) {
            receivedDeployedAddresses("WindDeployedContractAddress",
                    "WindPenaltyDeployedContractAddress", myAgent, deployedAddress2);
            receivedDeployedAddresses("PVDeployedContractAddress",
                    "PVPenaltyDeployedContractAddress", myAgent, deployedAddress2);
        }

        if (myAgent.getLocalName().equals("Consumer3")) {
            receivedDeployedAddresses("WindDeployedContractAddress",
                    "WindPenaltyDeployedContractAddress", myAgent, deployedAddress3);
            receivedDeployedAddresses("PVDeployedContractAddress",
                    "PVPenaltyDeployedContractAddress", myAgent, deployedAddress3);
        }


    }

    // получение развернутых адресов контрактов, отправка адресов счетчикам, добавление адреса в datastore
    private void receivedDeployedAddresses(String protocol, String penaltyProtocol,  Agent receiver, String deployedAddress){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol(protocol);
        ACLMessage msg = receiver.receive(messageTemplate);

        if (msg!=null){
            deployedAddress = msg.getContent();
            System.out.println(receiver.getLocalName() + " received " + deployedAddress +
                    " from " + msg.getSender().getLocalName());


            if (receiver.getLocalName().equals("Consumer1")) {

                // добавляем в datastore адрес контракта для каждого потребителя
                getDataStore().put(receiver.getLocalName() + "address", deployedAddress);
                // добавляем в datastore имя генератора, с кем потребитель заключил конракт
                getDataStore().put(receiver.getLocalName() + "conclude", msg.getSender().getLocalName());
                // отправка адресов счетчикам потребителей
                sendMsg(receiver, "DeployedAddress", "ConsumerCounter1", deployedAddress);
                // отправка счетчику потребителя имени ггенератора, с которым потребитлеь заключил конетракт
                sendMsg(receiver, "isConcludedConsumer", "ConsumerCounter1", msg.getSender().getLocalName());

                //System.out.println("====================DATATA STOTOER consumer1 " + getDataStore());

                // отправка сигнала в Матлаб о начале приема сигнала в систему
                sendingUDPSignal(receiver.getLocalName() + "conclude", flag1Connection, con1Wind1port, con1Wind2port, con1Wind3port,
                        con1PV1port, con1PV2port, con1PV3port);

                // развертывание сервисного контракта и отправка адресов сервисного контракта необходимым участникам
                // Если получили сообщение от PV1 или Wind1, то отправляем сообщение to Storage1
                if (msg.getSender().getLocalName().equals("PV1") || msg.getSender().getLocalName().equals("Wind1")) {
                    deployingServiceContract(receiver, "ServiceAddress", PUBLIC_KEY_Consumer1,
                            credentialsConsumer1, "ConsumerCounter1", "Storage1");
                }

                // Если получили сообщение от PV2 или Wind2, то отправляем сообщение to Storage2
                if (msg.getSender().getLocalName().equals("PV2") || msg.getSender().getLocalName().equals("Wind2")){
                    deployingServiceContract(receiver, "ServiceAddress", PUBLIC_KEY_Consumer1,
                            credentialsConsumer1, "ConsumerCounter1", "Storage2");
                }

                // Если получили сообщение от PV3 или Wind3, то отправляем сообщение to Storage3
                if (msg.getSender().getLocalName().equals("PV3") || msg.getSender().getLocalName().equals("Wind3")){
                    deployingServiceContract(receiver, "ServiceAddress", PUBLIC_KEY_Consumer1,
                            credentialsConsumer1, "ConsumerCounter1", "Storage3");
                }

            } else if (receiver.getLocalName().equals("Consumer2")){

                getDataStore().put(receiver.getLocalName() + "address", deployedAddress);
                getDataStore().put(receiver.getLocalName() + "conclude", msg.getSender().getLocalName());
                sendMsg(receiver, "DeployedAddress", "ConsumerCounter2", deployedAddress);
                // отправка счетчику потребителя имени ггенератора, с которым потребитлеь заключил конетракт
                sendMsg(receiver, "isConcludedConsumer", "ConsumerCounter2", msg.getSender().getLocalName());

                //System.out.println("====================DATATA STOTOER consumer2222 " + getDataStore());

                // отправка сигнала в Матлаб о начале приема сигнала в систему
                sendingUDPSignal(receiver.getLocalName() + "conclude", flag2Connection, con2Wind1port, con2Wind2port, con2Wind3port,
                        con2PV1port, con2PV2port, con2PV3port);

                // развертывание сервисного контракта и отправка адресов сервисного контракта необходимым участникам
                // Если получили сообщение от PV1 или Wind1, то отправляем сообщение to Storage1
                if (msg.getSender().getLocalName().equals("PV1") || msg.getSender().getLocalName().equals("Wind1")) {
                    deployingServiceContract(receiver, "ServiceAddress", PUBLIC_KEY_Consumer2,
                            credentialsConsumer2, "ConsumerCounter2", "Storage1");
                }

                // Если получили сообщение от PV2 или Wind2, то отправляем сообщение to Storage2
                if (msg.getSender().getLocalName().equals("PV2") || msg.getSender().getLocalName().equals("Wind2")){
                    deployingServiceContract(receiver, "ServiceAddress", PUBLIC_KEY_Consumer2,
                            credentialsConsumer2, "ConsumerCounter2", "Storage2");
                }

                // Если получили сообщение от PV3 или Wind3, то отправляем сообщение to Storage3
                if (msg.getSender().getLocalName().equals("PV3") || msg.getSender().getLocalName().equals("Wind3")){
                    deployingServiceContract(receiver, "ServiceAddress", PUBLIC_KEY_Consumer2,
                            credentialsConsumer2, "ConsumerCounter2", "Storage3");
                }


            } else {

                getDataStore().put(receiver.getLocalName() + "address", deployedAddress);
                getDataStore().put(receiver.getLocalName() + "conclude", msg.getSender().getLocalName());
                sendMsg(receiver, "DeployedAddress", "ConsumerCounter3", deployedAddress);
                // отправка счетчику потребителя имени ггенератора, с которым потребитлеь заключил конетракт
                sendMsg(receiver, "isConcludedConsumer", "ConsumerCounter3", msg.getSender().getLocalName());

                //System.out.println("====================DATATA STOTOER consumer3333 " + getDataStore());

                // отправка сигнала в Матлаб о начале приема сигнала в систему
                sendingUDPSignal(receiver.getLocalName() + "conclude", flag3Connection, con3Wind1port, con3Wind2port, con3Wind3port,
                        con3PV1port, con3PV2port, con3PV3port);

                // развертывание сервисного контракта и отправка адресов сервисного контракта необходимым участникам
                // Если получили сообщение от PV1 или Wind1, то отправляем сообщение to Storage1
                if (msg.getSender().getLocalName().equals("PV1") || msg.getSender().getLocalName().equals("Wind1")) {
                    deployingServiceContract(receiver, "ServiceAddress", PUBLIC_KEY_Consumer3,
                            credentialsConsumer3, "ConsumerCounter3", "Storage1");
                }

                // Если получили сообщение от PV2 или Wind2, то отправляем сообщение to Storage2
                if (msg.getSender().getLocalName().equals("PV2") || msg.getSender().getLocalName().equals("Wind2")){
                    deployingServiceContract(receiver, "ServiceAddress", PUBLIC_KEY_Consumer3,
                            credentialsConsumer3, "ConsumerCounter3", "Storage2");
                }

                // Если получили сообщение от PV3 или Wind3, то отправляем сообщение to Storage3
                if (msg.getSender().getLocalName().equals("PV3") || msg.getSender().getLocalName().equals("Wind3")){
                    deployingServiceContract(receiver, "ServiceAddress", PUBLIC_KEY_Consumer3,
                            credentialsConsumer3, "ConsumerCounter3", "Storage3");
                }

            }

        }else {
            block();
        }


        // Принятие адресов штрафных контрактов
        MessageTemplate messageTemplate1 = MessageTemplate.MatchProtocol(penaltyProtocol);
        ACLMessage aclMessage = receiver.receive(messageTemplate1);

        if (aclMessage!=null){
            System.out.println(receiver.getLocalName() + " receive penalty contract address: " + aclMessage.getContent() +
                    " from " + aclMessage.getSender().getLocalName());

            // Добавляем адреса штрафных контрактов в datastore потребителей
            if (receiver.getLocalName().equals("Consumer1")) {
                getDataStore().put(receiver.getLocalName() + "penaltyAddress", aclMessage.getContent());
                System.out.println();
                //System.out.println("====================DATATA STOTOER consumer1111 " + getDataStore());
                //System.out.println();
                // Отправка адреса штрафного контракта своему счетчику
                sendMsg(receiver, "PenaltyAddress","ConsumerCounter1", aclMessage.getContent());

            } else if (receiver.getLocalName().equals("Consumer2")) {
                getDataStore().put(receiver.getLocalName() + "penaltyAddress", aclMessage.getContent());
                //System.out.println();
                //System.out.println("====================DATATA STOTOER consumer2222 " + getDataStore());
                //System.out.println();
                sendMsg(receiver, "PenaltyAddress", "ConsumerCounter2", aclMessage.getContent());

            } else {
                getDataStore().put(receiver.getLocalName() + "penaltyAddress", aclMessage.getContent());
                //System.out.println();
                //System.out.println("====================DATATA STOTOER consumer3333 " + getDataStore());
                //System.out.println();
                sendMsg(receiver, "PenaltyAddress", "ConsumerCounter3", aclMessage.getContent());

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

    private void sendingUDPSignal(String consumerConclude, boolean flagConnection, int portWind1, int portWind2, int portWind3,
                                  int portPV1, int portPV2, int portPV3){


        if (getDataStore().get(consumerConclude)!=null) {

            // Wind1
            if (getDataStore().get(consumerConclude).equals("Wind1") && flagConnection){

                DatagramSocket datagramSocket = udpClass.connectionUDP(portWind1 + 200);
                udpClass.sendingUDP(datagramSocket, portWind1, Wind1sig);
                flagConnection = false;
            }

            // PV1
            if (getDataStore().get(consumerConclude).equals("PV1") && flagConnection) {

                DatagramSocket datagramSocket = udpClass.connectionUDP(portPV1 + 200);
                udpClass.sendingUDP(datagramSocket, portPV1, PV1sig);
                flagConnection = false;
            }

            // Wind2
            if (getDataStore().get(consumerConclude).equals("Wind2") && flagConnection){

                DatagramSocket datagramSocket = udpClass.connectionUDP(portWind2 + 200);
                udpClass.sendingUDP(datagramSocket, portWind2, Wind2sig);
                flagConnection = false;
            }

            //PV2
            if (getDataStore().get(consumerConclude).equals("PV2") && flagConnection){
                DatagramSocket datagramSocket = udpClass.connectionUDP(portPV2 + 200);
                udpClass.sendingUDP(datagramSocket, portPV2, PV2sig);
                flagConnection = false;
            }

            //Wind3
            if (getDataStore().get(consumerConclude).equals("Wind3") && flagConnection){
                DatagramSocket datagramSocket = udpClass.connectionUDP(portWind3 + 200);
                udpClass.sendingUDP(datagramSocket, portWind3, Wind3sig);
                flagConnection = false;
            }

            //PV3
            if (getDataStore().get(consumerConclude).equals("PV3") && flagConnection){
                DatagramSocket datagramSocket = udpClass.connectionUDP(portPV3 + 200);
                udpClass.sendingUDP(datagramSocket, portPV3, PV3sig);
                flagConnection = false;
            }
        }

    }

    private void deployingServiceContract(Agent deployer, String protocol,
                                          String publicKey, Credentials credentials,
                                          String consumerCounterName, String storageName){

        String serviceContractAddress;
        // Развертывание сервисного контракта
        try {
            serviceContractAddress = ServiceContract.deploy(web3j, credentials, contractGasProvider)
                    .send()
                    .getContractAddress();
            System.out.println(deployer.getLocalName() + " : " + publicKey + " deploy service contract with address" +
                    serviceContractAddress);

//            // Consumer создает экземпляр сервисного контракта и регистрируется в нем
//            ServiceContract serviceContract = ServiceContract.load(serviceContractAddress,
//                    web3j,credentials,contractGasProvider);
//
//            // Регистрация потребителя в сервисном контракте
//            if (serviceContract.registrationConsumer(publicKey).send().isStatusOK()){
//                System.out.println(deployer.getLocalName() + " register in service contract " +
//                        serviceContract.getContractAddress());
//            }

            // отправка адреса счетчику потребителя и накопителю
            sendMsg(deployer, protocol, consumerCounterName, serviceContractAddress);
            sendMsg(deployer, protocol, storageName, serviceContractAddress);

            // Добавдяем в DataStore адрес сервисного контракта
            getDataStore().put(deployer.getLocalName() + "serviceAddress",serviceContractAddress);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // TODO: Регистрация в контрактах (расписать каждый метод для своей задачи в контракте)



}
