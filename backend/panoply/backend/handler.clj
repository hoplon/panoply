(ns panoply.backend.handler
  (:require
    [adzerk.env               :as env]
    [castra.middleware        :refer [wrap-castra wrap-castra-session]]
    [ring.middleware.resource :refer [wrap-resource]]
    [panoply.backend.github   :refer [wrap-token]]
    [panoply.backend.api]))

(env/def
  GITHUB_CLIENT_ID :required
  GITHUB_SECRET_ID :required)

(defn handle [req]
    {:status  404
     :headers {"content-type" "text/plain"}
     :body    "The Panoply Service"})

(defn wrap-static [handle root-path root-file]
  (comp (wrap-resource handle root-path)
        (fn [req] (update req :uri #(if (= % "/") (str "/" root-file) %)))))

(def serve
  (-> handle
    (wrap-castra 'panoply.backend.api)
    (wrap-static "/static" "index.html")
    (wrap-token "/" GITHUB_CLIENT_ID GITHUB_SECRET_ID)
    (wrap-castra-session "a 16-byte secret")))
