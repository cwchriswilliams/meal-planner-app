(ns cwchriswilliams.meal-planner-app.components.save-button
  (:require [re-frame.core :as rf]
            [reagent-mui.material.button :refer [button]]))

(defn should-be-disabled?
  [field-values]
  (let [live-field-values (map :value field-values)]
    (or (some empty? live-field-values)
        (some #(= (:value %) (:original-value %)) field-values))))

(defn save-button
  [field-values dispatch-params]
  (let [live-field-values (map :value field-values)
        combined-dispatch-params (vec (concat dispatch-params live-field-values))]
    [button
     {:variant "contained"
      :color "success"
      :disabled (should-be-disabled? field-values)
      :on-click #(rf/dispatch combined-dispatch-params)}
     "Save"]))