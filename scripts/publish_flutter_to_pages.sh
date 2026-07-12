#!/usr/bin/env bash
set -euo pipefail

clojure -M:cljd compile clj-br.website
cat > lib/main.dart <<'DART'
import 'cljd-out/clj-br/website.dart' as app;

void main() {
  app.main();
}
DART
flutter build web --base-href /
perl -0pi -e 's/_flutter\.loader\.load\(\{\s*serviceWorkerSettings:\s*\{.*?\}\s*\}\);/_flutter.loader.load();/s' build/web/flutter_bootstrap.js
cp -R build/web/. .
