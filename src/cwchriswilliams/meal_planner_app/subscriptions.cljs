(ns cwchriswilliams.meal-planner-app.subscriptions
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :meal-items
 (fn [db _]
   (:meal-items db)))

(rf/reg-sub
 :meal-item
 :<- [:meal-items]
 (fn [meal-items [_ meal-id]]
   (get meal-items meal-id)))

(rf/reg-sub
 :is-favourite?
 :<- [:meal-items]
 (fn [meal-items [_ meal-name]]
   (get-in meal-items [meal-name :is-favourite?])))

(rf/reg-sub
 :favourite-items
 :<- [:meal-items]
 (fn [meal-items _]
   (filter #(:is-favourite? (second %)) meal-items)))

(rf/reg-sub
 :non-favourite-items
 :<- [:meal-items]
 (fn [meal-items _]
   (filter #((complement :is-favourite?) (second %)) meal-items)))

(rf/reg-sub
 :active-panel-details
 (fn [db _]
   (:active-panel-details db)))

(rf/reg-sub
 :meal-item-steps
 :<- [:meal-items]
 (fn [meal-items [_ item-id]]
   (:steps (get meal-items item-id))))
