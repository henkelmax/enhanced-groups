# Simple Voice Chat Enhanced Groups

This server side Fabric mod adds the command `/instantgroup`. This command automatically adds every player nearby to a
group, if they are not already in a group. The default range of this is 128 blocks. Alternatively you can provide the
range as the first argument of the command: `/instantgroup 256`.

![](https://media0.giphy.com/media/dDEqQ6hIwd9NvWscGF/giphy.gif)

## Config Options

The config file is located at `config/enhancedgroups/enhancedgroups.properties`.

| Name                                       | Default Value   | Description                                                  |
|--------------------------------------------|-----------------|--------------------------------------------------------------|
| `default_instant_group_range`              | `128`           | The default range of the command                             |
| `instant_group_name`                       | `Instant Group` | The name of the group that's created                         |
| `instant_group_command_permission_level`   | `0`             | The permission level needed for the `instantgroup` command   |
