package SmartContracts.Wind;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple8;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.0.1.
 */
public class WindPenaltyContract extends Contract {

    private static final String BINARY = "608060405261520860065534801561001657600080fd5b50612433806100266000396000f3fe6080604052600436106100fe5760003560e01c8063aa09643e11610095578063db93ca3d11610064578063db93ca3d14610912578063dc25b5af14610929578063e126c4bb1461097a578063e15fe88e146109cb578063e283195414610a1c576100fe565b8063aa09643e14610624578063c93d268314610675578063ca287809146106a3578063cca8b51d146106f4576100fe565b80638496527a116100d15780638496527a1461037d5780639ada654714610387578063a3ba0ce5146105a5578063a8fe2318146105f6576100fe565b806301bbca9d1461010357806310873cd51461011a57806312b7463d14610131578063427d8b5c1461034f575b600080fd5b34801561010f57600080fd5b50610118610a26565b005b34801561012657600080fd5b5061012f610b6f565b005b34801561013d57600080fd5b506101746004803603604081101561015457600080fd5b810190808035906020019092919080359060200190929190505050610cb8565b604051808060200189815260200180602001888152602001806020018781526020018060200186815260200185810385528d818151815260200191508051906020019080838360005b838110156101d85780820151818401526020810190506101bd565b50505050905090810190601f1680156102055780820380516001836020036101000a031916815260200191505b5085810384528b818151815260200191508051906020019080838360005b8381101561023e578082015181840152602081019050610223565b50505050905090810190601f16801561026b5780820380516001836020036101000a031916815260200191505b50858103835289818151815260200191508051906020019080838360005b838110156102a4578082015181840152602081019050610289565b50505050905090810190601f1680156102d15780820380516001836020036101000a031916815260200191505b50858103825287818151815260200191508051906020019080838360005b8381101561030a5780820151818401526020810190506102ef565b50505050905090810190601f1680156103375780820380516001836020036101000a031916815260200191505b509c5050505050505050505050505060405180910390f35b61037b6004803603602081101561036557600080fd5b8101908080359060200190929190505050610ea8565b005b610385610fff565b005b34801561039357600080fd5b506103ca600480360360408110156103aa57600080fd5b810190808035906020019092919080359060200190929190505050611160565b604051808060200189815260200180602001888152602001806020018781526020018060200186815260200185810385528d818151815260200191508051906020019080838360005b8381101561042e578082015181840152602081019050610413565b50505050905090810190601f16801561045b5780820380516001836020036101000a031916815260200191505b5085810384528b818151815260200191508051906020019080838360005b83811015610494578082015181840152602081019050610479565b50505050905090810190601f1680156104c15780820380516001836020036101000a031916815260200191505b50858103835289818151815260200191508051906020019080838360005b838110156104fa5780820151818401526020810190506104df565b50505050905090810190601f1680156105275780820380516001836020036101000a031916815260200191505b50858103825287818151815260200191508051906020019080838360005b83811015610560578082015181840152602081019050610545565b50505050905090810190601f16801561058d5780820380516001836020036101000a031916815260200191505b509c5050505050505050505050505060405180910390f35b3480156105b157600080fd5b506105f4600480360360208110156105c857600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611350565b005b6106226004803603602081101561060c57600080fd5b81019080803590602001909291905050506114dd565b005b34801561063057600080fd5b506106736004803603602081101561064757600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611634565b005b6106a16004803603602081101561068b57600080fd5b81019080803590602001909291905050506117a4565b005b3480156106af57600080fd5b506106f2600480360360208110156106c657600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506118fb565b005b34801561070057600080fd5b506107376004803603604081101561071757600080fd5b810190808035906020019092919080359060200190929190505050611a88565b604051808060200189815260200180602001888152602001806020018781526020018060200186815260200185810385528d818151815260200191508051906020019080838360005b8381101561079b578082015181840152602081019050610780565b50505050905090810190601f1680156107c85780820380516001836020036101000a031916815260200191505b5085810384528b818151815260200191508051906020019080838360005b838110156108015780820151818401526020810190506107e6565b50505050905090810190601f16801561082e5780820380516001836020036101000a031916815260200191505b50858103835289818151815260200191508051906020019080838360005b8381101561086757808201518184015260208101905061084c565b50505050905090810190601f1680156108945780820380516001836020036101000a031916815260200191505b50858103825287818151815260200191508051906020019080838360005b838110156108cd5780820151818401526020810190506108b2565b50505050905090810190601f1680156108fa5780820380516001836020036101000a031916815260200191505b509c5050505050505050505050505060405180910390f35b34801561091e57600080fd5b50610927611c3e565b005b34801561093557600080fd5b506109786004803603602081101561094c57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611d87565b005b34801561098657600080fd5b506109c96004803603602081101561099d57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611ef7565b005b3480156109d757600080fd5b50610a1a600480360360208110156109ee57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050612084565b005b610a24612211565b005b6008600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610afd576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600f8152602001807f4e6f742057696e64436f756e746572000000000000000000000000000000000081525060200191505060405180910390fd5b60005a90506009600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16316002819055506006545a82030160036000828254019250508190555050565b6008600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610c46576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600f8152602001807f4e6f742057696e64436f756e746572000000000000000000000000000000000081525060200191505060405180910390fd5b60005a90506007600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16316000819055506006545a82030160036000828254019250508190555050565b606060006060600060606000606060006008600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610d9f576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600f8152602001807f4e6f742057696e64436f756e746572000000000000000000000000000000000081525060200191505060405180910390fd5b89896000548b600054036040518060400160405280600481526020017f66696e6500000000000000000000000000000000000000000000000000000000815250939291906040518060400160405280602081526020017f77696e642062616c616e6365206265666f72652066696e65207061796d656e748152509291906040518060400160405280601f81526020017f77696e642062616c616e63652061667465722066696e65207061796d656e740081525091906040518060400160405280601981526020017f77696e642062616c616e636520696e63726561736564206f6e0000000000000081525090975097509750975097509750975097509295985092959890939650565b6007600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610f7f576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260088152602001807f4e6f742077696e6400000000000000000000000000000000000000000000000081525060200191505060405180910390fd5b6009600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f19350505050158015610ffb573d6000803e3d6000fd5b5050565b6007600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146110d6576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260088152602001807f4e6f742077696e6400000000000000000000000000000000000000000000000081525060200191505060405180910390fd5b600060026003540290506008600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f1935050505015801561115c573d6000803e3d6000fd5b5050565b606060006060600060606000606060006008600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614611247576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600f8152602001807f4e6f742057696e64436f756e746572000000000000000000000000000000000081525060200191505060405180910390fd5b89896001548b600154036040518060400160405280600481526020017f66696e6500000000000000000000000000000000000000000000000000000000815250939291906040518060400160405280602081526020017f77696e642062616c616e6365206265666f72652066696e65207061796d656e748152509291906040518060400160405280601f81526020017f77696e642062616c616e63652061667465722066696e65207061796d656e740081525091906040518060400160405280601981526020017f77696e642062616c616e636520696e63726561736564206f6e0000000000000081525090975097509750975097509750975097509295985092959890939650565b60011515600b600080815260200190815260200160002060000160149054906101000a900460ff16151514156113ee576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601d8152602001807f436f6e73756d657220686173206265656e20726567697374657265642100000081525060200191505060405180910390fd5b80600b600080815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001600b600080815260200190815260200160002060000160146101000a81548160ff021916908315150217905550600b600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600b60008081526020019081526020016000206001018190555050565b6009600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146115b4576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4e6f742073746f7261676500000000000000000000000000000000000000000081525060200191505060405180910390fd5b6007600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f19350505050158015611630573d6000803e3d6000fd5b5050565b60011515600c600080815260200190815260200160002060000160149054906101000a900460ff16151514156116b5576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260248152602001806123b96024913960400191505060405180910390fd5b80600c600080815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001600c600080815260200190815260200160002060000160146101000a81548160ff021916908315150217905550600c600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600c60008081526020019081526020016000206001018190555050565b600b600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161461187b576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600c8152602001807f4e6f7420636f6e73756d6572000000000000000000000000000000000000000081525060200191505060405180910390fd5b6007600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f193505050501580156118f7573d6000803e3d6000fd5b5050565b600115156009600080815260200190815260200160002060000160149054906101000a900460ff1615151415611999576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601c8152602001807f53746f7261676520686173206265656e2072656769737465726564210000000081525060200191505060405180910390fd5b806009600080815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060016009600080815260200190815260200160002060000160146101000a81548160ff0219169083151502179055506009600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600960008081526020019081526020016000206001018190555050565b60606000606060006060600060606000600a600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614611b6f576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260128152602001807f4e6f742053746f72616765436f756e746572000000000000000000000000000081525060200191505060405180910390fd5b89896002548b600254036040518060400160405280600481526020017f66696e650000000000000000000000000000000000000000000000000000000081525093929190604051806060016040528060238152602001612373602391399291906040518060600160405280602281526020016123dd6022913991906040518060400160405280601c81526020017f73746f726167652062616c616e636520696e63726561736564206f6e0000000081525090975097509750975097509750975097509295985092959890939650565b6008600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614611d15576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600f8152602001807f4e6f742057696e64436f756e746572000000000000000000000000000000000081525060200191505060405180910390fd5b60005a90506007600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16316001819055506006545a82030160036000828254019250508190555050565b60011515600a600080815260200190815260200160002060000160149054906101000a900460ff1615151415611e08576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260238152602001806123966023913960400191505060405180910390fd5b80600a600080815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001600a600080815260200190815260200160002060000160146101000a81548160ff021916908315150217905550600a600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600a60008081526020019081526020016000206001018190555050565b600115156008600080815260200190815260200160002060000160149054906101000a900460ff1615151415611f95576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260208152602001807f57696e64436f756e74657220686173206265656e20726567697374657265642181525060200191505060405180910390fd5b806008600080815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060016008600080815260200190815260200160002060000160146101000a81548160ff0219169083151502179055506008600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600860008081526020019081526020016000206001018190555050565b600115156007600080815260200190815260200160002060000160149054906101000a900460ff1615151415612122576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260198152602001807f57696e6420686173206265656e2072656769737465726564210000000000000081525060200191505060405180910390fd5b806007600080815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060016007600080815260200190815260200160002060000160146101000a81548160ff0219169083151502179055506007600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600760008081526020019081526020016000206001018190555050565b6009600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146122e8576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4e6f742073746f7261676500000000000000000000000000000000000000000081525060200191505060405180910390fd5b60006002600454029050600a600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f1935050505015801561236e573d6000803e3d6000fd5b505056fe73746f726167652062616c616e6365206265666f72652066696e65207061796d656e7453746f72616765436f756e74657220686173206265656e207265676973746572656421436f6e73756d6572436f756e74657220686173206265656e20726567697374657265642173746f726167652062616c616e63652061667465722066696e65207061796d656e74a265627a7a72315820a7e549fe4ee960e95cf29dd0c08e31332f9c46a167dd91ae299ca7cac50a986a64736f6c63430005110032";

