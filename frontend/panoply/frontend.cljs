(ns ^{:hoplon/page "index.html"} panoply.frontend
  (:refer-clojure
    :exclude [-])
  (:require
    [adzerk.env :as env]
    [castra.core     :refer [mkremote]]
    [javelin.core    :refer [defc]]
    [hoplon.core     :refer [with-init! if-tpl when-tpl text]]
    [hoplon.ui       :refer [window elem image b]]
    [hoplon.ui.attrs :refer [r c -]]
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

;;; styles ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;-- breakpoints ---------------------------------------------------------------;

(def sm 760)
(def md 1240)
(def lg 1480)

(defn >sm [& bks] (apply b (r 1 1) sm bks))

;-- sizes ---------------------------------------------------------------------;

(def gutter 6)

;-- colors --------------------------------------------------------------------;

(def black  (c 0x1F1F1F))
(def orange (c 0xE73624))
(def blue   (c 0x009BFF))
(def yellow (c 0xF5841F))

;-- fonts ---------------------------------------------------------------------;

(def menu-font {:f 21 :ff "opensans" :ft :800 :fc :white :fw 1})

;;; views ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn authentication-view []
  (elem :s (r 1 1) :a :mid
    (elem menu-font :s 400 :a :mid
      (elem :ph 16 :pv 12 :a :mid :r 5 :c orange :b 2 :bc yellow :m :pointer :click login!
        "Log Into GitHub"))))

(defn application-view []
  (list
    (elem menu-font :sh (r 1 1) :sv (b nil sm 60) :ph (* gutter 2) :a :mid :c orange :bt 6 :bc yellow
      (image :s 42 :url "http://hoplon.io/images/logos/hoplon-logo.png") 
      (elem :sh (>sm (- (r 1 1) 42)) :p gutter :g gutter :ah (b :mid sm :end) :av :mid
        (elem :m :pointer :click logout!
          (text  "Log Out ~(:login user)"))))
    (elem :sh (r 1 1) :sv (- (r 1 1) 60) :a :mid
      "Do Stuff!")))

(window
  :title     "Panoply"
  :initiated initiate!
  (if-tpl user
    (application-view)
    (authentication-view)))
