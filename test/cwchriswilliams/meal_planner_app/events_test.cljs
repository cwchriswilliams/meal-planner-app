(ns cwchriswilliams.meal-planner-app.events-test
  (:require [cljs.spec.alpha :as spec]
            [cljs.test :refer [deftest is testing]]
            [re-frame.core :as rf]
            [day8.re-frame.test :as rf-test]
            [cwchriswilliams.meal-planner-app.subscriptions]
            [cwchriswilliams.meal-planner-app.events :as evt]
            [cwchriswilliams.meal-planner-app.db :as db]))

(defn nothing-in-local-store
  []
  (rf/reg-cofx
   :local-store-db
   (fn [_ _] nil)))

(defn empty-local-store-db-fixture
  []
  (rf/reg-cofx
   :local-store-db
   (fn [cofx _]
     (assoc cofx :local-store-db
            {}))))

(def test-existing-meal-items {(uuid "5b216f0b-b7a3-4276-a75e-966591fbe2d0") {:name "Potatoes"}
                               (uuid "b081afbd-efb0-4ad9-9724-0c1b742a3220") {:name "Chicken"
                                                                              :steps {(uuid "a44fcddb-f406-495b-9545-9f0539da6a5e") {:title "Stir soup" :description "Stir the soup until it is stirred" :position 1}
                                                                                      (uuid "d40ff947-6ef9-4adc-9ebd-304699f91b93") {:title "Add basil" :description "Just a pinch" :position 4}}}})

(defn existing-local-store-db-fixture
  []
  (rf/reg-cofx
   :local-store-db
   (fn [cofx _]
     (assoc cofx :local-store-db
            test-existing-meal-items))))

(defn intiialize-local-store-with-db-fixture
  [initial-db]
  (rf/reg-cofx
   :local-store-db
   (fn [cofx _]
     (assoc cofx :local-store-db
            initial-db))))

