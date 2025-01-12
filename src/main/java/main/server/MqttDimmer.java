package main.server;
import main.ScreenShade;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class MqttDimmer {
    private final String TOPIC_STATE;

    private final MqttClient client;
    private final ScreenShade screenShade;

    public MqttDimmer(ScreenShade screenShade,
                      String clientId,
                      String broker,
                      String topicSet,
                      String topicState,
                      String username,
                      String password
    ) throws MqttException {
        this.screenShade = screenShade;
        this.TOPIC_STATE = topicState;

        client = new MqttClient(broker, clientId, new MemoryPersistence());


        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);

        if(username != null && password != null) {
            System.out.println("Connecting to MQTT broker (Using authentication): " + broker);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
        } else {
            System.out.println("Connecting to MQTT broker (No authentication): " + broker);
        }

        client.connect(options);




        if(client.isConnected()) {
            System.out.println("Successfully connected to MQTT broker");
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection to MQTT broker lost: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                System.out.println("Message arrived: " + topic + " - " + message.toString());
                //onMessageReceived(topic, message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("MQTT: Delivery complete");
            }
        });

        // QoS as 1: Messages should be received at least once, but duplicates are fine, so downgrading from QoS 2 is okay
        client.subscribe(topicSet, 1, this::onMessageReceived);

        screenShade.getMasterDimmer().addChangeListener(this::publishDimLevel);

        System.out.println("MQTT Client initialized: ");
        System.out.println("-  Client ID: " + clientId);
        System.out.println("-  Broker: " + broker);
        System.out.println("-  Subscribed to topic: " + topicSet);
        System.out.println("-  Publishing state to topic: " + topicState);
    }


    /**
     * Called when a brightness set message is received.
     */
    private void onMessageReceived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        //System.out.println("MQTT Client: Received payload: " + payload);

        try {
            int newDimLevelPercent = Integer.parseInt(payload);
            screenShade.setMasterDim(newDimLevelPercent);
            System.out.println("MQTT: Dim level set to: " + newDimLevelPercent + "%");

            // Publishing will happen automatically through the listener

        } catch (NumberFormatException e) {
            System.err.println("Invalid brightness value received: " + payload);
        }
    }

    // Publish the brightness state
    private void publishDimLevel(int dimLevel) {
        try {
            client.publish(TOPIC_STATE, String.valueOf(dimLevel).getBytes(), 1, false);
            System.out.println("Published dim level state: " + dimLevel);

        } catch (MqttException e) {
            System.err.println("Failed to publish dim level state: " + e.getMessage());
        }
    }

}
