package MQTTConnection;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class SubscriberCallback implements MqttCallback {

    public double energy;

    @Override
    public void connectionLost(Throwable throwable) {
        //System.out.println("Error connection");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage)  {
        //System.out.println("topic " + topic + " msg " + mqttMessage);

        try {
            energy = Double.parseDouble(String.valueOf(mqttMessage));
            //System.out.println("energy in messageArrived method topic : " + topic + " energy " + energy);
        }catch (NullPointerException e){
            System.out.println("Null value energy");
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

}
