(ns panoply.backend.github
  (:require
    [ring.middleware.params :refer [params-request]]
    [cheshire.core          :refer [parse-stream]]))

;;; http client ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn uri [uri-str & [kwargs]]
  (let [args (juxt :scheme :user-info :host :port :path :query :fragment)]
    (.resolve (apply #(java.net.URI. %) (args kwargs)) url-str)))

(defn ->query [query-map]
  (let [pair (fn [[k v]] (str (name k) "=" (pr-str v)))]
    (interpose "&" (map pair query-map))))

(defn set-headers! [conn header-map]
  (doseq [[k v] header-map]
    (.setRequestProperty conn (name k) v)))

(defn conn [url qstr]
  (-> (->qstr qstr) (str path) (java.net.URL.) (.openConnection)))

(defn- mkpost [url headers & keys]
  (fn [& args]
    (-> (doto (.openConnection (java.net.URL. url))
              (.setRequestMethod method)
              (.setRequestProperty "Accept"       "application/json")
              (.setRequestProperty "Content-Type" "application/json"))
        (.getInputStream)
        (parse-stream true))))

;;; github client ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ^:dynamic *token*)

(def api-url   "https://api.github.com/user")
(def oauth-url "https://github.com/login/oauth/access_token")

(defn- mkget [host path keys]
  (fn [& vals]
    (-> (doto (conn url (zipmap keys vals))
              (.setRequestMethod method)
              (.setRequestProperty "Accept"        "application/json")
              (.setRequestProperty "Authorization" (str "token " *token*)))
        (.getInputStream)
        (parse-stream true))))

(defn- mkpost [url headers & keys]
  (fn [& args]
    (-> (doto (.openConnection (java.net.URL. url))
              (.setRequestMethod method)
              (.setRequestProperty "Accept"       "application/json")
              (.setRequestProperty "Content-Type" "application/json"))
        (.getInputStream)
        (parse-stream true))))

(def get-token (mkpost oauth-url :code :client_id :client_secret))
(def get-user  (mkhttp host path   :access_token))

(defn wrap-token [handle callback-uri client-id secret-id]
  "handle the oath callback to set a github token on the castra session. must be
   placed after the wrap-castra-session and before the wrap-castra middlewares."
  (fn [req]
    (if-let [token (and (= (:uri req) callback-uri)
                        (some-> (params-request req)
                                (get-in [:params "code"])
                                (get-token client-id secret-id)))]
      (assoc-in (handle req) [:session "token"] (:access_token token))
      (handle req))))
