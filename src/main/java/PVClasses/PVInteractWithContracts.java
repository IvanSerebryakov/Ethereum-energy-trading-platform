package PVClasses;

import SmartContracts.PV.PVContract;
import SmartContracts.PV.PVPenaltyContract;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLCodec;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.rmi.MarshalException;

public class PVInteractWithContracts extends TickerBehaviour {

    private DataStore dataStore;

    private Web3j web3j;
    private ContractGasProvider contractGasProvider;

    // Открытые ключи PV генераторов
    private String PUBLIC_KEY_PV1;
    private String PUBLIC_KEY_PV2;
    private String PUBLIC_KEY_PV3;

    private Credentials credentialsPV1;
    private Credentials credentialsPV2;
    private Credentials credentialsPV3;

    // Адреса основного контракта PV
    private String deployedPVContractAddress;
    private String deployedPVContractAddress2;
    private String deployedPVContractAddress3;

    private String deployedPVPenaltyContractAddress;
    private String deployedPVPenaltyContractAddress2;
    private String deployedPVPenaltyContractAddress3;

    // Экземпляры контрактов
    private PVContract pvContract1;
    private PVContract pvContract2;
    private PVContract pvContract3;

    private PVPenaltyContract pvPenaltyContract1;
    private PVPenaltyContract pvPenaltyContract2;
    private PVPenaltyContract pvPenaltyContract3;

    // Флаги регистрации в контрактах
    private boolean flagRegister1;
    private boolean flagRegister2;
    private boolean flagRegister3;

    // Флаги регистрации ставок
    private boolean flagRegisterBet1;
    private boolean flagRegisterBet2;
    private boolean flagRegisterBet3;

    // Оплата за 1кВт энергии, покупаемой у накопителя
    private int paymentEnergyFromStorage = 60000;

    public PVInteractWithContracts(Agent a, long period, DataStore dataStore, Web3j web3j,
                                   ContractGasProvider contractGasProvider,
                                   String PUBLIC_KEY_PV1, String PUBLIC_KEY_PV2, String PUBLIC_KEY_PV3,
                                   Credentials credentialsPV1, Credentials credentialsPV2, Credentials credentialsPV3,
                                   boolean flagRegister1, boolean flagRegister2, boolean flagRegister3,
                                   boolean flagRegisterBet1, boolean flagRegisterBet2, boolean flagRegisterBet3) {
        super(a, period);

        this.dataStore = dataStore;
        setDataStore(dataStore);

        this.web3j = web3j;
        this.contractGasProvider = contractGasProvider;

        this.PUBLIC_KEY_PV1 = PUBLIC_KEY_PV1;
        this.PUBLIC_KEY_PV2 = PUBLIC_KEY_PV2;
        this.PUBLIC_KEY_PV3 = PUBLIC_KEY_PV3;

        this.credentialsPV1 = credentialsPV1;
        this.credentialsPV2 = credentialsPV2;
        this.credentialsPV3 = credentialsPV3;

        this.flagRegister1 = flagRegister1;
        this.flagRegister2 = flagRegister2;
        this.flagRegister3 = flagRegister3;

        this.flagRegisterBet1 = flagRegisterBet1;
        this.flagRegisterBet2 = flagRegisterBet2;
        this.flagRegisterBet3 = flagRegisterBet3;

    }

