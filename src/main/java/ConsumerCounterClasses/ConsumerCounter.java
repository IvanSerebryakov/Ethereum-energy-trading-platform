package ConsumerCounterClasses;

import ConnectionUDP.UDPClass;
import SmartContracts.PV.PVContract;
import SmartContracts.PV.PVPenaltyContract;
import SmartContracts.Service.ServiceContract;
import SmartContracts.Wind.WindContract;
import SmartContracts.Wind.WindPenaltyContract;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class ConsumerCounter extends Agent {

    private DataStore dataStore = new DataStore();

    private Web3j web3j = Web3j.build(new HttpService("HTTP://127.0.0.1:7545"));

    private final BigInteger GAS_PRICE = DefaultGasProvider.GAS_PRICE;
    private final BigInteger GAS_LIMIT = DefaultGasProvider.GAS_LIMIT;

    private final String PUBLIC_KEY_ConsumerCounter1 = "0x440FBe4A17d9463896510E5C9211A51d9C7FA7c3";
    private final String PUBLIC_KEY_ConsumerCounter2 = "0x8BaBcC5d4F2F8EA62C77BD546b0f482d0688Cc87";
    private final String PUBLIC_KEY_ConsumerCounter3 = "0x39f5e9BFD1eD4837bDB81915fFc2eEDe1F46E8Ed";

    private final String PRIVATE_KEY_ConsumerCounter1 = "7c0c426dd3a99aa8b84ec0cd944d48ebc378c29037eb553e55e4dd5faa53fae3";
    private final String PRIVATE_KEY_ConsumerCounter2 = "9eddd0128713dd15e035937dbbc2c1e4bc2592e8ac9701de7f91bc60769cfbae";
    private final String PRIVATE_KEY_ConsumerCounter3 = "0d85d6954524bb2f936dc3c0b0d96438e7ee95004a2a1b98480cfa56b6ea7772";

    private Credentials credentialsConsumerCounter1 = Credentials.create(PRIVATE_KEY_ConsumerCounter1);
    private Credentials credentialsConsumerCounter2 = Credentials.create(PRIVATE_KEY_ConsumerCounter2);
    private Credentials credentialsConsumerCounter3 = Credentials.create(PRIVATE_KEY_ConsumerCounter3);

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

    private UDPClass udpClass = new UDPClass();

    // Флаги регитсрации
    private boolean isRegisteredWind1 = true;
    private boolean isRegisteredWind2 = true;
    private boolean isRegisteredWind3 = true;

    private boolean isRegisteredPV1 = true;
    private boolean isRegisteredPV2 = true;
    private boolean isRegisteredPV3 = true;

    // Флаги установки нового соединения
    private boolean flagConnectionWind1 = true;
    private boolean flagConnectionWind2 = true;
    private boolean flagConnectionWind3 = true;

    private boolean flagConnectionPV1 = true;
    private boolean flagConnectionPV2 = true;
    private boolean flagConnectionPV3 = true;

    // Флаги для создания экземпляров коонтрактов
    private boolean isCreating1 = true;
    private boolean isCreating2 = true;
    private boolean isCreating3 = true;

    private DatagramSocket datagramSocketPV1;
    private DatagramSocket datagramSocketPV2;
    private DatagramSocket datagramSocketPV3;

    private int iterWind1 = 0;
    private int iterWind2 = 0;
    private int iterWind3 = 0;

    private int nWind1 = 1;
    private int nWind2 = 1;
    private int nWind3 = 1;

    private int iterPV1 = 0;
    private int iterPV2 = 0;
    private int iterPV3 = 0;

    private int nPV1 = 1;
    private int nPV2 = 1;
    private int nPV3 = 1;

    private int counterTime = 0;
    private int counterTime2 = 0;

    // Флаги регистрации в сервисных контрактах
    private boolean flagRegister1 = true;
    private boolean flagRegister2 = true;
    private boolean flagRegister3 = true;

    private int counterTime3 = 0;

    // Массив зарегистрированных в сервисных контрактах
    private List<String> registerList = new ArrayList<>();


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

        addBehaviour(new ConsumerCounterInteractionWithContracts(this, 7000,
                dataStore, web3j, contractGasProvider, PUBLIC_KEY_ConsumerCounter1,
                PUBLIC_KEY_ConsumerCounter2, PUBLIC_KEY_ConsumerCounter3,
                credentialsConsumerCounter1, credentialsConsumerCounter2, credentialsConsumerCounter3,
                windContract1, windContract2, windContract3, windPenaltyContract1, windPenaltyContract2,
                windPenaltyContract3, pvContract1, pvContract2, pvContract3,
                pvPenaltyContract1, pvPenaltyContract2, pvPenaltyContract3, udpClass,
                isRegisteredWind1, isRegisteredWind2, isRegisteredWind3, isRegisteredPV1,
                isRegisteredPV2, isRegisteredPV3, flagConnectionWind1, flagConnectionWind2, flagConnectionWind3,
                flagConnectionPV1, flagConnectionPV2, flagConnectionPV3));

        addBehaviour(new ConCounterInteractWithPVContracts(this, 7000,
                dataStore, web3j, contractGasProvider, PUBLIC_KEY_ConsumerCounter1,
                PUBLIC_KEY_ConsumerCounter2, PUBLIC_KEY_ConsumerCounter3,
                credentialsConsumerCounter1, credentialsConsumerCounter2, credentialsConsumerCounter3,
                pvContract1, pvContract2, pvContract3,
                pvPenaltyContract1, pvPenaltyContract2, pvPenaltyContract3,
                udpClass, isRegisteredPV1, isRegisteredPV2, isRegisteredPV3,
                flagConnectionPV1, flagConnectionPV2, flagConnectionPV3,
                datagramSocketPV1, datagramSocketPV2, datagramSocketPV3,
                isCreating1, isCreating2, isCreating3,
                iterPV1, iterPV2, iterPV3, nWind1, nWind2, nWind3, counterTime2));

        addBehaviour(new ConCounterInteractWithWindContracts(this, 7000, dataStore, web3j,
                contractGasProvider,PUBLIC_KEY_ConsumerCounter1,
                PUBLIC_KEY_ConsumerCounter2, PUBLIC_KEY_ConsumerCounter3,
                credentialsConsumerCounter1, credentialsConsumerCounter2,
                credentialsConsumerCounter3,
                windContract1, windContract2, windContract3,
                windPenaltyContract1, windPenaltyContract2,
                windPenaltyContract3,
                udpClass, isRegisteredPV1, isRegisteredPV2,
        isRegisteredPV3,  flagConnectionPV1, flagConnectionPV2,
        flagConnectionPV3, datagramSocketPV1, datagramSocketPV2,
                datagramSocketPV3, isCreating1, isCreating2,
        isCreating3, iterWind1, iterWind2, iterWind3, nWind1, nWind2, nWind3, counterTime));

        addBehaviour(new ConCounterInteractWithServiceContract(this, 7000,
                dataStore, web3j,contractGasProvider,PUBLIC_KEY_ConsumerCounter1, PUBLIC_KEY_ConsumerCounter2, PUBLIC_KEY_ConsumerCounter3,
                credentialsConsumerCounter1, credentialsConsumerCounter2, credentialsConsumerCounter3,
                flagRegister1, flagRegister2, flagRegister3, counterTime3,
                registerList));

    }
}
