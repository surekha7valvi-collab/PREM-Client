# PREM Client (by _prem_king_72)

Simple liquid-glass PvP client for Minecraft 1.21.11 (Fabric). Touch-friendly, works on phone launchers.

Modules: FPS, CPS, Ping, Coordinates, Direction, Keystrokes, Armor durability.
Open the menu with the PREM button on the title screen / pause menu.
The menu scrolls with the mouse wheel AND with big ^ / v buttons, so it never gets stuck.

## Build it from your phone (free, GitHub Actions)
1. Make a free GitHub account, create a new empty repository (Add a README = off).
2. Add file > Upload files: select ALL the files from this zip's top level
   (build.gradle, settings.gradle, gradle.properties, fabric.mod.json, the 5 .java files, README.md).
3. Add file > Create new file. In the name box type exactly:  .github/workflows/build.yml
   Paste the text from build.yml.txt into it and commit.
4. Open the Actions tab. The "Build PREM Client" run starts by itself (wait ~3-5 minutes).
5. When it is green, open the run and download the artifact "PREM-Client-jar" (a zip with the .jar).
6. Put the .jar and Fabric API (1.21.11) in your mods folder.

If the run is red: open it, copy the red error lines and send them to me - I will fix the code.
If it says a version does not exist, change yarn_mappings / fabric_api_version in gradle.properties
to the newest 1.21.11 versions listed on fabricmc.net/develop.
