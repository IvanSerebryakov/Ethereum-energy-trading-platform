pragma solidity ^0.5.17;

contract ServiceContract {
    
     // Структуры для участников участников контракта
    
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
    
    // // Цены за 1кВт, разыгранные на аукционе
    // // для PV
    // uint auctionBetPV;
    // // для ветрогенератора
    // uint auctionBetWind;
    
    // Энергия системной услуги от накопителя (текущая)
    uint curStorageServiceEnergy;
    // Энергия системной услуги от накопителя (суммарная)
    uint sumStorageServiceEnergy;
    
    // Оплата за системную услугу для накопителя (текущая)
    uint curPaymentStorageServiceEnergy;
    // Оплата за системную услугу для накопителя (суммарная)
    uint sumPaymentStorageServiceEnergy;
    // Оплата за 1кВт энергии, покупаемой у накопителя
    uint paymentEnergyFromStorage = 100000;
    
    // Суммарная стоимость транзакций для счетчика потребителя
    uint consumerCounterTransactionCost;
    // Суммарная стоиомость транзакций для счетчика накопителя
    uint storageCounterTransactionCost;
    // Постоянная стоимость транзакции
    uint constTransactionCost = 21000;
    
    // Баланс счетчика накопителя до перевода комиссии
    uint balanceStorageCounterBefore;
    // Баланс счетчика накопителя после перевода комиссии
    uint balanceStorageCounterAfter;
    
    // Баланс счетчика потребителя до перевода комиссии
    uint balanceConsumerCounterBefore;
    // Баланс счетчика потребителя после перевода комиссии
    uint balanceConsumerCounterAfter;
    
    // Штраф невыплаченной суммы накопителю
    uint fine;
    // Разница установленной и фактической выплаты ждя накопителя
    uint diff;
    // Баланс накопителя для проверки
    uint balanceStorageForControl;
    
    //Каждый участник после заключения контракта зарегистрирует себя в контракте 
    
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
    
    modifier onlyStorage() {
        require(msg.sender == storageAddresses[0].storageAddress, 'Not storage');
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

    // Метод оплаты системной услуги для потребителя 
    // (системная услуга в данном контракте - компенсация разницы выработки и потребления для потребителя, 
    // т.к. между выработкой и потреблением энергии всегда присутсвует дисбаланс)
    // Гарантирующий поставщик системной услуги - накопитель
    // serviceEnergy - энергия систменой услуги
    
    // Метод оплаты за системные услуги от накопителя
    function paymentForSystemServiceToStorage(uint serviceEnergy) public payable onlyConsumer {
        curStorageServiceEnergy = serviceEnergy;
        curPaymentStorageServiceEnergy = serviceEnergy * paymentEnergyFromStorage;
        storageAddresses[0].storageAddress.transfer(curPaymentStorageServiceEnergy);
    }
    
    // Метод обновления суммарной оплаты за системные услуги от накопителя, а также
    // суммарной (системной) энергии, полученной от накопителя
    function updateStorageSevice(uint serviceEnergy) public onlyConsumerCounter {
        uint startGas = gasleft();
        sumStorageServiceEnergy += serviceEnergy;
        sumPaymentStorageServiceEnergy += curPaymentStorageServiceEnergy;
        consumerCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    // Метод проверки выполнения контракта (проверка установленной оплаты для накопителя)
    function controlPaymentForStorage() public onlyStorageCounter returns(
        string memory, uint) {
        uint startGas = gasleft();
        diff = sumStorageServiceEnergy * paymentEnergyFromStorage - sumPaymentStorageServiceEnergy;
        // Вызываем текущий баланс накопителя
        balanceStorageForControl = storageAddresses[0].storageAddress.balance;
        if (diff > 0){
            fine = diff + 20000;
        }
        if (diff == 0){
            fine = 0;
        }
        storageCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    // Метод для просмотра выполнения контракта
    function viewControlPaymentForStorage() public view onlyStorageCounter returns(
        string memory, uint, string memory, uint,string memory, uint){
            return ('Must be pay',sumStorageServiceEnergy * paymentEnergyFromStorage,
            'fact payment',sumPaymentStorageServiceEnergy,
            'fine',fine);
        }
    
    // Штраф, выплачиваемый накопителю, в случае недоплаты
    function fineToConsumerForStorage() public payable onlyConsumer{
        storageAddresses[0].storageAddress.transfer(fine);
    }
    
    // Проверка балансов накопителя после выплаты штрафа
    function controlFinePayment() public view onlyStorageCounter returns(
        string memory, uint, string memory, uint, string memory, uint,
        string memory, uint){
            return ('storage balance before fine payment',balanceStorageForControl,
            'storage balance after fine payment',storageAddresses[0].storageAddress.balance,
            'fine',fine,
            'balance increase on',storageAddresses[0].storageAddress.balance
            - balanceStorageForControl);
        }
   
    
    // Метод оплаты комиссии счетчику накопителя
    function paymentComissionToStorageCounter() public payable onlyStorage {
        balanceStorageCounterBefore = storageCounterAddresses[0].storageCounterAddress.balance;
        uint payment = storageCounterTransactionCost * 2;
        storageCounterAddresses[0].storageCounterAddress.transfer(payment);
        balanceStorageCounterAfter = storageCounterAddresses[0].storageCounterAddress.balance;
    }
    
    function viewCommiisionStorageCounter() public view returns(uint){
        return storageCounterTransactionCost * 2;
    }
    
    // Метод оплаты комиссии счетчику потребителя
    function paymentComissionToConsumerCounter() public payable onlyConsumer {
        balanceConsumerCounterBefore = consumerCounterAddresses[0].consumerCounterAddress.balance;
        uint payment = consumerCounterTransactionCost * 2;
        consumerCounterAddresses[0].consumerCounterAddress.transfer(payment);
        balanceConsumerCounterAfter = consumerCounterAddresses[0].consumerCounterAddress.balance;
    }
    
    function viewCommiisionConsumerCounter() public view returns(uint){
        return consumerCounterTransactionCost * 2;
    }
    
    // Контроль за выплатой комиссии счетчику накопителя
    function controlComissionPaymentToStorageCounter() public view onlyStorageCounter 
        returns(string memory, uint, string memory, uint){
        return ('must be pay', storageCounterTransactionCost * 2,
        'fact payment',balanceStorageCounterAfter - balanceStorageCounterBefore);
    }
    
    // Контроль за выплатой комиссии счетчику потребителя
    function controlComissionPaymentToConsumerCounter() public view onlyConsumerCounter
    returns(string memory, uint, string memory, uint){
        return ('must be pay',consumerCounterTransactionCost * 2,
        'fact payment',balanceConsumerCounterAfter - balanceConsumerCounterBefore);
    }
    
}