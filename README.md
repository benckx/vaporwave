<a href="https://paypal.me/benckx/2">
<img src="https://img.shields.io/badge/Donate-PayPal-green.svg"/>
</a>

# About

*Vaporwave* is a tool to manage, synchronize and backup ROMs and save files between different devices (computer, retro
handhelds, smartphone, etc.) It's inspired in part from the e-books
manager [calibre](https://github.com/kovidgoyal/calibre).

Name is based on _Steam_ (as the idea is to manage games and save files consistently between devices); the term
*vaporwave* is also commonly associated with 80's/90's nostalgia, and therefore with retro gaming.

![](img/ui1.png)

# Features

- Navigate local ROMs collection
- Add device based on its SSH connection
- Compare ROMs between computer and devices
- Compare save files between computer and devices, and detect where the more recent save files are stored
- Download/Upload ROMs files from/to devices
- Download/Upload save files from/to devices
- Tested on [ArkOS](https://github.com/christianhaitian/arkos/wiki) with a RG351M device

# Future Changes

- Add proper executable for Windows, Linux and macOS
- Test/Add support for other devices and device OS (it's likely some OS store their ROMs differently)
- Add/Remove ROMs from collection
- Update local ROMs folder
- Improve UI/UX (file transfer progress bars, warning when user override save files that seems more recent, etc.)
- Save sync: Add a function that detects - for each ROM - the most recent save from all the locations, and uploads it to
  all locations. This could run in the background and sync on regular basis
- Sync with local RetroArch installation
- Add a nice app icon

# How To

## Run

For now, it must be run from the code or IDE. Executables for all OS will be added later.

## Build

```
./gradlew clean build
```

## Update dependencies

```
 ./gradlew dependencyUpdates --refresh-dependencies
```

## Add a mocked device

For test purpose, it's possible to add mock devices. Add the following to `data/devices.json`.

```json
{
  "type": "be.encelade.vaporwave.model.devices.MockDevice",
  "name": "mock",
  "mockDataFileName": "ls-result-test-02",
  "mockDataMd5FileName": "md5-test-02"
}
```

Test files can be customized.

# Change logs

## Version 1.0

- Navigate local ROMs collection
- Add device based on its SSH connection
- Compare ROMs between computer and devices
- Compare save files between computer and devices, and detect where the more recent save files are stored
- Download/Upload ROMs files from/to devices
- Download/Upload save files from/to devices
- Tested on [ArkOS](https://github.com/christianhaitian/arkos/wiki) with a RG351M device
