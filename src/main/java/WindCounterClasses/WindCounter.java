package WindCounterClasses;

import jade.core.Agent;
import jade.core.behaviours.DataStore;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

public class WindCounter extends Agent {

    // Соединение с Ganache
    private Web3j web3j = Web3j.build(new HttpService("HTTP://127.0.0.1:7545"));

    // Цена газа (стоимость одной единицы газа в вэй)
    private final BigInteger GAS_PRICE = DefaultGasProvider.GAS_PRICE;
    // Лимит газа (предел газа - максимальное количество газа, которое пользователь
    // готов заплатить за выполнение транзакции (по умолчанию 21000 вэй))
    private final BigInteger GAS_LIMIT = DefaultGasProvider.GAS_LIMIT;

    private final String PUBLIC_KEY_WindCounter1 = "0x252C663FCf180a37FC89B0e177F9238533B6209f";
    private final String PUBLIC_KEY_WindCounter2 = "0x7D69bc6D8b756CA2f33034ef3b05CB767b40b8f4";
    private final String PUBLIC_KEY_WindCounter3 = "0x031f0A92dab8f70b13e796075dBb973Bc5a7A879";

    private final String PRIVATE_KEY_WindCounter1 = "ca0f21194d3bcc053fe39584323e0c4297b4dc82682464b66b4dcd70f442506a";
    private final String PRIVATE_KEY_WindCounter2 = "640994d41c117068bbf17958fffcbb9767e47e84a41b65cc3cef192172b0007b";
    private final String PRIVATE_KEY_WindCounter3 = "8433f1b3ac1bee39d966d75cd61f9c6670b07dae589af2b0ee2e50679f22725a";

    private Credentials credentials1 = Credentials.create(PRIVATE_KEY_WindCounter1);
    private Credentials credentials2 = Credentials.create(PRIVATE_KEY_WindCounter2);
    private Credentials credentials3 = Credentials.create(PRIVATE_KEY_WindCounter3);

    private DataStore dataStore = new DataStore();

    // Флаги регистрации в основных контрактах
    private boolean flagRegister1 = true;
    private boolean flagRegister2 = true;
    private boolean flagRegister3 = true;

    // Флаги регистрации в штрафных контрактах
    private boolean flagRegisterPenalty1 = true;
    private boolean flagRegisterPenalty2 = true;
    private boolean flagRegisterPenalty3 = true;

    // Счетчик времени (потом закоментить)
    private int counterTime = 0;


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

        addBehaviour(new WindCounterInteractWithContracts(this, 7000, web3j, contractGasProvider,
                PUBLIC_KEY_WindCounter1, PUBLIC_KEY_WindCounter2, PUBLIC_KEY_WindCounter3, credentials1,
                credentials2, credentials3, dataStore, flagRegister1, flagRegister2, flagRegister3,
                flagRegisterPenalty1, flagRegisterPenalty2, flagRegisterPenalty3, counterTime));



    }
}
