(set-env! :exclusions '[org.clojure/clojure
                        org.clojure/clojurescript
                        org.clojure/tools.reader]
          :source-paths #{"frontend"}
          :resource-paths #{"backend"}
          :dependencies
          '[;; frontend
            [org.clojure/tools.reader "1.0.0-alpha1"]
            [org.clojure/clojurescript "1.7.228"]
            [hoplon "6.0.0-alpha17"]
            ;; dev-only frontend
            [adzerk/boot-cljs "1.7.228-2" :scope "test"]
            [adzerk/boot-reload "0.4.13" :scope "test"]
            ;; backend
            [org.clojure/clojure "1.8.0"]
            [hoplon/castra "3.0.0-alpha5" :exclusions [ring/* commons-codec]]
            [ring/ring-defaults "0.2.1" :exclusions [javax.servlet/servlet-api]]
            [compojure "1.5.1"]
            ;; dev-only backend
            [tailrecursion/boot-jetty "0.1.3" :scope "test"]
            [ring/ring-devel "1.5.0" :scope "test"]])

(require '[adzerk.boot-cljs :refer [cljs]])

(require
 '[adzerk.boot-cljs         :refer [cljs]]
 '[adzerk.boot-reload       :refer [reload]]
 '[hoplon.boot-hoplon       :refer [hoplon]]
 '[tailrecursion.boot-jetty :refer [serve]])

(deftask dev
  "Build panoply for local development."
  [p port PORT int "Port number to run local web server on. Default is 8000"]
  (comp
   (watch)
   (speak :theme "woodblock")
   (hoplon)
   (reload)
   (cljs)
   (web :serve 'panoply.backend.handler-dev/app)
   (repl :server true)
   (serve :port (or port 8000))))

