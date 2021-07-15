package WindClasses;

import ConnectionUDP.UDPClass;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

public class WindAgent extends Agent {

    private DataStore dataStore = new DataStore();

    // Соединение с Ganache
    private Web3j web3j = Web3j.build(new HttpService("HTTP://127.0.0.1:7545"));

    // Цена газа (стоимость одной единицы газа в вэй)
    private final BigInteger GAS_PRICE = DefaultGasProvider.GAS_PRICE;
    // Лимит газа (предел газа - максимальное количество газа, которое пользователь
    // готов заплатить за выполнение транзакции (по умолчанию 21000 вэй))
    private final BigInteger GAS_LIMIT = DefaultGasProvider.GAS_LIMIT;

    // Открытые ключи ветрогенераторов
    private final String PUBLIC_KEY_wind1 = "0x92111ADC01a7d7310c4318DCf94Ef904Fb550Fd9";
    private final String PUBLIC_KEY_wind2 = "0x4225824A65D436E522F744b87674015C6029f1F9";
    private final String PUBLIC_KEY_wind3 = "0xf8287a529E73D70b95b9AEd65658D449423910fD";

    // Закрытые ключи ветрогенераторов
    private final String PRIVATE_KEY_wind1 = "701c9e21d8fb58e02468487f4f1271813f567cee4225c668b49e16b37f564c88";
    private final String PRIVATE_KEY_wind2 = "8dd6529374c52a07f834b45535e072e06786989f0b96b583620579a7fdec20b3";
    private final String PRIVATE_KEY_wind3 = "7621dd0fd9aa773c396ee5be308bacd2e53adaae3b6d9ef495c86a6adf8092ae";

    private Credentials credentials1 = Credentials.create(PRIVATE_KEY_wind1);
    private Credentials credentials2 = Credentials.create(PRIVATE_KEY_wind2);
    private Credentials credentials3 = Credentials.create(PRIVATE_KEY_wind3);

    private String deployedWindContractAddress;
    private String deployedWindContractAddress2;
    private String deployedWindContractAddress3;

    // Адреса штрафных контрактов
    private String deployedWindPenaltyContractAddress;
    private String deployedWindPenaltyContractAddress2;
    private String deployedWindPenaltyContractAddress3;

    private UDPClass udpClass = new UDPClass();

    // Флаги соединений по UDp
    private boolean flagWind1 = true;
    private boolean flagWind2 = true;
    private boolean flagWind3 = true;

    // Флаги регистрации в контрактах
    private boolean flagRegisterContract1 = true;
    private boolean flagRegisterContract2 = true;
    private boolean flagRegisterContract3 = true;

    // Флаги регистрации ставок
    private boolean flagRegisterBet1 = true;
    private boolean flagRegisterBet2 = true;
    private boolean flagRegisterBet3 = true;

    // Флаг об окончании аукциона - запрет отправки цен
    private boolean auctionCompleted1 = true;
    private boolean auctionCompleted2 = true;
    private boolean auctionCompleted3 = true;

    // Перемменные для старта нового аукциона
    private String startNewAuction;
    private String startNewAuctionWind1;
    private String startNewAuctionWind2;
    private String startNewAuctionWind3;


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
        addBehaviour(new RegisterWindAuctionService(this, 7000));
        addBehaviour(new WindPriceList(this, 7000, dataStore, auctionCompleted1, auctionCompleted2, auctionCompleted3,
                startNewAuction, startNewAuctionWind1, startNewAuctionWind2, startNewAuctionWind3));
        addBehaviour(new WindDetermingWinner(this, 7000, dataStore,
                web3j, contractGasProvider,
                PUBLIC_KEY_wind1, PUBLIC_KEY_wind2, PUBLIC_KEY_wind3,
                credentials1, credentials2, credentials3,
                deployedWindContractAddress, deployedWindContractAddress2, deployedWindContractAddress3,
                deployedWindPenaltyContractAddress, deployedWindPenaltyContractAddress2, deployedWindPenaltyContractAddress3,
                udpClass, flagWind1, flagWind2, flagWind3));
        addBehaviour(new WindInteractWithContracts(this, 7000, web3j, contractGasProvider,
                PUBLIC_KEY_wind1, PUBLIC_KEY_wind2, PUBLIC_KEY_wind3, credentials1, credentials2, credentials3,
                dataStore, flagRegisterContract1, flagRegisterContract2, flagRegisterContract3,
                flagRegisterBet1, flagRegisterBet2, flagRegisterBet3));
    }
}
