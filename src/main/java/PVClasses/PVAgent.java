package PVClasses;

import ConnectionUDP.UDPClass;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

public class PVAgent extends Agent {

    private DataStore dataStore = new DataStore();

    private Web3j web3j = Web3j.build(new HttpService("HTTP://127.0.0.1:7545"));
    // стоимость за одну единицу газа
    private final BigInteger GAS_PRICE = DefaultGasProvider.GAS_PRICE;
    // плата комиссии за транзакцию
    private final BigInteger GAS_LIMIT = DefaultGasProvider.GAS_LIMIT;

    // Открытые ключи PV генераторов
    private final String PUBLIC_KEY_PV1 = "0x8e18205DaE134B86d3F69650569dd81020EC2D31";
    private final String PUBLIC_KEY_PV2 = "0xfFad767893d53AA75FEdcFD8d3f68AaF66E42B57";
    private final String PUBLIC_KEY_PV3 = "0xDafF1a7CF87e4A44D767f68710b2C190Dd03F7b4";

    // Закрытые ключи генераторов
    private final String PRIVATE_KEY_PV1 = "69a181f43aa2064cb685e6beccd3c26b95ff88491aba086403fc60a3a31122fb";
    private final String PRIVATE_KEY_PV2 = "6581659d49313f5b3e25e0d492fe0a3ab42882dceefd54fec5f78ac6de8ada2c";
    private final String PRIVATE_KEY_PV3 = "0ee0758feb2003bd2898c2356ba7633fdee121d3007ac1d6f47259d6e3fdd59d";

    private Credentials credentialsPV1 = Credentials.create(PRIVATE_KEY_PV1);
    private Credentials credentialsPV2 = Credentials.create(PRIVATE_KEY_PV2);
    private Credentials credentialsPV3 = Credentials.create(PRIVATE_KEY_PV3);

    // Адреса основного контракта PV
    private String deployedPVContractAddress;
    private String deployedPVContractAddress2;
    private String deployedPVContractAddress3;

    private String deployedPVPenaltyContractAddress;
    private String deployedPVPenaltyContractAddress2;
    private String deployedPVPenaltyContractAddress3;

    private UDPClass udpClass = new UDPClass();

    // Флаги соединений UDp
    private boolean flagPV1 = true;
    private boolean flagPV2 = true;
    private boolean flagPV3 = true;

    // Флаги регистрации в контрактах
    private boolean flagRegister1 = true;
    private boolean flagRegister2 = true;
    private boolean flagRegister3 = true;

    // Флаги регистрации ставок
    private boolean flagRegisterBet1 = true;
    private boolean flagRegisterBet2 = true;
    private boolean flagRegisterBet3 = true;


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
        addBehaviour(new RegisterPVAuctionService(this, 7000));
        addBehaviour(new PVPriceList(this, 7000, dataStore));
        addBehaviour(new PVDetermingWinner(this, 7000, dataStore, web3j, contractGasProvider,
                PUBLIC_KEY_PV1, PUBLIC_KEY_PV2, PUBLIC_KEY_PV3,
                credentialsPV1, credentialsPV2, credentialsPV3,
                deployedPVContractAddress, deployedPVContractAddress2, deployedPVContractAddress3,
                deployedPVPenaltyContractAddress, deployedPVPenaltyContractAddress2, deployedPVPenaltyContractAddress3,
                udpClass, flagPV1, flagPV2, flagPV3));
        addBehaviour(new PVInteractWithContracts(this, 7000, dataStore, web3j, contractGasProvider,
                PUBLIC_KEY_PV1, PUBLIC_KEY_PV2, PUBLIC_KEY_PV3,
                credentialsPV1, credentialsPV2, credentialsPV3,
                flagRegister1, flagRegister2, flagRegister3, flagRegisterBet1, flagRegisterBet2, flagRegisterBet3));
    }
}
