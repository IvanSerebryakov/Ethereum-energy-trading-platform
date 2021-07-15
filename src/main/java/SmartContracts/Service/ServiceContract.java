package SmartContracts.Service;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple6;
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
public class ServiceContract extends Contract {

    private static final String BINARY = "6080604052620186a0600855615208600b5534801561001d57600080fd5b506120418061002d6000396000f3fe6080604052600436106100f35760003560e01c8063aa09643e1161008a578063dc25b5af11610059578063dc25b5af146106dc578063df1cb3641461072d578063e9613cf114610737578063ed7baea214610927576100f3565b8063aa09643e14610605578063b142286114610656578063c6fd6f0d14610681578063ca2878091461068b576100f3565b80636f1a5475116100c65780636f1a5475146102965780637d0137511461032d578063809ef58c146104aa578063a3ba0ce5146105b4576100f3565b80630c055189146100f85780631812d5f81461012657806325f0f543146102305780632eda28871461025b575b600080fd5b6101246004803603602081101561010e57600080fd5b8101908080359060200190929190505050610931565b005b34801561013257600080fd5b5061013b610a9b565b604051808060200185815260200180602001848152602001838103835287818151815260200191508051906020019080838360005b8381101561018b578082015181840152602081019050610170565b50505050905090810190601f1680156101b85780820380516001836020036101000a031916815260200191505b50838103825285818151815260200191508051906020019080838360005b838110156101f15780820151818401526020810190506101d6565b50505050905090810190601f16801561021e5780820380516001836020036101000a031916815260200191505b50965050505050505060405180910390f35b34801561023c57600080fd5b50610245610c04565b6040518082815260200191505060405180910390f35b34801561026757600080fd5b506102946004803603602081101561027e57600080fd5b8101908080359060200190929190505050610c11565b005b3480156102a257600080fd5b506102ab610d29565b6040518080602001838152602001828103825284818151815260200191508051906020019080838360005b838110156102f15780820151818401526020810190506102d6565b50505050905090810190601f16801561031e5780820380516001836020036101000a031916815260200191505b50935050505060405180910390f35b34801561033957600080fd5b50610342610eb5565b6040518080602001878152602001806020018681526020018060200185815260200184810384528a818151815260200191508051906020019080838360005b8381101561039c578082015181840152602081019050610381565b50505050905090810190601f1680156103c95780820380516001836020036101000a031916815260200191505b50848103835288818151815260200191508051906020019080838360005b838110156104025780820151818401526020810190506103e7565b50505050905090810190601f16801561042f5780820380516001836020036101000a031916815260200191505b50848103825286818151815260200191508051906020019080838360005b8381101561046857808201518184015260208101905061044d565b50505050905090810190601f1680156104955780820380516001836020036101000a031916815260200191505b50995050505050505050505060405180910390f35b3480156104b657600080fd5b506104bf611061565b604051808060200185815260200180602001848152602001838103835287818151815260200191508051906020019080838360005b8381101561050f5780820151818401526020810190506104f4565b50505050905090810190601f16801561053c5780820380516001836020036101000a031916815260200191505b50838103825285818151815260200191508051906020019080838360005b8381101561057557808201518184015260208101905061055a565b50505050905090810190601f1680156105a25780820380516001836020036101000a031916815260200191505b50965050505050505060405180910390f35b3480156105c057600080fd5b50610603600480360360208110156105d757600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506111ca565b005b34801561061157600080fd5b506106546004803603602081101561062857600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611357565b005b34801561066257600080fd5b5061066b6114c7565b6040518082815260200191505060405180910390f35b6106896114d4565b005b34801561069757600080fd5b506106da600480360360208110156106ae57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506116dd565b005b3480156106e857600080fd5b5061072b600480360360208110156106ff57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611865565b005b6107356119d4565b005b34801561074357600080fd5b5061074c611b2b565b604051808060200189815260200180602001888152602001806020018781526020018060200186815260200185810385528d818151815260200191508051906020019080838360005b838110156107b0578082015181840152602081019050610795565b50505050905090810190601f1680156107dd5780820380516001836020036101000a031916815260200191505b5085810384528b818151815260200191508051906020019080838360005b838110156108165780820151818401526020810190506107fb565b50505050905090810190601f1680156108435780820380516001836020036101000a031916815260200191505b50858103835289818151815260200191508051906020019080838360005b8381101561087c578082015181840152602081019050610861565b50505050905090810190601f1680156108a95780820380516001836020036101000a031916815260200191505b50858103825287818151815260200191508051906020019080838360005b838110156108e25780820151818401526020810190506108c7565b50505050905090810190601f16801561090f5780820380516001836020036101000a031916815260200191505b509c5050505050505050505050505060405180910390f35b61092f611d78565b005b6002600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610a08576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600c8152602001807f4e6f7420636f6e73756d6572000000000000000000000000000000000000000081525060200191505060405180910390fd5b80600481905550600854810260068190555060008080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc6006549081150290604051600060405180830381858888f19350505050158015610a97573d6000803e3d6000fd5b5050565b60606000606060006003600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610b7a576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260148152602001807f4e6f7420636f6e73756d657220636f756e74657200000000000000000000000081525060200191505060405180910390fd5b600260095402600e54600f54036040518060400160405280600b81526020017f6d7573742062652070617900000000000000000000000000000000000000000081525091906040518060400160405280600c81526020017f66616374207061796d656e74000000000000000000000000000000000000000081525090935093509350935090919293565b6000600260095402905090565b6003600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610ce8576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260148152602001807f4e6f7420636f6e73756d657220636f756e74657200000000000000000000000081525060200191505060405180910390fd5b60005a905081600560008282540192505081905550600654600760008282540192505081905550600b545a8203016009600082825401925050819055505050565b606060006001600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610e04576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260128152602001807f4e6f742053746f72616765436f756e746572000000000000000000000000000081525060200191505060405180910390fd5b60005a9050600754600854600554020360118190555060008080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163160128190555060006011541115610e8657614e20601154016010819055505b60006011541415610e9a5760006010819055505b600b545a820301600a60008282540192505081905550509091565b6060600060606000606060006001600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610f98576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260128152602001807f4e6f742053746f72616765436f756e746572000000000000000000000000000081525060200191505060405180910390fd5b600854600554026007546010546040518060400160405280600b81526020017f4d757374206265207061790000000000000000000000000000000000000000008152509291906040518060400160405280600c81526020017f66616374207061796d656e74000000000000000000000000000000000000000081525091906040518060400160405280600481526020017f66696e650000000000000000000000000000000000000000000000000000000081525090955095509550955095509550909192939495565b60606000606060006001600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614611140576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260128152602001807f4e6f742053746f72616765436f756e746572000000000000000000000000000081525060200191505060405180910390fd5b6002600a5402600c54600d54036040518060400160405280600b81526020017f6d7573742062652070617900000000000000000000000000000000000000000081525091906040518060400160405280600c81526020017f66616374207061796d656e74000000000000000000000000000000000000000081525090935093509350935090919293565b600115156002600080815260200190815260200160002060000160149054906101000a900460ff1615151415611268576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601d8152602001807f436f6e73756d657220686173206265656e20726567697374657265642100000081525060200191505060405180910390fd5b806002600080815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060016002600080815260200190815260200160002060000160146101000a81548160ff0219169083151502179055506002600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600260008081526020019081526020016000206001018190555050565b600115156003600080815260200190815260200160002060000160149054906101000a900460ff16151514156113d8576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401808060200182810382526024815260200180611fc76024913960400191505060405180910390fd5b806003600080815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060016003600080815260200190815260200160002060000160146101000a81548160ff0219169083151502179055506003600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600360008081526020019081526020016000206001018190555050565b60006002600a5402905090565b6002600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146115ab576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600c8152602001807f4e6f7420636f6e73756d6572000000000000000000000000000000000000000081525060200191505060405180910390fd5b6003600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600e81905550600060026009540290506003600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f19350505050158015611685573d6000803e3d6000fd5b506003600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600f8190555050565b6001151560008080815260200190815260200160002060000160149054906101000a900460ff161515141561177a576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601c8152602001807f53746f7261676520686173206265656e2072656769737465726564210000000081525060200191505060405180910390fd5b8060008080815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550600160008080815260200190815260200160002060000160146101000a81548160ff02191690831515021790555060008080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16316000808081526020019081526020016000206001018190555050565b600115156001600080815260200190815260200160002060000160149054906101000a900460ff16151514156118e6576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401808060200182810382526023815260200180611fa46023913960400191505060405180910390fd5b806001600080815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550600180600080815260200190815260200160002060000160146101000a81548160ff0219169083151502179055506001600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600160008081526020019081526020016000206001018190555050565b6002600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614611aab576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600c8152602001807f4e6f7420636f6e73756d6572000000000000000000000000000000000000000081525060200191505060405180910390fd5b60008080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc6010549081150290604051600060405180830381858888f19350505050158015611b28573d6000803e3d6000fd5b50565b606060006060600060606000606060006001600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614611c12576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260128152602001807f4e6f742053746f72616765436f756e746572000000000000000000000000000081525060200191505060405180910390fd5b60125460008080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163160105460125460008080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163103604051806060016040528060238152602001611f816023913993929190604051806060016040528060228152602001611feb602291399291906040518060400160405280600481526020017f66696e650000000000000000000000000000000000000000000000000000000081525091906040518060400160405280601381526020017f62616c616e636520696e637265617365206f6e0000000000000000000000000081525090975097509750975097509750975097509091929394959697565b60008080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614611e4e576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4e6f742073746f7261676500000000000000000000000000000000000000000081525060200191505060405180910390fd5b6001600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600c8190555060006002600a540290506001600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f19350505050158015611f28573d6000803e3d6000fd5b506001600080815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1631600d819055505056fe73746f726167652062616c616e6365206265666f72652066696e65207061796d656e7453746f72616765436f756e74657220686173206265656e207265676973746572656421436f6e73756d6572436f756e74657220686173206265656e20726567697374657265642173746f726167652062616c616e63652061667465722066696e65207061796d656e74a265627a7a72315820e74963533b822930e2b1d2ba0be10e92a1d7604d3800f8185fc9eb267be0f80764736f6c63430005110032";

