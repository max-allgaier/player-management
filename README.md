# PlayerManagement
Minecraft plugin that assists server owners in moderating players.

## Features 
* PostgreSQL integration
* Command line interface
* Ban system
* Async
* Highly configurable (YAML)

## Build
1. Install and setup [Docker Desktop](https://www.docker.com/products/docker-desktop/)
2. Run `git clone https://github.com/max-allgaier/player-management.git`
3. Run `mvn clean package`

## Commands
`ban <player> [duration] [reason]`
* Bans a player for a certain amount of time with a reason
* Duration and reason are optional arguments (duration defaults to permanent)
* Example: `ban rulebreaker123 7days12hours30minutes Breaking rules`
  * Bans the player with username `rulebreaker123` for 7 days, 12 hours, and 30 minutes with the reason `Breaking rules`

`unban <player> [reason]`
* Unbans a player with a reason
* Reason is an optional argument
* Example: `unban rulebreaker123 Appealed in ticket #6113`
  * Unbans the player with username `rulebreaker123` with the reason `Appealed in ticket #6113`

`mute <player> [duration] [reason]`
* Mutes a player for a certain amount of time with a reason
* Duration and reason are optional arguments (duration defaults to permanent)
* Example: `mute rulebreaker123 7days12hours30minutes Breaking rules`
  * Mutes the player with username `rulebreaker123` for 7 days, 12 hours, and 30 minutes with the reason `Breaking rules`

`unmute <player> [reason]`
* Unmutes a player with a reason
* Reason is an optional argument
* Example: `unmute rulebreaker123 Appealed in ticket #6113`
  * Unmutes the player with username `rulebreaker123` with the reason `Appealed in ticket #6113`
