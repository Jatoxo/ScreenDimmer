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
                      String topicAvailability,
                      String username,
                      String password
    ) throws MqttException {
        this.screenShade = screenShade;
        this.TOPIC_STATE = topicState;

        client = new MqttClient(broker, clientId, new MemoryPersistence());


        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        if(topicAvailability != null) {
            options.setWill(topicAvailability, "offline".getBytes(), 1, false);
        }



        if(username != null && password != null) {
            System.out.println("Connecting to MQTT broker (Using authentication): " + broker);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
        } else {
            System.out.println("Connecting to MQTT broker (No authentication): " + broker);
        }

        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                System.out.println("MQTT Client connected to broker: " + serverURI);


                try {
                    // QoS as 1: Messages should be received at least once, but duplicates are fine, so downgrading from QoS 2 is okay
                    client.subscribe(topicSet, 1, (topic, message) -> onMessageReceived(topic, message));

                    //Publish an "available" message
                    if(topicAvailability != null) {
                        client.publish(topicAvailability, "online".getBytes(), 1, false);
                    }
                } catch(MqttException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection with broker lost: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {}

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });


        client.connect(options);

        //if(client.isConnected()) {
        //    System.out.println("Successfully connected to MQTT broker");
        //}


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