    public static final String FUNC_CONTROLCOMISSIONPAYMENTTOCONSUMERCOUNTER = "controlComissionPaymentToConsumerCounter";

    public static final String FUNC_CONTROLCOMISSIONPAYMENTTOSTORAGECOUNTER = "controlComissionPaymentToStorageCounter";

    public static final String FUNC_CONTROLFINEPAYMENT = "controlFinePayment";

    public static final String FUNC_CONTROLPAYMENTFORSTORAGE = "controlPaymentForStorage";

    public static final String FUNC_FINETOCONSUMERFORSTORAGE = "fineToConsumerForStorage";

    public static final String FUNC_PAYMENTCOMISSIONTOCONSUMERCOUNTER = "paymentComissionToConsumerCounter";

    public static final String FUNC_PAYMENTCOMISSIONTOSTORAGECOUNTER = "paymentComissionToStorageCounter";

    public static final String FUNC_PAYMENTFORSYSTEMSERVICETOSTORAGE = "paymentForSystemServiceToStorage";

    public static final String FUNC_REGISTRATIONCONSUMER = "registrationConsumer";

    public static final String FUNC_REGISTRATIONCONSUMERCOUNTER = "registrationConsumerCounter";

    public static final String FUNC_REGISTRATIONSTORAGE = "registrationStorage";

