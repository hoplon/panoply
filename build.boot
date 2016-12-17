(set-env! :exclusions '[org.clojure/clojure
                        org.clojure/clojurescript
                        org.clojure/tools.reader]
          :source-paths #{"frontend"}
          :resource-paths #{"backend"}
          :dependencies
          '[;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            ;; Frontend
            ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            [org.clojure/tools.reader  "1.0.0-alpha1"]
            [org.clojure/clojurescript "1.7.228"]
            [hoplon                    "6.0.0-alpha17"]
            ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            ;; Dev-time only frontend
            ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            [adzerk/boot-cljs          "1.7.228-2"
             :scope "test"]
            [adzerk/boot-reload        "0.4.13"
             :scope "test"]
            ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            ;; Backend
            ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            [org.clojure/clojure       "1.8.0"]
            [hoplon/castra             "3.0.0-alpha5"
             :exclusions [ring/* commons-codec]]
            [ring/ring-defaults        "0.2.1"
             :exclusions [javax.servlet/servlet-api]]
            [compojure                 "1.5.1"]
            [cheshire                  "5.5.0"
             :exclusions [com.fasterxml.jackson.core/jackson-core]]
            [clj-http                  "2.0.0"
             :exclusions [riddley commons-io commons-codec]]
            ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            ;; Dev-time only backend
            ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            [tailrecursion/boot-jetty  "0.1.3"
             :scope "test"]
            [ring/ring-devel           "1.5.0"
             :scope "test"]
            ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            ;; Common
            ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            [adzerk/env "0.3.1"]])

(require
 '[adzerk.boot-cljs         :refer [cljs]]
 '[adzerk.boot-reload       :refer [reload]]
 '[hoplon.boot-hoplon       :refer [hoplon]]
 '[tailrecursion.boot-jetty :refer [serve]])

(deftask dev
  "Build panoply for local development."
  []
  (comp
   (watch)
   (speak :theme "woodblock")
   (hoplon)
   (reload :on-jsload 'panoply.frontend.rpc/init)
   (cljs)
   (web :serve 'panoply.backend.handler-dev/app)
   (serve :port 8000)))

