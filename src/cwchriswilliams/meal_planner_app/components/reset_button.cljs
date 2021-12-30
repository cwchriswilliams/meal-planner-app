(ns cwchriswilliams.meal-planner-app.components.reset-button
  (:require [reagent-mui.material.button :refer [button]]))

(defn should-be-disabled?
  [field-values]
  (every? #(= (:value %) (:original-value %)) field-values))

(defn reset-values
  [field-values]
  (doall (map #(reset! (:atom %) (:original-value %)) field-values)))

(defn reset-button
  [field-values]
  [button
   {:variant "contained"
    :color "error"
    :disabled (should-be-disabled? field-values)
    :on-click #(reset-values field-values)}
   "Reset"])