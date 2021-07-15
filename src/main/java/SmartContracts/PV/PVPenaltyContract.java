package SmartContracts.PV;

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
public class PVPenaltyContract extends Contract {

    private static final String BINARY = "608060405234801561001057600080fd5b50612595806100206000396000f3fe6080604052600436106101095760003560e01c8063938b636011610095578063ca28780911610064578063ca28780914610954578063dc25b5af146109a5578063de09a00d146109f6578063e1ff394914610a24578063e283195414610a5257610109565b8063938b636014610643578063a3ba0ce514610861578063aa09643e146108b2578063ae4f1b521461090357610109565b8063361ecbc8116100dc578063361ecbc8146103b55780636934d47a146103cc5780637aac295f146103f757806384eb5bae1461040e578063920549cc1461042557610109565b806318bfb2af1461010e57806319567e4f1461015f57806329e781ef1461018d57806330a7de5414610197575b600080fd5b34801561011a57600080fd5b5061015d6004803603602081101561013157600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610a5c565b005b61018b6004803603602081101561017557600080fd5b8101908080359060200190929190505050610be9565b005b610195610d40565b005b3480156101a357600080fd5b506101da600480360360408110156101ba57600080fd5b810190808035906020019092919080359060200190929190505050610ea1565b604051808060200189815260200180602001888152602001806020018781526020018060200186815260200185810385528d818151815260200191508051906020019080838360005b8381101561023e578082015181840152602081019050610223565b50505050905090810190601f16801561026b5780820380516001836020036101000a031916815260200191505b5085810384528b818151815260200191508051906020019080838360005b838110156102a4578082015181840152602081019050610289565b50505050905090810190601f1680156102d15780820380516001836020036101000a031916815260200191505b50858103835289818151815260200191508051906020019080838360005b8381101561030a5780820151818401526020810190506102ef565b50505050905090810190601f1680156103375780820380516001836020036101000a031916815260200191505b50858103825287818151815260200191508051906020019080838360005b83811015610370578082015181840152602081019050610355565b50505050905090810190601f16801561039d5780820380516001836020036101000a031916815260200191505b509c5050505050505050505050505060405180910390f35b3480156103c157600080fd5b506103ca611091565b005b3480156103d857600080fd5b506103e16111da565b6040518082815260200191505060405180910390f35b34801561040357600080fd5b5061040c611306565b005b34801561041a57600080fd5b5061042361144f565b005b34801561043157600080fd5b506104686004803603604081101561044857600080fd5b810190808035906020019092919080359060200190929190505050611598565b604051808060200189815260200180602001888152602001806020018781526020018060200186815260200185810385528d818151815260200191508051906020019080838360005b838110156104cc5780820151818401526020810190506104b1565b50505050905090810190601f1680156104f95780820380516001836020036101000a031916815260200191505b5085810384528b818151815260200191508051906020019080838360005b83811015610532578082015181840152602081019050610517565b50505050905090810190601f16801561055f5780820380516001836020036101000a031916815260200191505b50858103835289818151815260200191508051906020019080838360005b8381101561059857808201518184015260208101905061057d565b50505050905090810190601f1680156105c55780820380516001836020036101000a031916815260200191505b50858103825287818151815260200191508051906020019080838360005b838110156105fe5780820151818401526020810190506105e3565b50505050905090810190601f16801561062b5780820380516001836020036101000a031916815260200191505b509c5050505050505050505050505060405180910390f35b34801561064f57600080fd5b506106866004803603604081101561066657600080fd5b81019080803590602001909291908035906020019092919050505061174e565b604051808060200189815260200180602001888152602001806020018781526020018060200186815260200185810385528d818151815260200191508051906020019080838360005b838110156106ea5780820151818401526020810190506106cf565b50505050905090810190601f1680156107175780820380516001836020036101000a031916815260200191505b5085810384528b818151815260200191508051906020019080838360005b83811015610750578082015181840152602081019050610735565b50505050905090810190601f16801561077d5780820380516001836020036101000a031916815260200191505b50858103835289818151815260200191508051906020019080838360005b838110156107b657808201518184015260208101905061079b565b50505050905090810190601f1680156107e35780820380516001836020036101000a031916815260200191505b50858103825287818151815260200191508051906020019080838360005b8381101561081c578082015181840152602081019050610801565b50505050905090810190601f1680156108495780820380516001836020036101000a031916815260200191505b509c5050505050505050505050505060405180910390f35b34801561086d57600080fd5b506108b06004803603602081101561088457600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061193e565b005b3480156108be57600080fd5b50610901600480360360208110156108d557600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611acb565b005b34801561090f57600080fd5b506109526004803603602081101561092657600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611c3b565b005b34801561096057600080fd5b506109a36004803603602081101561097757600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611dc8565b005b3480156109b157600080fd5b506109f4600480360360208110156109c857600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611f55565b005b610a2260048036036020811015610a0c57600080fd5b81019080803590602001909291905050506120c5565b005b610a5060048036036020811015610a3a57600080fd5b810190808035906020019092919050505061221c565b005b610a5a612373565b005b600115156006600080815260200190815260200160002060000160149054906101000a900460ff1615151415610afa576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260178152602001807f505620686173206265656e20726567697374657265642100000000000000000081525060200191505060405180910390fd5b806006600080815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060016006600080815260200190815260200160002060000160146101000a81548160ff0219169083151502179055506006600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600660008081526020019081526020016000206001018190555050565b600a600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610cc0576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600c8152602001807f4e6f7420636f6e73756d6572000000000000000000000000000000000000000081525060200191505060405180910390fd5b6006600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f19350505050158015610d3c573d6000803e3d6000fd5b5050565b6006600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610e17576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260068152602001807f4e6f74205056000000000000000000000000000000000000000000000000000081525060200191505060405180910390fd5b600060026003540290506008600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f19350505050158015610e9d573d6000803e3d6000fd5b5050565b606060006060600060606000606060006008600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610f88576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600d8152602001807f4e6f74205056436f756e7465720000000000000000000000000000000000000081525060200191505060405180910390fd5b89896001548b600154036040518060400160405280600481526020017f66696e6500000000000000000000000000000000000000000000000000000000815250939291906040518060400160405280601e81526020017f70762062616c616e6365206265666f72652066696e65207061796d656e7400008152509291906040518060400160405280601d81526020017f70762062616c616e63652061667465722066696e65207061796d656e7400000081525091906040518060400160405280601781526020017f70762062616c616e636520696e63726561736564206f6e00000000000000000081525090975097509750975097509750975097509295985092959890939650565b6009600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614611168576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260128152602001807f4e6f742053746f72616765436f756e746572000000000000000000000000000081525060200191505060405180910390fd5b60005a90506007600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16316002819055506005545a82030160046000828254019250508190555050565b60006008600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146112b3576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600d8152602001807f4e6f74205056436f756e7465720000000000000000000000000000000000000081525060200191505060405180910390fd5b6006600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631905090565b6008600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146113dd576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600d8152602001807f4e6f74205056436f756e7465720000000000000000000000000000000000000081525060200191505060405180910390fd5b60005a90506006600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16316000819055506005545a82030160036000828254019250508190555050565b6008600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614611526576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600d8152602001807f4e6f74205056436f756e7465720000000000000000000000000000000000000081525060200191505060405180910390fd5b60005a90506006600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16316001819055506005545a82030160036000828254019250508190555050565b606060006060600060606000606060006009600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161461167f576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260128152602001807f4e6f742053746f72616765436f756e746572000000000000000000000000000081525060200191505060405180910390fd5b89896002548b600254036040518060400160405280600481526020017f66696e6500000000000000000000000000000000000000000000000000000000815250939291906040518060600160405280602381526020016124d56023913992919060405180606001604052806022815260200161253f6022913991906040518060400160405280601c81526020017f73746f726167652062616c616e636520696e63726561736564206f6e0000000081525090975097509750975097509750975097509295985092959890939650565b606060006060600060606000606060006008600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614611835576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600d8152602001807f4e6f74205056436f756e7465720000000000000000000000000000000000000081525060200191505060405180910390fd5b89896000548b600054036040518060400160405280600481526020017f66696e6500000000000000000000000000000000000000000000000000000000815250939291906040518060400160405280601e81526020017f70762062616c616e6365206265666f72652066696e65207061796d656e7400008152509291906040518060400160405280601d81526020017f70762062616c616e63652061667465722066696e65207061796d656e7400000081525091906040518060400160405280601781526020017f70762062616c616e636520696e63726561736564206f6e00000000000000000081525090975097509750975097509750975097509295985092959890939650565b60011515600a600080815260200190815260200160002060000160149054906101000a900460ff16151514156119dc576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601d8152602001807f436f6e73756d657220686173206265656e20726567697374657265642100000081525060200191505060405180910390fd5b80600a600080815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001600a600080815260200190815260200160002060000160146101000a81548160ff021916908315150217905550600a600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600a60008081526020019081526020016000206001018190555050565b60011515600b600080815260200190815260200160002060000160149054906101000a900460ff1615151415611b4c576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252602481526020018061251b6024913960400191505060405180910390fd5b80600b600080815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001600b600080815260200190815260200160002060000160146101000a81548160ff021916908315150217905550600b600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600b60008081526020019081526020016000206001018190555050565b600115156008600080815260200190815260200160002060000160149054906101000a900460ff1615151415611cd9576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601e8152602001807f5056436f756e74657220686173206265656e207265676973746572656421000081525060200191505060405180910390fd5b806008600080815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060016008600080815260200190815260200160002060000160146101000a81548160ff0219169083151502179055506008600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600860008081526020019081526020016000206001018190555050565b600115156007600080815260200190815260200160002060000160149054906101000a900460ff1615151415611e66576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601c8152602001807f53746f7261676520686173206265656e2072656769737465726564210000000081525060200191505060405180910390fd5b806007600080815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060016007600080815260200190815260200160002060000160146101000a81548160ff0219169083151502179055506007600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600760008081526020019081526020016000206001018190555050565b600115156009600080815260200190815260200160002060000160149054906101000a900460ff1615151415611fd6576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260238152602001806124f86023913960400191505060405180910390fd5b806009600080815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060016009600080815260200190815260200160002060000160146101000a81548160ff0219169083151502179055506009600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600960008081526020019081526020016000206001018190555050565b6007600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161461219c576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4e6f742073746f7261676500000000000000000000000000000000000000000081525060200191505060405180910390fd5b6006600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f19350505050158015612218573d6000803e3d6000fd5b5050565b6006600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146122f3576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260068152602001807f4e6f74205056000000000000000000000000000000000000000000000000000081525060200191505060405180910390fd5b6007600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f1935050505015801561236f573d6000803e3d6000fd5b5050565b6007600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161461244a576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4e6f742073746f7261676500000000000000000000000000000000000000000081525060200191505060405180910390fd5b600060026004540290506009600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f193505050501580156124d0573d6000803e3d6000fd5b505056fe73746f726167652062616c616e6365206265666f72652066696e65207061796d656e7453746f72616765436f756e74657220686173206265656e207265676973746572656421436f6e73756d6572436f756e74657220686173206265656e20726567697374657265642173746f726167652062616c616e63652061667465722066696e65207061796d656e74a265627a7a72315820c1995b9d7a4f13dfc798f26e598f7106d0bdc336bad6cd0e695b002335e38cf764736f6c63430005110032";

