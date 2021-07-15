package PVCounterClasses;

import MQTTConnection.MQTTSubscriber;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

public class PVCounter extends Agent {

    private Web3j web3j = Web3j.build(new HttpService("HTTP://127.0.0.1:7545"));

    private final BigInteger GAS_PRICE = DefaultGasProvider.GAS_PRICE;
    private final BigInteger GAS_LIMIT = DefaultGasProvider.GAS_LIMIT;

    private final String PUBLIC_KEY_PVCounter1 = "0xff76909BD18bE061D6bb7147a2E45e5272BB628E";
    private final String PUBLIC_KEY_PVCounter2 = "0xC8C254FB192db59cF6ADeE02FA7dA03435973C57";
    private final String PUBLIC_KEY_PVCounter3 = "0xCe69061f4D027C865095D932a15F7afC47040e05";

    private final String PRIVATE_KEY_PVCounter1 = "a2810a54308e1b4d1b7005ad4078c7322ebe405f23913001fd6425d7e46c1e3c";
    private final String PRIVATE_KEY_PVCounter2 = "c0dbc2e83b8df6964a9bb37f9110eb0f21e48009f597de37a732b83988138ca5";
    private final String PRIVATE_KEY_PVCounter3 = "85dc751f70fe3a6e3220ab390b728ce9a7c56ee3ab5ef80d1622200256b00b8b";

    private Credentials credentialsPVCounter1 = Credentials.create(PRIVATE_KEY_PVCounter1);
    private Credentials credentialsPVCounter2 = Credentials.create(PRIVATE_KEY_PVCounter2);
    private Credentials credentialsPVCounter3 = Credentials.create(PRIVATE_KEY_PVCounter3);

    private DataStore dataStore = new DataStore();

    // Флаги регистрации в контрактах
    private boolean flagRegister1 = true;
    private boolean flagRegister2 = true;
    private boolean flagRegister3 = true;

    // Флаги регистрации в штрафных контрактах
    private boolean flagRegisterPenalty1 = true;
    private boolean flagRegisterPenalty2 = true;
    private boolean flagRegisterPenalty3 = true;

    // Счетчики времени (потом закомментить после принятия данных по MQTT о времени)
    private int counter1 = 0;
    private int counter2 = 0;
    private int counter3 = 0;

    // Отсчеты суток
    private int n1 = 1;
    private int n2 = 1;
    private int n3 = 1;

    // Счетчик времени
    private int counterTime = 0;

    private int day = 0;

    private MQTTSubscriber mqttSubscriber = new MQTTSubscriber();

    @Override
    protected void setup() {

        ContractGasProvider contractGasProvider = new ContractGasProvider() {
            @Override
            public BigInteger getGasPrice(String s) {
                return GAS_PRICE;
            }

            @Override
            public BigInteger getGasPrice() {
                return GAS_PRICE;
            }

            @Override
            public BigInteger getGasLimit(String s) {
                return GAS_LIMIT;
            }

            @Override
            public BigInteger getGasLimit() {
                return GAS_LIMIT;
            }
        };


        addBehaviour(new PVCounterInteractWithContracts(this, 7000, web3j, contractGasProvider,
                PUBLIC_KEY_PVCounter1, PUBLIC_KEY_PVCounter2, PUBLIC_KEY_PVCounter3,
                credentialsPVCounter1, credentialsPVCounter2, credentialsPVCounter3, dataStore,
                flagRegister1, flagRegister2, flagRegister3,
                flagRegisterPenalty1, flagRegisterPenalty2, flagRegisterPenalty3, counter1, counter2, counter3,
                n1, n2, n3, counterTime, day, mqttSubscriber));

    }
}
