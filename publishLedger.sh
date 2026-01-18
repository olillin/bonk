LEDGER_VERSION="$(grep -oP '(?<=ledger_version=)[ A-Za-z0-9\.-]+' gradle.properties)"

# Prevent running multiple times
if [[ -d "$HOME/.m2/repository/com/github/quiltservertools/ledger/$LEDGER_VERSION+local" ]]; then
  echo "Found Ledger in mavenLocal"
else
  echo "Publishing Ledger to mavenLocal..."

  LEDGER_DIR="$(mktemp -d -t ledger-src-XXXXXXXX)"

  echo "Cloning Ledger into $LEDGER_DIR"
  git clone https://github.com/QuiltServerTools/Ledger "$LEDGER_DIR/ledger"

  if [[ -d "$LEDGER_DIR/ledger" ]]; then
    echo "Publishing Ledger to mavenLocal"
    (cd "$LEDGER_DIR/ledger" && ./gradlew publishToMavenLocal)
    echo "Ledger published successfully"
  else
    echo "Failed to clone Ledger"
  fi
fi