# clj-br.github.io
clj-br website 

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