    public static final String FUNC_COMISSIONTOPVCOUNTER = "comissionToPVCounter";

    public static final String FUNC_COMISSIONTOSTORAGECOUNTER = "comissionToStorageCounter";

    public static final String FUNC_INSTALLPVBALANCEAFTERFINEPAYMENT = "installPVBalanceAfterFinePayment";

    public static final String FUNC_INSTALLPVBALANCEAFTERSTORAGEPAYMENT = "installPVBalanceAfterStoragePayment";

    public static final String FUNC_INSTALLSTORAGEBALANCEAFTERPVPAYMENT = "installStorageBalanceAfterPVPayment";

    public static final String FUNC_NOTPAYMENTFORPVCONSUMPTIONENERGY = "notPaymentForPVConsumptionEnergy";

    public static final String FUNC_NOTPAYMENTFORPVREPURCHASEENERGYFROMSTORAGE = "notPaymentForPVRePurchaseEnergyFromStorage";

    public static final String FUNC_NOTPAYMENTSTORAGEFORPV = "notPaymentStorageForPV";

    public static final String FUNC_REGISTRATIONCONSUMER = "registrationConsumer";

    public static final String FUNC_REGISTRATIONCONSUMERCOUNTER = "registrationConsumerCounter";

    public static final String FUNC_REGISTRATIONPV = "registrationPV";

