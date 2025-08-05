#!/bin/zsh

# Config
PACKAGE_NAME="se.itdata.notes"
DB_NAME="note_db.db"
DB_DIR="$HOME/db"
# === Derived Files ===
DB_FILES=("$DB_NAME" "${DB_NAME}-wal" "${DB_NAME}-shm")

# Toggle false if you dont want to delete local or remote
DELETE_LOCAL=true
DELETE_REMOTE=true

# Colors
GREEN='\e[32m'
RED='\e[31m'
YELLOW='\e[33m'
BLUE='\e[34m'
RESET='\e[0m'

if $DELETE_REMOTE; then
    echo -e "${BLUE}[INFO]${RESET} Deleting database from Android device: $PACKAGE_NAME"
    adb root >/dev/null 2>&1
    for FILE in "${DB_FILES[@]}"; do
        echo -e "${BLUE}→ Removing $FILE from device...${RESET}"
        adb shell "run-as $PACKAGE_NAME rm /data/data/$PACKAGE_NAME/databases/$FILE" 2>/dev/null
    done
    echo -e "${GREEN}[OK]${RESET} Android DB files removed (if they existed)."
fi

if $DELETE_LOCAL; then
    echo -e "${BLUE}[INFO]${RESET} Deleting local database files from: $DB_DIR"
    for FILE in "${DB_FILES[@]}"; do
        FILE_PATH="$DB_DIR/$FILE"
        if [[ -f "$FILE_PATH" ]]; then
            echo -e "${BLUE}→ Removing $FILE_PATH${RESET}"
            rm "$FILE_PATH"
        else
            echo -e "${YELLOW}[WARNING]${RESET} File not found: $FILE_PATH"
        fi
    done
    echo -e "${GREEN}[OK]${RESET} Local DB files removed (if they existed)."
fi

