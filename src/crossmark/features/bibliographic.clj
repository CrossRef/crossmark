(ns crossmark.features.bibliographic
  "Bibliographic data concerning the article."
  (:require [crossmark.util :as util]))

(defn decorate-work-info
  "Retrieve bibliographic info for work."
  [item]
  (assoc item :title (-> item :from-md-api :title first)
              :publication (-> item :from-md-api :container-title first)
              :published-date (util/maybe-date (-> item :from-md-api :published-print :date-parts first))
              :update-policy (-> item :from-md-api :update-policy)
              :publisher (-> item :from-md-api :publisher)))

(defn decorate-remove-orcid-authors
  "Remove ORCID assertions of authors. There shouldn't be any, and authors should be captured in the :author section anyway."
  [item]
  (assoc-in
    item
    [:from-md-api :assertion]
    (->> item :from-md-api :assertion (remove #(.equalsIgnoreCase (get % :name "") "orcid")))))

(defn decorate-author [item]
  ; hashmaps of :family :given optional :ORCID
  (let [authors (-> item :from-md-api :author)
        has-author (not (empty? authors))]
  (assoc item
   :has-author has-author
   :author authors)))
