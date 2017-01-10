(ns crossmark.features.funder
  "Crossmark Funding Information (formerly Fundref)")

(defn decorate-funder
  "Set :has-funder and :funder for work."
  [item]
  (let [funder-info (-> item :from-md-api :funder)
        funders (map (fn [{funder-name :name award-numbers :award}]
                          {:funder-name funder-name :award-numbers award-numbers}) funder-info)]
    (assoc item :has-funder (not-empty funders)
                :funder funders)))
