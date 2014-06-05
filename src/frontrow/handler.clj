(ns frontrow.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as json]
            [ring.util.response :as ring]))

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

(defn delete-data []
  (reset! kv-store {}))

(defroutes app-routes
  (GET "/store/average_of_averages" [] (ring/response (avg-of-avgs)))
  (GET "/store/data/:k/average" [k] (ring/response (avg-key k)))
  ;; Even though the following is a POST method,
  ;; it makes sense to pass the number data as part
  ;; of the URL instead of explicitly in the request body
  ;; to simplify usage of the API.
  (POST ["/store/data/:k/:number", :number #"[0-9]+"] [k number] (ring/response (add-number k (Integer/parseInt number))))
  (DELETE "/store" [] (ring/response (delete-data)))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
    (json/wrap-json-body)
    (json/wrap-json-response)))

(defn -main [& m]
  (jetty/run-jetty app {:port 8321}))
