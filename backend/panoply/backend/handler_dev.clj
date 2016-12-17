(ns panoply.backend.handler-dev
  "DEVELOPMENT-ONLY entrypoint that wraps the application with a wrap-reload
  handler."
  (:require [panoply.backend.handler :as handler]
            [ring.middleware.reload :refer [wrap-reload]]
            [clojure.java.io :as io]))

(def src-dirs
  (filter #(not (.endsWith % ".jar"))
          (.split (System/getProperty "fake.class.path") ":")))

(def app
  (-> #'handler/app
      (wrap-reload {:dirs src-dirs})))
