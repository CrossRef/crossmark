(ns crossmark.features.clinical-trials-test
  (:require [clojure.test :refer :all]
            [org.httpkit.fake :as fake]
            [crossmark.test-util :as test-util]
            [crossmark.features.clinical-trials :as clinical-trials]))

(deftest works-for-ctn
  (testing "works-for-ctn should contact API with appropriate query and return all results."
    (fake/with-fake-http [{:url "https://api.crossref.org/v1/works"
                           :method :get
                           :query-params {"rows" 100 "filter"
                                          "clinical-trial-number:isrctn12345"}}
                          (test-util/load-api "test/ctn-query.json")]
      (let [response (clinical-trials/works-for-ctn "isrctn12345")]
        (is (= (count response) 2) "Both items should be returend")
        (is (= response
               [{:doi "10.5555/up-up", :title "Trebuchets: Up up and Away", :relation-type "Pre-results"}
                {:doi "10.6666/trebuchet", :title ["Trebuchets or Catapults? Catapults!"], :relation-type nil}])
                "Items should be returned with correct DOI, title and relation-type correctly transformed.")
        (is (= (-> response first :relation-type) "Pre-results") "Recognised relation-type should be transformed to human-readable")
        (is (= (-> response second :relation-type) nil) "Unrecognised relation-type should be transformed to human-readable")))))

(deftest lookup-registry-name
  (testing "lookup-registry-name should retrieve name from API when recognised")

  (testing "lookup-registry-name should return default name if missing"))

(deftest decorate-clinical-trial-number
  (testing "decorate-clinical-trial-number should decorate with human-readable info"
    (let [base (test-util/base "test/10.5555-up-up.json" {})
          result (clinical-trials/decorate-clinical-trial-number base)]
      (fake/with-fake-http [{:url "https://api.crossref.org/v1/works"
                             :method :get
                             :query-params {"rows" 100 "filter"
                                            "clinical-trial-number:isrctn12345"}}
                            (test-util/load-api "test/ctn-query.json")
                            
                            "https://api.crossref.org/v1/works/10.18810%2Fisrctn"
                            (test-util/load-api "test/10.18810-isrctn.json")]
        (is (true? (:has-clinical-trial-number result)) "When an item has CTNs, :has-clinical-trial-number should be true")
        (is (= (-> result :clinical-trial-number first :registry-name) "ISRCTN.org") "When recognised, the CTN Registry name should be retrieved"))))

  (testing "decorate-clinical-trial-number should indicate when there are no CTNs"
    (let [base (test-util/base "test/10.5555-catapult.json" {})
      result (clinical-trials/decorate-clinical-trial-number base)]
      (is (false? (:has-clinical-trial-number result)) "When no CTNs, :has-clinical-trial-number should be false")
      (is (empty? (:clinical-trial-number result)) "When no CTNs, :clinical-trial-number should be empty"))))
