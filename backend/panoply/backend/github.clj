(ns panoply.backend.github
  (:require
    [clj-http.client        :as http]
    [ring.middleware.params :refer [params-request]]
    [cheshire.core          :refer [parse-string]]))

(defn- mkrpc [verb url & keys]
  (let [config {:accept :json :content-type :json}
        params (if (= verb http/get) :query-params :form-params)
        call   #(->> (zipmap keys %) (assoc config params) (verb url))
        error  #(if (:error %) (throw (ex-info (:error_description %) %)) %)]
    (fn [& args]
      (future (-> (call args) :body (parse-string true) error)))))

(def get-token (mkrpc http/post "https://github.com/login/oauth/access_token" :code :client_id :client_secret))
(def get-user  (mkrpc http/get  "https://api.github.com/user"))

(defn wrap-token [handle callback-uri client-id secret-id]
  "handle the oath callback to set a github token on the castra session. must be
   placed after the wrap-castra-session and before the wrap-castra middlewares."
  (fn [req]
    (if-let [token (and (= (:uri req) callback-uri)
                        (some-> (params-request req)
                                (get-in [:params "code"])
                                (get-token client-id secret-id)))]
      (assoc-in (handle req) [:session "token"] (:access_token @token))
      (handle req))))
