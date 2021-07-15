pragma solidity ^0.5.17;

contract PVPenaltyContract{
    
    // Баланс PV после выплаты штрафа потребителем
    uint pvBalanceAfterConsumerPayment;
    // Баланс PV после выплаты штрафа накопителем
    uint pvBalanceAfterStoragePayment;
    // Баланс накопителя после выплаты штрафа солнечным генератором
    uint storageBalanceAfterPVPayment;
    // Суммарная стоимость транзакций счетчика PV
    uint pvCounterTransactionCost;
    // Суммарная стоимость транзакций счетчика накопителя
    uint storageCounterTransactionCost;
    
    // Постоянная стоимость транзакции
    uint constTransactionCost;
    
    // Структуры для участников участников контракта
    //Генераторы и накопители
    struct pvGenerator {
        address payable pvGeneratorAddress;
        bool isRegistered;
        uint firstAddressBalance;
    }
    //Сопоставление ключ-значение (можем получать составляющие структуры по 
    // ключу uint)
    mapping (uint => pvGenerator) pvGenerators;
    
    // Накопитель
    struct storageAddr {
        address payable storageAddress;
        bool isRegistered;
        uint firstAddressBalance;
    }
    mapping (uint => storageAddr) storageAddresses;
    
    // Счетчики для генераторов и накопителя
    struct pvCounter {
        address payable pvCounterAddress;
        bool isRegistered;
        uint firstAddressBalance;
    }
    mapping (uint => pvCounter) pvCounterAddresses;
    
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
    
    //Каждый участник после заключения контракта зарегистрирует себя в контракте 
    //Регситрация для PV
    function registrationPV(address payable newAddress) public{
        //Ставим требование, если true, то больше нельзя регистрировать в этой функции
        require (pvGenerators[0].isRegistered != true, 'PV has been registered!');
        pvGenerators[0].pvGeneratorAddress = newAddress;
        pvGenerators[0].isRegistered = true;
        pvGenerators[0].firstAddressBalance = pvGenerators[0].pvGeneratorAddress.balance;
    }
    
    //Регистрация для накопителя
    function registrationStorage(address payable newAddress) public {
        require (storageAddresses[0].isRegistered != true, 'Storage has been registered!');
        storageAddresses[0].storageAddress = newAddress;
        storageAddresses[0].isRegistered = true;
        storageAddresses[0].firstAddressBalance = storageAddresses[0].storageAddress.balance;
    }
    //Регистрация для счетчика солнечного генератора
    function registrationPVCounter(address payable newAddress) public {
        require (pvCounterAddresses[0].isRegistered != true, 'PVCounter has been registered!');
        pvCounterAddresses[0].pvCounterAddress = newAddress;
        pvCounterAddresses[0].isRegistered = true;
        pvCounterAddresses[0].firstAddressBalance = pvCounterAddresses[0].pvCounterAddress.balance;
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
    modifier onlyPV() {
        require(msg.sender == pvGenerators[0].pvGeneratorAddress, 'Not PV');
        _;
    }
    
    modifier onlyStorage() {
        require(msg.sender == storageAddresses[0].storageAddress, 'Not storage');
        _;
    }
    
    modifier onlyPVCounter() {
        require(msg.sender == pvCounterAddresses[0].pvCounterAddress, 'Not PVCounter');
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
    
    // Метод, возвращающий баланс до выплаты штрафа
    function viewPVBalnceBeforeFine() public view onlyPVCounter returns(uint){
        return pvGenerators[0].pvGeneratorAddress.balance;
    }
    // Метод выплаты штрафа, в случае невыплаченной в срок суммы потребителем за потребляемую 
    // энергию
    function notPaymentForPVConsumptionEnergy(uint fineFromMainContract)
    public payable onlyConsumer {
        pvGenerators[0].pvGeneratorAddress.transfer(fineFromMainContract);
    }
    
    // Метод, устанавливающий баланс PV после выплаты штрафа
    function installPVBalanceAfterFinePayment() public onlyPVCounter {
        uint startGas = gasleft();
        pvBalanceAfterConsumerPayment = pvGenerators[0].pvGeneratorAddress.balance;
        pvCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    // Проверка выплаты штрафа и увеличения баланса PV на нужную сумму
    function totalPaymentFineForPVConsumptionEnergy(uint fine,uint balanceBefore)
    public view onlyPVCounter returns(string memory, uint,
    string memory, uint, string memory, uint, string memory, uint){
        return ('fine',fine,
        'pv balance before fine payment',balanceBefore,
        'pv balance after fine payment',pvBalanceAfterConsumerPayment,
        'pv balance increased on',pvBalanceAfterConsumerPayment - balanceBefore);
    }
    
    // Метод выплаты штрафа, в случае невыплаченной в срок суммы солнечным генератором, накопителю,
    // за покупку энергии
    function notPaymentForPVRePurchaseEnergyFromStorage(uint fineFromMainContract)
    public payable onlyPV {
        storageAddresses[0].storageAddress.transfer(fineFromMainContract);
    }
    
    // Метод, устанавливающий баланс накопителя после выплаты штрафа
    function installStorageBalanceAfterPVPayment() public onlyStorageCounter {
        uint startGas = gasleft();
        storageBalanceAfterPVPayment = storageAddresses[0].storageAddress.balance;
        storageCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    // Проверка выплаты штрафа и увеличения баланса накопителя на нужную сумму
    function totalPVPaymentForRePurchaseEnergy(uint fine, uint balanceBefore)
    public view onlyStorageCounter returns(string memory, uint,
    string memory, uint, string memory, uint, string memory, uint){
        return ('fine',fine,
        'storage balance before fine payment',balanceBefore,
        'storage balance after fine payment',storageBalanceAfterPVPayment,
        'storage balance increased on',storageBalanceAfterPVPayment - balanceBefore);
    }
    
    // Метод выплаты штрафа, в случае невыплаченной в срок суммы накопителем, солнечному генератору,
    // за покупку энергии
    function notPaymentStorageForPV(uint fineFromMainContract)
    public payable onlyStorage {
        pvGenerators[0].pvGeneratorAddress.transfer(fineFromMainContract);
    }
    
    // Метод, устанавливающий баланс PV после выплаты штрафа
    function installPVBalanceAfterStoragePayment() public onlyPVCounter {
        uint startGas = gasleft();
        pvBalanceAfterStoragePayment = pvGenerators[0].pvGeneratorAddress.balance;
        pvCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    // Метод проверки выплаты штрафа и балансов PV до и после выплаты штрафа
    function totalPaymentStorageForPv(uint fine, uint balanceBefore)
    public view onlyPVCounter returns(string memory, uint,
    string memory, uint, string memory, uint, string memory, uint){
        return ('fine',fine,
        'pv balance before fine payment',balanceBefore,
        'pv balance after fine payment',pvBalanceAfterStoragePayment,
        'pv balance increased on',pvBalanceAfterStoragePayment - balanceBefore);
    }
    
    // Метод выплаты комиссии счетчику солнечного генератора
    function comissionToPVCounter() public payable onlyPV {
        uint comission = pvCounterTransactionCost * 2;
        pvCounterAddresses[0].pvCounterAddress.transfer(comission);
    }
    
    // Метод выплаты комиссии счетчику накопителя
    function comissionToStorageCounter() public payable onlyStorage {
        uint comission = storageCounterTransactionCost * 2;
        storageCounterAddresses[0].storageCounterAddress.transfer(comission);
    }
    
    
}