(ns frontrow.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :as json]))

(def store-filename "store.dat")

(defn serialize-store [store-value]
  (spit store-filename store-value))

(defn deserialize-store []
  (if (.exists (java.io.File. store-filename))
    (atom (read-string (slurp store-filename)))
    (atom {})))

(def kv-store (deserialize-store))

(add-watch kv-store :changed (fn [k r os ns] (serialize-store ns)))

(defn average [coll]
  (/ (apply + coll) (count coll)))

(defn avg-of-avgs
  "Computes average of averages for all values in the store.
  TODO: Test for parallelism benefits on bigger data stores."
  []
  {:average (if-let [store-vals (vals @kv-store)]
              (float (average (map average store-vals))))})

(defn avg-key [k]
  {:average (if-let [v ((keyword k) @kv-store)]
              (float (average v)))})

(defn create-or-conj
  "Given a map, key, and value, will attempt to update the map so that the
  value is conj'd with the vector at the key, or else create a new vector
  with just the value as the only element."
  [m k v]
  (assoc m k
          (vec (conj (k m)
                     v))))

(defn add-number [k v]
  (let [k (keyword k)]
    (k (swap! kv-store #(create-or-conj %1 k v)))))

(defroutes app-routes
  (context "/store" [] (defroutes store-routes
    (GET  "/average_of_averages" [] (avg-of-avgs))
    (context "/data" [] (defroutes store-data-routes
      (GET "/:keyword/average" [k] (avg-key k))
      ;; Even though the following is a POST method,
      ;; it makes sense to pass the number as part
      ;; of the URL instead of explicitly in the request body.
      (POST ["/:keyword/:number", :number #"[0-9]+"] [k v] (add-number k v))))))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
    (json/wrap-json-body)
    (json/wrap-json-response)))
