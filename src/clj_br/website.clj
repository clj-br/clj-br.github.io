(ns clj-br.website
  (:require [io.pedestal.http :as http]
            [hiccup2.core :as h]
            [io.pedestal.http.route :as route]
            [ring.util.mime-type :as mime]
            [clojure.pprint :as pp])
  (:import (java.nio.charset StandardCharsets)))

(def links
  [{:titulo "Grupo no Telegram"
    #_#_:descricao ""
    :href   "https://t.me/clojurebrasil"}
   {:titulo "Duvidas e discussões"
    #_#_:descricao ""
    :href   "https://github.com/clj-br/forum/discussions"}])

(defn index
  [req]
  (let [head [:head
              [:meta {:charset (str StandardCharsets/UTF_8)}]
              [:meta {:name    "viewport"
                      :content "width=device-width, initial-scale=1.0"}]
              [:meta {:name    "description"
                      :content "clj-br"}]
              [:script {:src  "https://cdn.jsdelivr.net/gh/borkdude/scittle@0.0.1/js/scittle.js"
                        :type "application/javascript"}]
              [:script {:crossorigin "true"
                        :src         "https://unpkg.com/react@17/umd/react.production.min.js"}]
              [:script {:crossorigin "true"
                        :src         "https://unpkg.com/react-dom@17/umd/react-dom.production.min.js"}]
              [:script {:src  "https://cdn.jsdelivr.net/gh/borkdude/scittle@0.0.1/js/scittle.reagent.js"
                        :type "application/javascript"}]
              [:title "clj-br"]
              [:style (h/raw "
 // div::before {content:'[:div \"'}
 // div::after {content:'\"]'}
 title::before {content:'[:title \"'}
 title::after {content:'\"]'}
 // h1::before {content:'[:h1 \"'}
 // h1::after {content:'\"]'}
 ul::before {content:'[:ul '}
 ul::after {content:']'}
 p::before {content:'[:p \"'}
 p::after {content:'\"]'}
 pre::before {content:'[:pre \"'}
 pre::after {content:'\"]'}
 code::before {content:'[:code \"'}
 code::after {content:'\"]'}
 a::before {content:'[:a \"'}
 a::after {content:'\"]'}
 aside::before {content:'[:aside \"'}
 aside::after {content:'\"]'}
 blockquote::before {content:'[:blockquote \"'}
 blockquote::after {content:'\"]'}
 em::before {content:'[:em \"'}
 em::after {content:'\"]'}
 strong::before {content:'[:strong \"'}
 strong::after {content:'\"]'}")]]





        lista-principal (into [:ul
                               {:style {:list-style-type "none"}}]
                          (for [{:keys [titulo
                                        descricao
                                        href]} links]
                            [:li
                             {:style {:padding "1em"}}
                             [:a {:target "_blank"
                                  :rel    "noreferrer noopener"
                                  :href   href}
                              titulo]]))
        body [:body
              {:style {:max-width "38em"
                       :margin    "auto"}}
              [:div
               {:style {:text-align "center"}}
               [:img {:style {:width  "20vh"
                              :height "20vh"}
                      :src   "/logo.jpg"}]]
              [:h1 "Comunidade Clojure Brasil"]
              #_[:p "TODO: Descrição"]
              [:div
               {:id "playground"}
               lista-principal]
              [:textarea
               {:id   "editor"
                :cols 60
                :rows 20}
               (with-out-str
                 (pp/pprint lista-principal))]
              [:div {:id "playground"}]
              [:button {:id "go"}
               "go"]
              [:script (h/raw "
let doThing = () => {
  try {
  let editor = document.getElementById('editor')
  let component = scittle.core.eval_string(editor.value)
  window.loop_component = component
  scittle.core.eval_string(`
  (require '[reagent.core :as r]
           '[reagent.dom :as rdom])
  (def state (r/atom {:clicks 0}))
  (rdom/render js/window.loop_component (.getElementById js/document \"playground\"))
  `)
  } catch {
  
  }
  setTimeout(() => doThing(), 1000)
}
doThing();
let button = document.getElementById('go')
button.onclick = doThing;
              ")]
              #_[:script {:type "application/x-scittle"}
                 (h/raw (str '(do
                                (require '[reagent.core :as r]
                                  '[reagent.dom :as rdom])

                                (def state (r/atom {:clicks 0}))

                                (def my-component
                                  (read (.-value (js/document.getElementById "editor"))))

                                (rdom/render [my-component] (.getElementById js/document "playground")))))]]]
    {:body    (->> [:html
                    {:lang "pt-br"}
                    head
                    body]
                (h/html {:mode :html})
                (str "<!DOCTYPE html>\n"))
     :headers {"Content-Type"            (mime/default-mime-types "html")
               "Content-Security-Policy" ""}

     :status  200}))

(def routes
  `#{["/" :get index]})

(defonce *server (atom nil))

(defn -main
  [& _]
  (swap! *server (fn [st]
                   (some-> st http/stop)
                   (-> {::http/routes    (fn []
                                           (route/expand-routes @#'routes))
                        ::http/port      8080
                        ::http/join?     false
                        ::http/file-path "resources"
                        ::http/type      :jetty}
                     http/default-interceptors
                     http/dev-interceptors
                     http/create-server
                     http/start))))
