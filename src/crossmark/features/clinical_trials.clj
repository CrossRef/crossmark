(ns crossmark.features.clinical-trials
  "Look up Clinical Trial links information."
  (:require [crossmark.util :as util]))

(def ctn-relation-type-names
  {"postResults" "Post-results"
   "preResults" "Pre-results"
   "results" "Results"})

(def unknown-registry-name "Unknown")

(defn works-for-ctn [ctn]
  ; Only show first 100 linked documents.
  (let [works (get-in (util/fetch-json util/works-api-endpoint {:query-params {"rows" 100 "filter" (str "clinical-trial-number:" ctn)}}) [:message :items])
        ; To map of doi, title. 
        doi-title (->> works
          (map (fn [work]
            (let [ctns (:clinical-trial-number work)
                  ; relation-type may or may not be present.
                  relation-type (first (keep #(when (= (:clinical-trial-number %) ctn)
                                                (:type %)) ctns))

                  relation-type (ctn-relation-type-names relation-type)]
              {:doi (:DOI work)
               :title (-> work :title first)
               :relation-type relation-type}))))]
    doi-title))

(def registry-name-cache (atom {}))

(defn get-registry-name
  "Get registry name from DOI, cached unless unknown."
  [doi]
  (if-let [result (get @registry-name-cache doi)]
    result
    (let [result (util/get-doi-title doi unknown-registry-name)]
      (when-not (= result unknown-registry-name)
        (swap! registry-name-cache assoc doi result))
      result)))

(defn decorate-clinical-trial-number [item]
  (let [ctns (-> item :from-md-api :clinical-trial-number)
        ctns-with-works (map #(assoc % :other-works (works-for-ctn (:clinical-trial-number %))) ctns)
        withs-with-registry-names (map #(assoc % :registry-name (get-registry-name (:registry %))) ctns-with-works)
        has-ctns (not (empty? ctns))]
  (assoc item
    :clinical-trial-number withs-with-registry-names
    :has-clinical-trial-number has-ctns)))
