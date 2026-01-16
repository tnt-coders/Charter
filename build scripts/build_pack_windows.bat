@echo off
set MVN_CMD=mvn
where %MVN_CMD% >nul 2>nul
if %errorlevel% neq 0 (
    set MVN_CMD="C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.1.1\plugins\maven\lib\maven3\bin\mvn.cmd"
)

CALL %MVN_CMD% -P lwjgl-natives-windows-amd64^
 -Dlwjgl.natives=natives-windows^
 clean package

tar.exe -a -cf "Charter-windows-%version%.zip" -C "target" "Charter"

jpackage -i target/Charter^
 --main-jar Charter.jar^
 --win-dir-chooser^
 --win-shortcut^
 --icon src/main/resources/icon.ico^
 --app-version "%version%"^
 --vendor Lordszynencja^
 --license-file LICENSE^
 --copyright "SBD 3-Clause"^
 -n Charter^
 --file-associations AssociationProjectFile.properties^
 -t msi^
 -d target

CD target
REN "Charter-%version%.msi" "Charter-windows-%version%-installer.msi"
MOVE "Charter-windows-%version%-installer.msi" ../
CD ../