    @Override
    protected void onTick() {

        //System.out.println(myAgent.getLocalName() + " datastorerererer================ " + getDataStore());

        if (myAgent.getLocalName().equals("PV1")) {

            // Создание экземпляров и регистрация в контрактах
            registerInContract(myAgent, PUBLIC_KEY_PV1, credentialsPV1
            );

            // Регистрация ставки победителя
            registerBetForPV(myAgent, PUBLIC_KEY_PV1);

            // Оплата за перезакупку энергии у Storage1
            rePurchaseStorageEnergyForPV(myAgent);

            // Получение сообщение о выплате комиссии и оплата
            // комиссии счетчику потребителя
            //receiveMsgAboutCommission(myAgent,credentialsPV1,"PVCounter1");

        } else if (myAgent.getLocalName().equals("PV2")) {

            // Создание экземпляров и регистрация в контрактах
            registerInContract(myAgent, PUBLIC_KEY_PV2, credentialsPV2
            );

            // Регистрация ставки победителя
            registerBetForPV(myAgent, PUBLIC_KEY_PV2);

            // Оплата за перезакупку энергии у Storage2
            rePurchaseStorageEnergyForPV(myAgent);

            // Получение сообщение о выплате комиссии и оплата
            // комиссии счетчику потребителя
            //receiveMsgAboutCommission(myAgent,credentialsPV2,"PVCounter2");

        } else {

            // Создание экземпляров и регистрация в контрактах
            registerInContract(myAgent, PUBLIC_KEY_PV3, credentialsPV3
            );

            // Регистрация ставки победителя
            registerBetForPV(myAgent, PUBLIC_KEY_PV3);

            // Оплата за перезакупку энергии у Storage3
            rePurchaseStorageEnergyForPV(myAgent);

            // Получение сообщение о выплате комиссии и оплата
            // комиссии счетчику потребителя
            //receiveMsgAboutCommission(myAgent,credentialsPV3,"PVCounter3");

        }

    }



