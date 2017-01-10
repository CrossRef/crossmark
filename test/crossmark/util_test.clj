(ns crossmark.util-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as clj-time]
            [crossmark.util :as util]))

(deftest maybe-date
  (testing "maybe-date can construct from an API response format date."
    (is (= (clj-time/date-time 2016 1 1) (util/maybe-date [2016])))
    (is (= (clj-time/date-time 2016 1 1) (util/maybe-date [2016 1])))
    (is (= (clj-time/date-time 2016 1 2) (util/maybe-date [2016 1 2])))))


(deftest ^:todo fetch-json
  (testing "fetch-json should fetch JSON and return with keyword keys."))

; TODO should get contents of message?

(deftest ^:todo fetch-json-retry
  (testing "fetch-json should retry API queries."))

(deftest ^:todo fetch-md-api
  (testing "fetch-md-api uses custom API endpoint when provided in config"))

(deftest ^:todo fetch-reverse-updates
  (testing "fetch-reverse-updates should retrieve all updates to DOI")

  (testing "date should be retrieved correctly")

  (testing "every result should have given keys"))

(deftest ^:todo get-doi-title
  (testing "get-doi-title should retrieve first title when more than one available")

  (testing "get-doi-title should return default when none available"))

(deftest ^:todo possibly-normalise-doi
  (testing "possibly-normalise-doi should ignore non-DOIs")
  
  (testing "possibly-normalise-doi should ignore existing well-formed DOIs")

  (testing "possibly-normalise-doi should fix incorrectly expressed DOIs"))