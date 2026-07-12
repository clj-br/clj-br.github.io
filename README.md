# clj-br.github.io

Site da comunidade Clojure Brasil publicado em
`https://clj-br.github.io/`.

O site publicado e um app Flutter web gerado a partir de ClojureDart. A
raiz do repositorio contem o build estatico que o GitHub Pages serve.

## Desenvolvimento

Instale/use as ferramentas pelo mise:

```shell
mise install
```

Inicialize os arquivos gerados pelo Flutter/ClojureDart quando necessario:

```shell
mise run flutter-init
```

Compile o codigo ClojureDart:

```shell
mise run flutter-compile
```

Rode o app em modo desenvolvimento:

```shell
mise run flutter-web
```

Para abrir em outro navegador, nao abra `web/index.html` diretamente com
`file://`. O diretorio `web/` e entrada de build do Flutter; ele precisa
ser servido por HTTP:

```shell
mise run flutter-web-server
```

Depois abra `http://localhost:8081`.

## Publicacao

Para gerar o app Flutter web em `build/web/`:

```shell
mise run flutter-build-web
```

Para atualizar o que o GitHub Pages serve na raiz do repositorio:

```shell
mise run github-pages-build
```

Esse comando compila `src/clj_br/website.cljd`, executa `flutter build web
--base-href /` e copia `build/web/` para a raiz. Os arquivos gerados na
raiz (`index.html`, `main.dart.js`, `assets/`, `canvaskit/`, `icons/`,
etc.) sao ignorados pelo git no dia a dia -- fonte da verdade e `web/` +
`src/`. Para efetivamente publicar, adicione-os manualmente ignorando o
`.gitignore` antes do commit:

```shell
git add -f index.html main.dart.js flutter.js flutter_bootstrap.js \
  flutter_service_worker.js manifest.json version.json favicon.png \
  assets canvaskit icons
```

## Estrutura

- `src/clj_br/website.cljd`: codigo-fonte principal do app Flutter web
  (unica fonte do site -- substitui o antigo gerador Pedestal).
- `web/`: configuracao de entrada do Flutter web.
- `index.html`, `main.dart.js`, `assets/`, `canvaskit/`, `icons/`:
  build estatico publicado pelo GitHub Pages (gerado, gitignored).
- `scripts/publish_flutter_to_pages.sh`: script usado por
  `mise run github-pages-build`.

O app segue o exemplo `babashka/sci/examples/cljd-flutter-repl`: um
editor dentro do app avalia codigo Clojure via SCI (para o modo
ClojureDart widget) e o resultado aparece no painel de preview.

Existem dois modos de renderizacao:

- **ClojureDart widget**: widgets Flutter criados diretamente por
  ClojureDart, avaliados via SCI.
- **Hiccup/Reagent**: roda o runtime real Scittle + Reagent + React
  (o mesmo stack de clojurebrasil.com.br) embutido via `HtmlElementView`,
  em vez de emular Reagent dentro do ClojureDart.