    // TODO: Метод создания экземпляров контрактов и регистрации в контрактах
    private void registerInContract(Agent pv, String publicKey, Credentials credentials
                                    ){

        // Проверка, что в datastore значения != null
        if (getDataStore().get(pv.getLocalName() + "address")!=null &&
        getDataStore().get(pv.getLocalName() + "penaltyAddress")!=null){

            String deployedAddress = String.valueOf(getDataStore().get(pv.getLocalName() + "address"));
            String penaltyAddress = String.valueOf(getDataStore().get(pv.getLocalName() + "penaltyAddress"));


            try {
            // Создание экземпляров контрактов
            if (pv.getLocalName().equals("PV1") && flagRegister1){

                pvContract1 = PVContract.load(deployedAddress, web3j, credentials, contractGasProvider);
                System.out.println(pv.getLocalName() + " with public key " + publicKey + " create pvContract sample " +
                        pvContract1.getContractAddress());
                pvPenaltyContract1 = PVPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);
                System.out.println(pv.getLocalName() + " with public key " + publicKey + " create pvPenaltyContract sample " +
                        pvPenaltyContract1.getContractAddress());

                // Регисттрация в контрактах

                System.out.println("======== " + pv.getLocalName() + " REGISTER IN CONTRACTS ===========");
                    if (pvContract1.registrationPV(publicKey).send().isStatusOK()){
                        System.out.println(pv.getLocalName() + " register in pv contract " +
                                pvContract1.getContractAddress());
                    }

                    if (pvPenaltyContract1.registrationPV(publicKey).send().isStatusOK()){
                        System.out.println(pv.getLocalName() + " register in pvPenaltyContract " +
                                pvPenaltyContract1.getContractAddress());
                    }

                    flagRegister1 = false;

            } else if (pv.getLocalName().equals("PV2") && flagRegister2){

                pvContract2 = PVContract.load(deployedAddress, web3j, credentials, contractGasProvider);
                System.out.println(pv.getLocalName() + " with public key " + publicKey + " create pvContract2 sample " +
                        pvContract2.getContractAddress());

                pvPenaltyContract2 = PVPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);
                System.out.println(pv.getLocalName() + " with public key " + publicKey + " create pvPenaltyContract2 sample " +
                        pvPenaltyContract2.getContractAddress());

                System.out.println("======== " + pv.getLocalName() + " REGISTER IN CONTRACTS ===========");
                if (pvContract2.registrationPV(publicKey).send().isStatusOK()){
                    System.out.println(pv.getLocalName() + " register in pv contract2 " +
                            pvContract2.getContractAddress());
                }

                if (pvPenaltyContract2.registrationPV(publicKey).send().isStatusOK()){
                    System.out.println(pv.getLocalName() + " register in pvPenaltyContract2 " +
                            pvPenaltyContract2.getContractAddress());
                }

                flagRegister2 = false;

            } else if (pv.getLocalName().equals("PV3") && flagRegister3){

                pvContract3 = PVContract.load(deployedAddress, web3j, credentials, contractGasProvider);
                System.out.println(pv.getLocalName() + " with public key " + publicKey + " create pvContract3 sample " +
                        pvContract3.getContractAddress());

                pvPenaltyContract3 = PVPenaltyContract.load(penaltyAddress, web3j, credentials, contractGasProvider);
                System.out.println(pv.getLocalName() + " with public key " + publicKey + " create pvPenaltyContract3 sample " +
                        pvPenaltyContract3.getContractAddress());

                System.out.println("======== " + pv.getLocalName() + " REGISTER IN CONTRACTS ===========");
                if (pvContract3.registrationPV(publicKey).send().isStatusOK()){
                    System.out.println(pv.getLocalName() + " register in pv contract3 " +
                            pvContract3.getContractAddress());
                }

                if (pvPenaltyContract3.registrationPV(publicKey).send().isStatusOK()){
                    System.out.println(pv.getLocalName() + " register in pvPenaltyContract3 " +
                            pvPenaltyContract3.getContractAddress());
                }

                flagRegister3 = false;
            }

            } catch (Exception e) {
                System.out.println();
            }
        }
    }


    // TODO: Метод регистрации ставки победителя для PV
    private void registerBetForPV(Agent pv, String publicKey) {

        // После регистрации PV вписывает в контракт ставку победителя
        // Используем флаг регистрации для регистрации ставки победителя
        // ( если флаг регистрации = false, то берем !false и программа зайдет в цикл
        // регистрации ставки
        if (getDataStore().get(pv.getLocalName() + "bet")!=null) {

            double bet = Double.parseDouble(String.valueOf(getDataStore().get(pv.getLocalName() + "bet")));
            try {

                // Проверка, если PV1
            if (pv.getLocalName().equals("PV1")) {


                if (pvContract1!=null && flagRegisterBet1) {

                    if (pvContract1.registerBetForPV(BigInteger.valueOf((int) bet)).send().isStatusOK()) {
                        System.out.println(pv.getLocalName() + " with publicKey : " + publicKey +
                                " register bet " + bet + " in pvContract1 " + pvContract1.getContractAddress());
                    }

                    flagRegisterBet1 = false;
                }

            }


            // Проверка, если PV2
            if (pv.getLocalName().equals("PV2")){

                if (pvContract2!=null && flagRegisterBet2) {


                    if (pvContract2.registerBetForPV(BigInteger.valueOf((int) bet)).send().isStatusOK()) {
                        System.out.println(pv.getLocalName() + " with publicKey : " + publicKey +
                                " register bet " + bet + " in pvContract2 " + pvContract2.getContractAddress());
                    }

                    flagRegisterBet2 = false;

                }

//                flagRegisterBet2 = false;
            }

            // Проверка, если PV3
            if (pv.getLocalName().equals("PV3")){

                if (pvContract3!=null && flagRegisterBet3) {

                    if (pvContract3.registerBetForPV(BigInteger.valueOf((int) bet)).send().isStatusOK()) {
                        System.out.println(pv.getLocalName() + " with publicKey : " + publicKey +
                                " register bet " + bet + " in pvContract3 " + pvContract3.getContractAddress());
                    }

                    flagRegisterBet3 = false;

                }

//                flagRegisterBet3 = false;
            }


            } catch (Exception e) {
                System.out.println();
            }

        }
    }


    // TODO: PV принимает кол-во энергии от своего счетчика, которое необходимо
    //  купить у накопителя, чтобы выполнились условия поставки энергии
    private void rePurchaseStorageEnergyForPV(Agent pv){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("RePurchaseStorageEnergyPV");
        ACLMessage msg = pv.receive(messageTemplate);

        if (msg!=null){
            // После принятия сообщения от PVCounter
            // PV оплачивает энергию накопителю
            int needEnergy = (int)Double.parseDouble(msg.getContent());

            try {
            if (pv.getLocalName().equals("PV1")) {
                System.out.println(pv.getLocalName() + " receive " + needEnergy +
                        " for repurchase from Storage1" + " from " +
                        msg.getSender().getLocalName());

                int payment = needEnergy * paymentEnergyFromStorage;
                System.out.println(pv.getLocalName() + " need to pay " +
                        payment);

                if (pvContract1!=null){
                    if (pvContract1.rePurchaseStorageEnergyForPV(BigInteger.valueOf(needEnergy),
                            BigInteger.valueOf(payment)).send().isStatusOK()){
                        System.out.println(pv.getLocalName() + " paid for repurchase energy " +
                                " from Storage1");

                    }

                    // После совершения оплаты накопитель отписывается своему счетчику, что оплатил
                    // энергию to Storage1
                    sendMsg(pv,"PaymentToStorageCompletedPV",
                            "PVCounter1","PaymentToStorageCompletedPV");
                }
            }

            if (pv.getLocalName().equals("PV2")) {
                System.out.println(pv.getLocalName() + " receive " + needEnergy +
                        " for repurchase from Storage2" + " from " +
                        msg.getSender().getLocalName());

                int payment = needEnergy * paymentEnergyFromStorage;
                System.out.println(pv.getLocalName() + " need to pay " +
                        payment);

                if (pvContract2!=null){
                    if (pvContract2.rePurchaseStorageEnergyForPV(BigInteger.valueOf(needEnergy),
                            BigInteger.valueOf(payment)).send().isStatusOK()){
                        System.out.println(pv.getLocalName() + " paid for repurchase energy " +
                                " from Storage2");
                    }

                    // После совершения оплаты накопитель отписывается своему счетчику, что оплатил
                    // энергию to Storage2
                    sendMsg(pv,"PaymentToStorageCompletedPV",
                            "PVCounter2","PaymentToStorageCompletedPV");
                }
            }

            if (pv.getLocalName().equals("PV3")){
                System.out.println(pv.getLocalName() + " receive " + needEnergy +
                        " for repurchase from Storage3" + " from " +
                        msg.getSender().getLocalName());

                int payment = needEnergy * paymentEnergyFromStorage;
                System.out.println(pv.getLocalName() + " need to pay " +
                        payment);

                if (pvContract3!=null){
                    if (pvContract3.rePurchaseStorageEnergyForPV(BigInteger.valueOf(needEnergy),
                            BigInteger.valueOf(payment)).send().isStatusOK()){
                        System.out.println(pv.getLocalName() + " paid for repurchase energy " +
                                " from Storage3");
                    }

                    // После совершения оплаты накопитель отписывается своему счетчику, что оплатил
                    // энергию to Storage3
                    sendMsg(pv,"PaymentToStorageCompletedPV",
                            "PVCounter3","PaymentToStorageCompletedPV");
                }
            }

            } catch (Exception e) {
                System.out.println();
            }


        }else {
            block();
        }
    }


    // TODO: PV принимает сообщение от своего счетчика о выплате
    //  комиссии и оплачивает эту комиссию. Затем PV
    //  отписывается своему счетчику, что оплатил комиссию
    private void receiveMsgAboutCommission(Agent pv, Credentials credentials,
                                           String pvCounterName){

        MessageTemplate messageTemplate = MessageTemplate.MatchProtocol("NeedToPayCommission");
        ACLMessage msg = pv.receive(messageTemplate);

        if (msg!=null){
            System.out.println(pv.getLocalName() +
                    " receive " + msg.getContent() +
                    " from " + msg.getSender().getLocalName());

            // PV достает из datastore адреса своего развернутого контракта
            try {

            if (getDataStore().get(pv.getLocalName() + "address")!=null){
                String deployedAddress = String.valueOf(
                        getDataStore().get(pv.getLocalName() + "address")
                );

                // PV создает экземпляр контракта в этом методе
                PVContract pvContract = PVContract.load(deployedAddress,web3j,
                        credentials,contractGasProvider);
                System.out.println(pv.getLocalName() +
                        " create pvContract sample " + pvContract.getContractAddress());

                // После создания экземпляра контракта
                // в этом методе PV выполняет транзакцию
                // по оплате комиссии своему счетчику

                // PV смотрит сколько нужно оплатить

                    int commissionToPVCounter = pvContract.viewComissionPVCounter().send().intValue();
                    System.out.println(pv.getLocalName() + " need to pay " +
                            commissionToPVCounter + " to " +
                            pvCounterName);

                if (pvContract.commisionToPVCounter(BigInteger.valueOf(commissionToPVCounter))
                    .send().isStatusOK()){
                    System.out.println(pv.getLocalName() + " paid " +
                            "commission to " + pvCounterName);

                    // TODO: После оплаты комиссии PV отписывается
                    //  счетчику PVCounter, что оплатил ему комиссию
                    sendMsg(pv,"CommissionToPVCounterPaid",
                            pvCounterName,"CommissionToPVCounterPaid");
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
