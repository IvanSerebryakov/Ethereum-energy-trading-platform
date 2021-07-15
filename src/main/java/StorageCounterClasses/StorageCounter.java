package StorageCounterClasses;

import jade.core.Agent;
import jade.core.behaviours.DataStore;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

public class StorageCounter extends Agent {

    private Web3j web3j = Web3j.build(new HttpService("HTTP://127.0.0.1:7545"));

    private final BigInteger GAS_PRICE = DefaultGasProvider.GAS_PRICE;
    private final BigInteger GAS_LIMIT = DefaultGasProvider.GAS_LIMIT;

    private final String PUBLIC_KEY_StorageCounter1 = "0xAE2A71F23E78aF8F059c8Eaf7D3a0B074B34367c";
    private final String PUBLIC_KEY_StorageCounter2 = "0xad1fd8C1bE7C1FbFF04efc987fE8BCDcfE118565";
    private final String PUBLIC_KEY_StorageCounter3 = "0x6c34F306E93EaAcCf7bA93c460c48a7341C64166";

    private final String PRIVATE_KEY_StorageCounter1 = "d8a999719d47130332d75c99c23ca591ea7bf72f432d4571e8d37688f5b136a3";
    private final String PRIVATE_KEY_StorageCounter2 = "28fc2ae67dbe8cacc1d6685ab989ae37f7f017535e0a10fb71c395882fa09ef7";
    private final String PRIVATE_KEY_StorageCounter3 = "ac6b273380a81e1caff4b8d02d81d055bcf996f43441ec0d45fc8ec63cb38b71";

    private Credentials credentialsStorageCounter1 = Credentials.create(PRIVATE_KEY_StorageCounter1);
    private Credentials credentialsStorageCounter2 = Credentials.create(PRIVATE_KEY_StorageCounter2);
    private Credentials credentialsStorageCounter3 = Credentials.create(PRIVATE_KEY_StorageCounter3);

    private DataStore dataStore = new DataStore();

    // Флаги ррегистрации в основных контрактах
    private boolean flagRegister1 = true;
    private boolean flagRegister2 =true;
    private boolean flagRegister3 = true;

    // Флаги регистрации в штрафных контарутах
    private boolean flagRegisterFine1 = true;
    private boolean flagRegisterFine2 = true;
    private boolean flagRegisterFine3 = true;

    // Флаги ррегистрации в основных Wind контрактах
    private boolean flagRegisterWind1 = true;
    private boolean flagRegisterWind2 = true;
    private boolean flagRegisterWind3 = true;

    // Флаги регистрации в штрафных WInd контарутах
    private boolean flagRegisterFineWind1 = true;
    private boolean flagRegisterFineWind2 = true;
    private boolean flagRegisterFineWind3 = true;

    // Флаги регистрации в сервисных контрактах
    private boolean flagRegisterServiceContract1 = true;
    private boolean flagRegisterServiceContract2 = true;
    private boolean flagRegisterServiceContract3 = true;

    // TODO: Временные счетчики времени (закомментить после принятия времени из Матлаба)
    private int pv1Counter = 0;
    private int pv2Counter = 0;
    private int pv3Counter = 0;

    private int n1 = 1;
    private int n2 = 1;
    private int n3 = 1;

    // TODO: Счетчики времени (потом закомментить)
    private int counterWind1 = 0;
    private int counterWind2 = 0;
    private int counterWind3 = 0;

    private int counterTime = 0;

    // Счетчики суток
    private int windDay1 = 1;
    private int windDay2 = 1;
    private int windDay3 = 1;


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

        addBehaviour(new StorageCounterInteractWithContracts(this, 7000, web3j, contractGasProvider,
                PUBLIC_KEY_StorageCounter1, PUBLIC_KEY_StorageCounter2, PUBLIC_KEY_StorageCounter3,
                credentialsStorageCounter1, credentialsStorageCounter2, credentialsStorageCounter3,
                dataStore, flagRegister1, flagRegister2, flagRegister3,
                flagRegisterFine1, flagRegisterFine2, flagRegisterFine3,
                flagRegisterServiceContract1, flagRegisterServiceContract2, flagRegisterServiceContract3,
                pv1Counter, pv2Counter, pv3Counter, n1, n2, n3, flagRegisterWind1, flagRegisterWind2, flagRegisterWind3,
                flagRegisterFineWind1, flagRegisterFineWind2, flagRegisterFineWind3,
                counterWind1, counterWind2, counterWind3,
                windDay1, windDay2, windDay3, counterTime));

    }
}
