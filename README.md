### VATSIM Notify

This project allows Discord to be able to recieve notifications when a controller logs into VATSIM.
Filtering is also supported, so you will only see alerts for specific airports/facilities.

#### Notice

This is not an API of any sort, I would highly recommend against using this in another application. 
Most of this project is incomplete (specifically the Controller model). It works as it is, if you modify
it, it may not work the same or at all.

#### Configuration File

| Setting | Description |
| ------- | :---------: |
| DISCORD_TOKEN | The Bot token you want the Discord bot to login [with](https://discordapi.com).|
| POSITION_OPEN_NOTIFICATION | The message you want to be sent when a controller connects to the VATSIM network|
| POSITION_CLOSED_NOTIFICATION | The message you want to be sent when a controller who was online goes offline (optional) |
| CLOSING_NOTIFICATIONS | Enable (by setting it to yes or no) controller logoff notifications. |
| POSITIONS | Positions you want the discord bot to announce (example for KMEM, use MEM) |
| FILTER_POSITIONS | Enable position filtering (yes or no) |
| TEXT_CHANNEL | The channel id of the channel you want the bot to use.
