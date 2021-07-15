package MQTTConnection;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTSubscriber {

    private MqttAsyncClient mqttAsyncClient;
    private SubscriberCallback subscriberCallback = new SubscriberCallback();;


    public Double mqttSubscriber(String topic, String clientID){
        try {
            mqttAsyncClient = new MqttAsyncClient("tcp://localhost:1883",clientID);
//            subscriberCallback
            mqttAsyncClient.setCallback(subscriberCallback);
            IMqttToken token = mqttAsyncClient.connect();
            token.waitForCompletion();
            //System.out.println("msg " + token.getUserContext());

            mqttAsyncClient.subscribe(topic, 1);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return subscriberCallback.energy;
    }


    public Double receiveMqttMsg(String topic, /*MqttAsyncClient mqttAsyncClient,*/
                                 SubscriberCallback subscriberCallback){
        try {
//            mqttAsyncClient = new MqttAsyncClient("tcp://localhost:1883",clientID);
//            subscriberCallback = new SubscriberCallback();
//            mqttAsyncClient.setCallback(subscriberCallback);
//            IMqttToken token = mqttAsyncClient.connect();
//            token.waitForCompletion();
            //System.out.println("msg " + token.getUserContext());

            mqttAsyncClient.subscribe(topic, 1);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return subscriberCallback.energy;
    }

    public Double receivedMsg(){
        System.out.println("energy in receivedMsg method " + subscriberCallback.energy);
        return subscriberCallback.energy;
    }
}
