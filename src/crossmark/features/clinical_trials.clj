(ns crossmark.features.clinical-trials
  "Look up Clinical Trial links information."
  (:require [crossmark.util :as util]))

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

                  relation-type ({"postResults" "Post-results" "preResults" "Pre-results" "results" "Results"} relation-type)]
              {:doi (:DOI work)
               :title (-> work :title first)
               :relation-type relation-type}))))]
    doi-title))

(defn decorate-clinical-trial-number [item]
  (let [ctns (-> item :from-md-api :clinical-trial-number)
        ctns-with-works (map #(assoc % :other-works (works-for-ctn (:clinical-trial-number %))) ctns)
        withs-with-registry-names (map #(assoc % :registry-name (util/get-doi-title (:registry %) "Unknown")) ctns-with-works)
        has-ctns (not (empty? ctns))]
  (assoc item
    :clinical-trial-number withs-with-registry-names
    :has-clinical-trial-number has-ctns)))
