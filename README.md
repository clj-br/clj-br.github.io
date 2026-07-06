# clj-br.github.io
clj-br website 

# Flutter web

Este repositorio tambem pode rodar como um app Flutter web via
ClojureDart, seguindo o exemplo
`babashka/sci/examples/cljd-flutter-repl`.

```shell
mise run flutter-init
mise run flutter-compile
mise run flutter-run
```

Durante o desenvolvimento:

```shell
mise run flutter-web
```

O REPL SCI dentro do app renderiza widgets Flutter diretamente e tambem
renderiza vetores Hiccup em um widget `flutter_html`.

# Contribuindo

Inicie um REPL

```shell
clj
=>
```

Carregue o servidor e inicie ele

```clojure
(do 
  (require 'clj-br.website :reload)
  (clj-br.website/-main))
```

Repita esse comando para carregar as alterações feitas no arquivo
sem precisar reiniciar o REPL.

O servidor ficará disponivel em localhost:8080

# Estrutura do projeto

O codigo-fonte principal do app Flutter web fica em
`src/repl/app.cljd`. Ele segue o exemplo `cljd-flutter-repl` do SCI:
um editor dentro do app avalia codigo Clojure via SCI e mostra o
resultado no painel de preview.

Existem dois caminhos de renderizacao:

- widgets Flutter criados diretamente por ClojureDart;
- vetores Hiccup convertidos para HTML e exibidos por `flutter_html`.

Arquivos gerados pelo ClojureDart e pelo Flutter ficam ignorados no Git,
exceto `web/`, que guarda a configuracao web do projeto Flutter.
