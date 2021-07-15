package StorageClasses;

import jade.core.Agent;
import jade.core.behaviours.DataStore;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Storage extends Agent {

    // Соединение с Ganache
    private Web3j web3j = Web3j.build(new HttpService("HTTP://127.0.0.1:7545"));

    // Цена газа (стоимость одной единицы газа в вэй)
    private final BigInteger GAS_PRICE = DefaultGasProvider.GAS_PRICE;
    // Лимит газа (предел газа - максимальное количество газа, которое пользователь
    // готов заплатить за выполнение транзакции (по умолчанию 21000 вэй))
    private final BigInteger GAS_LIMIT = DefaultGasProvider.GAS_LIMIT;

    // Открытые ключи накопителей
    private final String PUBLIC_KEY_Storage1 = "0xd765790d36A72409d7d963b905167816030F73e2";
    private final String PUBLIC_KEY_Storage2 = "0x588B184FbbB16c279336B1AEF35F2cADb5831705";
    private final String PUBLIC_KEY_Storage3 = "0x7bEFCA21fA58a1D55Fe50c0045B1503e2C3814fB";

    // Закрытые ключи накопителей
    private final String PRIVATE_KEY_Storage1 = "a36c7f2f8da3dc754d04df18391e943d68778671c7b61b75f457cd26ed8d20ca";
    private final String PRIVATE_KEY_Storage2 = "937efbd82bd6d9b2b58e2d4edf17513fc202aebee3e553f39061e2d7976462ab";
    private final String PRIVATE_KEY_Storage3 = "83136195f8b9d611b68ab9e2b9a600350050b4b4261970cf64e4336ad19f241b";

    private Credentials credentialsStorage1 = Credentials.create(PRIVATE_KEY_Storage1);
    private Credentials credentialsStorage2 = Credentials.create(PRIVATE_KEY_Storage2);
    private Credentials credentialsStorage3 = Credentials.create(PRIVATE_KEY_Storage3);

    private DataStore dataStore = new DataStore();

    // Флаги регистрации в основных PV контрактах
    private boolean flagRegister1 = true;
    private boolean flagRegister2 = true;
    private boolean flagRegister3 = true;

    // Флаги регистрации в штрафных PV контрактах
    private boolean flagRegisterFine1 = true;
    private boolean flagRegisterFine2 = true;
    private boolean flagRegisterFine3 = true;

    // Флаги регистрации в основных Wind контрактах
    private boolean flagRegisterWind1 = true;
    private boolean flagRegisterWind2 = true;
    private boolean flagRegisterWind3 = true;

    // Флаги регистрации в штрафных Wind контрактах
    private boolean flagRegisterFineWind1 = true;
    private boolean flagRegisterFineWind2 = true;
    private boolean flagRegisterFineWind3 = true;

    // Флаги регистрации в сервисных коонтрактах
    private boolean flagRegisterService1 = true;
    private boolean flagRegisterService2 = true;
    private boolean flagRegisterService3 = true;

    // Массив для того, чтобы проверять, что
    // накопитель купил энергию у одного из своих генераторов
    // и если купил, то пока не получит всю энергию,
    // не может купить у другого
    private List<String> generatorsName = new ArrayList<>();


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

        addBehaviour(new StorageInteractWithContracts(this, 7000, web3j, contractGasProvider,
                PUBLIC_KEY_Storage1, PUBLIC_KEY_Storage2, PUBLIC_KEY_Storage3,
                credentialsStorage1, credentialsStorage2, credentialsStorage3, dataStore,
                flagRegister1, flagRegister2, flagRegister3,
                flagRegisterFine1, flagRegisterFine2, flagRegisterFine3,
                flagRegisterService1, flagRegisterService2, flagRegisterService3,
                generatorsName, flagRegisterWind1, flagRegisterWind2, flagRegisterWind3,
                flagRegisterFineWind1, flagRegisterFineWind2, flagRegisterFineWind3));

    }
}
