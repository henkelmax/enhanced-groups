# Simple Voice Chat Enhanced Groups

A server side Fabric mod providing useful features to Simple Voice Chat groups.

## Features

### Instant Groups

The command `/instantgroup`  automatically adds every player nearby to a group,
if they are not already in a group.
The default range of this is 128 blocks.
Alternatively you can provide the range as the first argument of the command: `/instantgroup 256`.

![](https://media0.giphy.com/media/dDEqQ6hIwd9NvWscGF/giphy.gif)

### Group Summary

Players that join the server will get a quick summary of all active voice chat groups.

## Config Options

The config file is located at `config/enhancedgroups/enhancedgroups.properties`.

| Name                                     | Default Value          | Description                                                               |
|------------------------------------------|------------------------|---------------------------------------------------------------------------|
| `default_instant_group_range`            | `128`                  | The default range for the instant group command if no range was provided  |
| `instant_group_name`                     | `Instant Group`        | The name of the instant group                                             |
| `instant_group_command_permission_level` | `0`                    | The permission level of the `instantgroup` command                        |
| `group_summary`                          | `true`                 | If a summary of all groups should be shown when a player joins the server |
