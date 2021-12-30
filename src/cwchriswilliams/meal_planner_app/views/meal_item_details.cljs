(ns cwchriswilliams.meal-planner-app.views.meal-item-details
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [reagent-mui.material.divider :refer [divider]]
            [reagent-mui.material.dialog :refer [dialog]]
            [reagent-mui.material.dialog-title :refer [dialog-title]]
            [reagent-mui.material.typography :refer [typography]]
            [reagent-mui.material.container :refer [container]]
            [reagent-mui.material.stack :refer [stack]]
            [reagent-mui.material.paper :refer [paper]]
            [reagent-mui.material.button :refer [button]]
            [reagent-mui.icons.edit :refer [edit]]
            [reagent-mui.icons.add :refer [add]]
            [reagent-mui.icons.delete-icon :refer [delete]]
            [reagent-mui.icons.arrow-drop-down :refer [arrow-drop-down]]
            [reagent-mui.icons.arrow-drop-up :refer [arrow-drop-up]]
            [reagent-mui.icons.arrow-back :refer [arrow-back]]
            [reagent-mui.material.icon-button :refer [icon-button]]
            [cwchriswilliams.meal-planner-app.routes :as routes]))

(defn delete-step-dialog
  [is-delete-dialog-open? {:keys [title meal-item-id step-id on-cancel-action]}]
  [dialog {:open is-delete-dialog-open?}
   [dialog-title "Delete this step?"]
   [container
    [:div (str "Are you sure you want to delete the " title " step?")]
    [:div "This action cannot be undone."]]
   [divider]
   [button {:color "error" :on-click #(rf/dispatch [:delete-step meal-item-id step-id])} "Delete"]
   [button {:on-click on-cancel-action} "Cancel"]])

(defn step-card
  [meal-item-id [id detail]]
  ^{:key id}
  [(let [is-delete-dialog-open? (r/atom false)
         dialog-details (-> detail
                            (assoc :meal-item-id meal-item-id)
                            (assoc :step-id id)
                            (assoc :on-cancel-action #(reset! is-delete-dialog-open? false)))]
     (fn []
       [:div [delete-step-dialog @is-delete-dialog-open? dialog-details]
        [paper {:elevation 2}
         [container
          [stack {:direction "row" :align-items "center" :justify-content "space-between"}
           [stack [icon-button [arrow-drop-up]] [icon-button [arrow-drop-down]]]
           [container [stack {:sx {:flex-grow 1}}
                       [:h3 (:title detail)]
                       [:div (:description detail)]]]
           [stack [icon-button {:on-click #(rf/dispatch [:navigate-to-edit-step-panel :edit-step-panel meal-item-id id])} [edit]]
            [icon-button {:on-click #(reset! is-delete-dialog-open? true)} [delete]]]]]]]))])

(defn meal-item-panel
  [details]
  (let [meal-item-id (uuid (:id details))
        meal-item-dets @(rf/subscribe [:meal-item meal-item-id])]
    [container
     [button {:on-click #(rf/dispatch [:navigate :meal-items-list-panel]) :start-icon (r/as-element [arrow-back])} "Back to Meal List"]
     [container [typography {:variant "h4"} (:name meal-item-dets) [icon-button {:on-click #(rf/dispatch [:navigate-to-element-by-id-panel :edit-meal-item-name-panel meal-item-id])} [edit]]]]
     [stack {:spacing 1} (doall (map #(step-card meal-item-id %1) @(rf/subscribe [:meal-item-steps meal-item-id])))
      [paper {:elevation 2}
       [button {:start-icon (r/as-element [add])
                :sx {:width "100%"}
                :on-click #(rf/dispatch [:navigate-to-element-by-id-panel :add-step-panel meal-item-id])}
        [:div "Add new step"]]]
      [paper {:elevation 2}
       [button {:start-icon (r/as-element [delete])
                :color "error"
                :sx {:width "100%"}
                :on-click #(rf/dispatch [:delete-meal-item meal-item-id])}
        [:div "Delete Meal Item"]]]]]))

(defmethod routes/panel :meal-item-panel [details]
  #(meal-item-panel (:details details)))
