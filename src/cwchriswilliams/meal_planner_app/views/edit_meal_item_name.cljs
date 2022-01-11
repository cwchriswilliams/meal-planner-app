(ns cwchriswilliams.meal-planner-app.views.edit-meal-item-name
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [reagent-mui.material.container :refer [container]]
            [reagent-mui.material.stack :refer [stack]]
            [reagent-mui.material.text-field :refer [text-field]]
            [reagent-mui.material.button :refer [button]]
            [reagent-mui.icons.arrow-back :refer [arrow-back]]
            [cwchriswilliams.meal-planner-app.view-helpers :refer [event-value]]
            [cwchriswilliams.meal-planner-app.routes :as routes]
            [cwchriswilliams.meal-planner-app.components.save-button :refer [save-button]]
            [cwchriswilliams.meal-planner-app.components.reset-button :refer [reset-button]]))

(defn edit-meal-item-name-panel
  [details]
  (let [meal-item-id (uuid (:id details))
        meal-item-dets @(rf/subscribe [:meal-item meal-item-id])
        field-value (r/atom (:name meal-item-dets))]
    (fn []
      [container
       [button {:on-click #(rf/dispatch [:navigate-to-element-by-id :meal-item-panel meal-item-id]) :start-icon (r/as-element [arrow-back])} "Back to Meal Item"]
       [stack {:spacing 2}
        [text-field {:variant "outlined"
                     :full-width true
                     :placeholder "Meal Item Name..."
                     :InputProps {:sx {:typography "h4"}}
                     :value @field-value
                     :on-change (fn [e] (reset! field-value (event-value e)))}]
        [stack
         {:direction "row" :justify-content "flex-end" :spacing 2}
         [reset-button [{:value @field-value :original-value (:name meal-item-dets) :atom field-value}]]
         [save-button [{:value @field-value :original-value (:name meal-item-dets)}] [:save-meal-item-name meal-item-id]]]]])))


(defmethod routes/panel :edit-meal-item-name-panel [details]
  #(edit-meal-item-name-panel (:details details)))
