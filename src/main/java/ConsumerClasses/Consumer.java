package ConsumerClasses;

import ConnectionUDP.UDPClass;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

public class Consumer extends Agent {

    private boolean flagBet = true;
    private boolean flagPV = true;

    private DataStore dataStore = new DataStore();
    private UDPClass udpClass = new UDPClass();

    private boolean flag1Connection = true;
    private boolean flag2Connection = true;
    private boolean flag3Connection = true;

    private Web3j web3j = Web3j.build(new HttpService("HTTP://127.0.0.1:7545"));

    private final BigInteger GAS_PRICE = DefaultGasProvider.GAS_PRICE;
    private final BigInteger GAS_LIMIT = DefaultGasProvider.GAS_LIMIT;

    private final String PUBLIC_KEY_Consumer1 = "0xE0F6a46B9ff1e134C38fdEd6e27dD5000c994126";
    private final String PUBLIC_KEY_Consumer2 = "0x674D1A5b0Cb7F5c077fF2fDdB8C0756AC79DC2Ff";
    private final String PUBLIC_KEY_Consumer3 = "0x88f980D2a3DDa7a25868534808CAFC5fCD54887E";

    private final String PRIVATE_KEY_Consumer1 = "ceb5febb5990a3379461cb3e92dd065319a32ba01fd9dcf67543fda759195537";
    private final String PRIVATE_KEY_Consumer2 = "b14dd7db752dc96f6b405e5bfec724b32ac196e524530f99022064e39dcb7b10";
    private final String PRIVATE_KEY_Consumer3 = "b49744e4973fe19fd6e0bfc93fdcaab4f8e042d4d6eaf82e4cc1d1ea62ff9c0e";

    private Credentials credentialsConsumer1 = Credentials.create(PRIVATE_KEY_Consumer1);
    private Credentials credentialsConsumer2 = Credentials.create(PRIVATE_KEY_Consumer2);
    private Credentials credentialsConsumer3 = Credentials.create(PRIVATE_KEY_Consumer3);

    // Флаги регитсрации в pv контрактах
    private boolean isRegistered1 = true;
    private boolean isRegistered2 = true;
    private boolean isRegistered3 = true;

    // Флаги регистрации в wind контрактах
    private boolean isRegisteredWind1 = true;
    private boolean isRegisteredWind2 = true;
    private boolean isRegisteredWind3 = true;

    private int iter1 = 0;
    private int iter2 = 0;
    private int iter3 = 0;

    private String auctionCompleted1;
    private String auctionCompleted2;
    private String auctionCompleted3;

    // Флаги регистрации в сервисных контрактах
    private boolean flagRegisterService1 = true;
    private boolean flagRegisterService2 = true;
    private boolean flagRegisterService3 = true;


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
        addBehaviour(new SearchingAuctionService(this, 7000));
        addBehaviour(new ConsumerAuctionBehaviour(this, 7000, flagBet, flagPV, dataStore,
                auctionCompleted1, auctionCompleted2, auctionCompleted3));
        addBehaviour(new ConsumerInteractionWithContracts(this, 7000, dataStore, udpClass,
                flag1Connection, flag2Connection, flag3Connection,
                web3j, contractGasProvider, PUBLIC_KEY_Consumer1, PUBLIC_KEY_Consumer2, PUBLIC_KEY_Consumer3,
                credentialsConsumer1, credentialsConsumer2, credentialsConsumer3
                ));

        addBehaviour(new ConsumerWithPVContracts(this,7000, web3j, contractGasProvider,
                PUBLIC_KEY_Consumer1, PUBLIC_KEY_Consumer2, PUBLIC_KEY_Consumer3,
                credentialsConsumer1, credentialsConsumer2,
                credentialsConsumer3, dataStore, isRegistered1, isRegistered2, isRegistered3));

        addBehaviour(new ConsumerWithWindContracts(this, 7000, web3j, contractGasProvider,
                PUBLIC_KEY_Consumer1, PUBLIC_KEY_Consumer2, PUBLIC_KEY_Consumer3,
                credentialsConsumer1,credentialsConsumer2,credentialsConsumer3,
                dataStore, isRegisteredWind1, isRegisteredWind2, isRegisteredWind3, iter1, iter2, iter3));

        addBehaviour(new ConsumerWithServiceContracts(this, 7000, dataStore,
                web3j, contractGasProvider, PUBLIC_KEY_Consumer1,
                PUBLIC_KEY_Consumer2, PUBLIC_KEY_Consumer3,
                credentialsConsumer1, credentialsConsumer2, credentialsConsumer3,
                flagRegisterService1, flagRegisterService2, flagRegisterService3));
    }
}
