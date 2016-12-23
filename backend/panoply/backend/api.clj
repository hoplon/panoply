(ns panoply.backend.api
  (:require ;; [thelarch.db :as db]
    [panoply.backend.github :as gh]
    [castra.core  :refer [defrpc *session*]]
    [javelin.core :refer [with-let]]))

(defn authorized? [] (prn :session @*session*) (:token @*session*))

(defrpc get-user []
  {:rpc/pre [(authorized?)]}
  (gh/get-user (:token @*session*)))

(defrpc put-todos [todos]
  {:rpc/pre [(authorized?)]}
  (let [login (get-in @*session* [:user :login])]
    (comment "TODO")))
