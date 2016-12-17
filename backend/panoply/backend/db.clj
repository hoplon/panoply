(ns panoply.backend.db)

(defonce db
  (atom {}))

(defn register!
  [user]
  (swap! db assoc :user user))
