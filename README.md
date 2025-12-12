# PlayerManagement
Minecraft plugin that assists server owners in moderating players.

## Features 
* PostgreSQL integration
* Command line interface
* Ban system
* Mute system
* Async (won't lag the server thread)
* Highly configurable (through YAML)

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

## Configuration
Almost everything is configurable. 
Below is the YAML configuration (generated automatically on first startup):
```yaml
database:
  type: "postgres"
  host: "host"
  port: 12345
  database-name: "database_name"
  username: "username"
  password: "password"

duration:
  keywords:
    permanent: ["permanently", "permanent", "perm"]
    day: ["days", "day", "d"]
    hour: ["hours", "hour", "hrs", "hr"]
    minute: ["minutes", "minute", "mins", "min", "m"]
    second: ["seconds", "second", "secs", "sec", "s"]
  display:
    permanent: "permanent"
    second-singular: "second"
    second-plural: "seconds"
    day-singular: "day"
    day-plural: "days"
    minute-singular: "minute"
    minute-plural: "minutes"
    hour-singular: "hour"
    hour-plural: "hours"
  suggestions: ["permanent", "365days", "30days", "1day", "12hours", "6hours", "30minutes"]

messages:
  internal-error: "Something went wrong."
  console-display-name: "[CONSOLE]"
  no-reason-fallback: "no reason provided"

  player-banned: "{issuer} banned {target} with reason {reason}."
  player-unbanned: "{issuer} unbanned {target} with reason {reason}."
  player-already-banned-error: "{target} is already banned."
  player-is-not-banned-error: "{target} is not banned."
  ban-screen: |
    You currently banned!
    Ban duration: {duration}

  player-muted: "{issuer} muted {target} with reason {reason}."
  player-unmuted: "{issuer} unmuted {target} with reason {reason}."
  player-already-muted-error: "{target} is already muted."
  player-is-not-muted-error: "{target} is not muted."
  muted-message: |
    You are currently muted!
    Mute duration: {duration}

# DO NOT TOUCH VERSION.
version: "0.0.1" 
```
