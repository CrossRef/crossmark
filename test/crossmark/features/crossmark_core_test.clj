(ns crossmark.features.crossmark-core-test
  (:require [clojure.test :refer :all]
            [crossmark.test-util :as test-util]
            [crossmark.features.crossmark-core :as crossmark-core]))

(deftest ^:todo decorate-domain-info
  (testing "decorate-domain-info should not set :has-domain-exlusive-violation if the 'referring domain' is a PDF"
    (let [base (test-util/base "test/10.5555-catapult.json" {:request {:referring-domain :pdf}})
          result (crossmark-core/decorate-domain-info base)]
      (is (false? (result :has-domain-exclusive-violation)) "No domain exlusive violation on PDF")))

  (testing "decorate-domain-info should set :has-domain-exlusive-violation if :content-domain is set in the MDAPI data and :crossmark-restriction true and the domain doesn't match"
    (let [base (test-util/base "test/10.5555-catapult.json" {:request {:referring-domain "NOT-CORRECT.COM"}})
          result (crossmark-core/decorate-domain-info base)]
      (is (true? (result :has-domain-exclusive-violation)) "Domain exlusive violation on incorrect domain.")))

  (testing "decorate-domain-info should report no violation if :crossmark-restriction true and one of multiple domains match."
    (let [base-1 (test-util/base "test/10.5555-catapult.json" {:request {:referring-domain "catapults-international.org"}})
          result-1 (crossmark-core/decorate-domain-info base-1)
          base-2 (test-util/base "test/10.5555-catapult.json" {:request {:referring-domain "catapults-international.com"}})
          result-2 (crossmark-core/decorate-domain-info base-2)]
      (is (false? (result-1 :has-domain-exclusive-violation)) "No conflict on first correct domain.")
      (is (false? (result-2 :has-domain-exclusive-violation)) "No conflict on second correct domain.")))

  (testing "decorate-domain-info should not set :has-domain-exlusive-violation if :content-domain is set in the MDAPI data but :crossmark-restriction is false")

  (testing "decorate-domain-info should allow for subdomains to be supplied, reporting :has-domain-exlusive-violation false"
    (let [base (test-util/base "test/10.5555-catapult.json" {:request {:referring-domain "pages.subdomain.catapults-international.org"}})
          result (crossmark-core/decorate-domain-info base)]
      (is (false? (result :has-domain-exclusive-violation)) "No conflict on correct subomain."))))

(deftest ^:todo decorate-updates
  (testing "decorate-updates should choose label in preference from supplied, or default from update-types-labels, or take the 'type' attribute.")
  
  (testing "decorate-updates should choose headline from known vocab, or choose default.")

  (testing "decorate-updates should set :error flag for certain update types"))

(deftest ^:todo group-assertions
  (testing "group-assertions should group a sequence of assertions a sequence of groups that contains all original assertions")

  (testing "every group produced by group-assertions should have the same 'group'")

  (testing "if no 'group' is supplied, assertions should be in singleton groups")

  (testing "items should remain in original order within groups")

  (testing "groups should be sorted by the original order of the first item in each group"))

(deftest decorate-extra-assertions
  (testing "decorate-extra-assertions should group all present assertions and set :has-extra-assertions if there are any")

  (testing "decorate-extra-assertions should set :has-extra-assertions if there are no present assertions"))

(deftest decorate-dois
  (testing "decorate-dois should fix DOIs wherever they are found!"
    (is (= (crossmark-core/decorate-dois
            {:word-numbers
             {:one {:two {:three "10.5555/three"}},
              :one-hundred "doi.org/10.5555/one-hundred"},
             :number-numbers {1 {2 "http://doi.org/10.5555/2"}},
             :dois
             {:good "https://doi.org/10.5555/good",
              :bad "http://dx.doi.org/10.5555/bad",
              :ugly "doi:10.5555/ugly"},
             "strings" {"bread" {"and" {"butter" "10.5555/bread-and-butter"}}}})

          {:word-numbers
           {:one {:two {:three "https://doi.org/10.5555/three"}},
            :one-hundred "https://doi.org/10.5555/one-hundred"},
           :number-numbers {1 {2 "https://doi.org/10.5555/2"}},
           :dois
           {:good "https://doi.org/10.5555/good",
            :bad "https://doi.org/10.5555/bad",
            :ugly "https://doi.org/10.5555/ugly"},
           "strings"
           {"bread"
            {"and" {"butter" "https://doi.org/10.5555/bread-and-butter"}}}})
    "DOIs expressed as raw, doi-scheme and with deprecated resolvers and protocols are transformed in any arbitrary structure.")))
