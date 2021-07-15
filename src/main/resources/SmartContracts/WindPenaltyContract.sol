pragma solidity ^0.5.17;

// Контракт для пары Ветрогенератор - Потребитель
// , который предназначен для выплаты штрафных санкций
contract WindPenaltyContract {
    
    // баланс ветрогенератора после выплаты штрафа потребителем
    uint windBalanceAfterConsumerPayment;
    // баланс ветрогенератора после выплаты штрафа накопителем
    uint windBalanceAfterStoragePayment;
    // баланс накопителя после выплаты штрафа
    uint storageBalanceAfterFinePayment;
    
    // суммарная стоимость всех транзакций счетчика ветрогенератора
    uint windCounterTransactionCost;
    // суммарная стоимость всех транзакций счетчика накопителя
    uint storageCounterTransactionCost;
    // суммарная стоимость всех транзакций счетчика потребителя
    uint consumerCounterTransactionCost;
    
    // постоянная стоимость транзакции
    uint constTransactionCost = 21000;
    
    
    
    // Ветрогенератор
    struct windGenerator {
        address payable windGeneratorAddress;
        bool isRegistered;
        uint firstAddressBalance;
    }
    mapping (uint => windGenerator) windGenerators;
    
    struct windCounter {
        address payable windCounterAddress;
        bool isRegistered;
        uint firstAddressBalance;
    }
    mapping (uint => windCounter) windCounterAddresses;
    
    // Накопитель
    struct storageAddr {
        address payable storageAddress;
        bool isRegistered;
        uint firstAddressBalance;
    }
    mapping (uint => storageAddr) storageAddresses;
    
    struct storageCounter {
        address payable storageCounterAddress;
        bool isRegistered;
        uint firstAddressBalance;
    }
    mapping (uint => storageCounter) storageCounterAddresses;
    
    // Потребитель и счетчик потребителя
    struct consumerAddr {
        address payable consumerAddress;
        bool isRegistered;
        uint firstAddressBalance;
    }
    mapping (uint => consumerAddr) consumerAddresses;
    
    struct consumerCounter {
        address payable consumerCounterAddress;
        bool isRegistered;
        uint firstAddressBalance;
    }
    mapping (uint => consumerCounter) consumerCounterAddresses;
    
    //Регистрация для ветрогенератора
    function registrationWind(address payable newAddress) public {
        require (windGenerators[0].isRegistered != true, 'Wind has been registered!');
        windGenerators[0].windGeneratorAddress = newAddress;
        windGenerators[0].isRegistered = true;
        windGenerators[0].firstAddressBalance = windGenerators[0].windGeneratorAddress.balance;
    }
    
     // Регистрация для счетчика ветрогенератора
    function registrationWindCounter(address payable newAddress) public {
        require (windCounterAddresses[0].isRegistered != true, 'WindCounter has been registered!');
        windCounterAddresses[0].windCounterAddress = newAddress;
        windCounterAddresses[0].isRegistered = true;
        windCounterAddresses[0].firstAddressBalance = windCounterAddresses[0].windCounterAddress.balance;
    }
    
    //Регистрация для накопителя
    function registrationStorage(address payable newAddress) public {
        require (storageAddresses[0].isRegistered != true, 'Storage has been registered!');
        storageAddresses[0].storageAddress = newAddress;
        storageAddresses[0].isRegistered = true;
        storageAddresses[0].firstAddressBalance = storageAddresses[0].storageAddress.balance;
    }
    
    // Регистрация для счетчика накопителя
    function registrationStorageCounter(address payable newAddress) public {
        require (storageCounterAddresses[0].isRegistered != true, 'StorageCounter has been registered!');
        storageCounterAddresses[0].storageCounterAddress = newAddress;
        storageCounterAddresses[0].isRegistered = true;
        storageCounterAddresses[0].firstAddressBalance = storageCounterAddresses[0].storageCounterAddress.balance;
    }
    // Регистрация для потребителя
    function registrationConsumer(address payable newAddress) public {
        require (consumerAddresses[0].isRegistered != true, 'Consumer has been registered!');
        consumerAddresses[0].consumerAddress = newAddress;
        consumerAddresses[0].isRegistered = true;
        consumerAddresses[0].firstAddressBalance = consumerAddresses[0].consumerAddress.balance;
    }
    // Регистрация для счетчика потребителя
    function registrationConsumerCounter(address payable newAddress) public {
        require (consumerCounterAddresses[0].isRegistered != true, 'ConsumerCounter has been registered!');
        consumerCounterAddresses[0].consumerCounterAddress = newAddress;
        consumerCounterAddresses[0].isRegistered = true;
        consumerCounterAddresses[0].firstAddressBalance =
        consumerCounterAddresses[0].consumerCounterAddress.balance;
    }
    
    // Модификаторы для участников контракта
    modifier onlyWind() {
        require(msg.sender == windGenerators[0].windGeneratorAddress, 'Not wind');
        _;
    }
    
    modifier onlyStorage() {
        require(msg.sender == storageAddresses[0].storageAddress, 'Not storage');
        _;
    }
    
    modifier onlyWindCounter() {
        require(msg.sender == windCounterAddresses[0].windCounterAddress, 'Not WindCounter');
        _;
    }
    
    modifier onlyStorageCounter() {
        require(msg.sender == storageCounterAddresses[0].storageCounterAddress,
        'Not StorageCounter');
        _;
    }
    
    modifier onlyConsumer() {
        require(msg.sender == consumerAddresses[0].consumerAddress, 'Not consumer');
        _;
    }
    
    modifier onlyConsumerCounter() {
        require(msg.sender == consumerCounterAddresses[0].consumerCounterAddress,
        'Not consumer counter');
        _;
    }
    
    // Метод выплаты штрафа, в случае невыплаченной в срок суммы потребителем
    // за потребляемую энергию
    function notPaymentForWindConsumptionEnergy(uint fineFromMainContract) 
    public payable onlyConsumer {
        /* перевод штрафа на счет Wind */
        windGenerators[0].windGeneratorAddress.transfer(fineFromMainContract);
    }
    
    // Метод, устанавливающий баланс Wind после выплаты штрафа
    function installWindBalanceAfterFinePayment() public onlyWindCounter {
        uint startGas = gasleft();
        windBalanceAfterConsumerPayment = windGenerators[0].windGeneratorAddress.balance;
        windCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    } 
    
    // Проверка выплаты шттрафа и увелечения баланса генератора на нужную сумму
    function totalPaymentFineForWindConsumptionEnergy(uint fine, uint balanceBefore
    ) public view onlyWindCounter returns(string memory, uint,
    string memory, uint, string memory, uint, string memory, uint){
        /* метод, возврающий значения:
        1) размер штрафа
        2) баланс генератора до выплаты штрафа
        3) баланс генератора после выплаты штрафа
        4) размер увеличения баланса*/
        return ('fine',fine,
        'wind balance before fine payment',balanceBefore,
        'wind balance after fine payment',windBalanceAfterConsumerPayment,
        'wind balance increased on',windBalanceAfterConsumerPayment-balanceBefore);
    }
    
    // Метод выплаты штрафа, в случае невыплаченной в срок суммы ветрогенератором, накопителю,
    // за покупку энергии
    function notPaymentForWindRePurchaseEnergyFromStorage(uint fineFromMainContract)
    public payable onlyWind {
        storageAddresses[0].storageAddress.transfer(fineFromMainContract);
    }
    
    // Метод, устанавливающий баланс накопителя после выплаты штрафа
    function installStorageBalanceAfterFinePayment() public onlyWindCounter {
        uint startGas = gasleft();
        storageBalanceAfterFinePayment = storageAddresses[0].storageAddress.balance;
        windCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    // Проверка выплаты штрафа и увеличения баланса накопителя на нужную сумму
    function totalPaymentFineForWindRePurchaseEnergy(uint fine,uint balanceBefore)
    public view onlyStorageCounter returns(string memory, uint,
    string memory, uint, string memory, uint, string memory, uint){
        return ('fine',fine,
        'storage balance before fine payment',balanceBefore,
        'storage balance after fine payment',storageBalanceAfterFinePayment,
        'storage balance increased on',storageBalanceAfterFinePayment-balanceBefore);
    }
    
    
    // Метод выплаты штрафа ветрогенератору, в случае невыплаченной в срок суммы накопителем
    // за покупку энергии
    function notPaymentStorageForWind(uint fineFromMainContract)
    public payable onlyStorage {
        windGenerators[0].windGeneratorAddress.transfer(fineFromMainContract);
    }
    
    // Метод, устанавливающий баланс ветрогенератора после выплаты штрафа накопителем
    function installWindBalanceAfterStoragePayment() public onlyWindCounter {
        uint startGas = gasleft();
        windBalanceAfterStoragePayment = windGenerators[0].windGeneratorAddress.balance;
        windCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    // Проверка выплаты штрафа и увелечния баланса ветрогенератора на нужную сумму
    function totalPaymentStorageFineForWindEnergy(uint fine, uint balanceBefore)
    public view onlyWindCounter returns(string memory, uint,
    string memory, uint, string memory, uint, string memory, uint){
        return('fine',fine,
        'wind balance before fine payment',balanceBefore,
        'wind balance after fine payment',windBalanceAfterStoragePayment,
        'wind balance increased on',windBalanceAfterStoragePayment-balanceBefore);
    }
    
    function comissionToWindCounter() public payable onlyWind {
        uint comission = windCounterTransactionCost * 2;
        windCounterAddresses[0].windCounterAddress.transfer(comission);
    }
    
    function comissionToStorageCounter() public payable onlyStorage {
        uint comission = storageCounterTransactionCost * 2;
        storageCounterAddresses[0].storageCounterAddress.transfer(comission);
    }
    
}