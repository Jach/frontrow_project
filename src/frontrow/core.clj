(ns frontrow.core)

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

(defn ^{:get true :route "/average_of_averages"}
  avg-of-avgs
  "Computes average of averages for all values in the store.
  TODO: Test for parallelism benefits on bigger data stores."
  []
  {:average (if-let [store-vals (vals @kv-store)]
              (float (average (map average store-vals))))})

(defn ^{:get true :route "/average/<key>"}
  avg-key
  [k]
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

(defn ^{:post true :route "/add_number/<key>"}
  add-number
  [k v]
  (let [k (keyword k)]
    (k (swap! kv-store #(create-or-conj %1 k v)))))

