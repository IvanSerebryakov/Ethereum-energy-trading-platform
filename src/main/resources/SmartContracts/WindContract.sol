pragma solidity ^0.5.17;

contract WindContract {
    
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
    
    
    // для ветрогенератора
    uint auctionBetWind;
    
    // законтрактованная энергия для Wind (в Ваттах) за сутки
    uint contractEnergyWind = 45000;
    
    //минимальная цена за 1кВт для накопителя
    uint priceForStorage = 50000;
    
    // Вырабатываемая энергия от Wind
    uint transmittedWindEnergy;
    
    // Потребляемая энергия от Wind (суммарная)
    uint consumptionWindEnergy;
    // Потребляемая энергия от Wind (текущая)
    uint receivedWindEnergy;
    
    // Оплата за текущую потребляемую энергию для Wind
    uint paymentForCurWindEnergy;
    
    // Суммарная оплата за потребляемую энергию для Wind
    uint sumPaymentForWind;
    
    // Оплата за энергию для накопителя (Wind)
    uint storageWindPayment;
    
    //Энергия, которую купил накопитель у Wind
    uint storageWindEnergy;
    
    // Оплата за 1кВт энергии, покупаемой у накопителя
    uint paymentEnergyFromStorage = 60000;
    
    // количество энергии, перезакупаемой у накопителя для ветрогенератора 
    // (в случае неудачного прогноза погоды)
    uint rePurchaseEnergyForWind;
    //оплата за энергию, перезакупаемую у накопителя для ветрогенератора
    uint paymentRePurchaseEnergyForWind;
    
    //баланс накопителя до пперезакупки энергии (Wind)
    uint storageBalanceBeforeRePurchasePaymentWind;
    
    // Штраф за недовыплату накопителю (Wind)
    uint pennyForStorageWind;
    
    // Штраф за недовыплату Wind
    uint pennyForConsumerWind;
    
    // Разница балансов до и после выплаты за перезакупку энергии (Wind)
    uint differenceForStorageWind;
    
     // Разница фактической и установленной оплаты для Wind
    uint differenceForConsumerWind;
    
    // Разница фактической и установленной оплаты от накопителя для Wind
    uint differenceFromStorageForWind;
    
    // Штраф за недовыплату накопителм Wind
    uint pennyFromStorageForWind;
    
    // Суммарная энергия, перезакупленная у накопителя для Wind
    uint sumRePurchaseEnergyForWind;
    
    // Суммарная оплата за энергию, перезакупленную у накопителя для Wind
    uint sumPaymentRePurchaseEnergyForWind;
    
    // Суммарная энергия, купленная накопителем у Wind
    uint sumStorageWindEnergy;
    // Суммарная оплата за энергию, купленную накопителем у Wind
    uint sumPaymentWindEnergyForStorage;
    
    // Стоимость транзакций счетчика ветрогенератора
    uint windCounterTransactionCost;
    // Стоимость транзакций, совершаемых счетчиком накопителя
    uint storageCounterTransactionCost;
    // Стоимость транзакций, совершаемых счетчиком потребителя
    uint consumerCounterTransactionCost;
    // Постоянная стоимость каждой транзакции
    uint constTransactionCost = 21000;
    
    // Баланс ветрогенератора
    // для контроля выплаты штрафа  за невыплату установленной суммы за энергию
    uint controlBalanceOfWind;
    // Баланс накопителя 
    // для контроля выплаты штрафа за невыплату установленной суммы за энергию
    uint controlBalanceOfStorage;
    // баланс ветрогенератора для контроля выплаты штрафа накопителм
    uint controlWindBalance;
    
    // Балансы для контроля перевода комиссии счетчикам
    uint balanceWindCounterBefore;
    uint balanceWindCounterAfter;
    uint balanceStorageCounterBefore;
    uint balanceStorageCounterAfter;
    uint balanceConsumerCounterBefore;
    uint balanceConsumerCounterAfter;
    
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
    
    // Метод для регистрации ставки победителя для ветрогенератора
    function registerBetForWind(uint newBet) public onlyWind {
        auctionBetWind = newBet;
    }
    
     // Метод для просмотра установленной ставки для Wind
    function viewBetWind() public view returns(uint) {
        return auctionBetWind;
    }
    
    // Метод обновления количества вырабатываемой энергии для ветрогенератора
    function updateProductionWind(uint newEnergy) public onlyWindCounter {
        uint startGas = gasleft();
        transmittedWindEnergy += newEnergy;
        windCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    // Метод обновления потребляемой энергии потребителем от Wind
    function updateConsumptionWindEnergy(uint newEnergy) public onlyConsumerCounter {
        uint startGas = gasleft();
        receivedWindEnergy = newEnergy;
        consumptionWindEnergy += newEnergy;
        consumerCounterTransactionCost += 
        startGas - gasleft() + constTransactionCost;
    }
    
    // Метод обновления суммарной оплаты за потребляемую энергию для Wind
    function updatePaymentForWindEnergy() public onlyConsumerCounter {
        uint startGas = gasleft();
        sumPaymentForWind += paymentForCurWindEnergy;
        consumerCounterTransactionCost += 
        startGas - gasleft() + constTransactionCost;
    }
    
    // Метод оплаты за текущую потребляемую энергию для Wind
    function paymentForWindEnergy() public payable onlyConsumer {
        paymentForCurWindEnergy = receivedWindEnergy * auctionBetWind;
        windGenerators[0].windGeneratorAddress.transfer(paymentForCurWindEnergy);
    }
    
    // Метод оплаты за энергию, которую покупает накопитель у Wind
    // (накопитель закупает энергию по своей дешевой цене, если
    // посде подведения итогов остались излишки энергии (профицит))
    function paymentWindEnergyForStorage(uint newEnergy) public payable onlyStorage {
        storageWindEnergy = newEnergy;
        storageWindPayment = newEnergy * priceForStorage;
        windGenerators[0].windGeneratorAddress.transfer(storageWindPayment);
    }
    
    // Метод обновления оплаты энергии, которую закупает накопитель у Wind
    function updatePaymentWindEnergyForStorage() public onlyStorageCounter {
        uint startGas = gasleft();
        sumStorageWindEnergy += storageWindEnergy;
        sumPaymentWindEnergyForStorage += storageWindPayment;
        storageCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    // Метод перезакупки энергии у накопителя для Wind
    //needEnergy - необходимая энергия для перезакупки у накопителя для ветрогенератора
    //(выполнять этот Метод только после проверки выполнения условий контракта, если Wind 
    // выработал энергии меньше чем законтрактовано)
    function rePurchaseStorageEnergyForWind(uint needEnergy) public payable onlyWind {
        rePurchaseEnergyForWind = needEnergy;
        paymentRePurchaseEnergyForWind = needEnergy * paymentEnergyFromStorage;
        storageAddresses[0].storageAddress.transfer(paymentRePurchaseEnergyForWind);
    }
    
    // Метод обновления перезакупки энергии у накопителя для Wind
    function updatePaymentRePurchaseStorageEnergyForWind(uint needEnergy) public onlyWindCounter {
        uint startGas = gasleft();
        sumRePurchaseEnergyForWind += needEnergy;
        transmittedWindEnergy += needEnergy;
        sumPaymentRePurchaseEnergyForWind += paymentRePurchaseEnergyForWind;
        windCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    // Метод проверки условий выполнения оплаты за покупку энергии и 
    // расчет штрафа за недоплату у Wind
    function controlPaymentForWind() public onlyConsumerCounter {
        uint startGas = gasleft();
        // Старый вариант (заменил, т.к. receivedWindEnergy - это не суммарная, а текущая энергия)
        //differenceForConsumerWind = receivedWindEnergy * auctionBetWind - sumPaymentForWind;
        differenceForConsumerWind = consumptionWindEnergy * auctionBetWind - sumPaymentForWind;
        // Установим текущее значение баланса, которое будем проверять после выплаты штрафа
        // (в случае недовыплаты)
        controlBalanceOfWind = windGenerators[0].windGeneratorAddress.balance;
        if (differenceForConsumerWind == 0){
            pennyForConsumerWind = 0;
        }
        if (differenceForConsumerWind > 0){
            pennyForConsumerWind = differenceForConsumerWind + 20000;
        }
        consumerCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    
    // // Метод, возвращающий значения баланса ветрогенератора при подведении
    // // итогов контракта, и сумму 
    // function viewControlBalanceOfWindandFine() public view onlyWindCounter
    // returns(uint, uint){
    //     return (controlBalanceOfWind, pennyForConsumerWind);
    // }
    
    // Просмотр итогов контракта по оплате потребленной энергии для потребителя (в сутки)
    function viewPaymentForWind() public view onlyWindCounter returns(
        string memory, uint, string memory, uint, string memory, uint,
        string memory, uint){
            return ('Must be pay ',consumptionWindEnergy * auctionBetWind,
            'Fact payment ',sumPaymentForWind,
            'fine ',pennyForConsumerWind,
            'controlBalanceOfWind',controlBalanceOfWind);
    }
    
  
    // Метод проверки выполнения оплаты за перезакупку энергии у накопителя для Wind
    function controlPaymentWindForStorage() public onlyWindCounter {
        uint startGas = gasleft();
        differenceForStorageWind = sumRePurchaseEnergyForWind * paymentEnergyFromStorage 
        - sumPaymentRePurchaseEnergyForWind;
        controlBalanceOfStorage = storageAddresses[0].storageAddress.balance;
        if (differenceForStorageWind > 0){
            pennyForStorageWind = differenceForStorageWind + 20000;
        }
        if (differenceForStorageWind == 0){
            pennyForStorageWind = 0;
        }
        windCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    function viewPaymentWindForStorage() public view onlyWindCounter returns(
        string memory, uint, string memory, uint, string memory, uint,
        string memory, uint){
            return ('Must be pay for storage energy (wind) ',sumRePurchaseEnergyForWind * paymentEnergyFromStorage,
            'fact payment ',sumPaymentRePurchaseEnergyForWind,
            'penny ',pennyForStorageWind,
            'storage balance ',controlBalanceOfStorage);
    }
    
     // Метод проверки выполнения оплаты накопителем за покупку энергии у Wind
    function controlPaymentStorageForWind() public onlyStorageCounter {
        uint startGas = gasleft();
        differenceFromStorageForWind = sumStorageWindEnergy * priceForStorage - sumPaymentWindEnergyForStorage;
        controlWindBalance = windGenerators[0].windGeneratorAddress.balance;
        if (differenceFromStorageForWind > 0){
            pennyFromStorageForWind = differenceFromStorageForWind + 20000;
        }
        if (differenceFromStorageForWind == 0){
            pennyFromStorageForWind = 0;
        }
        storageCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    // Метод для просмотра выполнения оплаты накопителем за покупку энергии у Wind
    function viewPaymentStorageForWind() public view onlyStorageCounter returns(
        string memory, uint, string memory, uint, string memory, uint, string memory, uint
       ){
            return ('Must be pay for wind energy (storage) ', sumStorageWindEnergy * priceForStorage,
            'fact payment ',sumPaymentWindEnergyForStorage,
            'penny ',pennyFromStorageForWind,
            'wind balance',controlWindBalance);
    }
    
    // Метод контроля поставки электроэнергии за сутки для Wind
    function controlProductionEnergyPerDayWind(uint day) public view onlyWindCounter returns(
        string memory, uint) {
            uint contractEnergyPerDay = contractEnergyWind * day;
            if (transmittedWindEnergy < contractEnergyPerDay){
                uint difference = contractEnergyPerDay - transmittedWindEnergy;
                return ('less',difference);
            }
            
            if (transmittedWindEnergy > contractEnergyPerDay){
                uint difference = transmittedWindEnergy - contractEnergyPerDay;
                return ('more',difference);
            }
            
            if (transmittedWindEnergy == contractEnergyPerDay){
                return ('Ok',0);
            }
        }    
    
    // Метод для выплаты комисси для счетчика ветрогенератора
    // коммисия выплачивается по следующей схеме:
    // стоимость всех выполненных транзакций счетчика ветрогенератора * 2
    //function commisionForWindCounter() public payable
    // Контроль за выполнением условий контракта для PV
    
    // Коммисия счетчику ветрогенератора
    function commissionToWindCounter() public payable onlyWind {
        balanceWindCounterBefore = windCounterAddresses[0].windCounterAddress.balance;
        uint commission = windCounterTransactionCost * 2;
        windCounterAddresses[0].windCounterAddress.transfer(commission);
        balanceWindCounterAfter = windCounterAddresses[0].windCounterAddress.balance;
    }
    
    // Контроль за оплатой комиссии счетчику генератора
    function controlCommissionWindCounter() public view onlyWindCounter returns(
        string memory, uint, string memory, uint) {
        uint difference = balanceWindCounterAfter - balanceWindCounterBefore;
        uint installPayment = windCounterTransactionCost * 2;
        return ('Must be pay',installPayment,
            'fact payment',difference);
    }
    
    // коммисия счетчику накопителя
    function commissionToStorageCounter() public payable onlyStorage {
        balanceStorageCounterBefore = storageCounterAddresses[0].storageCounterAddress.balance;
        uint commission = storageCounterTransactionCost * 2;
        storageCounterAddresses[0].storageCounterAddress.transfer(commission);
        balanceStorageCounterAfter = storageCounterAddresses[0].storageCounterAddress.balance;
    }
    
    // Проверка оплаты комиссии счетчику накопителя
    function controlCommisionStorageCounter() public view onlyStorageCounter returns(
        string memory, uint, string memory, uint){
            uint difference = balanceStorageCounterAfter - balanceStorageCounterBefore;
            uint installPayment = storageCounterTransactionCost * 2;
            return ('Must be pay',installPayment,
            'fact payment',difference);
        }

    // коммисия счетчику потребителя
    function commissionToConsumerCounter() public payable onlyConsumer {
        balanceConsumerCounterBefore = consumerCounterAddresses[0].consumerCounterAddress.balance;
        uint commission = consumerCounterTransactionCost * 2;
        consumerCounterAddresses[0].consumerCounterAddress.transfer(commission);
        balanceConsumerCounterAfter = consumerCounterAddresses[0].consumerCounterAddress.balance;
    }
    
    // Проверка оплаты комиссии счечику потребителя
    function controlCommissionConsumerCounter() public view onlyConsumerCounter returns(
        string memory, uint, string memory, uint){
            uint difference = balanceConsumerCounterAfter - balanceConsumerCounterBefore;
            uint installPayment = consumerCounterTransactionCost * 2;
            return ('Must be pay',installPayment,
            'fact payment',difference);
    }
        
    
}