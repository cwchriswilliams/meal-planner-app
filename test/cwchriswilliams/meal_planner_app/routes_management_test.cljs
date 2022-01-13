(ns cwchriswilliams.meal-planner-app.routes-management-test
  (:require [cwchriswilliams.meal-planner-app.routes-management :as sut]
            [cljs.test :refer [testing is deftest]]))

(deftest register-view-for-view-map-test
  (testing "register-view-for-view-map adds the view to an empty view-map"
    (let [existing-map (atom {})] (sut/register-view-for-view-map existing-map :add-meal-item-panel +) (is (= @existing-map {:add-meal-item-panel +})))
    (let [existing-map (atom {})] (sut/register-view-for-view-map existing-map :add-meal-item-panel -) (is (= @existing-map {:add-meal-item-panel -})))
    (let [existing-map (atom {})] (sut/register-view-for-view-map existing-map :add-meal-item-panel-2 +) (is (= @existing-map {:add-meal-item-panel-2 +}))))
  (testing "register-view-for-view-map returns the assigned view"
    (is (= (sut/register-view-for-view-map (atom {}) :add-meal-item-panel +) {:add-meal-item-panel +}))
    (is (= (sut/register-view-for-view-map (atom {}) :add-meal-item-panel-2 +) {:add-meal-item-panel-2 +}))
    (is (= (sut/register-view-for-view-map (atom {}) :add-meal-item-panel -) {:add-meal-item-panel -})))
  (testing "register-view adds the view when the view-map is not empty"
    (let [existing-map (atom {})]
      ((sut/register-view-for-view-map existing-map :add-meal-item-panel +)
       (sut/register-view-for-view-map existing-map :add-meal-item-panel-2 -))
      (is (= @existing-map {:add-meal-item-panel + :add-meal-item-panel-2 -})))
    (let [existing-map (atom {})]
      ((sut/register-view-for-view-map existing-map :add-meal-item-panel-3 /)
       (sut/register-view-for-view-map existing-map :add-meal-item-panel-4 *))
      (is (= @existing-map {:add-meal-item-panel-3 / :add-meal-item-panel-4 *})))))

(deftest url-for-routes-test
  (testing "Returns nil for empty routes"
    (is (= (sut/url-for-routes [] :home) nil)))
  (testing "register-view-for-view-mapor no matching route"
    (is (= (sut/url-for-routes ["/" {"" :home}] :not-home) nil)))
  (testing "Returns the route for an empty url"
    (is (= (sut/url-for-routes ["/" {"" :home
                                     "meal-item" :meal-item-panel}] :home) "/"))
    (is (= (sut/url-for-routes ["/" {"" :home-2
                                     "meal-item" :meal-item-panel}] :home-2) "/")))
  (testing "Returns the route for a simple url"
    (is (= (sut/url-for-routes ["/" {"" :home
                                     "meal-item-2" :meal-item-panel-2
                                     "meal-item" :meal-item-panel}] :meal-item-panel) "/meal-item"))
    (is (= (sut/url-for-routes ["/" {"" :home
                                     "meal-item-2" :meal-item-panel-2
                                     "meal-item" :meal-item-panel}] :meal-item-panel-2) "/meal-item-2")))
  (testing "Returns the route for a url with correct params"
    (is (= (sut/url-for-routes ["/" {["meal-item/" :id] :meal-item-panel-with-id}] :meal-item-panel-with-id :id 17) "/meal-item/17"))
    (is (= (sut/url-for-routes ["/" {["meal-item/" :id] :meal-item-panel-with-id}] :meal-item-panel-with-id :id 23) "/meal-item/23"))
    (is (= (sut/url-for-routes ["/" {["meal-item/" :not-id] :meal-item-panel-with-id}] :meal-item-panel-with-id :not-id 17) "/meal-item/17"))
    (is (= (sut/url-for-routes ["/" {["meal-item-2/" :id] :meal-item-panel-with-id}] :meal-item-panel-with-id :id 17) "/meal-item-2/17"))
    (is (= (sut/url-for-routes ["/" {["meal-item-2/" :id "/" :not-id] :meal-item-panel-with-id}] :meal-item-panel-with-id :id 17 :not-id 23) "/meal-item-2/17/23")))
  (testing "Returns the same route regardless of the order parameters are provided in"
    (is (= (sut/url-for-routes ["/" {["meal-item-2/" :id "/" :not-id] :meal-item-panel-with-id}] :meal-item-panel-with-id :not-id 23 :id 17) "/meal-item-2/17/23")))
  (testing "Throws js/Error if the incorrect params are provided"
    (is (thrown? js/Error (sut/url-for-routes ["/" {["meal-item/" :id] :meal-item-panel-with-id}] :meal-item-panel-with-id :not-id 17)))))

(deftest parse-for-routes-test
  (testing "Throws js/Error for empty routes"
    (is (thrown? js/Error (sut/parse-for-routes [] "/meal-item"))))
  (testing "Returns nil for no matching route"
    (is (= (sut/parse-for-routes ["/" {"" :home}] "/meal-item") nil)))
  (testing "Returns true route for no matching route if true route provided"
    (is (= (sut/parse-for-routes ["/" [["" :home]
                                  [true :meal-item]]] "/meal-item") {:handler :meal-item})))
  (testing "Returns map with :handler for matching route"
    (is (= (sut/parse-for-routes ["/" {"" :home
                                       "meal-item" :meal-item}] "/") {:handler :home}))
    (is (= (sut/parse-for-routes ["/" {"" :home
                                       "meal-item" :meal-item}] "/meal-item") {:handler :meal-item})))
  (testing "Returns map with :handler and :route-params for matching route with params"
    (is (= (sut/parse-for-routes ["/" {["meal-item/" :id] :meal-item}] "/meal-item/17") {:handler :meal-item :route-params {:id "17"}}))
    (is (= (sut/parse-for-routes ["/" {["meal-item/" :not-id] :meal-item}] "/meal-item/18") {:handler :meal-item :route-params {:not-id "18"}}))
    (is (= (sut/parse-for-routes ["/" {["meal-item-2/" :id] :meal-item-2}] "/meal-item-2/17") {:handler :meal-item-2 :route-params {:id "17"}}))
    (is (= (sut/parse-for-routes ["/" {["meal-item/" :id "/" :not-id] :meal-item}] "/meal-item/17/23") {:handler :meal-item :route-params {:id "17" :not-id "23"}}))
    (is (= (sut/parse-for-routes ["/" {["meal-item/" :id] :meal-item-only-id
                                       ["meal-item/" :id "/" :not-id] :meal-item}] "/meal-item/17/23") {:handler :meal-item :route-params {:id "17" :not-id "23"}}))
    (is (= (sut/parse-for-routes ["/" {["meal-item/" :id] :meal-item-only-id
                                       ["meal-item/" :id "/" :not-id] :meal-item}] "/meal-item/17") {:handler :meal-item-only-id :route-params {:id "17"}}))))
