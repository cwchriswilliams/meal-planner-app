(ns cwchriswilliams.meal-planner-app.events
  (:require
   [cljs.spec.alpha :as spec]
   [cwchriswilliams.meal-planner-app.db :as mp-db]
   [expound.alpha :as expound]
   [re-frame.core :as rf]))

(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (spec/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (expound/expound a-spec db)) {}))))

(def check-spec-interceptor (rf/after (partial check-and-throw ::mp-db/db)))
(def check-mi-spec-interceptor (rf/after (partial check-and-throw ::mp-db/meal-items)))

(def persist-to-db-interceptor
  (rf/->interceptor
   :id :save-db-to-local-store
   :after (fn [input]
            (assoc-in input [:effects :save-db-to-local-store] (get-in input [:effects :db])))))

(def persist-mi-to-db-interceptor
  (rf/->interceptor
   :id :save-db-to-local-store
   :after (fn [input]
            (assoc-in input [:effects :save-db-to-local-store] (get-in input [:effects :db :meal-items])))))

(rf/reg-event-fx
 :initialize
 [(rf/inject-cofx :local-store-db)
  check-spec-interceptor]
 (fn [{:keys [local-store-db]} _]
   {:db (if local-store-db
          (assoc mp-db/default-db :meal-items local-store-db)
          mp-db/default-db)}))

(rf/reg-event-db
 :favourite-meal
 [(rf/path :meal-items) check-mi-spec-interceptor persist-to-db-interceptor]
 (fn [meal-items [_ meal-name]]
   (assoc-in meal-items [meal-name :is-favourite?] true)))

(rf/reg-event-db
 :unfavourite-meal
 [(rf/path :meal-items) check-mi-spec-interceptor persist-to-db-interceptor]
 (fn [meal-items [_ meal-id]]
   (update meal-items meal-id dissoc :is-favourite?)))

(defn navigate-fx [_ [_ panel-to-navigate-to]]
  {:navigate [panel-to-navigate-to]})

(rf/reg-event-fx :navigate navigate-fx)

(defn navigate-to-element-by-id-fx [_ [_ panel-to-navigate-to id]]
  {:navigate-to-element-by-id [panel-to-navigate-to id]})

(rf/reg-event-fx :navigate-to-element-by-id navigate-to-element-by-id-fx)

(defn navigate-to-edit-element-step-fx [_ [_ panel-to-navigate-to id step-id]]
  {:navigate-to-edit-step-panel [panel-to-navigate-to id step-id]})

(rf/reg-event-fx :navigate-to-edit-step-panel navigate-to-edit-element-step-fx)

(rf/reg-event-db
 :set-active-panel-details
 [check-spec-interceptor]
 (fn [db [_ active-panel-details]]
   (assoc db :active-panel-details active-panel-details)))

(defn save-meal-item-name-fx
  [{:keys [db]} [_ meal-id new-name]]
  {:db (assoc-in db [:meal-items meal-id :name] new-name)
   :fx [[:navigate-to-element-by-id [:meal-item-panel meal-id]]]})

(rf/reg-event-fx
 :save-meal-item-name
 [check-spec-interceptor persist-mi-to-db-interceptor]
 save-meal-item-name-fx)

(rf/reg-event-db
 :delete-step
 [(rf/path :meal-items) check-mi-spec-interceptor persist-to-db-interceptor]
 (fn [meal-items [_ meal-item-id id]]
           (update-in meal-items [meal-item-id :steps] dissoc id)))

(rf/reg-event-fx
 :delete-meal-item
 [check-spec-interceptor persist-mi-to-db-interceptor]
 (fn [{:keys [db]} [_ meal-item-id]]
   {:db (assoc db :meal-items (dissoc (:meal-items db) meal-item-id))
    :fx [[:navigate [:meal-items-list-panel]]]}))

(defn get-position-for-step
  [steps step-id]
  (get-in steps
          [step-id :position]
          (inc
           (apply max 0 (map #(:position (val %)) steps)))))

(rf/reg-event-fx
 :save-step-details
 [check-spec-interceptor persist-mi-to-db-interceptor]
 (fn [{:keys [db]} [_ meal-id step-id new-title new-description]]
   (let [steps (get-in db [:meal-items meal-id :steps])
         original-target-step (get steps step-id)
         updated-target-step (merge original-target-step {:title new-title
                                                          :description new-description
                                                          :position (get-position-for-step steps step-id)})]
     {:db (-> db
              (assoc-in [:meal-items meal-id :steps step-id] updated-target-step))
      :fx [[:navigate-to-element-by-id [:meal-item-panel meal-id]]]})))
