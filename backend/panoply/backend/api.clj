(ns panoply.backend.api
  (:require ;; [thelarch.db :as db]
   [panoply.backend.github :as gh]
   [castra.core :refer [defrpc *session*]]
   [javelin.core :refer [with-let]]))

(defrpc get-user [access-token]
  (with-let [user (gh/get-user access-token)]
    (swap! *session* assoc :user user)))

(defrpc put-todos [todos]
  {:rpc/pre [(:user @*session*)]}
  (let [login (get-in @*session* [:user :login])]
    (comment "TODO")))