    public static final String FUNC_COMISSIONTOSTORAGECOUNTER = "comissionToStorageCounter";

    public static final String FUNC_COMISSIONTOWINDCOUNTER = "comissionToWindCounter";

    public static final String FUNC_INSTALLSTORAGEBALANCEAFTERFINEPAYMENT = "installStorageBalanceAfterFinePayment";

    public static final String FUNC_INSTALLWINDBALANCEAFTERFINEPAYMENT = "installWindBalanceAfterFinePayment";

    public static final String FUNC_INSTALLWINDBALANCEAFTERSTORAGEPAYMENT = "installWindBalanceAfterStoragePayment";

    public static final String FUNC_NOTPAYMENTFORWINDCONSUMPTIONENERGY = "notPaymentForWindConsumptionEnergy";

    public static final String FUNC_NOTPAYMENTFORWINDREPURCHASEENERGYFROMSTORAGE = "notPaymentForWindRePurchaseEnergyFromStorage";

    public static final String FUNC_NOTPAYMENTSTORAGEFORWIND = "notPaymentStorageForWind";

    public static final String FUNC_REGISTRATIONCONSUMER = "registrationConsumer";

    public static final String FUNC_REGISTRATIONCONSUMERCOUNTER = "registrationConsumerCounter";

    public static final String FUNC_REGISTRATIONSTORAGE = "registrationStorage";

