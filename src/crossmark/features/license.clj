(ns crossmark.features.license
  "Article license related things."
  (:require [crossmark.util :as util]))

(def license-label-names {
  "vor" "Version of Record"
  "tdm" "Text and Data Mining"
  "am" "Accepted Manuscript"})

(defn decorate-license [item]
  (let [licenses (-> item :from-md-api :license)
        licenses (map (fn [{url :URL start :start content-version :content-version}]
                            {:name (get license-label-names content-version content-version)
                             :url url
                             :start (util/maybe-date (-> start :date-parts first))}) licenses)]
    (assoc item :has-license (not-empty licenses)
                :license licenses)))
