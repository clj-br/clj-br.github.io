#!/usr/bin/env bash
set -euo pipefail

clojure -M:cljd compile repl.app
cat > lib/main.dart <<'DART'
import 'cljd-out/repl/app.dart' as app;

void main() {
  app.main();
}
DART
flutter build web --base-href /
cp -R build/web/. .
