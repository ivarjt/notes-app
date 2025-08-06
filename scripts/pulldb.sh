#!/bin/zsh

# Config
PACKAGE_NAME="se.itdata.notes"
DB_NAME="note_db.db"
DB_DIR="$HOME/db"
DEST_PATH="$DB_DIR/$DB_NAME"
DB_FILES=("$DB_NAME" "${DB_NAME}-wal" "${DB_NAME}-shm")

# Colors
GREEN='\e[32m'
RED='\e[31m'
YELLOW='\e[33m'
BLUE='\e[34m'
RESET='\e[0m'

mkdir -p "$DB_DIR"

echo -e "${BLUE}[INFO]${RESET} Pulling database files from $PACKAGE_NAME..."
adb root >/dev/null 2>&1

for FILE in "${DB_FILES[@]}"; do
    echo -e "${BLUE}→ Pulling $FILE...${RESET}"
    adb shell "run-as $PACKAGE_NAME cat /data/data/$PACKAGE_NAME/databases/$FILE" > "$DB_DIR/$FILE" 2>/dev/null
done

if [[ -f "$DEST_PATH" ]]; then
    echo -e "${GREEN}[OK]${RESET} Database files saved to: $DB_DIR"
    echo -e "${BLUE}[INFO]${RESET} Opening in DB Browser for SQLite..."
    sqlitebrowser "$DEST_PATH"
else
    echo -e "${RED}[ERROR]${RESET} Failed to pull the main DB file. Check if the app is debuggable and the database exists."
fi

