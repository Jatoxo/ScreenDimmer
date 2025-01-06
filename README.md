![image](https://github.com/user-attachments/assets/6ee72a55-8645-482b-97c1-41726a258b98)


## Screen Dimmer 
Screen Dimmer (I called it ScreenShade, but that was already taken :( ) is an application for Windows that can reduce the brightness of your screen beyond the limits of your monitor.
It does this by placing a translucent dark window over the screen. 

This is a spiritual successor to [Dimmer](https://www.nelsonpires.com/software/dimmer), which lacked support for any external control, so I was inspired to make my own Dimmer app to add this and fix various other annoyances
with the original Dimmer.

## Features
- Supports multiple monitors
- Very lightweight
- Hides to system tray
- Supports external control:
  - via MQTT  
    Easily control the dim level with Homeassistant et al.
  - via raw TCP  
    Write your own app that sends dim levels to Dimmer
- Option to start minimized
- Command line arguments for overriding config


## Config file
Dimmer creates a default config file (config.properties) at the working directory. 
You can edit various settings in there:
```properties
# ScreenShade Configuration File

# Configured values are overridden by command line arguments.

# Dim level when starting the application 0 -> No dimming, 90 -> Maximum dimming
initialDimLevel = 0

# Whether to enable the TCP server
# Send raw integer values to port 8777 to set the master dim level
enableServer = false

# Whether to not show the GUI on startup
startMinimized = false

# MQTT Configuration
# All values except username and password must be provided,
# otherwise MQTT will be disabled.
# Send integer value as payload to the topicSet topic to set the master dim level
# Current dim level is published to the topicState topic

# mqtt.clientId = ScreenShade
# mqtt.broker = tcp://localhost:1883
# mqtt.username = your_username
# mqtt.password = your_password
# mqtt.topicState = screenshade/state
# mqtt.topicSet = screenshade/set
```


## Integrating with Homeassistant
![demonstration](https://github.com/user-attachments/assets/79d92476-d815-41d1-a029-878ca4bb572a)

Since this app acts as an MQTT client, it can easily integrate with Homeassistant

Prerequisites:
- Makes sure you have [setup an MQTT Broker](https://www.home-assistant.io/integrations/mqtt/#setting-up-a-broker)
- Edit the dimmer config file to point at the broker (your homeassistant url), enter the username and password you chose for the mqtt user on homeassistant and choose the state and set topics
  
Here's some examples of how you could integrate this app with homeassistant. These are code snipped from the `configuration.yaml` file


### Example 1:
Simple dimming slider
```yaml
#MQTT Sensor captures the current dim level being published by the app
mqtt:
  - sensor:
      name: "Monitor Dim Level"
      # This will be whatever you set in the Dimmer config file as the "mqtt.topicState".
      # It can be anything but something similar to this is fairly conventional:
      state_topic: "homeassistant/sensor/pcmonitor/dimlevel/state"

# Template number uses the sensor for its value and sends the dim levels when you change the number
# This can then be set up as a slider in your dashboard
template:
  - number:
      - name: PC Monitor Dim Level
        unique_id: pc_monitor_dim_level
        optimistic: true
        state: "{{states('sensor.pcmonitor_dim_level') | int}}"
        set_value:
          - action: mqtt.publish
            data:
              payload: "{{value | int}}"
              topic: "homeassistant/sensor/pcmonitor/dimlevel/set"
        step: 1
        min: 0
        max: 90
```

### Example 2:
Creating a light entity with brightness control. Turning on and off the monitor  
requires some solution to handle turning on and off [(Check out HASS.Agent)](https://github.com/hass-agent/hass.agent)

```yaml
#MQTT Sensor captures the current dim level being published by the app
mqtt:
  - sensor:
      name: "Monitor Dim Level"
      # This will be whatever you set in the Dimmer config file as the "mqtt.topicState".
      # It can be anything but something similar to this is fairly conventional:
      state_topic: "homeassistant/sensor/pcmonitor/dimlevel/state"

#Template light creates a light entity that uses the MQTT sensor to retrieve the brightness level
light:
  - platform: template
    lights:
      pc_monitor:
        unique_id: pc_monitor
        friendly_name: "PC Monitor"
        # This template uses the sensor created above, and maps the dim level (0-90) to a brightness level (255-0)
        level_template: "{{((90 - (states('sensor.monitor_dim_level') | int)) / 90) * 255}}"
        set_level:
          # Publish to the topic configured in Dimmer under mqtt.topicSet
          - action: mqtt.publish
            data:
              # This template receives the brightness level from homeassistant (0-255),
              # and converts it into a dim level for dimmer (90-0)
              payload: "{{((255 - brightness) / 255 * 90)|int }}"
              topic: "homeassistant/sensor/pcmonitor/dimlevel/set"
        # You could use a program like HASS.Agent for toggling on and off the monitor,
        # or just insert dummy actions that do nothing
        turn_on:
          - action: button.press
            entity_id: button.pc_monitor_on
        turn_off:
          - action: button.press
            entity_id: button.pc_monitor_off
```

 


