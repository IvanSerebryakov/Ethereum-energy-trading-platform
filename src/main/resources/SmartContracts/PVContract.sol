pragma solidity ^0.5.17;

contract PVContract {
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
    
    // Цены за 1кВт, разыгранные на аукционе
    // для PV
    uint auctionBetPV;
    
    // законтрактованная энергия для PV (в Ваттах) за сутки
    uint contractEnergyPV = 45000;
    
    //минимальная цена за 1кВт для накопителя
    uint priceForStorage = 50000;
    // Вырабатываемая энергия от PV
    uint transmittedPVEnergy;
    
    // Потребляемая энергия от PV (суммарная)
    uint consumptionPVEnergy;
    // Потребляемая энергия от PV (текущая)
    uint receivedPVEnergy;
    
    // Оплата за текущую потребляемую энергию для PV
    uint paymentForCurPVEnergy;
    
    // Суммарная оплата за потребляемую энергию для PV
    uint sumPaymentForPV;
    
    // Оплата за энергию для накопителя (PV)
    uint storagePVPayment;
    
    // Энергия, которую купил накопитель у PV
    uint storagePVEnergy;
    
    // Оплата за 1кВт энергии, покупаемой у накопителя
    uint paymentEnergyFromStorage = 60000;
    
    // количество энергии, презакупаемой у накопителя для солнечного генератора
    // (в случае неудачного прогноза погоды)
    uint rePurchaseEnergyForPV;
    // оплата за энергию, перезакупаемую у накопителя для солнечного генератора
    uint paymentRePurchaseEnergyForPV;
    
    //Для проверки переводов
    // баланс накопителя до перезакупкт энергии (PV)
    uint storageBalanceBeforeRePurchasePaymentPV;
    
    // Штраф за недовыплату накопителю (PV)
    uint pennyForStoragePV;
    
    // Штраф за недовыплату PV
    uint pennyForConsumerPV;
    
    // Разница балансов до и после выплаты за перезапку энергии (PV)
    uint differenceForStoragePV;
    
    // Разница фактической и установленной оплаты для PV
    uint differenceForConsumerPV;
    
    // Разница фактической и установленной оплаты от накопителя для PV
    uint differenceFromStorageForPV;
    
    // Штраф за недовыплату накопителем PV
    uint pennyFromStorageForPV;
    
    // Суммарная энергия, перезакупленная у накопителя для PV
    uint sumRePurchaseEnergyForPV;
    
    // Суммарная оплата за энергию, перезакупленную у накопителя для PV
    uint sumPaymentRePurchaseEnergyForPV;
    
    // Суммарная энергия, купленная накопителем у PV
    uint sumStoragePVEnergy;
    // Суммарная оплата за энергию, купленную накопителем у PV
    uint sumPaymentPVEnergyForStorage;
    
    // Стоимость транзакций, совершамаемых счетчиком ветрогенератора
    // internal - тип видимости переменной - 
    // переменная видна в этом контракте или в вытекающих из 
    // этого контракта других контрактов
    uint windCounterTransactionCost;
    // Стоимость транзакций, совершаемых счетчиком солнечного генератора
    uint pvCounterTransactionCost;
    // Стоимость транзакций, совершаемых счетчиком накопителя
    uint storageCounterTransactionCost;
    // Стоимость транзакций, совершаемых счетчиком потребителя
    uint consumerCounterTransactionCost;
    // Постоянная стоимость каждой транзакции
    uint constTransactionCost = 21000;
    // Балансы для контроля перевода комиссии счетчикам
    uint balancePVCounterBefore;
    uint balancePVCounterAfter;
    uint balanceStorageCounterBefore;
    uint balanceStorageCounterAfter;
    uint balanceConsumerCounterBefore;
    uint balanceConsumerCounterAfter;
    
    // Баланс PV для контроля выплаты потребителем за потребляемую энергию
    uint controlPVBalance;
    
    // Баланс накопителя для контроля оплаты за перезакупку энергии
    uint controlStorageBalance;
    
    // Баланс PV при проверке оплаты энергии, купленной у PV накопителем
    uint controlPVBalanceStorage;
    
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
    
    // Метод для регистрации ставки победителя для солнечного генератора
    function registerBetForPV(uint newBet) public onlyPV {
        auctionBetPV = newBet;
    }
    
    // Метод обновления количества вырабатываемой энергии энергии для PV
    function updateProductionPV(uint newEnergy) public onlyPVCounter {
        uint startGas = gasleft();
        transmittedPVEnergy += newEnergy;
        pvCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    // Метод обновления потребляемой энергии потребителем от PV
    function updateConsumptionPVenergy(uint newEnergy) public onlyConsumerCounter {
        uint startGas = gasleft();
        receivedPVEnergy = newEnergy;
        consumptionPVEnergy += newEnergy;
        consumerCounterTransactionCost +=
        startGas - gasleft() + constTransactionCost;
    }
    
    // Метод обновления суммарной оплаты за потребляемую энергию для PV
    // И здесь будем принимать оплату от потребителя (Потребитель
    // отписывается своему счетчику, что оплатил и отправляет сколько оплатил)
    // , чтобы обновление оплаты точно происходило
    function updatePaymentForPVEnergy() public onlyConsumerCounter {
        uint startGas = gasleft();
        sumPaymentForPV += paymentForCurPVEnergy;
        consumerCounterTransactionCost +=
        startGas - gasleft() + constTransactionCost;
    }
    
    // Метод оплаты за текущую потребляемую энергию для PV
    // Если оплата для PV проходить не будет, то будем принимать
    // на вход этой функции кол-во потребляемой энергии
    // от счетчика накопителя
    function paymentForPVEnergy() public payable onlyConsumer {
        paymentForCurPVEnergy = receivedPVEnergy * auctionBetPV; 
        pvGenerators[0].pvGeneratorAddress.transfer(paymentForCurPVEnergy);
    }
    
    // Метод для просмотра оплаты за текущую потребляемую энергию
    function viewPaymentForPVEnergy() public view onlyConsumer 
    returns(uint){
        return receivedPVEnergy * auctionBetPV;
    }
    
    // Метод оплаты за энергию, которую покупает накопитель у PV (если есть энергия и кто-то не расторговался)
    // newEnergy в этом методе - энергия, которую покупает накопитель (придет сумма из матлаба - сколько надо накопителю)
    function paymentPVEnergyForStorage(uint newEnergy) public payable onlyStorage {
        storagePVEnergy = newEnergy;
        storagePVPayment = newEnergy * priceForStorage;
        pvGenerators[0].pvGeneratorAddress.transfer(storagePVPayment);
    }
    
    // Метод обновления суммарной оплатой энергии, котрую закупает накопитель у PV (суммирование)
    function updatePaymentPVEnergyForStorage() public onlyStorageCounter {
        uint startGas = gasleft();
        sumStoragePVEnergy += storagePVEnergy;
        sumPaymentPVEnergyForStorage += storagePVPayment;
        storageCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    // Метод перезакупки энергии у накопителя для PV (выполнять этот Метод
    // только после проверки выполнения условий контракта, если PV 
    // выработало энергии меньше чем законтрактовано)
    function rePurchaseStorageEnergyForPV(uint needEnergy) public payable onlyPV {
        //storageBalanceBeforeRePurchasePaymentPV = storageAddresses[0].storageAddress.balance;
        rePurchaseEnergyForPV = needEnergy;
        paymentRePurchaseEnergyForPV = needEnergy * paymentEnergyFromStorage;
        storageAddresses[0].storageAddress.transfer(paymentRePurchaseEnergyForPV);
        
    }
    
    // Метод обновления перезакупки энергии у накопителя для PV
    function updatePaymentRePurchaseStorageEnergyForPV() public onlyPVCounter {
        uint startGas = gasleft();
        sumRePurchaseEnergyForPV += rePurchaseEnergyForPV;
        transmittedPVEnergy += rePurchaseEnergyForPV;
        sumPaymentRePurchaseEnergyForPV += paymentRePurchaseEnergyForPV;
        pvCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    // Контроль за оплатой осуществляется следующим образом:
    // Каждый счетчик фиксирует баланс того, кому осуществляется перевод до совершения транзакции
    // и после и расчитывает разницу. Она должна быть равна сумме перевода
    // Должна быть одна переменная в которую будет записываться баланс 
    // до совершения транзакции. После совершения транзакции сразу рассчитывается разница
    // Метод проверки условий выполнения оплаты за покупку энергии у PV
    function controlPaymentForPV() public onlyConsumerCounter {
        uint startGas = gasleft();
        // Проверяем разницу между установленной оплатой и оплатой, которую почситал счетчик
        // Старый вариант (изменил, т.к. использовал текущую потребляемую энергию, а не 
        // суммарную)
        //differenceForConsumerPV = receivedPVEnergy * auctionBetPV - sumPaymentForPV;
        differenceForConsumerPV = consumptionPVEnergy * auctionBetPV - sumPaymentForPV;
       
        controlPVBalance = pvGenerators[0].pvGeneratorAddress.balance;
        if (differenceForConsumerPV == 0){
            pennyForConsumerPV = 0;
        }
        if (differenceForConsumerPV > 0){
            pennyForConsumerPV = differenceForConsumerPV + 20000;
        }
        consumerCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    // Метод, возвращающий значения штрафа и баланса PV
    function viewPaymentForPV() public view onlyConsumerCounter returns(
        string memory, uint, string memory, uint, string memory, uint,
        string memory, uint){
            return ('Must be pay ',consumptionPVEnergy *  auctionBetPV,
            'Fact pay ',sumPaymentForPV,
            'penny ',pennyForConsumerPV,
            'pv balance',controlPVBalance);
    }
    
    // Метод проверки условий выполнения оплаты за перезакупку энергии у накопителя для PV
    function controlPaymentPVForStorage() public onlyPVCounter {
        uint startGas = gasleft();
        differenceForStoragePV = sumRePurchaseEnergyForPV * paymentEnergyFromStorage 
        - sumPaymentRePurchaseEnergyForPV;
        controlStorageBalance = storageAddresses[0].storageAddress.balance;
        // Если PV генератор не заплатил накопителю нужную сумму - с него списывается незаплаченная сумма + штраф
        if (differenceForStoragePV > 0){
            pennyForStoragePV = differenceForStoragePV + 20000;
        }
        if (differenceForStoragePV == 0){
            pennyForStoragePV = 0;
        }
        pvCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    function viewPaymentPVForStorage() public view onlyPVCounter returns(string memory, uint, string memory, uint,
        string memory, uint, string memory, uint){
        return ('Must be pay for storage energy (pv) ',sumRePurchaseEnergyForPV * paymentEnergyFromStorage,
           'fact payment ',sumPaymentRePurchaseEnergyForPV,
           ' penny ',pennyForStoragePV,
           'storage balance',controlStorageBalance);
    }
        
    // Метод проверки выполнения оплаты накопителем за покупку энергии у PV
    function controlPaymentStorageForPV() public onlyStorageCounter {
        uint startGas = gasleft();
        differenceFromStorageForPV = sumStoragePVEnergy * priceForStorage - sumPaymentPVEnergyForStorage;
        controlPVBalanceStorage = pvGenerators[0].pvGeneratorAddress.balance;
        if (differenceFromStorageForPV > 0){
            pennyFromStorageForPV = differenceFromStorageForPV + 20000;
        }
        if (differenceFromStorageForPV == 0){
            pennyFromStorageForPV = 0;
        }
        storageCounterTransactionCost += startGas - gasleft() + constTransactionCost;
    }
    
    // Метод для просмотра выполнения оплаты накопителем за покупку энергии у PV
    function viewPaymentStorageForPV() public view onlyStorageCounter returns(
        string memory, uint, string memory, uint, string memory, uint, string memory, uint){
            return ('Must be pay for pv energy (storage) ',sumStoragePVEnergy * priceForStorage,
            'fact payment ',sumPaymentPVEnergyForStorage,
            'penny ',pennyFromStorageForPV,
            'pv balance',controlPVBalanceStorage);
            
    }
    
    // Метод для контроля поставки электроэнергии за сутки для PV
    function controlProductionEnergyPerDayPV(uint day) public view onlyPVCounter returns(
        string memory, uint) {
        // Если поставлено, меньше чем законтрактовано, 
        // то PV перезакупает энергию у накопителя
        uint contractEnergyPVPerDay = contractEnergyPV * day;
        if (transmittedPVEnergy < contractEnergyPVPerDay){
            uint difference = contractEnergyPVPerDay - transmittedPVEnergy;
            return ('less',difference);
        }
        // Если вырабатываемой энергии больше, чем законтрактованной, 
        // то накопитель закупает энергию по своей цене, 
        // если он не заряжен до предела, если заряжен, то снова выставляем энергию
        // на аукцион
        if (transmittedPVEnergy > contractEnergyPVPerDay){
            uint difference = transmittedPVEnergy - contractEnergyPVPerDay;
            return ('more', difference);
        }
        // Если равно, то ОК (возвращаем 'Ok',0)
        if (transmittedPVEnergy == contractEnergyPVPerDay){
            return ('Ok',0);
        }
        
    }
    
    // коммисия счетчику солнечного генератора
    function commisionToPVCounter() public payable onlyPV {
        balancePVCounterBefore = pvCounterAddresses[0].pvCounterAddress.balance;
        uint commission = pvCounterTransactionCost * 2;
        pvCounterAddresses[0].pvCounterAddress.transfer(commission);
        balancePVCounterAfter = pvCounterAddresses[0].pvCounterAddress.balance;
    }
    
    // Просмотр комиссии счечтчика солнечного генератора
    function viewComissionPVCounter() public view onlyPV returns(uint) {
        return pvCounterTransactionCost * 2;
    }
    
    // Проверка комиссии для счетчика солнечного генератора и начисление штрафа в случае невыплаты
    function controlCommissionPVCounter() public view onlyPVCounter returns(
        string memory, uint, string memory, uint){
            uint difference = balancePVCounterAfter - balancePVCounterBefore;
            uint installPayment = pvCounterTransactionCost * 2;
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
    
    // Просмотр комиссии счетчика накопителя
    function viewComissionStorageCounter() public view onlyStorage 
        returns(uint){
        return storageCounterTransactionCost * 2;
    }
    
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
    
    // Просмотр комиссии счетчику потребителя
    function viewComissionConsumerCounter() public view onlyConsumer 
    returns(uint){
        return consumerCounterTransactionCost * 2;
    }
    
    // Проверка выплаты комиссии счетчику потребителя
    function controlCommissionConsumerCounter() public view onlyConsumerCounter returns(
        string memory, uint, string memory, uint){
            uint difference = balanceConsumerCounterAfter - balanceConsumerCounterBefore;
            uint installPayment = consumerCounterTransactionCost * 2;
            return ('Must be pay',installPayment,
            'fact payment',difference);
    }
    
}