    public static final String FUNC_REGISTRATIONSTORAGECOUNTER = "registrationStorageCounter";

    public static final String FUNC_UPDATESTORAGESEVICE = "updateStorageSevice";

    public static final String FUNC_VIEWCOMMIISIONCONSUMERCOUNTER = "viewCommiisionConsumerCounter";

    public static final String FUNC_VIEWCOMMIISIONSTORAGECOUNTER = "viewCommiisionStorageCounter";

    public static final String FUNC_VIEWCONTROLPAYMENTFORSTORAGE = "viewControlPaymentForStorage";


    protected ServiceContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }


    protected ServiceContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<Tuple4<String, BigInteger, String, BigInteger>> controlComissionPaymentToConsumerCounter() {
        final Function function = new Function(FUNC_CONTROLCOMISSIONPAYMENTTOCONSUMERCOUNTER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple4<String, BigInteger, String, BigInteger>>(
                new Callable<Tuple4<String, BigInteger, String, BigInteger>>() {
                    @Override
                    public Tuple4<String, BigInteger, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<String, BigInteger, String, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<Tuple4<String, BigInteger, String, BigInteger>> controlComissionPaymentToStorageCounter() {
        final Function function = new Function(FUNC_CONTROLCOMISSIONPAYMENTTOSTORAGECOUNTER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple4<String, BigInteger, String, BigInteger>>(
                new Callable<Tuple4<String, BigInteger, String, BigInteger>>() {
                    @Override
                    public Tuple4<String, BigInteger, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<String, BigInteger, String, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<Tuple8<String, BigInteger, String, BigInteger, String, BigInteger, String, BigInteger>> controlFinePayment() {
        final Function function = new Function(FUNC_CONTROLFINEPAYMENT, 
                Arrays.<Type>asList(), 
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

    public RemoteCall<TransactionReceipt> controlPaymentForStorage() {
        final Function function = new Function(
                FUNC_CONTROLPAYMENTFORSTORAGE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> fineToConsumerForStorage(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_FINETOCONSUMERFORSTORAGE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> paymentComissionToConsumerCounter(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_PAYMENTCOMISSIONTOCONSUMERCOUNTER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> paymentComissionToStorageCounter(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_PAYMENTCOMISSIONTOSTORAGECOUNTER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> paymentForSystemServiceToStorage(BigInteger serviceEnergy, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_PAYMENTFORSYSTEMSERVICETOSTORAGE, 
                Arrays.<Type>asList(new Uint256(serviceEnergy)),
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

    public RemoteCall<TransactionReceipt> updateStorageSevice(BigInteger serviceEnergy) {
        final Function function = new Function(
                FUNC_UPDATESTORAGESEVICE, 
                Arrays.<Type>asList(new Uint256(serviceEnergy)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> viewCommiisionConsumerCounter() {
        final Function function = new Function(FUNC_VIEWCOMMIISIONCONSUMERCOUNTER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> viewCommiisionStorageCounter() {
        final Function function = new Function(FUNC_VIEWCOMMIISIONSTORAGECOUNTER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Tuple6<String, BigInteger, String, BigInteger, String, BigInteger>> viewControlPaymentForStorage() {
        final Function function = new Function(FUNC_VIEWCONTROLPAYMENTFORSTORAGE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple6<String, BigInteger, String, BigInteger, String, BigInteger>>(
                new Callable<Tuple6<String, BigInteger, String, BigInteger, String, BigInteger>>() {
                    @Override
                    public Tuple6<String, BigInteger, String, BigInteger, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple6<String, BigInteger, String, BigInteger, String, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (String) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue());
                    }
                });
    }


    public static ServiceContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new ServiceContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static ServiceContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ServiceContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<ServiceContract> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(ServiceContract.class, web3j, credentials, contractGasProvider, BINARY, "");
    }


    public static RemoteCall<ServiceContract> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(ServiceContract.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

}