(defn save-to-local-store-as-noop
  []
  (rf/reg-fx
   :save-db-to-local-store
   (fn [_meal-items])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; initialize test ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest initialize-event-test
  (rf-test/run-test-sync
   (nothing-in-local-store)
   (let [meal-items (rf/subscribe [:meal-items])]
     (testing "initialise sets up the initial db if no db in localstore"
       ; initial state
       (is (nil? @meal-items))

       ; event
       (rf/dispatch [:initialize])

       ; expected-result
       (is (= db/example-meal-items @meal-items))
       (is (spec/valid? ::db/meal-items @meal-items)))))

  (rf-test/run-test-sync
   (empty-local-store-db-fixture)
   (let [meal-items (rf/subscribe [:meal-items])]
     (testing "If there is an empty local store, uses an empty local store"
       ; initial state
       (is (nil? @meal-items))

       ; event
       (rf/dispatch [:initialize])

       ; expected-result
       (is (= {} @meal-items))
       (is (spec/valid? ::db/meal-items @meal-items)))))

  (rf-test/run-test-sync
   (existing-local-store-db-fixture)
   (let [meal-items (rf/subscribe [:meal-items])]
     (testing "If there is an existing local store, uses the local store"
       ; initial state
       (is (nil? @meal-items))

       ; event
       (rf/dispatch [:initialize])

       ; expected-result
       (is (= test-existing-meal-items @meal-items))
       (is (spec/valid? ::db/meal-items @meal-items))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; favourite-meal test ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest favourite-meal-event-test
  (doall (map #(rf-test/run-test-sync
                ((comp existing-local-store-db-fixture save-to-local-store-as-noop))
                (let [meal-items (rf/subscribe [:meal-items])]

                  (testing "favourite-meal sets is-favourite? to true on selected meal"
                    ; event
                    (rf/dispatch [:initialize])
                    (rf/dispatch [:favourite-meal %])

                    ; expected-result
                    (is (= (assoc-in test-existing-meal-items [% :is-favourite?] true)
                           @meal-items))
                    (is (spec/valid? ::db/meal-items @meal-items))))) [(uuid "5b216f0b-b7a3-4276-a75e-966591fbe2d0") (uuid "b081afbd-efb0-4ad9-9724-0c1b742a3220")]))
  (doall (map #(rf-test/run-test-sync
                ((comp existing-local-store-db-fixture save-to-local-store-as-noop))
                (let [meal-items (rf/subscribe [:meal-items])]

                  (testing "favourite-meal does nothing if the meal is currently favourited already"
                    ; event
                    (rf/dispatch [:initialize])
                    (rf/dispatch [:favourite-meal %])
                    (rf/dispatch [:favourite-meal %])

                    ; expected-result
                    (is (= (assoc-in test-existing-meal-items [% :is-favourite?] true)
                           @meal-items))
                    (is (spec/valid? ::db/meal-items @meal-items))))) [(uuid "5b216f0b-b7a3-4276-a75e-966591fbe2d0") (uuid "b081afbd-efb0-4ad9-9724-0c1b742a3220")])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; unfavourite-meal test ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest unfavourite-meal-event-test
  (doall (map (fn [meal-item-id]
                (rf-test/run-test-sync
                 ((comp #(intiialize-local-store-with-db-fixture (assoc-in test-existing-meal-items [meal-item-id :is-favourite?] true)) save-to-local-store-as-noop))
                 (let [meal-items (rf/subscribe [:meal-items])]
                   (testing "unfavourite-meal dissocs is-favourite? on selected meal"
                     ; event
                     (rf/dispatch [:initialize])
                     (rf/dispatch [:unfavourite-meal meal-item-id])

                     ; expected-result
                     (is (= test-existing-meal-items @meal-items))
                     (is (spec/valid? ::db/meal-items @meal-items))))))
              [(uuid "5b216f0b-b7a3-4276-a75e-966591fbe2d0") (uuid "b081afbd-efb0-4ad9-9724-0c1b742a3220")]))
  (doall (map (fn [meal-item-id]
                (rf-test/run-test-sync
                 ((comp #(intiialize-local-store-with-db-fixture (assoc-in test-existing-meal-items [meal-item-id :is-favourite?] true)) save-to-local-store-as-noop))
                 (let [meal-items (rf/subscribe [:meal-items])]
                   (testing "unfavourite-meal does nothing if selected meal already not favourite"
                     ; event
                     (rf/dispatch [:initialize])
                     (rf/dispatch [:unfavourite-meal meal-item-id])
                     (rf/dispatch [:unfavourite-meal meal-item-id])

                     ; expected-result
                     (is (= test-existing-meal-items @meal-items))
                     (is (spec/valid? ::db/meal-items @meal-items))))))
              [(uuid "5b216f0b-b7a3-4276-a75e-966591fbe2d0") (uuid "b081afbd-efb0-4ad9-9724-0c1b742a3220")])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; navigate test ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest navigate-fx-test
  (testing "Naviage with a panel stores navigate effect"
    (is (= {:navigate [:test-panel]} (evt/navigate-fx :ignored [:ignored :test-panel])))))

(deftest navigate-to-element-by-id-fx-test
  (testing "navigate-to-element-by-id-fx with a panel and an id stores navigate effect"
    (is (= {:navigate-to-element-by-id [:test-panel :id-to-navigate-to]} (evt/navigate-to-element-by-id-fx :ignored [:ignored :test-panel :id-to-navigate-to])))
    (is (= {:navigate-to-element-by-id [:test-panel :id-to-navigate-to-2]} (evt/navigate-to-element-by-id-fx :ignored [:ignored :test-panel :id-to-navigate-to-2])))))

(deftest navigate-to-edit-element-step-fx-test
  (testing "navigate-to-edit-element-step-fx with a panel, an id and an id stores navigate effect"
    (is (= {:navigate-to-edit-step-panel [:test-panel :id-to-navigate-to :step-id-to-navigate-to]} (evt/navigate-to-edit-element-step-fx :ignored [:ignored :test-panel :id-to-navigate-to :step-id-to-navigate-to])))
    (is (= {:navigate-to-edit-step-panel [:test-panel :id-to-navigate-to-2 :step-id-to-navigate-to]} (evt/navigate-to-edit-element-step-fx :ignored [:ignored :test-panel :id-to-navigate-to-2 :step-id-to-navigate-to])))
    (is (= {:navigate-to-edit-step-panel [:test-panel :id-to-navigate-to :step-id-to-navigate-to-2]} (evt/navigate-to-edit-element-step-fx :ignored [:ignored :test-panel :id-to-navigate-to :step-id-to-navigate-to-2])))
    (is (= {:navigate-to-edit-step-panel [:test-panel :id-to-navigate-to-2 :step-id-to-navigate-to-2]} (evt/navigate-to-edit-element-step-fx :ignored [:ignored :test-panel :id-to-navigate-to-2 :step-id-to-navigate-to-2])))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; save meal item name test ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest save-meal-item-name-event-test
  (doall (map (fn [[meal-item-id meal-name]]
                (rf-test/run-test-sync
                 ((comp existing-local-store-db-fixture save-to-local-store-as-noop))
                 (let [meal-items (rf/subscribe [:meal-items])]
                   (testing "save-meal-item-name sets the meal-item name"
                     ; event
                     (rf/dispatch [:initialize])
                     (rf/dispatch [:save-meal-item-name meal-item-id meal-name])

                     ; expected-result
                     (is (= (assoc-in test-existing-meal-items [meal-item-id :name] meal-name) @meal-items))
                     (is (spec/valid? ::db/meal-items @meal-items))))))
              (map vector [(uuid "5b216f0b-b7a3-4276-a75e-966591fbe2d0") (uuid "b081afbd-efb0-4ad9-9724-0c1b742a3220")] ["Name-1" "Name-2"]))))

(deftest save-meal-item-name-fx-test
  (testing "save-meal-item-name-fx stores navigate effect and updates meal name"
    (is (= {:db {:meal-items {:meal-item-id-1 {:name "new-name-1"}}}
            :fx [[:navigate-to-element-by-id [:meal-item-panel :meal-item-id-1]]]}
           (evt/save-meal-item-name-fx {:db {:meal-items {:meal-item-id-1 {:name "original name"}}}} [:ignored :meal-item-id-1 "new-name-1"])))
    (is (= {:db {:meal-items {:meal-item-id-1 {:name "original name"} :meal-item-id-2 {:name "new-name-2"}}}
            :fx [[:navigate-to-element-by-id [:meal-item-panel :meal-item-id-2]]]}
           (evt/save-meal-item-name-fx {:db {:meal-items {:meal-item-id-1 {:name "original name"} :meal-item-id-2 {:name "original name-1"}}}} [:ignored :meal-item-id-2 "new-name-2"])))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; delete step test ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn build-delete-step-event-test
  [step-id]
  (rf-test/run-test-sync
   ((comp existing-local-store-db-fixture save-to-local-store-as-noop))
   (let [meal-items (rf/subscribe [:meal-items])]

     (testing "delete step deletes the selected step"
                    ; event
       (let [meal-item-id (uuid "b081afbd-efb0-4ad9-9724-0c1b742a3220")]
         (rf/dispatch [:initialize])
         (rf/dispatch [:delete-step meal-item-id step-id])

                    ; expected-result
         (is (= (update-in test-existing-meal-items [meal-item-id :steps] dissoc step-id)
                @meal-items))
         (is (spec/valid? ::db/meal-items @meal-items)))))))

(deftest delete-step-event-test
  (doall (map build-delete-step-event-test
              [(uuid "a44fcddb-f406-495b-9545-9f0539da6a5e") (uuid "d40ff947-6ef9-4adc-9ebd-304699f91b93")])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; delete meal-item test ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest delete-meal-item-test
  (doall (map (fn [meal-item-id]
                (rf-test/run-test-sync
                 ((comp existing-local-store-db-fixture save-to-local-store-as-noop))
                 (let [meal-items (rf/subscribe [:meal-items])]

                   (testing "delete meal-item deletes the selected meal-item"
                    ; event
                     (rf/dispatch [:initialize])
                     (rf/dispatch [:delete-meal-item meal-item-id])

                    ; expected-result
                     (is (= (dissoc test-existing-meal-items meal-item-id)
                            @meal-items)
                         (is (spec/valid? ::db/meal-items @meal-items)))))))
              [(uuid "5b216f0b-b7a3-4276-a75e-966591fbe2d0") (uuid "b081afbd-efb0-4ad9-9724-0c1b742a3220")])))

(deftest save-step-details-test
  (doall (map (fn [[meal-item-id step-id new-title new-description expected-position]]
                (rf-test/run-test-sync
                 ((comp existing-local-store-db-fixture save-to-local-store-as-noop))
                 (let [meal-items (rf/subscribe [:meal-items])]

                   (testing "save step details updates the selected step if exists"
                    ; event
                     (rf/dispatch [:initialize])
                     (rf/dispatch [:save-step-details meal-item-id step-id new-title new-description])
                    ; expected-result
                     (is (= (assoc-in test-existing-meal-items [meal-item-id :steps step-id] {:title new-title :description new-description :position expected-position})
                            @meal-items)
                         (is (spec/valid? ::db/meal-items @meal-items)))))))
              (map vector
                   [(uuid "b081afbd-efb0-4ad9-9724-0c1b742a3220") (uuid "b081afbd-efb0-4ad9-9724-0c1b742a3220")]
                   [(uuid "a44fcddb-f406-495b-9545-9f0539da6a5e") (uuid "d40ff947-6ef9-4adc-9ebd-304699f91b93")]
                   ["New Title 1" "New Title 2"]
                   ["Description 1" "Description 2"]
                   [1 4])))
  (doall (map (fn [[meal-item-id step-id new-title new-description expected-position]]
                (rf-test/run-test-sync
                 ((comp existing-local-store-db-fixture save-to-local-store-as-noop))
                 (let [meal-items (rf/subscribe [:meal-items])]

                   (testing "save step details adds the selected step if does not exist"
                    ; event
                     (rf/dispatch [:initialize])
                     (rf/dispatch [:save-step-details meal-item-id step-id new-title new-description])
                    ; expected-result
                     (is (= (assoc-in test-existing-meal-items [meal-item-id :steps step-id] {:title new-title :description new-description :position expected-position})
                            @meal-items)
                         (is (spec/valid? ::db/meal-items @meal-items)))))))
              (map vector
                   [(uuid "5b216f0b-b7a3-4276-a75e-966591fbe2d0") (uuid "b081afbd-efb0-4ad9-9724-0c1b742a3220")]
                   [(uuid "a44fcddb-f406-495b-9545-9f0539da6a52") (uuid "d40ff947-6ef9-4adc-9ebd-304699f91b92")]
                   ["New Title 1" "New Title 2"]
                   ["Description 1" "Description 2"]
                   [1 5]))))

(deftest get-position-for-step-test
  (testing "Returns the existing value if step with that id already exist"
    (is (= 7 (evt/get-position-for-step {:step-id-1 {:position 7}} :step-id-1)))
    (is (= 2 (evt/get-position-for-step {:step-id-2 {:position 2}} :step-id-2))))
  (testing "Returns the highest existing value + 1 if step with that id does not exist"
    (is (= 8 (evt/get-position-for-step {:step-id-1 {:position 7}} :step-id-2)))
    (is (= 6 (evt/get-position-for-step {:step-id-1 {:position 5} :step-id-2 {:position 2}} :step-id-3)))))
