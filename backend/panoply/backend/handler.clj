(ns panoply.backend.handler
  (:require
   [adzerk.env                     :as env]
   [castra.middleware              :refer [wrap-castra]]
   [clojure.java.io                :as    io]
   [compojure.core                 :refer [defroutes GET]]
   [compojure.route                :refer [resources not-found]]
   [ring.middleware.defaults       :refer [wrap-defaults api-defaults]]
   [ring.middleware.resource       :refer [wrap-resource]]
   [ring.middleware.session        :refer [wrap-session]]
   [ring.middleware.session.cookie :refer [cookie-store]]
   [ring.util.response             :refer [content-type resource-response]]
   ;; For wrap-reload to know about
   [panoply.backend.github         :as gh]
   [panoply.backend.db             :as db]
   [panoply.backend.api]))

(env/def
  PANOPLY_HOST "localhost:8000")

(defroutes app-routes
  (GET "/" req
       (-> "index.html"
           (resource-response)
           (content-type "text/html")))
  (GET "/github-callback" {{session-code :code} :params}
       (when-let [access-token (gh/get-access-token session-code)]
         (let [user (gh/get-user access-token)]
           (db/register! user)
           {:status 302
            :cookies {"access-token" access-token}
            :headers {"location" (format "http://%s/" PANOPLY_HOST)}})))
  (resources "/" {:root ""})
  (not-found "404 Not Found"))

(def app
  (-> app-routes
      (wrap-castra 'panoply.backend.api)
      (wrap-session {:store (cookie-store "a 16-byte secret")})
      (wrap-defaults api-defaults)
      (wrap-resource "public")))
