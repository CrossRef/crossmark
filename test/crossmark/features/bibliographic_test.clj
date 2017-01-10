(ns crossmark.features.bibliographic-test
  (:require [clojure.test :refer :all]
            [crossref.util.date :as cr-date]
            [crossmark.test-util :as test-util]
            [crossmark.features.bibliographic :as bibliographic]))

(deftest decorate-work-info
  (testing "Bibliographic work information should be retrieved from API response"
    (let [base (test-util/base "test/10.5555-up-up.json")
          result (bibliographic/decorate-work-info base)]
      (is (= (:title result) "Trebuchets: Up up and Away") "First title should be chosen.")
      (is (= (:publication result) "Journal of Trebuchets and Lesser Siege Engines") "First container-title should be chosen as the publication name.")
      (is (= (:published-date result) (cr-date/crossref-date 1986 2 1)) "Published date should be taken from published-print")
      (is (= (:update-policy result) "http://dx.doi.org/10.5555/crossmark_policy") "Crossmark Update Policy should be taken.")
      (is (= (:publisher result) "Oversize Weapons Inc")))))

(deftest decorate-author
  (let [base (test-util/base "test/10.5555-up-up.json")
        result (bibliographic/decorate-author base)]
    (testing "decorate-author should select all authors"
      (is
        (=
          (set (map #(select-keys % [:family :given]) (:author result)))
          #{{:family "Carberry" :given "Josiah"}
            {:family "Puddleduck" :given "Jemima"}})
        "All first and given names should be selected")
      (is (true? (:has-author result)))))
  (let [base (test-util/base "test/10.5555-catapult.json")
        result (bibliographic/decorate-author base)]
    (testing "decorate-author should represent when no authors"
      (is (false? (:has-author result)))
      (is (empty? (:authors result))))))

(deftest decorate-remove-orcid-authors
  (testing "decorate-author should ignore ORCID-based assertions, case insensitive"
    (let [base (test-util/base "test/10.5555-up-up.json")
          result (bibliographic/decorate-remove-orcid-authors base)]
      ; There are 5 assertions in 10.5555/up-up 
      ; 2 of them are ORCID assertions with different cases.
      (is (= 3 (count (-> result :from-md-api :assertion))) "Not all assertions should be removed")
      (is (every? #{"received" "accepted" "published"} (map :name (-> result :from-md-api :assertion))) "Non-ORCID assertions should not be altered"))))




        


