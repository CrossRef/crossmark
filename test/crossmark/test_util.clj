(ns crossmark.test-util
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [crossmark.core :refer :all]
            [clojure.java.io :as io]))

(defn load-api
  "Load JSON file from test resources but don't parse, formatted as an API response."
  [filename]
  (slurp (io/reader (io/resource filename))))

(defn load-json-api
  "Load and parse JSON file from test resources, formatted as an API response."
  [filename]
  (json/read-str (load-api filename) :key-fn keyword))

(defn load-json-content
  "Load JSON file from test resources, remove the API wrapping."
  [filename]
  (let [loaded (load-json-api filename)]
    (condp = (:message-type loaded)
      "work" (get-in loaded [:message])
      "work-list" (get-in loaded [:message :items])
      (get-in loaded [:message]))))

(defn base
  "Create a base object as per crossmark.data with given DOIs."
  [mdapi-filename]
  {:from-md-api (load-json-content mdapi-filename)})
