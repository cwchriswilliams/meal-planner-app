(ns cwchriswilliams.meal-planner-app.views.edit-step-panel
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [reagent-mui.material.container :refer [container]]
            [reagent-mui.material.stack :refer [stack]]
            [reagent-mui.material.paper :refer [paper]]
            [reagent-mui.material.button :refer [button]]
            [reagent-mui.material.text-field :refer [text-field]]
            [reagent-mui.icons.arrow-back :refer [arrow-back]]
            [cwchriswilliams.meal-planner-app.view-helpers :refer [event-value]]
            [cwchriswilliams.meal-planner-app.components.save-button :refer [save-button]]
            [cwchriswilliams.meal-planner-app.components.reset-button :refer [reset-button]]))

(defn edit-step-panel
  [details]
  (let [meal-item-id (uuid (:id details))
        step-id (if (:step-id details) (uuid (:step-id details)) (random-uuid))
        meal-item-dets @(rf/subscribe [:meal-item meal-item-id])
        step-dets (get-in meal-item-dets [:steps step-id])
        original-title-val (:title step-dets)
        title-val (r/atom original-title-val)
        original-desc-val (:description step-dets)
        desc-val (r/atom original-desc-val)]
    (fn []
      [container
       [button {:on-click #(rf/dispatch [:navigate-to-element-by-id :meal-item-panel meal-item-id]) :start-icon (r/as-element [arrow-back])} "Back to Meal Item"]
       [stack {:spacing 2}
        [paper {:elevation 2}
         [container
          [stack {:direction "row" :align-items "center" :justify-content "space-between" :sx {:my 3}}
           [container [stack {:sx {:flex-grow 1} :spacing 1}
                       [text-field {:variant "outlined"
                                    :full-width true
                                    :placeholder "Step title..."
                                    :InputProps {:sx {:typography "h6"}}
                                    :value @title-val
                                    :on-change (fn [e] (reset! title-val (event-value e)))}]
                       [text-field {:variant "outlined"
                                    :full-width true
                                    :multiline true
                                    :min-rows 3
                                    :placeholder "Description..."
                                    :InputProps {:sx {:typography "body1"}}
                                    :value @desc-val
                                    :on-change (fn [e] (reset! desc-val (event-value e)))}]]]]]]
        [stack
         {:direction "row" :justify-content "flex-end" :spacing 2}
         [reset-button [{:value @title-val :original-value original-title-val :atom title-val} {:value @desc-val :original-value original-desc-val :atom desc-val}]]
         [save-button [{:value @title-val :original-value original-title-val} {:value @desc-val :original-value original-desc-val}] [:save-step-details meal-item-id step-id]]]]])))
