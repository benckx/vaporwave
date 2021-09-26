sh dist-clean.sh

gradleVersion=$(grep version gradle.properties)
appVersion=${gradleVersion#"version="}
jarFile=vaporwave-"${appVersion}".jar
distLinuxFolder=dist-linux-"${appVersion}"
distWin64Folder=dist-win64-jre-"${appVersion}"

echo "release: ${appVersion}"
echo "jarFile: ${jarFile}"

./gradlew clean build

mkdir $distLinuxFolder
mkdir $distWin64Folder

cp build/libs/"${jarFile}" "${distLinuxFolder}"/"${jarFile}"
cp build/libs/"${jarFile}" "${distWin64Folder}"/"${jarFile}"

mkdir "${distLinuxFolder}"/data
mkdir "${distWin64Folder}"/data

cp -rv data/extensions.csv "${distLinuxFolder}"/extensions.csv
cp -rv data/extensions.csv "${distWin64Folder}"/extensions.csv

cp -rv README.md "${distLinuxFolder}"
cp -rv README.md "${distWin64Folder}"

echo "java -jar ${jarFile}" >"${distLinuxFolder}"/game_jme.sh
echo 'jre1.8.0_251\\bin\\java -jar '"${jarFile}"'' >"${distWin64Folder}"/game_jme.bat

chmod +x "${distLinuxFolder}"/*.sh

echo 'jre1.8.0_251\\bin\\java -jar '"${jarFile}"'' >dist-win64-"${appVersion}"/vaporwave.bat

cp -r ~/Dev/jre1.8.0_251 "${distWin64Folder}"/

zip -r "${distLinuxFolder}".zip "${distLinuxFolder}"
zip -r "${distWin64Folder}".zip "${distWin64Folder}"
