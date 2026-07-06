#!/usr/bin/env bash
set -euo pipefail

clojure -M:cljd compile repl.app
flutter build web --base-href /
cp -R build/web/. .
