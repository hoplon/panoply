(ns ^{:hoplon/page "index.html"} panoply.frontend
  (:require
    [adzerk.env :as env]
    [castra.core     :refer [mkremote]]
    [javelin.core    :refer [defc]]
    [hoplon.core     :refer [with-init! if-tpl when-tpl text]]
    [hoplon.ui       :refer [window elem]]
    [hoplon.ui.attrs :refer [r]]
    [goog.net.Cookies]))

;;; environment ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(env/def PANOPLY_GH_BASIC_CLIENT_ID :required)

(def auth-url "https://github.com/login/oauth/authorize")

;;; state ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def cks (goog.net.Cookies. js/document))

(defc state   nil)
(defc user    nil)
(defc tree    nil)
(defc error   nil)
(defc loading [])

;;; service ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def get-user (mkremote 'panoply.backend.api/get-user user error loading))

;;; queries ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;; commands ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn initiate! [_ _ _ _]
  (let [cookies (reduce #(assoc %1 %2  (.get cks %2))  {}  (.getKeys cks))]
    (get-user (.get cookies "access-token"))))

(defn login! []
  (let [url (str auth-url "?client_id=" PANOPLY_GH_BASIC_CLIENT_ID)]
    (set! (.-location js/window) url)))

(defn logout! []
  (.remove cks "access-token")
  (get-user nil)
  (.reload (.-location js/window)))

;;; views ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(window
  :title     "Panoply"
  :initiated initiate!
  (elem :sh (r 1 1)
    "Panopoly")
  (if-tpl user
    (elem :sh (r 1 1)
      (text "Logged in as ~(:login user)"))
    (elem :sh (r 1 1) :m :pointer :click login!
      "Log in with GitHub"))
  (when-tpl user
    (elem :sh (r 1 1) :m :pointer :click logout!
      "Log out")))
