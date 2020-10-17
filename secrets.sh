#!/bin/bash

# ##############################################################################
# Secrets installation script for AndroidGameCollection
# ##############################################################################

# Declare variables
bashDir="$(dirname ${BASH_SOURCE[0]})";
scriptDir="$(cd ${bashDir} >/dev/null 2>&1 && pwd)";
cp="cp -vf";
games=(
  "Emojidoku"
  "FlagQuiz"
  "FlagQuizEU"
  "FlagQuizUSA"
  "FlappyBlock"
  "FloodIt"
  "Sudoku"
  "Superpong"
  "TapTapTap"
  "TicTacToe"
);

# Print the directory paths
echo "Welcome ${USER}, to secrets installation script for AndroidGameCollection.";

# Read the full path to the secrets directory
read -p "==> Please enter full path to the secret files directory: " dirPath;
echo "- Source directory: ${dirPath}";
echo "- Target directory: ${scriptDir}";

# Print actions to be taken, for verification
for game in "${games[@]}"; do 
  echo "VERIFY: ${dirPath}/${game}_Keys.xml => ${scriptDir}/${game}/src/main/res/values/keys.xml"; 
done;

# Ask permission from user and copy files
read -p "==> Are you sure to copy the files? (files will be overwritten) [y/N] " yn;
if [[ $yn =~ [yY](es)* ]]; then
  for game in "${games[@]}"; do 
    $cp "${dirPath}/${game}_Keys.xml" "${scriptDir}/${game}/src/main/res/values/keys.xml"; 
  done;
else
  echo "==> Script was aborted by the user."
  exit;
fi

# Print info that execution finished
echo "==> Script was finished succesfully.";
