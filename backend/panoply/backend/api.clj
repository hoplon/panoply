(ns panoply.backend.api
  (:require [castra.core :refer [defrpc *session*]]))

(defrpc get-state []
  (swap! *session* update-in [:id] #(or % (rand-int 100)))
  {:random 102 :session (:id @*session*)})
