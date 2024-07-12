(ns clj-br.website
  (:require [io.pedestal.http :as http]
            [hiccup2.core :as h]
            [io.pedestal.http.route :as route]
            [ring.util.mime-type :as mime]
            [clojure.pprint :as pp]
            [clojure.string :as string]
            [io.pedestal.interceptor :as interceptor]
            [clojure.java.io :as io]
            [cheshire.core :as json])
  (:import (java.nio.charset StandardCharsets)
           (java.io File)
           (java.net URLEncoder)))

(def links
  [{:titulo "Grupo no Telegram"
    :href   "https://t.me/clojurebrasil"}
   {:titulo "Duvidas e discussões"
    :href   "https://github.com/clj-br/forum/discussions"}
   {:titulo "Vagas e oportunidades de trabalho"
    :href   "https://github.com/clj-br/vagas"}])

(def theme-color
  "#1793d1")

(def background-color
  "#4d4d4d")
(def canonical
  "https://clj-br.github.io/")


(set! *warn-on-reflection* true)

(defmacro scittle!
  [& forms]
  (let [pp-forms (for [form forms]
                   (list `pp/pprint (list 'quote form)))
        out-forms (cons `with-out-str pp-forms)]
    `(h/raw ~out-forms)))

(def style "
headers > img {
  max-width: 20vh;
  max-height: 20vh;
}
body {
  text-align: center;
  padding: 2vh;
  max-width: 50em;
  margin: auto;
}
")

(def lista-principal
  (into [:ul.list-group.list-group-flush]
    (for [{:keys [titulo
                  href]} links]
      [:li.list-group-item
       [:a {:target "_blank"
            :rel    "noreferrer noopener"
            :href   href}
        titulo]])))

(defn index
  [req]
  (let [head [:head
              [:meta {:charset (str StandardCharsets/UTF_8)}]
              [:meta {:name    "viewport"
                      :content "width=device-width, initial-scale=1.0"}]
              [:meta {:name    "description"
                      :content "Clojure Brasil"}]
              [:meta {:name    "theme-color"
                      :content theme-color}]
              [:meta {:property "og:type" :content "website"}]
              [:meta {:property "og:description" :content "Comunidade Clojure Brasil"}]
              [:meta {:property "og:title" :content "Clojure Brasil"}]
              [:meta {:property "og:url" :content canonical}]
              [:meta {:property "og:image" :content (str canonical "/resources/logo.svg")}]
              [:title "Clojure Brasil"]
              [:link {:rel "stylesheet"
                      :href "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
                      :integrity "sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
                      :crossorigin "anonymous"}]
              [:link {:rel  "manifest"
                      :href (str "data:application/json,"
                              (URLEncoder/encode (json/generate-string
                                                   {:theme_color      theme-color
                                                    :start_url        canonical
                                                    :name             "Clojure Brasil",
                                                    :background_color background-color
                                                    :short_name       "clj-br"
                                                    :icons            [{:src   (str canonical "/resources/logo.svg")
                                                                        :sizes "192x192"
                                                                        :type  "image/png"}
                                                                       {:src   (str canonical "/resources/logo.svg")
                                                                        :sizes "512x512"
                                                                        :type  "image/svg+xml"}]
                                                    :display          "minimal-ui",
                                                    :manifest_version 2,
                                                    :version          "1"})
                                (str StandardCharsets/UTF_8)))}]
              [:link {:rel  "canonical"
                      :href canonical}]
              [:link {:rel "icon" :href "resources/logo.svg"}]
              [:style (h/raw style)]]
        body [:body
              [:headers
               [:img {:alt (str "Logotipo da clojure Brasil, que mistura o logotipo original, inspirado em yin yang"
                             ", com o formato de uma arara, mantendo as cores originais: tons leves de azul e verde.")
                      :src "resources/logo.svg"}]]
              [:h1 "Clojure Brasil"]
              [:p "Comece a aprender agora mesmo!"]
              [:ul.nav.nav-tabs
               {:style {:display         "flex"
                        :flex-wrap       "wrap"
                        :justify-content "center"}}
               (for [{:keys [codigo rotulo]} [{:codigo (with-out-str
                                                         (pp/pprint lista-principal))
                                               :rotulo "Website"}
                                              {:codigo (scittle!
                                                         (+ 1 2))
                                               :rotulo "Soma simples"}
                                              {:codigo (scittle!
                                                         (require '[reagent.core :as r])
                                                         (def *n (r/atom 0))
                                                         (defn contador []
                                                           [:div
                                                            [:div (str @*n)]
                                                            [:button.btn.btn-light.mt-1
                                                             {:onClick (fn [] (swap! *n inc))}
                                                             "Incrementar"]]))
                                               :rotulo "Contador"}
                                              {:codigo "(require '[reagent.core :as r])

(def *notas (r/atom {0 {:nota \"Conhecer Clojure\" :feito? true}
                     1 {:nota \"Aprender Clojure\"}}))

(def *nova-nota (r/atom \"\"))

(defn ui-root []
  [:div
   [:ol.list-group.mb-3.text-start
    (for [[id {:keys [nota feito?]}] @*notas]
      [:li.list-group-item.d-flex {:key id, :style {:gap \"8px\"}}
       [:input.form-check-input
        {:type \"checkbox\"
         :on-change #(swap! *notas update-in [id :feito?] not)
         :checked feito?}]
       [:span.flex-grow-1 nota]
       (when feito?
         [:button.btn.btn-danger.btn-sm.py-0 {:on-click (fn [] (swap! *notas dissoc id))} \"x\"])])]
[:form.input-group {:on-submit (fn [evt] (.preventDefault evt)
                             (swap! *notas assoc (random-uuid) {:nota @*nova-nota})
                         (reset! *nova-nota \"\"))}
   [:input {:class \"form-control\"
            :value @*nova-nota
            :on-change (fn [evt] (reset! *nova-nota (-> evt .-target .-value)))}]
[:button.btn.btn-outline-secondary \"+\"]]]) "

                                               :rotulo "Aplicativo de afazeres"}]]
                 [:li.nav-item
                  [:a.nav-link
                   {:class (when (= rotulo "Website") "active")
                    :data-value  codigo
                    :data-target "editor"
                    :onClick    (string/join ";\n"
                                              ["[...document.querySelectorAll(\".nav-link\")].map((el) => el.classList.remove(\"active\"))"
                                               "this.classList.add(\"active\")"
                                               "document.getElementById(this.dataset.target).value = this.dataset.value"
                                               "document.getElementById(this.dataset.target).onkeyup()"])}
                   rotulo]])]
              [:div.py-3
               {:style {:display "grid"
                        :grid-template-columns "1fr 1fr"
                        :gap "16px"}}
               [:textarea
                {:style        {:width "100%", :font-family "monospace", :font-size "12px"}
                 :id           "editor"
                 :autocomplete "off"
                 :spellcheck   false
                 :onkeyup      "this.dataset.state == 'done' ? this.dataset.state = 'idle' : null"
                 :rows         20}
                (with-out-str
                  (pp/pprint lista-principal))]
               [:div {:style {:border "1px solid var(--bs-secondary-bg)"}}
                [:div.p-3 {:id "playground"}]
                [:pre {:id "stderr"}]]]
              [:script {:src  "https://cdn.jsdelivr.net/gh/borkdude/scittle@0.0.2/js/scittle.js"
                        :type "application/javascript"}]
              [:script {:crossorigin "true"
                        :src         "https://unpkg.com/react@17/umd/react.production.min.js"}]
              [:script {:crossorigin "true"
                        :src         "https://unpkg.com/react-dom@17/umd/react-dom.production.min.js"}]
              [:script {:src  "https://cdn.jsdelivr.net/gh/borkdude/scittle@0.0.2/js/scittle.reagent.js"
                        :type "application/javascript"}]
              [:script {:type "application/x-scittle"}
               (scittle!
                 (require '[reagent.core :as r]
                   '[reagent.dom :as rdom])
                 (defn render
                   []
                   (let [stderr (.getElementById js/document "stderr")
                         editor (.getElementById js/document "editor")
                         dataset-state (-> editor .-dataset .-state)]
                     (when (or (= dataset-state js/undefined)
                               (= "idle" dataset-state))
                       (set! (-> editor .-dataset .-state) "loading")
                       (try
                         (let [component (-> js/window
                                           .-scittle
                                           .-core
                                           (.eval_string (.-value editor)))]
                           (rdom/render (cond
                                          (fn? component) [component]
                                          (var? component) [component]
                                          (vector? component) component
                                          :else [:pre (pr-str component)])
                             (.getElementById js/document "playground"))
                           (set! (.-innerText stderr) ""))
                         (catch :default ex
                           (set! (.-innerText stderr) (str (ex-message ex) "\n"
                                                        (str (ex-data ex)))))))
                     (set! (-> editor .-dataset .-state) "done"))
                   (js/setTimeout render 1000))
                 (render))]]]
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

(def not-found-interceptor
  (interceptor/interceptor
    {:name  ::not-found-interceptor
     :leave (fn [{:keys [request response]
                  :as   ctx}]
              (let [uri (-> request :uri (string/split #"\/"))
                    ^File f (apply io/file "." uri)
                    ^File f (if (.isDirectory f)
                              (apply io/file "." (concat uri ["index.html"]))
                              f)]
                (when (and (-> response :status #{200})
                        (-> ctx :request :request-method #{:get}))
                  (with-open [output-stream (io/output-stream f)]
                    (io.pedestal.http.impl.servlet-interceptor/write-body-to-stream (:body response) output-stream)))
                (cond
                  (http/response? response) ctx
                  :else (if (.exists f)
                          (assoc ctx :response {:body    f
                                                :headers {"Content-Type" (mime/ext-mime-type (.getName f))}
                                                :status  200})
                          (assoc ctx :response {:body   "Not found"
                                                :status 404})))))}))

(defonce *server (atom nil))

(defn -main
  [& _]
  (swap! *server (fn [st]
                   (some-> st http/stop)
                   (-> {::http/routes                (fn []
                                                       (route/expand-routes @#'routes))
                        ::http/port                  8080
                        ::http/join?                 false
                        ::http/not-found-interceptor not-found-interceptor
                        ::http/type                  :jetty}
                     http/default-interceptors
                     http/dev-interceptors
                     http/create-server
                     http/start))))

(comment
  (-main)
  nil)
