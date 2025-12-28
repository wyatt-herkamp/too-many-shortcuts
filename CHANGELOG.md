# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.0.16.beta0]
- 1.21.11 Support

## [0.0.15.beta1] (10-26-2025)
- Fix Broken Add Entry
- Ensure that the keybinding is unbound and remove from the maps before removing it.

## [0.0.15.beta0] (10-25-2025)
- Initial 1.21.9 and 1.21.10 support



## [0.0.14] (09-07-2025)
- Fixed Crash On 1.21.1 [#42](https://github.com/wyatt-herkamp/too-many-shortcuts/issues/42)
- Upgrade GitHub Actions 
- New Icon

## [0.0.13] (07-18-2025)
- Updated to Minecraft 1.21.8
## [0.0.12] (07-04-2025)
- Fixed [#29](https://github.com/wyatt-herkamp/too-many-shortcuts/issues/29)
- Possibly Fixed [#33](https://github.com/wyatt-herkamp/too-many-shortcuts/issues/33) this might be a change that gets reverted. I am curious to see if this breaks something
- Fixed [#38](https://github.com/wyatt-herkamp/too-many-shortcuts/issues/38) Not being able to reset unbound keybindings
- Updated to loom 1.11
## [0.0.11] (06-17-2025)
- Added Support for Minecraft 1.20.6, 1.21.1, 1.21.2, 1.21.3, 1.21.6
- Extracted a lot of API into a separate module.
- Config System is now in the API module.
- Fixed Alternative Escape Binding working with Mouse Buttons
- Fixed selectedKeyBinding not being returned to null after being set [#27](https://github.com/wyatt-herkamp/too-many-shortcuts/issues/27)
- Broke Most of the Translations, will be fixed in the next release.
- Abstracted Part of the Keybinding Screen so that I can support more versions

## [0.0.10] (06-14-2025)

- Updated to Minecraft 1.21.5
- Added a new keybinding to shift the hotbar left or right.
- Fabric Loader Version Update to 0.16.14
- Fabric Kotlin Version Update to 1.13.3+kotlin.2.1.21
- Kotlin Version Update to 2.1.21
- Added support for Minecraft ~~1.21.6-pre4~~ 1.21.6-rc.1
- Added support for Minecraft 1.21.3
- Fixed Bug Where Alternative Keybindings were not working with custom keybindings.

## [0.0.9] (2025-03-03)

- On modifier release. Before removing an active keybinding. Make sure it needs said modifier.
- Fixed Game Crash when using free binds list.  [#28](https://github.com/wyatt-herkamp/too-many-shortcuts/issues/28)

## [0.0.8] (2025-02-25)

- Fixed Rebinding to a modifier keeps previous
  modifiers [#25](https://github.com/wyatt-herkamp/too-many-shortcuts/issues/25)
- Fixed Unable to bind modifiers alone [#13](https://github.com/wyatt-herkamp/too-many-shortcuts/issues/13)
- Fixed reset is disabled when modifiers are present but key is the default
- Updated to Loom 1.10
- Updated Yarn Mappings build.8
- Updated Fabric API to 0.118
- Updated Fabric Kotlin to 1.13.1
- Stopped overwriting ControlsOptionsScreen completely to open TMSKeyBindsScreen
  Fixes [#20](https://github.com/wyatt-herkamp/too-many-shortcuts/issues/20)

## [0.0.7] (2024-12-29)

- Updated to 1.21.4

## [0.0.6] (2024-10-27)

- Updated to 1.21.3
- `togglePlayerModelPart` renamed to `setPlayerModelPart`
- Dropped Mouse Keybinding Support
- Fixed Crash due to Mixin Issue

## [0.0.5] (2024-08-21)

- Removed ` dev.kingtux.tms.api.PriorityKeyBinding` to use Amecs API PriorityKeyBinding
- Updated to Minecraft 1.21.1

## [0.0.4] (2024-07-22)

- Improve Keybindings Screen

## [0.0.3] (2024-07-05)

- Fixed bug where hitting the `esc` key would close the keybinding screen.
  Issue [#9](https://github.com/wyatt-herkamp/too-many-shortcuts/issues/9)

## [0.0.2] (2024-07-05)

- Rewrote the Keybinding Screen
- Fixed bug with ordering of hotbar items. Issue [#2](https://github.com/wyatt-herkamp/too-many-shortcuts/issues/2)

## [0.0.1] (2024-06-26)

Initial release.


[0.0.1]:https://github.com/wyatt-herkamp/too-many-shortcuts/releases/tag/0.0.1

[0.0.2]:https://github.com/wyatt-herkamp/too-many-shortcuts/releases/tag/0.0.2

[0.0.3]:https://github.com/wyatt-herkamp/too-many-shortcuts/releases/tag/0.0.3

[0.0.8]:https://github.com/wyatt-herkamp/too-many-shortcuts/releases/tag/0.0.8

[0.0.8]:https://github.com/wyatt-herkamp/too-many-shortcuts/releases/tag/0.0.9
[0.0.10]:https://github.com/wyatt-herkamp/too-many-shortcuts/releases/tag/0.0.10
[0.0.11]:https://github.com/wyatt-herkamp/too-many-shortcuts/releases/tag/0.0.11