    public static final String FUNC_REGISTRATIONPVCOUNTER = "registrationPVCounter";

    public static final String FUNC_REGISTRATIONSTORAGE = "registrationStorage";

    public static final String FUNC_REGISTRATIONSTORAGECOUNTER = "registrationStorageCounter";

    public static final String FUNC_TOTALPVPAYMENTFORREPURCHASEENERGY = "totalPVPaymentForRePurchaseEnergy";

    public static final String FUNC_TOTALPAYMENTFINEFORPVCONSUMPTIONENERGY = "totalPaymentFineForPVConsumptionEnergy";

    public static final String FUNC_TOTALPAYMENTSTORAGEFORPV = "totalPaymentStorageForPv";

    public static final String FUNC_VIEWPVBALNCEBEFOREFINE = "viewPVBalnceBeforeFine";


    protected PVPenaltyContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }


    protected PVPenaltyContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> comissionToPVCounter(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_COMISSIONTOPVCOUNTER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> comissionToStorageCounter(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_COMISSIONTOSTORAGECOUNTER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> installPVBalanceAfterFinePayment() {
        final Function function = new Function(
                FUNC_INSTALLPVBALANCEAFTERFINEPAYMENT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> installPVBalanceAfterStoragePayment() {
        final Function function = new Function(
                FUNC_INSTALLPVBALANCEAFTERSTORAGEPAYMENT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> installStorageBalanceAfterPVPayment() {
        final Function function = new Function(
                FUNC_INSTALLSTORAGEBALANCEAFTERPVPAYMENT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> notPaymentForPVConsumptionEnergy(BigInteger fineFromMainContract, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_NOTPAYMENTFORPVCONSUMPTIONENERGY, 
                Arrays.<Type>asList(new Uint256(fineFromMainContract)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> notPaymentForPVRePurchaseEnergyFromStorage(BigInteger fineFromMainContract, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_NOTPAYMENTFORPVREPURCHASEENERGYFROMSTORAGE, 
                Arrays.<Type>asList(new Uint256(fineFromMainContract)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> notPaymentStorageForPV(BigInteger fineFromMainContract, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_NOTPAYMENTSTORAGEFORPV, 
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

    public RemoteCall<TransactionReceipt> registrationPV(String newAddress) {
        final Function function = new Function(
                FUNC_REGISTRATIONPV, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> registrationPVCounter(String newAddress) {
        final Function function = new Function(
                FUNC_REGISTRATIONPVCOUNTER, 
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

    public RemoteCall<Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger>> totalPVPaymentForRePurchaseEnergy(BigInteger fine, BigInteger balanceBefore) {
        final Function function = new Function(FUNC_TOTALPVPAYMENTFORREPURCHASEENERGY, 
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

    public RemoteCall<Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger>> totalPaymentFineForPVConsumptionEnergy(BigInteger fine, BigInteger balanceBefore) {
        final Function function = new Function(FUNC_TOTALPAYMENTFINEFORPVCONSUMPTIONENERGY, 
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

    public RemoteCall<Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger>> totalPaymentStorageForPv(BigInteger fine, BigInteger balanceBefore) {
        final Function function = new Function(FUNC_TOTALPAYMENTSTORAGEFORPV, 
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

    public RemoteCall<BigInteger> viewPVBalnceBeforeFine() {
        final Function function = new Function(FUNC_VIEWPVBALNCEBEFOREFINE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }


    public static PVPenaltyContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new PVPenaltyContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static PVPenaltyContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PVPenaltyContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<PVPenaltyContract> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(PVPenaltyContract.class, web3j, credentials, contractGasProvider, BINARY, "");
    }


    public static RemoteCall<PVPenaltyContract> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(PVPenaltyContract.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

}