    public static final String FUNC_REGISTRATIONSTORAGECOUNTER = "registrationStorageCounter";

    public static final String FUNC_REGISTRATIONWIND = "registrationWind";

    public static final String FUNC_REGISTRATIONWINDCOUNTER = "registrationWindCounter";

    public static final String FUNC_TOTALPAYMENTFINEFORWINDCONSUMPTIONENERGY = "totalPaymentFineForWindConsumptionEnergy";

    public static final String FUNC_TOTALPAYMENTFINEFORWINDREPURCHASEENERGY = "totalPaymentFineForWindRePurchaseEnergy";

    public static final String FUNC_TOTALPAYMENTSTORAGEFINEFORWINDENERGY = "totalPaymentStorageFineForWindEnergy";


    protected WindPenaltyContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }


    protected WindPenaltyContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> comissionToStorageCounter(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_COMISSIONTOSTORAGECOUNTER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> comissionToWindCounter(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_COMISSIONTOWINDCOUNTER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> installStorageBalanceAfterFinePayment() {
        final Function function = new Function(
                FUNC_INSTALLSTORAGEBALANCEAFTERFINEPAYMENT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> installWindBalanceAfterFinePayment() {
        final Function function = new Function(
                FUNC_INSTALLWINDBALANCEAFTERFINEPAYMENT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> installWindBalanceAfterStoragePayment() {
        final Function function = new Function(
                FUNC_INSTALLWINDBALANCEAFTERSTORAGEPAYMENT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> notPaymentForWindConsumptionEnergy(BigInteger fineFromMainContract, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_NOTPAYMENTFORWINDCONSUMPTIONENERGY, 
                Arrays.<Type>asList(new Uint256(fineFromMainContract)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> notPaymentForWindRePurchaseEnergyFromStorage(BigInteger fineFromMainContract, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_NOTPAYMENTFORWINDREPURCHASEENERGYFROMSTORAGE, 
                Arrays.<Type>asList(new Uint256(fineFromMainContract)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> notPaymentStorageForWind(BigInteger fineFromMainContract, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_NOTPAYMENTSTORAGEFORWIND, 
                Arrays.<Type>asList(new Uint256(fineFromMainContract)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> registrationConsumer(String newAddress) {
        final Function function = new Function(
                FUNC_REGISTRATIONCONSUMER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> registrationConsumerCounter(String newAddress) {
        final Function function = new Function(
                FUNC_REGISTRATIONCONSUMERCOUNTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> registrationStorage(String newAddress) {
        final Function function = new Function(
                FUNC_REGISTRATIONSTORAGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> registrationStorageCounter(String newAddress) {
        final Function function = new Function(
                FUNC_REGISTRATIONSTORAGECOUNTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> registrationWind(String newAddress) {
        final Function function = new Function(
                FUNC_REGISTRATIONWIND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> registrationWindCounter(String newAddress) {
        final Function function = new Function(
                FUNC_REGISTRATIONWINDCOUNTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger>> totalPaymentFineForWindConsumptionEnergy(BigInteger fine, BigInteger balanceBefore) {
        final Function function = new Function(FUNC_TOTALPAYMENTFINEFORWINDCONSUMPTIONENERGY, 
                Arrays.<Type>asList(new Uint256(fine),
                new Uint256(balanceBefore)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger>>(
                new Callable<Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger>>() {
                    @Override
                    public Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (String) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (String) results.get(6).getValue(), 
                                (BigInteger) results.get(7).getValue());
                    }
                });
    }

    public RemoteCall<Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger>> totalPaymentFineForWindRePurchaseEnergy(BigInteger fine, BigInteger balanceBefore) {
        final Function function = new Function(FUNC_TOTALPAYMENTFINEFORWINDREPURCHASEENERGY, 
                Arrays.<Type>asList(new Uint256(fine),
                new Uint256(balanceBefore)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger>>(
                new Callable<Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger>>() {
                    @Override
                    public Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (String) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (String) results.get(6).getValue(), 
                                (BigInteger) results.get(7).getValue());
                    }
                });
    }

    public RemoteCall<Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger>> totalPaymentStorageFineForWindEnergy(BigInteger fine, BigInteger balanceBefore) {
        final Function function = new Function(FUNC_TOTALPAYMENTSTORAGEFINEFORWINDENERGY, 
                Arrays.<Type>asList(new Uint256(fine),
                new Uint256(balanceBefore)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger>>(
                new Callable<Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger>>() {
                    @Override
                    public Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (String) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (String) results.get(6).getValue(), 
                                (BigInteger) results.get(7).getValue());
                    }
                });
    }


    public static WindPenaltyContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new WindPenaltyContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static WindPenaltyContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new WindPenaltyContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<WindPenaltyContract> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(WindPenaltyContract.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<WindPenaltyContract> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(WindPenaltyContract.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

}
