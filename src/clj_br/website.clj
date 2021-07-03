(ns clj-br.website
  (:require [io.pedestal.http :as http]
            [hiccup2.core :as h]
            [io.pedestal.http.route :as route]
            [ring.util.mime-type :as mime]
            [clojure.pprint :as pp]
            [clojure.string :as string]
            [io.pedestal.interceptor :as interceptor]
            [clojure.java.io :as io])
  (:import (java.nio.charset StandardCharsets)
           (java.io File)))

(set! *warn-on-reflection* true)

(defmacro scittle!
  [& forms]
  (let [pp-forms (for [form forms]
                   (list `pp/pprint (list 'quote form)))
        out-forms (cons `with-out-str pp-forms)]
    `(h/raw ~out-forms)))

(def links
  [{:titulo "Grupo no Telegram"
    #_#_:descricao ""
    :href   "https://t.me/clojurebrasil"}
   {:titulo "Duvidas e discussões"
    #_#_:descricao ""
    :href   "https://github.com/clj-br/forum/discussions"}])

(def style "
headers > img {
  max-width: 20vh;
  max-height: 20vh;
}
body {
  text-align: center;
  padding: 2vh;
  max-width: 38em;
  margin: auto;
}

ul {
  list-style-type: none;
}
li {
  padding: 1em;
}
")

(def lista-principal
  (into [:ul]
    (for [{:keys [titulo
                  href]} links]
      [:li
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
                      :content "clj-br"}]
              [:script {:src  "https://cdn.jsdelivr.net/gh/borkdude/scittle@0.0.2/js/scittle.js"
                        :type "application/javascript"}]
              [:script {:crossorigin "true"
                        :src         "https://unpkg.com/react@17/umd/react.production.min.js"}]
              [:script {:crossorigin "true"
                        :src         "https://unpkg.com/react-dom@17/umd/react-dom.production.min.js"}]
              [:script {:src  "https://cdn.jsdelivr.net/gh/borkdude/scittle@0.0.2/js/scittle.reagent.js"
                        :type "application/javascript"}]
              [:title "clj-br"]
              [:style (h/raw style)]]
        body [:body
              [:headers
               [:img {:src "resources/logo.jpg"}]]
              [:h1 "Clojure Brasil"]
              [:div
               {:id "playground"}
               lista-principal]
              [:p "Comece a aprender agora mesmo!"]
              [:ul
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
                                                         (defn contador
                                                           []
                                                           [:div
                                                            [:div (str "Contador: " @*n)]
                                                            [:button
                                                             {:onClick (fn []
                                                                         (swap! *n inc))}
                                                             "incrementar"]]))
                                               :rotulo "Contador"}]]
                 [:li
                  [:button
                   {:data-value  codigo
                    :data-target "editor"
                    :onClick     (string/join ";\n"
                                   ["document.getElementById(this.dataset.target).value = this.dataset.value"
                                    "document.getElementById(this.dataset.target).onkeyup()"])}
                   rotulo]])]
              [:textarea
               {:id      "editor"
                :onkeyup "this.dataset.state == 'done' ? this.dataset.state = 'idle' : null"
                :cols    60
                :rows    20}
               (with-out-str
                 (pp/pprint lista-principal))]
              [:div {:id "playground"}]
              [:pre {:id "stderr"}]
              [:script {:type "application/x-scittle"}
               (scittle!
                 (require '[reagent.core :as r]
                   '[reagent.dom :as rdom])
                 (defn render
                   []
                   (let [stderr (.getElementById js/document "stderr")
                         editor (.getElementById js/document "editor")]
                     (when (-> editor .-dataset .-state #{"idle"})
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
                          (assoc ctx :response {:body   f
                                                :status 200})
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
