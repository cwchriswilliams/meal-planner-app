(ns cwchriswilliams.meal-planner-app.db
  (:require [cljs.reader]
            [cljs.spec.alpha :as s]
            [re-frame.core :as rf]))

(def example-meal-items
  {(uuid "5b216f0b-b7a3-4276-a75e-966591fbe2d0") {:name "Potatoes"}
   (uuid "b081afbd-efb0-4ad9-9724-0c1b742a3220") {:name "Chicken"
                                                  :steps{(uuid "a44fcddb-f406-495b-9545-9f0539da6a5e") {:title "Stir soup" :description "Stir the soup until it is stirred" :position 1}
                                                         (uuid "a9aefa92-6b20-4238-97b2-e8dcf1630f3c") {:title "Wait" :position 3}
                                                         (uuid "7936e033-f7a0-46cf-875c-e981d369e1c3") {:title "Fry potatoes" :description "Add to a pot and do some stuff" :position 2}
                                                         (uuid "d40ff947-6ef9-4adc-9ebd-304699f91b93") {:title "Add basil" :description "Just a pinch" :position 4}}}
   (uuid "b6d13158-9d62-49e3-9121-5f8fac1ff3c4") {:name "Yorkies"
                                                  :steps {(uuid "84304ef0-8dcd-4400-a643-864c2a84cd2e") {:title "Yorkies Stir soup" :description "Stir the soup until it is stirred" :position 1}
                                                          (uuid "6c4dc01c-d76e-405d-984c-15b4f50eb9d6") {:title "Wait" :position 3}
                                                          (uuid "63d8fdb9-345c-435c-bd21-9ccee8ac8636") {:title "Yorkies Fry potatoes" :description "Add to a pot and do some stuff" :position 2}
                                                          (uuid "d0ff9351-484e-4f2a-8be6-cfa3645ecfff") {:title "Yorkies Add basil" :description "Just a pinch" :position 4}}}})
(s/def ::active-panel #{:meal-items-list-panel
                        :meal-item-panel
                        :edit-meal-item-name-panel
                        :add-step-panel
                        :edit-step-panel})

(s/def ::active-panel-details (s/keys :req-un [::active-panel]))
(s/def ::meal-item-id uuid?)
(s/def ::name (s/and string? #(< 0 (count %) 50)))
(s/def ::step-id uuid?)
(s/def ::title (s/and string? #(< 0 (count %) 50)))
(s/def ::description (s/and string? #(< 0 (count %) 255)))
(s/def ::position pos-int?)
(s/def ::step-details (s/keys :req-un [::title ::position]
                              :opt-un [::description]))
(s/def ::steps (s/and (s/map-of ::step-id ::step-details)
                      (fn [inp] (apply distinct? (map #(:position (val %)) inp)))))
(s/def ::meal-item-details (s/keys :req-un [::name]
                                   :opt-un [::steps]))
(s/def ::meal-items (s/map-of ::meal-item-id ::meal-item-details ))
(s/def ::db (s/keys :req-un [::meal-items ::active-panel-details]))

(comment
  (require '[clojure.spec.gen.alpha :as gen])
  (require '[clojure.test.check.generators])
  (gen/sample (s/gen ::db))
  (s/valid? ::steps {
    (uuid "5b216f0b-b7a3-4276-a75e-966591fbe2d0") {:title "Bob" :position 7}
    (uuid "5b216f0b-b7a3-4276-a75e-966591fbe2d2") {:title "Fred" :position 8}})
)

(def default-db
  {:meal-items example-meal-items
   :active-panel-details {:active-panel :meal-items-list-panel}})


(def ls-key "meal-planner-app->meal-items")

(defn meal-items->local-store
  "Puts meal-items into localStorage"
  [meal-items]
  (.setItem js/localStorage ls-key (str meal-items)))

(rf/reg-cofx
 :local-store-db
 (fn [cofx _]
   (assoc cofx :local-store-db
          (some->> (.getItem js/localStorage ls-key)
                   (cljs.reader/read-string)))))

(rf/reg-fx
 :save-db-to-local-store
 meal-items->local-store)
