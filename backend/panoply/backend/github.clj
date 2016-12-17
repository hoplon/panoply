(ns panoply.backend.github
  (:require
   [adzerk.env      :as env]
   [clj-http.client :as http]
   [cheshire.core   :as json]))

(env/def
  PANOPLY_GH_BASIC_CLIENT_ID :required
  PANOPLY_GH_BASIC_SECRET_ID :required)

(defn get-access-token [session-code]
  (some-> (http/post "https://github.com/login/oauth/access_token"
                     {:form-params {:client_id PANOPLY_GH_BASIC_CLIENT_ID
                                    :client_secret PANOPLY_GH_BASIC_SECRET_ID
                                    :code session-code}
                      :headers {"accept" "application/json"}})
          :body
          (json/parse-string true)
          :access_token))

(defn get-user [token]
  (some->
   (http/get "https://api.github.com/user"
             {:query-params {"access_token" token}})
   :body
   (json/parse-string true)))
