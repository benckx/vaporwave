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
- Improve save file comparison logic to better take into account cases where only the srm file was modified (the state
  file being the one actually containing the save states)
- Manage PPSSPP save files
- Save sync: Add a function that detects - for each ROM - the most recent save from all the locations, and uploads it to
  all locations. This could run in the background and sync on regular basis
- Manage multiple ROMs folders
- Manage save files and ROMs as 2 separate folders (to make it easier to backup save files on Dropbox, without uploading
  ROMs files )
- Sync with local RetroArch installation
- Add a nice app icon

# How To

## Run

* Download the last zip on [releases page](https://github.com/benckx/vaporwave/releases).
  * `dist-win64-jre-*` contains the JAR, a JRE version 8 and an executable bat file. The bat file is a simple shortcut
    that launches the JAR file on the packaged JRE.
  * `dist-linux-*` contains the JAR and a sh file, which simply contains the command `java -jar vaporwave.jar`.
  * The JAR file is identical in the 2 zip files. The one containing the JRE is just easier to use.
* Decompress somewhere on your computer (it can be moved later)
* On Windows:
  * Run the bat file `vaporwave.bat`
  * On the Windows 10 warning message, click "More Info", then "Run anyway"
* On Linux / macOS:
  * Run the JAR file on Java 8: `java -jar vaporwave.jar`
* From the code:
  * Import the project as a Gradle project in your IDE of choice, and run the Main

## Build

### Build the project

```
./gradlew clean build
```

### Package the zip files

```
./dist.sh
```

## Update dependencies

```
 ./gradlew dependencyUpdates --refresh-dependencies
```

## Add a mock device

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
- Tested on [RetroOZ](https://github.com/southoz/RetroOZ/wiki) with a PowKiddy RGB10Max 
