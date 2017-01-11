(ns crossmark.features.crossmark-core
  "Assertions, updates, Crossmark-service related display.
   Also handles display of alert in box to two levels: 'alert' and 'error'"
  (:require [crossmark.util :as util]
            [clj-time.core :as clj-time]
            [clojure.walk :refer [postwalk]]))

(def update-types-labels {
  "addendum" "Addendum"
  "clarification" "Clarification"
  "correction" "Correction"
  "corrigendum" "Corrigendum"
  "erratum" "Erratum"
  "expression_of_concern" "Expression of concern"
  "new_edition" "New edition"
  "new_version" "New version"
  "partial_retraction" "Partial retraction"
  "removal" "Removal"
  "retraction" "Retraction"
  "withdrawal" "Withdrawal"})

; Show a red error box with a cruciform glyph or merely a yellow 'alert' box with an exclamation mark.
(def error-types
  "List of types that comprise 'error' update types. The rest are 'alert'."
   #{"removal" "retraction" "partial_retraction" "withdrawal"})

(defn decorate-domain-info [item]
  (let [referring-domain (-> item :request :referring-domain)
        domain-exclusive (-> item :from-md-api :content-domain :crossmark-restriction)
        allowed-domains (set (->> item :from-md-api :content-domain :domain))
        
        ; referring-domain can be, e.g. :unknown
        matched-domains (not-empty (filter #(.endsWith (str referring-domain) %) allowed-domains))

        is-pdf (= :pdf (-> item :request :referring-domain))

        violation (boolean (and domain-exclusive
                             (not is-pdf)
                             (not matched-domains)))]
  (assoc item :has-domain-exclusive-violation violation)))

(defn decorate-updates [item]
  (let [; Fetch the 'update-to' component of each updating DOI.
        formatted-updates (map
                           (fn [update]
                             {:doi (:from update)
                              :label (or (:label update)
                                         (update-types-labels (:type update))
                                         (:type update))
                              
                              ; Controlled vocab of headlines
                              :headline (cond
                                (= "withdrawal" (:type update)) "Withdrawal"
                                (= "retraction" (:type update)) "Retraction"
                                :default "Updates are available")

                              :error? (boolean (error-types (:type update)))

                              :date (util/maybe-date (:date update))})
                           (:reverse-updates item))

        ; If date-stamp supplied, only show updates after this date.
        date-filtered (if-let [date-stamp (:date-stamp item)]
                        (filter #(clj-time/after? (:date %) date-stamp) formatted-updates)
                        formatted-updates)]
    (assoc item
      :has-update (not-empty date-filtered)
      :updates date-filtered)))


(defn group-assertions
  "Take a sequence of assertions, return a sequence of groups of assertions in order.
  When an assertion isn't part of a group, put it on its own in an anonymous group.

  Return as seq of [group-label, assertions-in-group]"
  [assertions]
  (let [; Put each assertion in a group and remember where it occurred in the original input.
        all-grouped (map-indexed (fn [ndx assertion]
                      (assoc assertion
                        :original-index ndx
                        :group (or (:group assertion)
                                   {:label nil :name (str "__anon__" ndx)}))) assertions)

        ; Group by the group label. A seq of seqs.
        grouped (vals (group-by (comp :name :group) all-grouped))

        ; Sort list of groups by the first time an element in it first appeared.
        sorted-groups (sort-by #(apply min (map :original-index %)) grouped)

        ; Now sort within groups by the order if there is one.
        sorted (map #(sort-by :order %) sorted-groups)

        ; Into seq of vects of [label, assertions]. 
        ; label could be nil for anonymous groups.
        with-label (map (fn [group-of-assertions]
                          [(-> group-of-assertions first :group :label) group-of-assertions]) sorted)]
  with-label))

(defn decorate-extra-assertions
  "Decorate whatever assertions may be left (after some may have been removed into other features).
   This should be applied right at the end of the chain of decorators."
  [item]
  (let [assertions (-> item :from-md-api :assertion)
        grouped (group-assertions assertions)]
(assoc item :has-extra-assertions (boolean (not-empty assertions))
            :extra-assertion-groups grouped)))

(defn decorate-dois
  "Format any DOIs we find."
  [item]
  (postwalk util/possibly-normalise-doi item))
