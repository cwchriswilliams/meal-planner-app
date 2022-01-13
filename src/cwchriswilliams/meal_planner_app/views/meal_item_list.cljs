(ns cwchriswilliams.meal-planner-app.views.meal-item-list
  (:require [re-frame.core :as rf]
            [reagent-mui.material.icon-button :refer [icon-button]]
            [reagent-mui.material.divider :refer [divider]]
            [reagent-mui.material.list :as mui-list]
            [reagent-mui.material.list-item :refer [list-item]]
            [reagent-mui.material.list-item-text :refer [list-item-text]]
            [reagent-mui.material.list-item-button :refer [list-item-button]]
            [reagent-mui.material.list-item-icon :refer [list-item-icon]]
            [reagent-mui.icons.add :refer [add]]
            [reagent-mui.icons.star :refer [star]]
            [reagent-mui.icons.star-border-outlined :refer [star-border-outlined]]))

(defn meal-item-list-item
  [meal-item]
  ^{:key (key meal-item)}
  [list-item
   (if @(rf/subscribe [:is-favourite? (key meal-item)])
     [icon-button {:on-click #(rf/dispatch [:unfavourite-meal (key meal-item)])} [star]]
     [icon-button {:on-click #(rf/dispatch [:favourite-meal (key meal-item)])} [star-border-outlined]])
   [list-item-button
    [list-item-text {:on-click #(rf/dispatch [:navigate-to-element-by-id :meal-item-panel (key meal-item)]) :primary (str (:name (val meal-item)))}]]])

(defn add-new-mean-item []
  [list-item [list-item-button [list-item-icon [add]] [list-item-text {:primary "Add new item"}]]])

(defn meal-item-list
  []
  (let [favourite-items @(rf/subscribe [:favourite-items])
        non-favourite-items @(rf/subscribe [:non-favourite-items])]
    [:div [mui-list/list {:sx {:width "100%"}}
           (doall (map meal-item-list-item favourite-items))
           (when (seq favourite-items) [divider])
           (doall (map meal-item-list-item non-favourite-items))
           [divider]
           [add-new-mean-item]]]))
