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

Esse comando compila `src/repl/app.cljd`, executa `flutter build web
--base-href /` e copia `build/web/` para a raiz. Depois disso, commit os
arquivos alterados/gerados da raiz.

## Estrutura

- `src/repl/app.cljd`: codigo-fonte principal do app Flutter web.
- `web/`: configuracao de entrada do Flutter web.
- `index.html`, `main.dart.js`, `assets/`, `canvaskit/`, `icons/`:
  build estatico publicado pelo GitHub Pages.
- `scripts/publish_flutter_to_pages.sh`: script usado por
  `mise run github-pages-build`.
- `src/clj_br/website.clj`: gerador estatico antigo, mantido por enquanto.

O app segue o exemplo `babashka/sci/examples/cljd-flutter-repl`: um
editor dentro do app avalia codigo Clojure via SCI e mostra o resultado
no painel de preview.

Existem dois caminhos de renderizacao:

- widgets Flutter criados diretamente por ClojureDart;
- vetores Hiccup convertidos para HTML e exibidos por `flutter_html`.

## Gerador antigo

O antigo servidor Pedestal ainda pode ser iniciado com:

```shell
mise run static-site
```

Ele fica disponivel em `http://localhost:8080`, mas o caminho principal
do site agora e o build Flutter web publicado na raiz.
